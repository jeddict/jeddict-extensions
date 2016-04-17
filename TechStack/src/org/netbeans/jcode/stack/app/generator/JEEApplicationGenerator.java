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
package org.netbeans.jcode.stack.app.generator;

import java.io.IOException;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.core.util.PersistenceHelper;
import org.netbeans.jcode.entity.info.EntityResourceBeanModel;
import org.netbeans.jcode.entity.info.EntityResourceModelBuilder;
import org.netbeans.jcode.mvc.core.MVCApplicationGeneratorFactory;
import org.netbeans.jcode.mvc.core.MVCBaseApplicationGenerator;
import org.netbeans.jcode.stack.config.data.*;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.openide.util.Exceptions;

/**
 *
 * @author Gaurav Gupta
 */
public class JEEApplicationGenerator {

    private static JEEApplicationGenerator INSTANCE;

    private JEEApplicationGenerator() {

    }

    public static JEEApplicationGenerator getInstance() {
        if (INSTANCE == null) {
            synchronized (JEEApplicationGenerator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JEEApplicationGenerator();
                }
            }
        }
        return INSTANCE;
    }

    public static void generate(ProgressHandler progressHandler, ApplicationConfigData applicationConfigData) {
        try {
            final Project project = applicationConfigData.getProject();
            final SourceGroup sourceGroup = applicationConfigData.getSourceGroup();

//            FileObject targetFolder = sourceGroup.getRootFolder();
//            FileObject wizardSrcRoot = targetFolder;//(FileObject)wizard.getProperty(         WizardProperties.TARGET_SRC_ROOT);

            /*
             *  Visual panel is used from several wizards. One of them
             *  has several options for target source roots ( different for
             *  entities, generation classes ).
             *  There is special property WizardProperties.TARGET_SRC_ROOT
             *  which is set up by wizard panel. This property should be used
             *  as target source root folder.
             */
//            if (wizardSrcRoot != null) {
//                targetFolder = wizardSrcRoot;
//            }

//            String targetPackage = SourceGroupSupport.getPackageForFolder(targetFolder);
//            final String resourcePackage = ((MVCData) applicationConfigData.getControllerLayerConfig()).getPackage();//TODO NPE
//            String controllerPackage = resourcePackage;//(String) wizard.getProperty(WizardProperties.CONTROLLER_PACKAGE);
            List<String> entities = applicationConfigData.getEntities();
            final PersistenceHelper.PersistenceUnit pu = (PersistenceHelper.PersistenceUnit) new PersistenceHelper(project).getPersistenceUnit();

            /*
             * There should be ALL found entities but they needed to compute closure.
             * Persistence wizard already has computed closure. So there is no need
             * in all other entities.
             * Current CTOR of builder and method <code>build</code> is not changed
             * for now but should be changed later after  review of its usage.
             */
            EntityResourceModelBuilder builder = new EntityResourceModelBuilder(
                    project, sourceGroup, entities);
            EntityResourceBeanModel model = builder.build();
            final MVCBaseApplicationGenerator generator = MVCApplicationGeneratorFactory.newInstance(project);
            generator.initialize(model, project, pu);
            generator.generate(progressHandler, applicationConfigData);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

}
