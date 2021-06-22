package au.hahl.keycloak.mappers;

import java.security.acl.Group;
import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.ModelException;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.ldap.idm.query.internal.LDAPQuery;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;
import org.keycloak.storage.user.SynchronizationResult;
import org.keycloak.models.utils.KeycloakModelUtils;

import au.hahl.keycloak.UserDetails;
import au.hahl.keycloak.UserService;

import lombok.extern.jbosslog.JBossLog;

/**
 * @author Mark Hahl <mark@hahl.id.au>
 */
@JBossLog
public class ExternalAPILdapGroupImportMapper extends AbstractLDAPStorageMapper {

    private final UserService userService;
    private final ComponentModel componentModel;
    private final LDAPStorageProvider ldapProvider;

    public ExternalAPILdapGroupImportMapper(ComponentModel mapperModel, LDAPStorageProvider ldapProvider) {
        super(mapperModel, ldapProvider);
        this.componentModel = mapperModel;
        this.ldapProvider = ldapProvider;

        var url = componentModel.get(ExternalAPILdapImportMapperFactory.URL_PROPERTY);
        this.userService = new UserService(url);
    }

    protected void removeUserFromKeycloakgroup(GroupModel keycloakGroup, UserModel keycloakUser) {
        if (!keycloakUser.isMemberOf(keycloakGroup)) {
            return;
        }
        keycloakUser.leaveGroup(keycloakGroup);
    }

    protected void addUserToKeycloakGroup(GroupModel keycloakGroup, UserModel keycloakUser) {
        if (keycloakUser.isMemberOf(keycloakGroup)) {
            return;
        }
        keycloakUser.joinGroup(keycloakGroup);
    }

    protected GroupModel createKeycloakGroup(String groupName, RealmModel realm, SynchronizationResult syncResult) {
        var group = realm.searchForGroupByNameStream(groupName, null, null)
                        .findFirst()
                        .orElse(null);

        if (group != null) {
            log.info("group exist");
            return group;
        }

        log.info("group NO NO exist");

        try {
            return realm.createGroup(groupName);
        } catch (ModelException e) {
            syncResult.increaseFailed();
        }
        return null;
    }


    public SynchronizationResult syncApiGroupDataToKeycloak(UserService userService, UserModel keycloakUser, RealmModel realm) {
        SynchronizationResult syncResult = new SynchronizationResult() {
            @Override
            public String getStatus() {
                return String.format("%d imported groups, %d updated groups, %d removed groups", getAdded(), getUpdated(), getRemoved());
            }
        };

        log.debugf("Syncing groups from API into Keycloak DB. Mapper is [%s], LDAP provider is [%s]", componentModel.getName(), ldapProvider.getModel().getName());

        try {

            UserDetails apiUser = userService.getUserDetails(keycloakUser.getUsername());
            List<String> apiGroups = apiUser.getGroups();
            List<String> keycloakGroups = keycloakUser.getGroupsStream()
                                                    .map(g -> g.getName())
                                                    .collect(Collectors.toList());

            List<String> groupsToAdd = apiGroups;
            List<String> groupsToRemove = keycloakGroups;


            /* Return the differenes */
            groupsToAdd.removeAll(keycloakGroups);
            groupsToRemove.removeAll(apiGroups);

            log.info(groupsToAdd.toString());
            log.info(groupsToRemove.toString());


            try {
                KeycloakModelUtils.runJobInTransaction(ldapProvider.getSession().getKeycloakSessionFactory(), session -> {
                    for (String groupName : groupsToAdd) {
                        GroupModel group = createKeycloakGroup(groupName, realm, syncResult);
                        addUserToKeycloakGroup(group, keycloakUser);
                        syncResult.increaseAdded();
                    }
                    for (String groupName : groupsToRemove) {
                        var group = realm.searchForGroupByNameStream(groupName, null, null)
                            .findFirst()
                            .orElse(null);

                        if (group == null){
                            log.warnf("tried to remove group %s from user %ss which doesnt exist",
                                             groupName, keycloakUser.getUsername());
                        }
                        removeUserFromKeycloakgroup(group, keycloakUser);
                    }
                });
            } catch (ModelException e) {
                syncResult.increaseFailed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return syncResult;

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
        syncApiGroupDataToKeycloak(userService, keycloakUser, realm);
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
