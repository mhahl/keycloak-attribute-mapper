package au.hahl.keycloak;

import java.io.IOException;
import java.util.Map;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPDn;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.query.internal.LDAPQuery;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;

import lombok.extern.jbosslog.JBossLog;

/* REST Client */
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

@JBossLog
public class ExternalAPILdapAttributeImportMapper extends AbstractLDAPStorageMapper {

    public static final String URL_PROPERTY = "api.url.property";

    private final ComponentModel componentModel;

    public ExternalAPILdapAttributeImportMapper(ComponentModel mapperModel, LDAPStorageProvider ldapProvider) {
        super(mapperModel, ldapProvider);
        this.componentModel = mapperModel;
    }    

    /**
     * Return attribues to set from the API.
     * @param dn  User DN
     * @param uri Service URL
     * @return Attributes to set for the user.
     */
    Map<String, Object> callApi(LDAPObject ldapUser, String url) throws IOException {

        /* Build request json */
        JSONObject json = new JSONObject();
        json.put("dn", ldapUser.getDn().toString());
        json.put("uuid", ldapUser.getUuid());

        /* Build HTTP Post request */
        var client = HttpClientBuilder.create().build();
        StringEntity params = new StringEntity(json.toString());

        HttpPost request = new HttpPost(url);
        request.addHeader("content-type", "application/json");
        request.setEntity(params);

        try (var response = client.execute(request)) {
            log.info("sent http");
        } catch (Exception e) {
            log.error("error in http post request:\n\t" + e.getMessage());
        }

        return null;
    
    }

    @Override
    public void onImportUserFromLDAP(LDAPObject ldapUser, UserModel user, RealmModel realm, boolean isCreate) {

        try {
            Map<String, Object> attributes;
            callApi(ldapUser, componentModel.get(ExternalAPILdapAttributeImportMapper.URL_PROPERTY));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public void onRegisterUserToLDAP(LDAPObject ldapUser, UserModel localUser, RealmModel realm) {
    }

    @Override
    public UserModel proxy(LDAPObject ldapUser, UserModel delegate, RealmModel realm) {
        return delegate;
    }

    @Override
    public void beforeLDAPQuery(LDAPQuery query) {
    }
}
