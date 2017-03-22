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
package org.netbeans.jcode.ng.main.domain;

import java.util.List;
import java.util.Set;

/**
 *
 * @author jGauravGupta
 */
public interface NGApplicationConfig {

    public static final String JWT_AUTHENTICATION_TYPE = "jwt";
    public static final String UAA_AUTHENTICATION_TYPE = "uaa";
    public static final String SESSION_AUTHENTICATION_TYPE = "session";
    public static final String OAUTH2_AUTHENTICATION_TYPE = "oauth2";

    public static final String MICROSERVICE_APPLICATION_TYPE = "microservice";
    public static final String UAA_APPLICATION_TYPE = "uaa";
    public static final String GATEWAY_APPLICATION_TYPE = "gateway";
    public static final String MONOLITH_APPLICATION_TYPE = "monolith";

    public static final String SPRING_WEBSOCKET = "spring-websocket";

    public static final String ELASTIC_SEARCH_ENGINE = "elasticsearch";
    public static final String SQL_DATABASE_TYPE = "sql";
    public static final String CASSANDRA_DATABASE_TYPE = "cassandra";
    public static final String MONGODB_DATABASE_TYPE = "mongodb";
    public static final String H2_DISK_DATABASE_TYPE = "h2Disk";
    public static final String H2_MEMORY_DATABASE_TYPE = "h2Memory";
    
    
    /**
     * @return the angularAppName
     */
    String getAngularAppName();

    /**
     * @return the applicationPath
     */
    String getApplicationPath();

    /**
     * @return the applicationType
     */
    String getApplicationType();

    /**
     * @return the authenticationType
     */
    String getAuthenticationType();

    /**
     * @return the baseName
     */
    String getBaseName();

    String getBuildTool();

    /**
     * @return the camelizedBaseName
     */
    String getCamelizedBaseName();

    /**
     * @return the capitalizedBaseName
     */
    String getCapitalizedBaseName();

    /**
     * @return the clientFramework
     */
    String getClientFramework();

    /**
     * @return the clientPackageManager
     */
    String getClientPackageManager();

    String getDIST_DIR();

    /**
     * @return the dasherizedBaseName
     */
    String getDasherizedBaseName();

    /**
     * @return the databaseType
     */
    String getDatabaseType();

    /**
     * @return the devDatabaseType
     */
    String getDevDatabaseType();

    /**
     * @return the entities
     */
    List<NGEntity> getEntities();

    /**
     * @return the hibernateCache
     */
    String getHibernateCache();

    /**
     * @return the jhiPrefix
     */
    String getJhiPrefix();

    /**
     * @return the jhiPrefixCapitalized
     */
    String getJhiPrefixCapitalized();

    /**
     * @return the languages
     */
    Set<String> getLanguages();

    /**
     * @return the lowercaseBaseName
     */
    String getLowercaseBaseName();

    /**
     * @return the messageBroker
     */
    String getMessageBroker();

    /**
     * @return the restPackage
     */
    String getRestPackage();

    /**
     * @return the searchEngine
     */
    String getSearchEngine();

    /**
     * @return the websocket
     */
    String getWebsocket();

    /**
     * @return the enableAudits
     */
    boolean isEnableAudits();

    /**
     * @return the enableConfiguration
     */
    boolean isEnableConfiguration();

    /**
     * @return the enableDocs
     */
    boolean isEnableDocs();

    /**
     * @return the enableHealth
     */
    boolean isEnableHealth();

    /**
     * @return the enableLogs
     */
    boolean isEnableLogs();

    /**
     * @return the enableMetrics
     */
    boolean isEnableMetrics();

    /**
     * @return the enableProfile
     */
    boolean isEnableProfile();

    /**
     * @return the enableSocialSignIn
     */
    boolean isEnableSocialSignIn();

    /**
     * @return the enableTranslation
     */
    boolean isEnableTranslation();

    /**
     * @return the protractorTests
     */
    boolean isProtractorTests();

    /**
     * @return the skipClient
     */
    boolean isSkipClient();

    /**
     * @return the skipServer
     */
    boolean isSkipServer();

    boolean isSkipUserManagement();

    /**
     * @return the useSass
     */
    boolean isUseSass();

    /**
     * @param angularAppName the angularAppName to set
     */
    void setAngularAppName(String angularAppName);

    /**
     * @param applicationPath the applicationPath to set
     */
    void setApplicationPath(String applicationPath);

    /**
     * @param authenticationType the authenticationType to set
     */
    void setAuthenticationType(String authenticationType);

    /**
     * @param baseName the baseName to set
     */
    void setBaseName(String baseName);

    void setBuildTool(String buildTool);

    /**
     * @param clientFramework the clientFramework to set
     */
    void setClientFramework(String clientFramework);

    /**
     * @param clientPackageManager the clientPackageManager to set
     */
    void setClientPackageManager(String clientPackageManager);

    /**
     * @param databaseType the databaseType to set
     */
    void setDatabaseType(String databaseType);

    /**
     * @param devDatabaseType the devDatabaseType to set
     */
    void setDevDatabaseType(String devDatabaseType);

    /**
     * @param enableAudits the enableAudits to set
     */
    void setEnableAudits(boolean enableAudits);

    /**
     * @param enableConfiguration the enableConfiguration to set
     */
    void setEnableConfiguration(boolean enableConfiguration);

    /**
     * @param enableDocs the enableDocs to set
     */
    void setEnableDocs(boolean enableDocs);

    /**
     * @param enableHealth the enableHealth to set
     */
    void setEnableHealth(boolean enableHealth);

    /**
     * @param enableLogs the enableLogs to set
     */
    void setEnableLogs(boolean enableLogs);

    /**
     * @param enableMetrics the enableMetrics to set
     */
    void setEnableMetrics(boolean enableMetrics);

    /**
     * @param enableProfile the enableProfile to set
     */
    void setEnableProfile(boolean enableProfile);

    /**
     * @param enableTranslation the enableTranslation to set
     */
    void setEnableTranslation(boolean enableTranslation);

    /**
     * @param entities the entities to set
     */
    void setEntities(List<NGEntity> entities);

    /**
     * @param hibernateCache the hibernateCache to set
     */
    void setHibernateCache(String hibernateCache);

    /**
     * @param jhiPrefix the jhiPrefix to set
     */
    void setJhiPrefix(String jhiPrefix);

    /**
     * @param languages the languages to set
     */
    void setLanguages(Set<String> languages);

    /**
     * @param messageBroker the messageBroker to set
     */
    void setMessageBroker(String messageBroker);

    /**
     * @param protractorTests the protractorTests to set
     */
    void setProtractorTests(boolean protractorTests);

    /**
     * @param restPackage the restPackage to set
     */
    void setRestPackage(String restPackage);

    /**
     * @param searchEngine the searchEngine to set
     */
    void setSearchEngine(String searchEngine);

    /**
     * @param skipClient the skipClient to set
     */
    void setSkipClient(boolean skipClient);

    /**
     * @param skipServer the skipServer to set
     */
    void setSkipServer(boolean skipServer);

    /**
     * @param useSass the useSass to set
     */
    void setUseSass(boolean useSass);

    /**
     * @param websocket the websocket to set
     */
    void setWebsocket(String websocket);
    
}
