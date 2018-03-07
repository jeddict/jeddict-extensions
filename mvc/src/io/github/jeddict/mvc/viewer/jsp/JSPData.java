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
package io.github.jeddict.mvc.viewer.jsp;

import io.github.jeddict.jcode.LayerConfigData;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import static io.github.jeddict.rest.applicationconfig.RestConfigPanel.DEFAULT_RESOURCE_FOLDER;

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
        if (StringUtils.isBlank(resourceFolder)) {
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
