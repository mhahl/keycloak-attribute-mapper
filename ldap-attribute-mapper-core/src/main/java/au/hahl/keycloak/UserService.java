package au.hahl.keycloak;

import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/* REST Client */
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mark Hahl <mark@hahl.id.au>
 */
public class UserService {

    @Getter @Setter
    public String apiUrl;

    public UserService(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * Return attribues to set from the API.
     * @param dn  Username
     * @param uri Service URL
     * @return UserModel user representation.
     */
    public UserDetails getUserDetails(String userDn) throws IOException, InvalidUserException {

        /* Build the json. */
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestNode = mapper.createObjectNode();
        requestNode.put("username", userDn);

        /* Build HTTP Post request */
        var client = HttpClientBuilder.create().build();
        String json = mapper.writeValueAsString(requestNode);
        StringEntity params = new StringEntity(json);

        /* Build the HTTP Post and set the required paramaers */
        HttpPost request = new HttpPost(this.apiUrl);
        request.addHeader("content-type", "application/json");
        request.setEntity(params);

        try (var response = client.execute(request)) {
            var entity = response.getEntity();

            /**
             * Check the response is HTTP 200 and the data has been been mapped
             * to the userDetils. Check if the username or attributes are null,
             * as they should never be null, only blank.
             */
            if (entity != null && response.getStatusLine().getStatusCode() == 200) {

                var result = EntityUtils.toString(entity);
                UserDetails user = mapper.readValue(result, UserDetails.class);

                if (user.username == null) {
                    throw new InvalidUserException("response does not contain a `username` paramater");
                }

                if (user.attributes == null) {
                    throw new InvalidUserException("response does not contain a `attributes` paramater");
                }

                return user;
            }
        } catch (com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException e) {
            throw new InvalidUserException("invalid json");
        }
        /* No value */
        throw new InvalidUserException("http request failed");
            
    }
}
