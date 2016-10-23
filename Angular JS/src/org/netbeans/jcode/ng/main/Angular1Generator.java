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
package org.netbeans.jcode.ng.main;

import org.netbeans.jcode.layer.Technology;
import static org.netbeans.jcode.layer.Technology.Type.VIEWER;
import org.netbeans.jcode.rest.controller.RESTGenerator;
import org.openide.util.lookup.ServiceProvider;
import org.netbeans.jcode.layer.Generator;
/**
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(type = VIEWER, label = "Angular JS 1", panel = AngularPanel.class, parents = {RESTGenerator.class})
public class Angular1Generator extends AngularGenerator {

    private static final String TEMPLATE = "org/netbeans/jcode/template/angular1/";
   
    public String getTemplatePath(){
        return TEMPLATE;
    }
    
}