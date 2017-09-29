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
package org.netbeans.jcode.rest.applicationconfig;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.netbeans.jcode.stack.config.data.LayerConfigData;

/**
 *
 * @author Gaurav Gupta
 */
public class RestConfigData extends LayerConfigData{
    
    public static final String DEFAULT_CLASS = "ApplicationConfig";
    public static final String DEFAULT_PATH = "resources";
    private String path;
    private String _class;
//    private String _package;
    private boolean useJersey;

//    /**
//     * @return the _package
//     */
//    public String getPackage() {
//        return _package;
//    }
//
//    /**
//     * @param _package the _package to set
//     */
//    public void setPackage(String _package) {
//        this._package = _package;
//    }

    /**
     * @return the path
     */
    public String getApplicationPath() {
        if(StringUtils.isBlank(path)){
            return DEFAULT_PATH;
        }
        return path;
    }

    /**
     * @param restPath the path to set
     */
    public void setApplicationPath(String restPath) {
        this.path = restPath;
    }

    /**
     * @return the _class
     */
    public String getApplicationClass() {
        if(StringUtils.isBlank(_class)){
            return DEFAULT_CLASS;
        }
        return _class;
    }

    /**
     * @param restClass the _class to set
     */
    public void setApplicationClass(String restClass) {
        this._class = restClass;
    }

    /**
     * @return the useJersey
     */
    public boolean isUseJersey() {
        return useJersey;
    }

    /**
     * @param useJersey the useJersey to set
     */
    public void setUseJersey(boolean useJersey) {
        this.useJersey = useJersey;
    }
    
    @Override
    public List<String> getUsageDetails() {
        return Collections.EMPTY_LIST;
    }
}
