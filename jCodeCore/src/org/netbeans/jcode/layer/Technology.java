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
package org.netbeans.jcode.layer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.netbeans.jcode.stack.config.panel.LayerConfigPanel;

/**
 *
 * @author jGauravGupta <gaurav.gupta.jc@gmail.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Technology {
    
    Type type();
    
    public Class<? extends LayerConfigPanel> panel() default LayerConfigPanel.class;
    
    public Class<? extends Generator>[] parents() default {};

    public String label() default "<none>";
    
    enum Type {
        BUSINESS,CONTROLLER,VIEWER;
    }
    
    
    
}
