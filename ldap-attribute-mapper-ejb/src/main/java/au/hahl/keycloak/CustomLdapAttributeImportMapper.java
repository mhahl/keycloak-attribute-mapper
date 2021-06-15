package au.hahl.keycloak;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.query.internal.LDAPQuery;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;

/* REST Client */
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;


public class CustomLdapAttributeImportMapper extends AbstractLDAPStorageMapper {

    static final String ID = "external-api-mapper";
    static final String API_URL_KEY = "external.api.url";
    static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(CustomLdapAttributeImportMapper.class);

    /* Get mapper settings */
    private final ComponentModel model;
    private final String apiUrl;

    public CustomLdapAttributeImportMapper(ComponentModel model, LDAPStorageProvider ldapProvider) {
        super(model, ldapProvider);
        this.model = model;

        this.apiUrl = model.get(API_URL_KEY);
        
    }

    @Override
    public void onImportUserFromLDAP(LDAPObject ldapUser, UserModel user, RealmModel realm, boolean isCreate) {
        var userDn = ldapUser.getDn();
        var request = new HttpGet();


        log.info(apiUrl);
        log.info("test");
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
