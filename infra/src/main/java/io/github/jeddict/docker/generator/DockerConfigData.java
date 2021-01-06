/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.docker.generator;

import io.github.jeddict.jcode.DatabaseType;
import io.github.jeddict.jcode.LayerConfigData;
import io.github.jeddict.jcode.RuntimeProvider;
import io.github.jeddict.jcode.annotation.Runtime;
import java.util.Arrays;
import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import static io.github.jeddict.util.StringUtils.isBlank;
import static io.github.jeddict.util.StringUtils.isNotBlank;
import org.openide.util.Lookup;

/**
 *
 * @author Gaurav Gupta
 */
public class DockerConfigData extends LayerConfigData {

    @JsonbTransient
    private Class<? extends RuntimeProvider> runtimeProviderClass;
    private String runtimeProviderClassName;
    private DatabaseType databaseType;
    private String databaseVersion;
    private String dbUserName = "myuser";
    private String dbPassword = "secret";
    private String dataSource = "SampleDS";
    private String dbName = "sample";
    private String dbHost;//db service/svc
    private String dbPort;

    private String dockerMachine;
    private String dockerUrl;
    private String dockerCertPath;
    private boolean dockerEnable;
    private boolean dockerActivated;//checkbox
    private String dockerNamespace;
    private String dockerRepository;
    
    public String getRuntimeProviderClassName() {
        return runtimeProviderClassName;
    }
    
    @JsonbTransient
    public Class<? extends RuntimeProvider> getRuntimeProviderClass() {
        if (runtimeProviderClass == null && runtimeProviderClassName != null) {
            this.runtimeProviderClass = getRuntimeProvider().getClass();
        }
        return runtimeProviderClass;
    }

    public void setRuntimeProviderClass(Class<? extends RuntimeProvider> runtimeProviderClass) {
        this.runtimeProviderClass = runtimeProviderClass;
        this.runtimeProviderClassName = runtimeProviderClass.getName();
    }

    @JsonbTransient
    public Runtime getRuntime() {
        Runtime runtime = null;
        if (getRuntimeProviderClass() != null) {
            runtime = getRuntimeProviderClass().getAnnotation(Runtime.class);
        }
        return runtime;
    }
    
    @JsonbTransient
    public RuntimeProvider getRuntimeProvider() {
        return Lookup.getDefault()
                .lookupAll(RuntimeProvider.class)
                .stream()
                .filter(provider -> provider.getClass().getName().equals(runtimeProviderClassName))
                .findAny()
                .map(provider -> provider)
                .orElseThrow(() -> new IllegalStateException());
    }

    public DatabaseType getDatabaseType() {
        if (getRuntime() == null || getRuntime().name().isEmpty()) {
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

    @JsonbTransient
    public boolean isDbInfoExist() {
        return isNotBlank(this.dbName);
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
     * @return the dockerUrl
     */
    public String getDockerUrl() {
        return dockerUrl;
    }

    /**
     * @param dockerUrl the dockerUrl to set
     */
    public void setDockerUrl(String dockerUrl) {
        this.dockerUrl = dockerUrl;
    }

    /**
     * @return the dockerCertPath
     */
    public String getDockerCertPath() {
        return dockerCertPath;
    }

    /**
     * @param dockerCertPath the dockerCertPath to set
     */
    public void setDockerCertPath(String dockerCertPath) {
        this.dockerCertPath = dockerCertPath;
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
        return Arrays.asList(
                getDatabaseType() != null ? getDatabaseType().getDisplayName() : null,
                getRuntime() != null ? getRuntime().displayName() : null, 
                dockerEnable ? "Docker" : null
        );
    }

    /**
     * @return the dockerNamespace
     */
    public String getDockerNamespace() {
        if (isBlank(dockerNamespace)) {
            return "${project.groupId}";
        }
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
        if (isBlank(dockerRepository)) {
            return "${project.artifactId}";
        }
        return dockerRepository;
    }

    /**
     * @param dockerRepository the dockerRepository to set
     */
    public void setDockerRepository(String dockerRepository) {
        this.dockerRepository = dockerRepository;
    }
}


