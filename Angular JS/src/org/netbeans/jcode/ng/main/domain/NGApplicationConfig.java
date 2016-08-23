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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.netbeans.jcode.core.util.StringHelper.camelCase;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import static org.netbeans.jcode.core.util.StringHelper.kebabCase;

/**
 *
 * @author jGauravGupta
 */
public class NGApplicationConfig {

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
    
    private String applicationPath;//rest path

    public String MAIN_SRC_DIR = "src/main/webapp/";
    public String TEST_SRC_DIR = "src/test/javascript/";
    
    public String angularAppName;
    public boolean enableTranslation;
    public String nativeLanguage = "en";
    private Set<String> languages = new HashSet<>(Arrays.asList("en"));

    public boolean enableSocialSignIn;
    public String authenticationType;
    
    public String jhiPrefix;
    public String jhiPrefixCapitalized;
    
    public String applicationType;
    public String websocket;
    
    public String searchEngine;
    public String databaseType;
    public String devDatabaseType;
    public String hibernateCache;//ehcache2
    
    public String baseName;
    public String capitalizedBaseName;
    public String camelizedBaseName;
    public String dasherizedBaseName;
    public String lowercaseBaseName;
    
    public boolean useSass = false;
    
    public List<NGEntity> entities;
//    uaaBaseName
    
    private boolean enableMetrics = false;
    private boolean enableLogs = false;
    private boolean enableHealth = false;
    private boolean enableConfiguration = false;
    private boolean enableAudits = false;
    private boolean enableProfile = false;
    private boolean enableDocs = false;

    /**
     * @return the jhiPrefixCapitalized
     */
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
        if(baseName == null){
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

}
