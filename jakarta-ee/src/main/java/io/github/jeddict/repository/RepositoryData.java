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
package io.github.jeddict.repository;

import io.github.jeddict.jcode.LayerConfigData;
import java.util.Collections;
import java.util.List;
import static io.github.jeddict.util.StringUtils.EMPTY;
import static io.github.jeddict.util.StringUtils.isBlank;
import javax.json.bind.annotation.JsonbProperty;

/**
 *
 * @author Gaurav Gupta
 */
public class RepositoryData extends LayerConfigData {

    @JsonbProperty("package")
    private String repositoryPackage;

    @JsonbProperty("prefixName")
    private String repositoryPrefixName;

    @JsonbProperty("suffixName")
    private String repositorySuffixName;

    private String servicePackage;

    private String servicePrefixName;

    private String serviceSuffixName;

    private boolean isNamed;

    private boolean cdi = true;

    /**
     * @return the repositoryPackage
     */
    public String getRepositoryPackage() {
        return repositoryPackage;
    }

    /**
     * @param repositoryPackage the repositoryPackage to set
     */
    public void setRepositoryPackage(String repositoryPackage) {
        this.repositoryPackage = repositoryPackage;
    }

    /**
     * @return the repositorySuffixName
     */
    public String getRepositorySuffixName() {
        if (isBlank(repositorySuffixName)) {
            repositorySuffixName = "Repository";
        }
        return repositorySuffixName;
    }

    /**
     * @param repositorySuffixName the repositorySuffixName to set
     */
    public void setRepositorySuffixName(String repositorySuffixName) {
        this.repositorySuffixName = repositorySuffixName;
    }

    /**
     * @return the repositoryPrefixName
     */
    public String getRepositoryPrefixName() {
        if (isBlank(repositoryPrefixName)) {
            repositoryPrefixName = EMPTY;
        }
        return repositoryPrefixName;
    }

    /**
     * @param repositoryPrefixName the repositoryPrefixName to set
     */
    public void setRepositoryPrefixName(String repositoryPrefixName) {
        this.repositoryPrefixName = repositoryPrefixName;
    }

    /**
     * @return the servicePackage
     */
    public String getServicePackage() {
        if (isBlank(servicePackage)) {
            servicePackage = "service";
        }
        return servicePackage;
    }

    /**
     * @param servicePackage the servicePackage to set
     */
    public void setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
    }

    /**
     * @return the serviceSuffixName
     */
    public String getServiceSuffixName() {
        if (isBlank(serviceSuffixName)) {
            serviceSuffixName = "Service";
        }
        return serviceSuffixName;
    }

    /**
     * @param serviceSuffixName the serviceSuffixName to set
     */
    public void setServiceSuffixName(String serviceSuffixName) {
        this.serviceSuffixName = serviceSuffixName;
    }

    /**
     * @return the servicePrefixName
     */
    public String getServicePrefixName() {
        if (isBlank(servicePrefixName)) {
            servicePrefixName = EMPTY;
        }
        return servicePrefixName;
    }

    /**
     * @param servicePrefixName the servicePrefixName to set
     */
    public void setServicePrefixName(String servicePrefixName) {
        this.servicePrefixName = servicePrefixName;
    }

    /**
     * @return the isNamed
     */
    public boolean isNamed() {
        return isNamed;
    }

    /**
     * @param isNamed the isNamed to set
     */
    public void setNamed(boolean isNamed) {
        this.isNamed = isNamed;
    }

    /**
     * @return the isCDI
     */
    public boolean isCDI() {
        return cdi;
    }

    /**
     * @param cdi the cdi to set
     */
    public void setCDI(boolean cdi) {
        this.cdi = cdi;
    }

    @Override
    public List<String> getUsageDetails() {
        return Collections.<String>emptyList();
    }
}
