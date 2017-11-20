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
import org.apache.commons.lang3.StringUtils;
import org.jcode.infra.DatabaseType;
import org.jcode.infra.ServerType;
import org.netbeans.jcode.stack.config.data.LayerConfigData;

/**
 *
 * @author Gaurav Gupta
 */
public class DockerConfigData extends LayerConfigData {

    private ServerType serverType;
    private DatabaseType databaseType;
    private String databaseVersion;
    private String dbUserName = "myuser";
    private String dbPassword = "secret";
    private String dataSource = "SampleDS";
    private String dbName = "sample";
    private String dbHost;//db service/svc
    private String dbPort;

    private String dockerMachine;
    private boolean dockerEnable;
    private boolean dockerActivated;//checkbox
    private String dockerNamespace;
    private String dockerRepository;
    
    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public DatabaseType getDatabaseType() {
        if (serverType == null || serverType == ServerType.NONE) {
            return null;
        }
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public String getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(String databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public boolean isDbInfoExist() {
        return StringUtils.isNotBlank(this.dbName);
    }

    /**
     * @return the dockerMachine
     */
    public String getDockerMachine() {
        return dockerMachine;
    }

    /**
     * @param dockerMachine the dockerMachine to set
     */
    public void setDockerMachine(String dockerMachine) {
        this.dockerMachine = dockerMachine;
    }

    /**
     * @return the dockerEnable
     */
    public boolean isDockerEnable() {
        return dockerEnable;
    }

    /**
     * @param dockerEnable the dockerEnable to set
     */
    public void setDockerEnable(boolean dockerEnable) {
        this.dockerEnable = dockerEnable;
    }

    /**
     * @return the dockerActivated
     */
    public boolean isDockerActivated() {
        return dockerActivated;
    }

    /**
     * @param dockerActivated the dockerActivated to set
     */
    public void setDockerActivated(boolean dockerActivated) {
        this.dockerActivated = dockerActivated;
    }

    /**
     * @return the dbHost
     */
    public String getDbHost() {
        return dbHost;
    }

    /**
     * @param dbHost the dbHost to set
     */
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    /**
     * @return the dbPort
     */
    public String getDbPort() {
        return dbPort;
    }

    /**
     * @param dbPort the dbPort to set
     */
    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    @Override
    public List<String> getUsageDetails() {
        return Arrays.asList(getDatabaseType().getDisplayName(),
                getServerType().getDisplayName(), dockerEnable ? "Docker" : null);
    }

    /**
     * @return the dockerNamespace
     */
    public String getDockerNamespace() {
        return dockerNamespace;
    }

    /**
     * @param dockerNamespace the dockerNamespace to set
     */
    public void setDockerNamespace(String dockerNamespace) {
        this.dockerNamespace = dockerNamespace;
    }

    /**
     * @return the dockerRepository
     */
    public String getDockerRepository() {
        return dockerRepository;
    }

    /**
     * @param dockerRepository the dockerRepository to set
     */
    public void setDockerRepository(String dockerRepository) {
        this.dockerRepository = dockerRepository;
    }
}
