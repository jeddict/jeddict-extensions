/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.jcode.stack;

import org.netbeans.jcode.stack.config.panel.LayerConfigPanel;
import org.netbeans.jcode.mvc.viewer.jsp.JSPPanel;

/**
 *
 * @author Gaurav Gupta
 */
public enum ViewerLayer implements TechnologyLayer {

    NONE(NONE_LABEL, null),
    JSP("JSP (Java Server Page)", JSPPanel.class);//,HTML5_ANGULAR_JS("HTML5 & Angular JS");

    private final String label;
    private final Class<? extends LayerConfigPanel> configPanel;

    private ViewerLayer(String label, Class<? extends LayerConfigPanel> configPanel) {
        this.label = label;
        this.configPanel = configPanel;
    }

    /**
     * @return the configPanel
     */
    public Class<? extends LayerConfigPanel> getConfigPanel() {
        return configPanel;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
