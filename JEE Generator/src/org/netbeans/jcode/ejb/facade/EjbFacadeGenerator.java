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
package org.netbeans.jcode.ejb.facade;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import static org.netbeans.jcode.core.util.Constants.JAVA_EXT;
import org.netbeans.jcode.core.util.POMManager;
import org.netbeans.jcode.core.util.StringHelper;
import org.netbeans.jcode.core.util.SourceGroupSupport;
import static org.netbeans.jcode.core.util.StringHelper.firstLower;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import org.netbeans.jcode.entity.info.EntityResourceBeanModel;
import static org.netbeans.jcode.generator.internal.util.Util.pluralize;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Technology;
import static org.netbeans.jcode.layer.Technology.Type.BUSINESS;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Generates EJB facades for entity classes.
 *
 * @author Gaurav Gupta
 */

@ServiceProvider(service=Generator.class)
@Technology(type=BUSINESS, label="Session Bean Facade", panel=SessionBeanPanel.class)
public final class EjbFacadeGenerator implements Generator{
    
    private static final String TEMPLATE = "org/netbeans/jcode/template/";
    private static final String FACADE_ABSTRACT = "Abstract"; //NOI18N
    protected static final String EJB_STATELESS = "javax.ejb.Stateless"; //NOI18N
    
    @ConfigData
    private SessionBeanData beanData;
    
    @ConfigData
    private Project project; 
    
    @ConfigData
    private ApplicationConfigData applicationConfigData;
    
    @ConfigData
    private SourceGroup source; 
    
    @ConfigData
    private EntityResourceBeanModel model;
    
    @ConfigData
    private ProgressHandler handler;
     
    @Override
    public void execute() throws IOException {
        handler.progress(Console.wrap(EjbFacadeGenerator.class, "MSG_Progress_Now_Generating", FG_RED, BOLD, UNDERLINE));
        generate(model.getEntityInfos().stream().map(ei -> ei.getType()).collect(toList()));
        addMavenDependencies("pom/facade/_pom.xml");
    }
    
    private void addMavenDependencies(String pom) {
        if(POMManager.isMavenProject(project)){
            POMManager pomManager = new POMManager(TEMPLATE + pom, project);
            pomManager.setSourceVersion("1.8");
            pomManager.execute();
            pomManager.commit();
        } else {
            handler.warning(NbBundle.getMessage(EjbFacadeGenerator.class, "TITLE_Maven_Project_Not_Found"),
                    NbBundle.getMessage(EjbFacadeGenerator.class, "MSG_Maven_Project_Not_Found"));
        }
    }
    
    public Set<FileObject> generate(List<String> entities) throws IOException {
        final Set<FileObject> createdFiles = new HashSet<>();

        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(source, beanData.getPackage(), true);

        generateAbstract(targetFolder, true);
        for (String entity : entities) {
            handler.progress(beanData.getPrefixName() + JavaIdentifiers.unqualify(entity) + beanData.getSuffixName());
            createdFiles.add(generate(targetFolder, entity, true));
        }

        return createdFiles;
    }



    private FileObject generateAbstract(FileObject targetFolder, boolean overrideExisting) throws IOException {
        //create the abstract facade class
        String fileName = beanData.getPrefixName() + FACADE_ABSTRACT + beanData.getSuffixName();
        FileObject afFO = targetFolder.getFileObject(fileName, JAVA_EXT);//skips here
        
        if (afFO != null) {
            if (overrideExisting) {
                afFO.delete();
            } else {
                throw new IOException("File already exists exception: " + afFO.getPath());
            }
        }
        
        afFO = org.netbeans.jcode.core.util.FileUtil.expandTemplate("org/netbeans/jcode/ejb/facade/resource/AbstractFacade.java.ftl", targetFolder, fileName+'.'+JAVA_EXT, Collections.singletonMap("package", beanData.getPackage()));
       
        try {//subclass created using java.source api so class resolution is required
            JavaSource.forFileObject(afFO).runWhenScanFinished((CompilationController cc) -> {
                cc.toPhase(Phase.ELEMENTS_RESOLVED);
            }, true).get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return afFO;
    }

    /**
     * Generates the facade for the given entity class.
     * <i>Package private visibility for tests</i>.
     *
     * @param targetFolder the folder where the facade and interfaces are
     * generated.
     * @param entityClass the FQN of the entity class for which the facade is
     * generated.
     * @param pkg the package prefix for the generated facede.
     * @param hasRemote specifies whether a remote interface is generated.
     * @param hasLocal specifies whether a local interface is generated.
     * @param strategyClass the entity manager lookup strategy.
     *
     * @return a set containing the generated files.
     */
    private FileObject generate(FileObject targetFolder, final String entityFQN, boolean overrideExisting) throws IOException {
        final String entitySimpleName = JavaIdentifiers.unqualify(entityFQN);
        String abstractFileName = beanData.getPrefixName() + FACADE_ABSTRACT + beanData.getSuffixName();
        String facadeName = beanData.getPrefixName() + entitySimpleName + beanData.getSuffixName();
        // create the facade
        FileObject existingFO = targetFolder.getFileObject(facadeName, JAVA_EXT);
        if (existingFO != null) {
            if (overrideExisting) {
                existingFO.delete();
            } else {
                throw new IOException("File already exists exception: " + existingFO.getPath());
            }
        }
    
        String entityClass = firstUpper(entitySimpleName);
        String entityInstance = firstLower(entitySimpleName);
        
        Map<String, Object> param = new HashMap<>();
        param.put("EntityClass", entityClass);
        param.put("EntityClassPlural", pluralize(firstUpper(entitySimpleName)));
        param.put("EntityClass_FQN", entityFQN);
        param.put("entityInstance", entityInstance);
        param.put("entityInstancePlural", pluralize(firstLower(entitySimpleName)));
        
        param.put("AbstractFacade", abstractFileName);
        param.put("EntityFacade", facadeName);
        param.put("PU", applicationConfigData.getPersistenceUnitName());
        param.put("package", beanData.getPackage());
        
        existingFO = org.netbeans.jcode.core.util.FileUtil.expandTemplate("org/netbeans/jcode/ejb/facade/resource/EntityFacade.java.ftl", targetFolder, facadeName+'.'+JAVA_EXT, param);
       
        return existingFO;
    }
    
}
