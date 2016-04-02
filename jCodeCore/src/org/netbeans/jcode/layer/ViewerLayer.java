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
package org.netbeans.jcode.layer;

import static org.netbeans.jcode.layer.ControllerLayer.MVC;

/**
 *
 * @author Gaurav Gupta
 */
public enum ViewerLayer {

    JSP("JSP (Java Server Page)", new ControllerLayer[]{MVC});//,HTML5_ANGULAR_JS("HTML5 & Angular JS");

    private final String label;
    private final ControllerLayer[] controllerLayers;

    private ViewerLayer(String label, ControllerLayer[] controllerLayers) {
        this.label = label;
        this.controllerLayers = controllerLayers;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the controllerLayers
     */
    public ControllerLayer[] getControllerLayers() {
        return controllerLayers;
    }

}
