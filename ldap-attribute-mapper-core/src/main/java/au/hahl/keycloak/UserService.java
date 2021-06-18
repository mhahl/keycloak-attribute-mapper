package au.hahl.keycloak;

import java.util.Map;
import java.io.*;

/* REST Client */
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

public class UserService {

    @Getter @Setter
    public String apiUrl;

    public UserService(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * Return attribues to set from the API.
     * @param dn  User DN
     * @param uri Service URL
     * @return Attributes to set for the user.
     */
    Map<String, Object> getUserDetails(String userDn) throws IOException {

        /* Build request json */
        JSONObject json = new JSONObject();
        json.put("dn", userDn);

        /* Build HTTP Post request */
        var client = HttpClientBuilder.create().build();
        StringEntity params = new StringEntity(json.toString());

        HttpPost request = new HttpPost(this.apiUrl);
        request.addHeader("content-type", "application/json");
        request.setEntity(params);

        try (var response = client.execute(request)) {
            var entity = response.getEntity();
            if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                var result = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            throw e;
        }

        return null;
    
    }
}
