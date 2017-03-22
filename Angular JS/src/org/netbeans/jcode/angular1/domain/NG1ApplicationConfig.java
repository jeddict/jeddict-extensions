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
package org.netbeans.jcode.angular1.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.netbeans.jcode.core.util.StringHelper.camelCase;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import static org.netbeans.jcode.core.util.StringHelper.kebabCase;
import org.netbeans.jcode.ng.main.domain.NGApplicationConfig;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.MONOLITH_APPLICATION_TYPE;
import static org.netbeans.jcode.ng.main.domain.NGApplicationConfig.SQL_DATABASE_TYPE;
import org.netbeans.jcode.ng.main.domain.NGEntity;

/**
 *
 * @author jGauravGupta
 */
public class NG1ApplicationConfig implements NGApplicationConfig {

    //Path
    public String MAIN_DIR = "src/main/";
    public String TEST_DIR = "src/test/";
    public String CLIENT_MAIN_SRC_DIR = MAIN_DIR + "webapp/";
    public String CLIENT_TEST_SRC_DIR = TEST_DIR + "javascript/";
    public String MAIN_SRC_DIR = CLIENT_MAIN_SRC_DIR;
    public String TEST_SRC_DIR = CLIENT_TEST_SRC_DIR;
    public String CLIENT_DIST_DIR = "www/";
    public String ANGULAR_DIR = MAIN_DIR + "webapp/app/";
    public String BUILD_DIR;
    public String DIST_DIR;

    private String applicationPath;//rest path
    public String buildTool;
    private String clientPackageManager;

    public String angularAppName;
    public String applicationType;//gateway , monolith
    public String serverPort = "8080";
    public String microserviceAppName;
    public String baseName;
    public String capitalizedBaseName;
    public String camelizedBaseName;
    public String dasherizedBaseName;
    public String lowercaseBaseName;
    public List<NGEntity> entities;
    private String restPackage;
    public String authenticationType;

    //i18n
    public boolean enableTranslation;
    public String nativeLanguage = "en";
    private Set<String> languages = new HashSet<>(Arrays.asList("en"));

    public String jhiPrefix;
    public String jhiPrefixCapitalized;

    //Persistence
    public String searchEngine;
    public String databaseType;
    public String devDatabaseType;
    public String hibernateCache;//ehcache2

    public String websocket;
    public String messageBroker;//kafka
    private String clientFramework;// angular1
    public boolean useSass;

    //test
    public String[] testFrameworks = {};//protractor
    private boolean protractorTests;

    //filter    
    private boolean enableSocialSignIn;
    private boolean skipUserManagement;
    private boolean skipClient;
    private boolean skipServer;

    private boolean enableMetrics;
    private boolean enableLogs;
    private boolean enableHealth;
    private boolean enableConfiguration;
    private boolean enableAudits;
    private boolean enableProfile;
    private boolean enableDocs;

    /**
     * @return the jhiPrefixCapitalized
     */
    public String getJhiPrefixCapitalized() {
        if (jhiPrefixCapitalized == null) {
            jhiPrefixCapitalized = firstUpper(getJhiPrefix());
        }
        return jhiPrefixCapitalized;
    }

    /**
     * @return the authenticationType
     */
    public String getAuthenticationType() {
        if (authenticationType == null) {
            authenticationType = "jwt";
        }
        return authenticationType;
    }

    /**
     * @param authenticationType the authenticationType to set
     */
    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    /**
     * @return the applicationType
     */
    public String getApplicationType() {
        if (applicationType == null) {
            applicationType = MONOLITH_APPLICATION_TYPE;
        }
        return applicationType;
    }

    /**
     * @return the enableTranslation
     */
    public boolean isEnableTranslation() {
        return enableTranslation;
    }

    /**
     * @param enableTranslation the enableTranslation to set
     */
    public void setEnableTranslation(boolean enableTranslation) {
        this.enableTranslation = enableTranslation;
    }

    /**
     * @return the angularAppName
     */
    public String getAngularAppName() {
        return angularAppName;
    }

    /**
     * @param angularAppName the angularAppName to set
     */
    public void setAngularAppName(String angularAppName) {
        this.angularAppName = angularAppName;
    }

    /**
     * @return the enableSocialSignIn
     */
    public boolean isEnableSocialSignIn() {
        return enableSocialSignIn;
    }

    /**
     * @return the websocket
     */
    public String getWebsocket() {
        return websocket;
    }

    /**
     * @param websocket the websocket to set
     */
    public void setWebsocket(String websocket) {
        this.websocket = websocket;
    }

    /**
     * @return the searchEngine
     */
    public String getSearchEngine() {
        return searchEngine;
    }

    /**
     * @param searchEngine the searchEngine to set
     */
    public void setSearchEngine(String searchEngine) {
        this.searchEngine = searchEngine;
    }

    /**
     * @return the databaseType
     */
    public String getDatabaseType() {
        if (databaseType == null) {
            databaseType = SQL_DATABASE_TYPE;
        }
        return databaseType;
    }

    /**
     * @param databaseType the databaseType to set
     */
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    /**
     * @return the jhiPrefix
     */
    public String getJhiPrefix() {
        if (jhiPrefix == null) {
            jhiPrefix = EMPTY;
        }
        return jhiPrefix;
    }

    /**
     * @param jhiPrefix the jhiPrefix to set
     */
    public void setJhiPrefix(String jhiPrefix) {
        this.jhiPrefix = jhiPrefix;
    }

    /**
     * @return the devDatabaseType
     */
    public String getDevDatabaseType() {
        return devDatabaseType;
    }

    /**
     * @param devDatabaseType the devDatabaseType to set
     */
    public void setDevDatabaseType(String devDatabaseType) {
        this.devDatabaseType = devDatabaseType;
    }

    /**
     * @return the baseName
     */
    public String getBaseName() {
        if (baseName == null) {
            baseName = EMPTY;
        }
        return baseName;
    }

    /**
     * @param baseName the baseName to set
     */
    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    /**
     * @return the capitalizedBaseName
     */
    public String getCapitalizedBaseName() {
        if (capitalizedBaseName == null) {
            capitalizedBaseName = firstUpper(getBaseName());
        }
        return capitalizedBaseName;
    }

    /**
     * @return the camelizedBaseName
     */
    public String getCamelizedBaseName() {
        if (camelizedBaseName == null) {
            camelizedBaseName = camelCase(baseName);
        }
        return camelizedBaseName;
    }


    public boolean isSkipUserManagement() {
        return skipUserManagement;
    }

    /**
     * @return the dasherizedBaseName
     */
    public String getDasherizedBaseName() {
        if (dasherizedBaseName == null) {
            dasherizedBaseName = kebabCase(baseName);
        }
        return dasherizedBaseName;
    }

    /**
     * @return the lowercaseBaseName
     */
    public String getLowercaseBaseName() {
        if (lowercaseBaseName == null) {
            lowercaseBaseName = baseName.toLowerCase();
        }
        return lowercaseBaseName;
    }

    /**
     * @return the useSass
     */
    public boolean isUseSass() {
        return useSass;
    }

    /**
     * @param useSass the useSass to set
     */
    public void setUseSass(boolean useSass) {
        this.useSass = useSass;
    }

    /**
     * @return the hibernateCache
     */
    public String getHibernateCache() {
        return hibernateCache;
    }

    /**
     * @param hibernateCache the hibernateCache to set
     */
    public void setHibernateCache(String hibernateCache) {
        this.hibernateCache = hibernateCache;
    }

    /**
     * @return the languages
     */
    public Set<String> getLanguages() {
        return languages;
    }

    /**
     * @param languages the languages to set
     */
    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    /**
     * @return the applicationPath
     */
    public String getApplicationPath() {
        return applicationPath;
    }

    /**
     * @param applicationPath the applicationPath to set
     */
    public void setApplicationPath(String applicationPath) {
        this.applicationPath = applicationPath;
    }

    /**
     * @return the entities
     */
    public List<NGEntity> getEntities() {
        return entities;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(List<NGEntity> entities) {
        this.entities = entities;
    }

    /**
     * @return the enableMetrics
     */
    public boolean isEnableMetrics() {
        return enableMetrics;
    }

    /**
     * @param enableMetrics the enableMetrics to set
     */
    public void setEnableMetrics(boolean enableMetrics) {
        this.enableMetrics = enableMetrics;
    }

    /**
     * @return the enableLogs
     */
    public boolean isEnableLogs() {
        return enableLogs;
    }

    /**
     * @param enableLogs the enableLogs to set
     */
    public void setEnableLogs(boolean enableLogs) {
        this.enableLogs = enableLogs;
    }

    /**
     * @return the enableHealth
     */
    public boolean isEnableHealth() {
        return enableHealth;
    }

    /**
     * @param enableHealth the enableHealth to set
     */
    public void setEnableHealth(boolean enableHealth) {
        this.enableHealth = enableHealth;
    }

    /**
     * @return the enableConfiguration
     */
    public boolean isEnableConfiguration() {
        return enableConfiguration;
    }

    /**
     * @param enableConfiguration the enableConfiguration to set
     */
    public void setEnableConfiguration(boolean enableConfiguration) {
        this.enableConfiguration = enableConfiguration;
    }

    /**
     * @return the enableAudits
     */
    public boolean isEnableAudits() {
        return enableAudits;
    }

    /**
     * @param enableAudits the enableAudits to set
     */
    public void setEnableAudits(boolean enableAudits) {
        this.enableAudits = enableAudits;
    }

    /**
     * @return the enableProfile
     */
    public boolean isEnableProfile() {
        return enableProfile;
    }

    /**
     * @param enableProfile the enableProfile to set
     */
    public void setEnableProfile(boolean enableProfile) {
        this.enableProfile = enableProfile;
    }

    /**
     * @return the enableDocs
     */
    public boolean isEnableDocs() {
        return enableDocs;
    }

    /**
     * @param enableDocs the enableDocs to set
     */
    public void setEnableDocs(boolean enableDocs) {
        this.enableDocs = enableDocs;
    }

    /**
     * @return the restPackage
     */
    public String getRestPackage() {
        return restPackage;
    }

    /**
     * @param restPackage the restPackage to set
     */
    public void setRestPackage(String restPackage) {
        this.restPackage = restPackage;
    }

    /**
     * @return the messageBroker
     */
    public String getMessageBroker() {
        return messageBroker;
    }

    /**
     * @param messageBroker the messageBroker to set
     */
    public void setMessageBroker(String messageBroker) {
        this.messageBroker = messageBroker;
    }

    /**
     * @return the clientFramework
     */
    public String getClientFramework() {
        return clientFramework;
    }

    /**
     * @param clientFramework the clientFramework to set
     */
    public void setClientFramework(String clientFramework) {
        this.clientFramework = clientFramework;
    }

    public String getBuildTool() {
        return buildTool;
    }

    public void setBuildTool(String buildTool) {
        this.buildTool = buildTool;
    }

    public String getDIST_DIR() {
        if (DIST_DIR == null) {
            if ("maven".equals(this.buildTool)) {
                this.BUILD_DIR = "target/";
            } else {
                this.BUILD_DIR = "build/";
            }
            DIST_DIR = this.BUILD_DIR + CLIENT_DIST_DIR;
        }
        return DIST_DIR;
    }

    /**
     * @return the protractorTests
     */
    public boolean isProtractorTests() {
        return protractorTests;
    }

    /**
     * @param protractorTests the protractorTests to set
     */
    public void setProtractorTests(boolean protractorTests) {
        this.protractorTests = protractorTests;
    }

    /**
     * @return the clientPackageManager
     */
    public String getClientPackageManager() {
        return clientPackageManager;
    }

    /**
     * @param clientPackageManager the clientPackageManager to set
     */
    public void setClientPackageManager(String clientPackageManager) {
        this.clientPackageManager = clientPackageManager;
    }

    /**
     * @return the skipClient
     */
    public boolean isSkipClient() {
        return skipClient;
    }

    /**
     * @param skipClient the skipClient to set
     */
    public void setSkipClient(boolean skipClient) {
        this.skipClient = skipClient;
    }

    /**
     * @return the skipServer
     */
    public boolean isSkipServer() {
        return skipServer;
    }

    /**
     * @param skipServer the skipServer to set
     */
    public void setSkipServer(boolean skipServer) {
        this.skipServer = skipServer;
    }

}
