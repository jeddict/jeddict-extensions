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
package org.jcode.docker.generator;

import java.util.Arrays;
import java.util.List;
import static org.jcode.docker.generator.ServerType.PAYARA;
import static org.jcode.docker.generator.ServerType.WILDFLY;

/**
 *
 * @author jGauravGupta
 */
public enum DatabaseType {
    DERBY("Derby", Arrays.asList("--"), false, Arrays.asList(PAYARA)),
    H2("H2", Arrays.asList("--"), false, Arrays.asList(WILDFLY)),
    MYSQL("MySQL", Arrays.asList("latest","5.5","5.6","5.7", "8.0"), true),
    MARIA_DB("MariaDB", Arrays.asList("latest", "10.1", "10.0", "5.5"), true, Arrays.asList(PAYARA)),
    POSTGRESQL("PostgreSQL", Arrays.asList("latest", "9.6", "9.5", "9.4", "9.3", "9.2"), true);
    
    private String displayName;
    private List<String> version;
    private boolean dockerSupport;
    private List<ServerType> supportedServer;

    private DatabaseType(String displayName, List<String> version, boolean dockerSupport) {
        this.displayName = displayName;
        this.version = version;
        this.dockerSupport = dockerSupport;
    }
    
    private DatabaseType(String displayName, List<String> version, boolean dockerSupport, List<ServerType> supportedServer) {
        this(displayName, version, dockerSupport);
        this.supportedServer = supportedServer;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public List<String> getVersion() {
        return version;
    }
    
    public boolean isDockerSupport() {
        return dockerSupport;
    }
    
    public List<ServerType> getSupportedServer() {
        if(supportedServer == null){
            return Arrays.asList(ServerType.values());
        }
        return supportedServer;
    }
    
    public boolean isServerSupported(ServerType server) {
        if(supportedServer == null){
            return true;
        }
        return supportedServer.contains(server);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
