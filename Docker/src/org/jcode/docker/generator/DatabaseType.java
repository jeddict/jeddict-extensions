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
import org.apache.commons.lang.StringUtils;
import static org.jcode.docker.generator.ServerType.PAYARA;
import static org.jcode.docker.generator.ServerType.PAYARA_MICRO;
import static org.jcode.docker.generator.ServerType.WILDFLY;
import static org.jcode.docker.generator.ServerType.WILDFLY_SWARM;
import org.netbeans.api.db.explorer.DatabaseConnection;

/**
 *
 * @author jGauravGupta
 */
public enum DatabaseType {
    DERBY("Derby", "1527",
            new DatabaseDriver("org.apache.derby", "derby", "10.13.1.1", "org.apache.derby.jdbc.ClientDriver"),
            Arrays.asList("--"), false, Arrays.asList(PAYARA, PAYARA_MICRO)),
    H2("H2", "test",
            new DatabaseDriver("com.h2database", "h2", "1.4.193", "org.h2.Driver"),
            Arrays.asList("--"), false, Arrays.asList(WILDFLY, WILDFLY_SWARM)),
    MYSQL("MySQL", "3306",
            new DatabaseDriver("mysql", "mysql-connector-java", "5.1.38", "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource"),
            Arrays.asList("latest", "5.5", "5.6", "5.7", "8.0"), true),
    MARIA_DB("MariaDB", "3306",
            new DatabaseDriver("org.mariadb.jdbc", "mariadb-java-client", "1.5.8", "org.mariadb.jdbc.MariaDbDataSource"),
            Arrays.asList("latest", "10.1", "10.0", "5.5"), true),
    POSTGRESQL("PostgreSQL", "5432",
            new DatabaseDriver("postgresql", "postgresql", "9.1-901.jdbc4", "org.postgresql.xa.PGXADataSource"),
            Arrays.asList("latest", "9.6", "9.5", "9.4", "9.3", "9.2"), true);

    private String displayName;
    private final String defaultPort;
    private DatabaseDriver driver;
    private List<String> version;
    private boolean dockerSupport;
    private List<ServerType> supportedServer;

    private DatabaseType(String displayName, String defaultPort, DatabaseDriver driver,
            List<String> version, boolean dockerSupport) {
        this.displayName = displayName;
        this.defaultPort = defaultPort;
        this.driver = driver;
        this.version = version;
        this.dockerSupport = dockerSupport;
    }

    private DatabaseType(String displayName, String defaultPort, DatabaseDriver driver,
            List<String> version, boolean dockerSupport, List<ServerType> supportedServer) {
        this(displayName, defaultPort, driver, version, dockerSupport);
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
        if (supportedServer == null) {
            return Arrays.asList(ServerType.values());
        }
        return supportedServer;
    }

    public boolean isServerSupported(ServerType server) {
        if (supportedServer == null) {
            return true;
        }
        return supportedServer.contains(server);
    }

    /**
     * @return the defaultPort
     */
    public String getDefaultPort() {
        return defaultPort;
    }

    /**
     * @return the driver
     */
    public DatabaseDriver getDriver() {
        return driver;
    }
    
    public boolean isMatchingDatabase(DatabaseConnection databaseConnection){
        if (databaseConnection != null) {
            if (StringUtils.containsIgnoreCase(databaseConnection.getDriverClass(), this.name())) {
                return true;
            }
            if (StringUtils.containsIgnoreCase(databaseConnection.getDatabaseURL(), this.name())) {
                return true;
            }
        }
        return false;
        
    }

    @Override
    public String toString() {
        return displayName;
    }
}
