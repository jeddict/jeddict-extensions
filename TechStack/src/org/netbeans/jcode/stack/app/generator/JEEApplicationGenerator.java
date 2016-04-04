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
import org.netbeans.jcode.entity.info.EntityResourceBeanModel;
import org.netbeans.jcode.entity.info.EntityResourceModelBuilder;
import org.netbeans.jcode.helper.PersistenceHelper;
import org.netbeans.jcode.mvc.core.MVCApplicationGeneratorFactory;
import org.netbeans.jcode.mvc.core.MVCBaseApplicationGenerator;
import org.netbeans.jcode.rest.util.RestUtils;
import org.netbeans.jcode.source.SourceGroupSupport;
import org.netbeans.jcode.stack.config.data.*;
import org.netbeans.jcode.stack.mvc.MVCData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author Gaurav Gupta
 */
public class JEEApplicationGenerator{

    private static JEEApplicationGenerator INSTANCE;
    
    private JEEApplicationGenerator(){
        
    }
    
    public static JEEApplicationGenerator getInstance(){
        if(INSTANCE==null){
            synchronized(JEEApplicationGenerator.class){
                if(INSTANCE==null){
                    INSTANCE = new JEEApplicationGenerator();
                }
            }
        }
        return INSTANCE;
    }
    
    public static void generate(ProgressHandler progressHandler, ApplicationConfigData applicationConfigData){
        try {
            final Project project = applicationConfigData.getProject();
            
            String restAppPackage = null;
            String restAppClass = null;
            
            final RestSupport restSupport = project.getLookup().lookup(RestSupport.class);
            boolean useJersey = false;//Boolean.TRUE.equals(wizard.getProperty(WizardProperties.USE_JERSEY));
            if (!useJersey) {
                RestSupport.RestConfig.IDE.setAppClassName(restAppPackage+"."+restAppClass); //NOI18N
            }
            if ( restSupport!= null ){
                try {
                    restSupport.ensureRestDevelopmentReady(useJersey ?
                            RestSupport.RestConfig.DD : RestSupport.RestConfig.IDE);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            
            FileObject targetFolder = applicationConfigData.getSourceGroup().getRootFolder();
            FileObject wizardSrcRoot = targetFolder;//(FileObject)wizard.getProperty(         WizardProperties.TARGET_SRC_ROOT);
            
            /*
            *  Visual panel is used from several wizards. One of them
            *  has several options for target source roots ( different for
            *  entities, generation classes ).
            *  There is special property WizardProperties.TARGET_SRC_ROOT
            *  which is set up by wizard panel. This property should be used
            *  as target source root folder.
            */
            if ( wizardSrcRoot != null ){
                targetFolder  = wizardSrcRoot;
            }
            
            String targetPackage = SourceGroupSupport.getPackageForFolder(targetFolder);
            final String resourcePackage = ((MVCData)applicationConfigData.getControllerLayerConfig()).getPackage();
            String controllerPackage = resourcePackage;//(String) wizard.getProperty(WizardProperties.CONTROLLER_PACKAGE);
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
                    project, entities );
            EntityResourceBeanModel model = builder.build();
            final MVCBaseApplicationGenerator generator = MVCApplicationGeneratorFactory.newInstance(project);
            generator.initialize(model, project, targetFolder, targetPackage,
                    resourcePackage, controllerPackage, pu);
//        pHandle.progress(50);
            
            // create application config class if required
            final FileObject restAppPack = restAppPackage == null ? null :
                    FileUtil.createFolder(targetFolder, restAppPackage.replace('.', '/'));
            final String appClassName = restAppClass;
            try {
                if ( restAppPack != null && appClassName!= null && !useJersey) {
                    RestUtils.createApplicationConfigClass(restSupport, restAppPack, appClassName);
                }
                RestUtils.disableRestServicesChangeListner(project);
                generator.generate(progressHandler);
//            pHandle.progress(80);
                restSupport.configure(resourcePackage);
            } catch(Exception iox) {
                Exceptions.printStackTrace(iox);
            } finally {
                RestUtils.enableRestServicesChangeListner(project);
            }
            
            // logging usage of wizard
            Object[] params = new Object[5];
            params[0] = "JAX-RS";
            params[1] = project.getClass().getName();
            J2eeModule j2eeModule = RestUtils.getJ2eeModule(project);
            params[2] = j2eeModule == null ? null : j2eeModule.getModuleVersion()+"(WAR)"; //NOI18N
            params[3] = "REST FROM ENTITY"; //NOI18N
//        pHandle.finish();
//            return Collections.<DataObject>singleton(DataFolder.findFolder(targetFolder));
            
        } catch(IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    
    }
    
}
