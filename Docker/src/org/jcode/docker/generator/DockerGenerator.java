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
package org.jcode.docker.generator;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import org.netbeans.jcode.ejb.facade.EjbFacadeGenerator;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Technology;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.openide.util.lookup.ServiceProvider;

/**
 * Generates EJB facades for entity classes.
 *
 * @author Gaurav Gupta
 */

@ServiceProvider(service=Generator.class)
@Technology(label="Docker", panel=DockerConfigPanel.class, sibling = {EjbFacadeGenerator.class})
public final class DockerGenerator implements Generator{
    
    private static final String TEMPLATE = "org/netbeans/xtechnology/generator/template/";
    private static final String FACADE_ABSTRACT = "Abstract"; //NOI18N
    protected static final String EJB_STATELESS = "javax.ejb.Stateless"; //NOI18N
    
    @ConfigData
    private DockerConfigData beanData;
    
    @ConfigData
    private Project project; 
    
    @ConfigData
    private ApplicationConfigData applicationConfigData;
    
    @ConfigData
    private EntityMappings entityMapping;
    
    @ConfigData
    private SourceGroup source; 
    
    @ConfigData
    private ProgressHandler handler;
     
    @Override
    public void execute() throws IOException {
        handler.progress(Console.wrap(DockerGenerator.class, "MSG_Progress_Now_Generating", FG_RED, BOLD, UNDERLINE));
    }
      


    
}
