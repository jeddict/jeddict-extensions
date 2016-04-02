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

import static org.netbeans.jcode.layer.ViewerLayer.JSP;

/**
 *
 * @author Gaurav Gupta
 */
public enum ControllerLayer {
    
    MVC("MVC 1.0", new ViewerLayer[]{JSP});//, REST("Rest");
    
    private final String label;
    private final ViewerLayer[] viewerLayers;

    private ControllerLayer(String label, ViewerLayer[] viewerLayers) {
        this.label = label;
        this.viewerLayers = viewerLayers;
    }
  

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the viewerLayers
     */
    public ViewerLayer[] getViewerLayers() {
        return viewerLayers;
    }
    
}
