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
import org.netbeans.jcode.ejb.facade.SessionBeanPanel;
import static org.netbeans.jcode.stack.ControllerLayer.MVC;

/**
 *
 * @author Gaurav Gupta
 */
public enum BusinessLayer implements TechnologyLayer {

    NONE(NONE_LABEL, null, new ControllerLayer[]{ControllerLayer.NONE}),
    SESSION_BEAN("Session Bean Facade", SessionBeanPanel.class, new ControllerLayer[]{ControllerLayer.NONE, MVC});

    private final String label;
    private final ControllerLayer[] controllerLayer;
    private final Class<? extends LayerConfigPanel> configPanel;

    private BusinessLayer(String label, Class<? extends LayerConfigPanel> configPanel, ControllerLayer[] controllerLayer) {
        this.label = label;
        this.configPanel = configPanel;
        this.controllerLayer= controllerLayer;
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

    /**
     * @return the configPanel
     */
    public Class<? extends LayerConfigPanel> getConfigPanel() {
        return configPanel;
    }

    /**
     * @return the controllerLayer
     */
    public ControllerLayer[] getControllerLayer() {
        return controllerLayer;
    }

}
