/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.hahl.keycloak.mappers;

import java.util.LinkedList;
import java.util.List;

import com.google.auto.service.AutoService;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.ldap.LDAPStorageProvider;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapper;
import org.keycloak.storage.ldap.mappers.AbstractLDAPStorageMapperFactory;
import org.keycloak.storage.ldap.mappers.LDAPStorageMapperFactory;

/**
 * @author <a href="mailto:mhahl@hahl.id.au">Mark Hahl</a>
 */
@AutoService(LDAPStorageMapperFactory.class)
public class ExternalAPILdapImportMapperFactory extends AbstractLDAPStorageMapperFactory {

    public static final String PROVIDER_ID = "external-api-ldap-mapper";

    protected static final String URL_PROPERTY = "api.url.property";
    protected static final String TYPE_PROPERTY = "api.type.property";
    protected static final List<String> IMPORT_TYPE_LIST = new LinkedList<>();
    protected static final List<ProviderConfigProperty> configProperties;

    static {
        configProperties = getConfigProps(null);
        for (ImportType type : ImportType.values()) {
            IMPORT_TYPE_LIST.add(type.toString());
        }
    }

    /**
     * Build the list of configuration.
     * @param parent
     * @return
     */
    private static List<ProviderConfigProperty> getConfigProps(ComponentModel parent) {

        return ProviderConfigurationBuilder.create()
                .property().name(URL_PROPERTY)
                           .label("URL")
                           .helpText("External API URL ")
                           .type(ProviderConfigProperty.STRING_TYPE)
                           .defaultValue("http://127.0.0.1:4567/")
                           .add()
                .property().name(TYPE_PROPERTY)
                            .label("Import Type")
                            .type(ProviderConfigProperty.LIST_TYPE)
                            .helpText("Import groups or attributes")
                            .options(IMPORT_TYPE_LIST)
                            .add()
                .build();
    }

    /**
     * Return the help text of the mapper.
     */
    @Override
    public String getHelpText() {
        return "Use an external API to map user attributes or groups.";
    }

    /**
     * Return a list of configuration properties.
     */
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    /**
     * Return a list of configuation properties.
     */
    @Override
    public List<ProviderConfigProperty> getConfigProperties(RealmModel realm, ComponentModel parent) {
        return getConfigProps(parent);
    }

    /**
     * Returns the Id for the extension/
     */
    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    /**
     * Returns either a attribute mapper or a group mapper.
     */
    @Override
    protected AbstractLDAPStorageMapper createMapper(ComponentModel mapperModel, LDAPStorageProvider federationProvider) {
        var type = ImportType.valueOf(mapperModel.get(TYPE_PROPERTY));

        if (type == ImportType.IMPORT_ATRIBUTES) {
            return new ExternalAPILdapAttributeImportMapper(mapperModel, federationProvider);
        } else {
            // XXX not implimented yet
        }
        return null;
    }
}
