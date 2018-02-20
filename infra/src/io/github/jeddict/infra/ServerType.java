/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.jeddict.infra;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;
import static java.util.Collections.singletonMap;
import java.util.List;
import java.util.Map;
import static io.github.jeddict.infra.DatabaseType.DERBY;
import static io.github.jeddict.infra.DatabaseType.H2;
import static io.github.jeddict.infra.ServerFamily.PAYARA_FAMILY;
import static io.github.jeddict.infra.ServerFamily.WILDFLY_FAMILY;
import io.github.jeddict.jcode.jpa.PersistenceProviderType;
import static io.github.jeddict.jcode.jpa.PersistenceProviderType.ECLIPSELINK;
import static io.github.jeddict.jcode.jpa.PersistenceProviderType.HIBERNATE;

/**
 *
 * @author jGauravGupta
 */
public enum ServerType {
    
    NONE(true, null, "<No Server Selected>", 
            null, null, EMPTY_MAP, false,
            null, null, EMPTY_LIST),
    PAYARA(true, PAYARA_FAMILY, "Payara", 
            ECLIPSELINK, H2, singletonMap(DERBY, "jdbc/__derby"), false,
           "${build.name}.war", "DockerFile_PAYARA.ftl", 
            asList("latest", "161", "161.1")),
    PAYARA_MICRO(true, PAYARA_FAMILY, "Payara Micro", 
            ECLIPSELINK, H2, singletonMap(DERBY, "jdbc/__derby"), false,
           "${build.name}.jar", "DockerFile_JAVA.ftl", 
            EMPTY_LIST),
    WILDFLY(false, WILDFLY_FAMILY, "Wildfly", 
            HIBERNATE, H2, EMPTY_MAP, false,
           "${build.name}.war", "DockerFile_WILDFLY.ftl", 
            asList("latest", "8.1.0.Final", "8.2.1.Final", "8.2.0.Final", "9.0.0.Final", "10.1.0.Final", "9.0.1.Final", "9.0.2.Final", "10.0.0.Final")),
    WILDFLY_SWARM(false, WILDFLY_FAMILY, "Wildfly Swarm", 
            HIBERNATE, H2, EMPTY_MAP, true,
          "${build.name}-swarm.jar", "DockerFile_JAVA.ftl", 
            EMPTY_LIST);
//    GLASSFISH("Glassfish", Arrays.asList("4.1.1","4.1.1-web")),

    private final ServerFamily family;
    private final String displayName;
    private final String binary;
    private final String template;
    private final List<String> version;
    private final PersistenceProviderType persistenceProviderType;
    private final DatabaseType defaultDB;
    private final Map<DatabaseType, String> embeddedDBs;
    private final boolean embeddedDBDriverRequired;
    private final boolean visible;

    private ServerType (boolean                     visible,
                        ServerFamily                family, 
                        String                      displayName, 
                        PersistenceProviderType     persistenceProviderType, 
                        DatabaseType                defaultDB,
                        Map<DatabaseType, String>   embeddedDBs, 
                        boolean                     embeddedDBDriverRequired,
                        String                      binary, 
                        String                      template, 
                        List<String>                version) {
        this.family = family;
        this.displayName = displayName;
        this.defaultDB = defaultDB;
        this.embeddedDBs = embeddedDBs;
        this.embeddedDBDriverRequired = embeddedDBDriverRequired;
        this.binary = binary;
        this.template = template;
        this.version = version;
        this.persistenceProviderType = persistenceProviderType;
        this.visible = visible;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getVersion() {
        return version;
    }

    /**
     * @return the persistenceProvider
     */
    public PersistenceProviderType getPersistenceProviderType() {
        return persistenceProviderType;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * @return the family
     */
    public ServerFamily getFamily() {
        return family;
    }

    /**
     * @return the binary
     */
    public String getBinary() {
        return binary;
    }

    /**
     * @return the template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * @return the embeddedDB
     */
    public DatabaseType getDeafultDB() {
        return defaultDB;
    }
    
    /**
     * @return the embeddedDB
     */
    public Map<DatabaseType, String> getEmbeddedDBs() {
        return embeddedDBs;
    }
    
    public boolean isEmbeddedDB(DatabaseType type){
        if(defaultDB == type){
            return true;
        }
        return embeddedDBs.keySet().contains(type);
    }

    /**
     * @return the embeddedDBDriverRequired
     */
    public boolean isEmbeddedDBDriverRequired() {
        return embeddedDBDriverRequired;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

}
