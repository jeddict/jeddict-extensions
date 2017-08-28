/**
 * Copyright [2016] Gaurav Gupta
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
package org.jcode.infra;

import java.util.Arrays;
import static java.util.Collections.EMPTY_LIST;
import java.util.List;
import static org.jcode.infra.DatabaseType.DERBY;
import static org.jcode.infra.DatabaseType.H2;
import static org.jcode.infra.ServerFamily.PAYARA_FAMILY;
import static org.jcode.infra.ServerFamily.WILDFLY_FAMILY;
import org.netbeans.jcode.jpa.PersistenceProviderType;
import static org.netbeans.jcode.jpa.PersistenceProviderType.ECLIPSELINK;
import static org.netbeans.jcode.jpa.PersistenceProviderType.HIBERNATE;

/**
 *
 * @author jGauravGupta
 */
public enum ServerType {
    
    NONE(true, null, "<No Server Selected>", null, null, false,
            null, null, EMPTY_LIST),
    PAYARA(true, PAYARA_FAMILY, "Payara", ECLIPSELINK, DERBY, false,
           "${build.name}.war", "DockerFile_PAYARA.ftl", Arrays.asList("latest", "161", "161.1")),
    PAYARA_MICRO(true, PAYARA_FAMILY, "Payara Micro", ECLIPSELINK, DERBY, false,
           "${build.name}.jar", "DockerFile_JAVA.ftl", EMPTY_LIST),
    WILDFLY(false, WILDFLY_FAMILY, "Wildfly", HIBERNATE, H2, false,
           "${build.name}.war", "DockerFile_WILDFLY.ftl", Arrays.asList("latest", "8.1.0.Final", "8.2.1.Final", "8.2.0.Final", "9.0.0.Final", "10.1.0.Final", "9.0.1.Final", "9.0.2.Final", "10.0.0.Final")),
    WILDFLY_SWARM(false, WILDFLY_FAMILY, "Wildfly Swarm", HIBERNATE, H2, true,
          "${build.name}-swarm.jar", "DockerFile_JAVA.ftl", EMPTY_LIST);
//    GLASSFISH("Glassfish", Arrays.asList("4.1.1","4.1.1-web")),

    private final ServerFamily family;
    private final String displayName;
    private final String binary;
    private final String template;
    private final List<String> version;
    private final PersistenceProviderType persistenceProviderType;
    private final DatabaseType embeddedDB;
    private final boolean embeddedDBDriverRequired;
    private final boolean visible;

    private ServerType (boolean                 visible,
                        ServerFamily            family, 
                        String                  displayName, 
                        PersistenceProviderType persistenceProviderType, 
                        DatabaseType            embeddedDB, 
                        boolean                 embeddedDBDriverRequired,
                        String                  binary, 
                        String                  template, 
                        List<String>            version) {
        this.family = family;
        this.displayName = displayName;
        this.embeddedDB = embeddedDB;
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
    public DatabaseType getEmbeddedDB() {
        return embeddedDB;
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
