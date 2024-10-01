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
package io.github.jeddict.jsf.viewer;

import io.github.jeddict.jcode.LayerConfigData;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Shiwani Gupta
 */
public class JsfViewerData extends LayerConfigData {

    private String prefixName;
    private String suffixName;
    private String _package;
    private boolean isNamed;
    private boolean cdi = true;
    
    /**
     * @return the _package
     */
    public String getPackage() {
        return _package;
    }

    /**
     * @param _package the _package to set
     */
    public void setPackage(String _package) {
        this._package = _package;
    }

    /**
     * @return the suffixName
     */
    public String getSuffixName() {
        if(StringUtils.isBlank(suffixName)){
            suffixName = "Repository";
        }
        return suffixName;
    }

    /**
     * @param suffixName the suffixName to set
     */
    public void setSuffixName(String suffixName) {
        this.suffixName = suffixName;
    }

    /**
     * @return the prefixName
     */
    public String getPrefixName() {
        return prefixName;
    }

    /**
     * @param prefixName the prefixName to set
     */
    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
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
