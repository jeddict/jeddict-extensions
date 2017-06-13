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
package org.netbeans.jcode.mvc.viewer.jsp;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import static org.netbeans.jcode.rest.applicationconfig.RestConfigPanel.DEFAULT_RESOURCE_FOLDER;
import org.netbeans.jcode.stack.config.data.LayerConfigData;

/**
 *
 * @author Gaurav Gupta
 */
public class JSPData extends LayerConfigData {
    
    public static final String DEFAULT_FOLDER = "WEB-INF/views";
    private String folder;
    
    private String resourceFolder;
    private boolean onlineTheme;

    /**
     * @return the folder
     */
    public String getFolder() {
        return folder;
    }

    /**
     * @param folder the folder to set
     */
    public void setFolder(String folder) {
        this.folder = folder;
    }

    /**
     * @return the onlineTheme
     */
    public boolean isOnlineTheme() {
        return onlineTheme;
    }

    /**
     * @param onlineTheme the onlineTheme to set
     */
    public void setOnlineTheme(boolean onlineTheme) {
        this.onlineTheme = onlineTheme;
    }

    /**
     * @return the resourceFolder
     */
    public String getResourceFolder() {
        if(StringUtils.isBlank(resourceFolder)){
            return DEFAULT_RESOURCE_FOLDER;
        }
        return resourceFolder;
    }

    /**
     * @param resourceFolder the resourceFolder to set
     */
    public void setResourceFolder(String resourceFolder) {
        this.resourceFolder = resourceFolder;
    }
    
        @Override
    public List<String> getUsageDetails() {
        return Arrays.asList(isOnlineTheme() ? "Online Theme" : null);
    }
}
