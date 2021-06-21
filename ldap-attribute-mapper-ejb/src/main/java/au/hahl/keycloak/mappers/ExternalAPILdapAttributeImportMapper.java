package au.hahl.keycloak.mappers;

import java.util.List;
import java.util.Map;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.query.internal.LDAPQuery;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;

import au.hahl.keycloak.UserDetails;
import au.hahl.keycloak.UserService;

import lombok.extern.jbosslog.JBossLog;

/**
 * @author Mark Hahl <mark@hahl.id.au>
 */
@JBossLog
public class ExternalAPILdapAttributeImportMapper extends AbstractLDAPStorageMapper {

    private final UserService userService;
    private final ComponentModel componentModel;

    public ExternalAPILdapAttributeImportMapper(ComponentModel mapperModel, LDAPStorageProvider ldapProvider) {
        super(mapperModel, ldapProvider);
        this.componentModel = mapperModel;

        var url = componentModel.get(ExternalAPILdapImportMapperFactory.URL_PROPERTY);
        this.userService = new UserService(url);
    }

    /**
     * Sync attributes when the user is imported from LDAP.
     * 
     * When the user is imported from LDAP query the API for the attribute details.
     * If the attribute is `null` then remove the attribute from the user.
     * 
     * Attributes which already have values will be writen over.
     */
    @Override
    public void onImportUserFromLDAP(LDAPObject ldapUser, UserModel keycloakUser, RealmModel realm, boolean isCreate) {
        try {

            UserDetails apiUser = userService.getUserDetails(keycloakUser.getUsername());
            for (Map.Entry<String, String[]> attribute : apiUser.attributes.entrySet()) {

                if (attribute.getValue() == null) {
                    keycloakUser.removeAttribute(attribute.getKey());
                    return;
                }

                var values = List.of(attribute.getValue());
                keycloakUser.setAttribute(attribute.getKey(), values);
            }

        } catch (Exception e) {
            e.printStackTrace();
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
