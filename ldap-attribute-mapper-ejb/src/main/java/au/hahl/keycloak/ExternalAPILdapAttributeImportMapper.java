package au.hahl.keycloak;

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

    private final ComponentModel componentModel;

    public ExternalAPILdapAttributeImportMapper(ComponentModel mapperModel, LDAPStorageProvider ldapProvider) {
        super(mapperModel, ldapProvider);
        this.componentModel = mapperModel;
    }    

            //callApi(ldapUser, componentModel.get(ExternalAPILdapAttributeImportMapper.URL_PROPERTY));
    // uid=svc-hahl-test_bind,cn=users,cn=accounts,dc=hahl,dc=id,dc=au

    @Override
    public void onImportUserFromLDAP(LDAPObject ldapUser, UserModel user, RealmModel realm, boolean isCreate) {
        //var client = new APIClient();
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
