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
package org.netbeans.jcode.stack.config.panel;

import java.awt.Panel;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.stack.config.data.LayerConfigData;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class LayerConfigPanel<T extends LayerConfigData> extends Panel {
    
    private T configData;
    
    public abstract void init(String _package, SourceGroup sourceGroup);

    /**
     * @return the configData
     */
    public T getConfigData() {
        return configData;
    }

    /**
     * @param configData the configData to set
     */
    public void setConfigData(T configData) {
        this.configData = configData;
    }
    
    
    
}
