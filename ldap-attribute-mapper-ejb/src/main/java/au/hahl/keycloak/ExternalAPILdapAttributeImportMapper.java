package au.hahl.keycloak;

import java.io.IOException;
import java.util.List;
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


@JBossLog
public class ExternalAPILdapAttributeImportMapper extends AbstractLDAPStorageMapper {

    public static final String URL_PROPERTY = "api.url.property";

    private final UserService userService;
    private final ComponentModel componentModel;

    public ExternalAPILdapAttributeImportMapper(ComponentModel mapperModel, LDAPStorageProvider ldapProvider) {
        super(mapperModel, ldapProvider);
        this.componentModel = mapperModel;

        var url = componentModel.get(URL_PROPERTY);
        this.userService = new UserService(url);
    }    

    @Override
    public void onImportUserFromLDAP(LDAPObject ldapUser, UserModel keycloakUser, RealmModel realm, boolean isCreate) {
        try {

            /* Retrieve the user details from the API */
            UserDetails apiUser = userService.getUserDetails(keycloakUser.getUsername());
            log.info(String.format("Updating keycloak user '%s' with external user '%s'",
                keycloakUser.getUsername(),
                apiUser.getUsername()));

            /* Update the user paramaters */
            for (Map.Entry<String, String[]> attribute : apiUser.attributes.entrySet()) {
                var values = List.of(attribute.getValue());
                keycloakUser.setAttribute(attribute.getKey(), values);
                log.info("Setting attribute " + attribute.getKey() + " to " + values.toString() );
            }
            

        } catch (Exception e) {
            log.error("could not return details: \n\t" + e.getMessage());
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
