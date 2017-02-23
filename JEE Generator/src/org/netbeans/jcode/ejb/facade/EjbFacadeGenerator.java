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
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.EMPTY;
import org.jcode.docker.generator.DockerGenerator;
import org.netbeans.jpa.modeler.spec.Entity;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import static org.netbeans.jcode.core.util.AttributeType.getWrapperType;
import static org.netbeans.jcode.core.util.AttributeType.isPrimitive;
import static org.netbeans.jcode.core.util.Constants.JAVA_EXT;
import org.netbeans.jcode.core.util.JavaIdentifiers;
import org.netbeans.jcode.core.util.POMManager;
import org.netbeans.jcode.core.util.SourceGroupSupport;
import static org.netbeans.jcode.core.util.StringHelper.firstLower;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import static org.netbeans.jcode.core.util.StringHelper.pluralize;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Technology;
import static org.netbeans.jcode.layer.Technology.Type.BUSINESS;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jpa.modeler.spec.DefaultAttribute;
import org.netbeans.jpa.modeler.spec.EmbeddedId;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.extend.Attribute;

/**
 * Generates EJB facades for entity classes.
 *
 * @author Gaurav Gupta
 */

@ServiceProvider(service = Generator.class)
@Technology(type = BUSINESS, label = "Session Bean Facade", panel = SessionBeanPanel.class, sibling = {DockerGenerator.class})
public final class EjbFacadeGenerator implements Generator {

    private static final String TEMPLATE = "org/netbeans/jcode/template/";
    public static final String FACADE_ABSTRACT = "Abstract"; //NOI18N
    protected static final String EJB_STATELESS = "javax.ejb.Stateless"; //NOI18N

    @ConfigData
    private SessionBeanData beanData;

    @ConfigData
    private Project project;

    @ConfigData
    private EntityMappings entityMapping;

    @ConfigData
    private SourceGroup source;

    @ConfigData
    private ApplicationConfigData appConfigData;

    @ConfigData
    private ProgressHandler handler;

    @Override
    public void execute() throws IOException {
        handler.progress(Console.wrap(EjbFacadeGenerator.class, "MSG_Progress_Now_Generating", FG_RED, BOLD, UNDERLINE));
        if (appConfigData.isCompleteApplication()) {
            generateAbstract(true);
            generateProducer();
            addMavenDependencies("pom/facade/_pom.xml");
        }
        generateFacade();

    }

    private void addMavenDependencies(String pom) {
        if (POMManager.isMavenProject(project)) {
            POMManager pomManager = new POMManager(TEMPLATE + pom, project);
            pomManager.setSourceVersion("1.8");
            pomManager.commit();
        } else {
            handler.warning(NbBundle.getMessage(EjbFacadeGenerator.class, "TITLE_Maven_Project_Not_Found"),
                    NbBundle.getMessage(EjbFacadeGenerator.class, "MSG_Maven_Project_Not_Found"));
        }
    }

    private Set<FileObject> generateFacade() throws IOException {
        final Set<FileObject> createdFiles = new HashSet<>();
        for (Entity entity : entityMapping.getGeneratedEntity().collect(toList())) {
            handler.progress(beanData.getPrefixName() + entity.getClazz() + beanData.getSuffixName());
            createdFiles.add(generate(entity, true));
        }

        return createdFiles;
    }

    private FileObject generateProducer() throws IOException {
        String _package = beanData.getPackage() + ".producer";
        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(source, _package, true);
        String fileName = "EntityManagerProducer";
        FileObject afFO = targetFolder.getFileObject(fileName, JAVA_EXT);//skips here
        if (afFO == null) {
            Map<String, Object> param = new HashMap<>();
            param.put("PU", entityMapping.getPersistenceUnitName());
            param.put("package", _package);
            handler.progress(fileName);
            afFO = org.netbeans.jcode.core.util.FileUtil.expandTemplate("org/netbeans/jcode/template/service/producer/EntityManagerProducer.java.ftl", targetFolder, fileName + '.' + JAVA_EXT, param);
        }
        return afFO;
    }

    private FileObject generateAbstract(boolean overrideExisting) throws IOException {
        //create the abstract facade class
        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(source, beanData.getPackage(), true);
        String fileName = beanData.getPrefixName() + FACADE_ABSTRACT + beanData.getSuffixName();
        FileObject afFO = targetFolder.getFileObject(fileName, JAVA_EXT);//skips here

        if (afFO != null) {
            if (overrideExisting) {
                afFO.delete();
            } else {
                throw new IOException("File already exists exception: " + afFO.getPath());
            }
        }
        handler.progress(fileName);
        afFO = org.netbeans.jcode.core.util.FileUtil.expandTemplate("org/netbeans/jcode/ejb/facade/resource/AbstractFacade.java.ftl", targetFolder, fileName + '.' + JAVA_EXT, Collections.singletonMap("package", beanData.getPackage()));

        return afFO;
    }

    /**
     * Generates the facade for the given entity class.
     *
     * @return the generated files.
     */
    private FileObject generate(final Entity entity, boolean overrideExisting) throws IOException {
        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(source, entity.getAbsolutePackage(beanData.getPackage()), true);
        String entityFQN = entity.getFQN();
        final String entitySimpleName = entity.getClazz();
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
        if (!entity.getAbsolutePackage(beanData.getPackage()).equals(beanData.getPackage())) { //if both EntityFacade and AbstractFacade are not in same package
            param.put("AbstractFacade_FQN", beanData.getPackage() + "." + abstractFileName);
        } else {
            param.put("AbstractFacade_FQN", EMPTY);
        }
        param.put("EntityFacade", facadeName);
        param.put("PU", entityMapping.getPersistenceUnitName());
        param.put("package", entity.getAbsolutePackage(beanData.getPackage()));

        Attribute idAttribute = entity.getAttributes().getIdField();
        if (idAttribute != null) {
            if (idAttribute instanceof Id) {
                String dataType_FQN = idAttribute.getDataTypeLabel();
                param.put("EntityPKClass_FQN", EMPTY);
                if (isPrimitive(dataType_FQN)) {
                    param.put("EntityPKClass", getWrapperType(dataType_FQN));
                } else {
                    String dataType = JavaIdentifiers.unqualify(dataType_FQN);
                    param.put("EntityPKClass", dataType);
                    if (dataType.length() != dataType_FQN.length()) {
                        param.put("EntityPKClass_FQN", dataType_FQN);
                    }
                }
            } else if (idAttribute instanceof EmbeddedId || idAttribute instanceof DefaultAttribute) {
                param.put("EntityPKClass", idAttribute.getDataTypeLabel());
                param.put("EntityPKClass_FQN", entity.getRootPackage() + '.' + idAttribute.getDataTypeLabel());
            }
        }

        existingFO = org.netbeans.jcode.core.util.FileUtil.expandTemplate("org/netbeans/jcode/ejb/facade/resource/EntityFacade.java.ftl", targetFolder, facadeName + '.' + JAVA_EXT, param);

        return existingFO;
    }

}
