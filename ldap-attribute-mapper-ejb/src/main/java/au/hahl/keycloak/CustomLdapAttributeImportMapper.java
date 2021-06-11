import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.query.internal.LDAPQuery;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;

import java.util.*;

public class CustomLdapAttributeImportMapper extends AbstractLDAPStorageMapper {

    static final String ID = "demo-ldapimport-mapper";

    private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(CustomLdapAttributeImportMapper.class);

    static final String ATTRIBUTES_TO_IMPORT_KEY = "attributesToImport";
    private final ComponentModel componentModel;

    public CustomLdapAttributeImportMapper(ComponentModel mapperModel, LDAPStorageProvider ldapProvider) {
        super(mapperModel, ldapProvider);
        this.componentModel = mapperModel;
    }

    @Override
    public void onImportUserFromLDAP(LDAPObject ldapUser, UserModel user, RealmModel realm, boolean isCreate) {
	log.info("XXXXXXX - onImportUserFromLDAP");
	List<String> list=new ArrayList<String>();  
	 //Adding elements in the List  
	list.add(user.getFirstName() + "@hahl.id.au");

	user.setEmail("properemail@exaple.com");
        user.setAttribute("example", list);	
        user.setAttribute("email", list);	
        user.setAttribute("mail", list);	


	log.info(ldapUser);
	log.info(user);
	
    }

    @Override
    public void onRegisterUserToLDAP(LDAPObject ldapUser, UserModel localUser, RealmModel realm) {
	log.info("XXXXXXX - onRegisterUserToLDAP");
        // NOOP
    }

    @Override
    public UserModel proxy(LDAPObject ldapUser, UserModel delegate, RealmModel realm) {
        return delegate;
    }

    @Override
    public void beforeLDAPQuery(LDAPQuery query) {
	log.info("XXXXXXX - beforeLDAPQuery");

        String attributeCsv = componentModel.getConfig().getFirst(ATTRIBUTES_TO_IMPORT_KEY);
	log.info("attributeCsv: " + attributeCsv);
        if (attributeCsv == null || attributeCsv.trim().isBlank()) {
	    log.info("no attributes");
            return;
        }

        for (String attributeCandidate : attributeCsv.trim().split(",")) {
            String attribute = attributeCandidate.trim();
            if (attribute.isBlank()) {
                continue;
            }
            query.addReturningLdapAttribute(attributeCandidate);
        }

	log.info(query);


    }
}
