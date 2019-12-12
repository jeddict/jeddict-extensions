/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.repository;

import io.github.jeddict.docker.generator.DockerGenerator;
import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.Generator;
import io.github.jeddict.jcode.annotation.ConfigData;
import io.github.jeddict.jcode.annotation.Technology;
import static io.github.jeddict.jcode.annotation.Technology.Type.REPOSITORY;
import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.*;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import static io.github.jeddict.jcode.util.AttributeType.getWrapperType;
import static io.github.jeddict.jcode.util.AttributeType.isPrimitive;
import io.github.jeddict.jcode.util.BuildManager;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT;
import static io.github.jeddict.jcode.util.FileUtil.expandTemplate;
import io.github.jeddict.jcode.util.JavaIdentifiers;
import static io.github.jeddict.jcode.util.ProjectHelper.getFolderForPackage;
import static io.github.jeddict.jcode.util.StringHelper.firstLower;
import static io.github.jeddict.jcode.util.StringHelper.firstUpper;
import static io.github.jeddict.jcode.util.StringHelper.pluralize;
import io.github.jeddict.jpa.spec.DefaultAttribute;
import io.github.jeddict.jpa.spec.EmbeddedId;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.Id;
import io.github.jeddict.jpa.spec.extend.Attribute;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static io.github.jeddict.util.StringUtils.EMPTY;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 * Generates repository for entity classes.
 *
 * @author Gaurav Gupta
 */

@ServiceProvider(service = Generator.class)
@Technology(type = REPOSITORY, 
        label = "CDI Repository", 
        panel = RepositoryPanel.class, 
        sibling = {DockerGenerator.class},
        microservice = true
)
public final class RepositoryGenerator implements Generator {

    private static final String TEMPLATE = "io/github/jeddict/template/";
    public static final String REPOSITORY_ABSTRACT = "Abstract"; 

    @ConfigData
    private RepositoryData repositoryData;

    @ConfigData
    private EntityMappings entityMapping;

    @ConfigData
    private ApplicationConfigData appConfigData;

    @ConfigData
    private ProgressHandler handler;
    
    private Project targetProject;

    private SourceGroup targetSource;
    
    private Project gatewayProject;

    private SourceGroup gatewaySource;
    
    private boolean jpa;
    
    private boolean jnosql;

    @Override
    public void execute() throws IOException {
        targetProject = appConfigData.getTargetProject();
        targetSource = appConfigData.getTargetSourceGroup();
        gatewayProject = appConfigData.getGatewayProject();
        gatewaySource = appConfigData.getGatewaySourceGroup();
        jpa = entityMapping.getGeneratedEntity()
                .filter(entity -> !entity.getNoSQL())
                .findAny().isPresent();
        jnosql = entityMapping.getGeneratedEntity()
                .filter(entity -> entity.getNoSQL())
                .findAny().isPresent();
        
        handler.progress(Console.wrap(RepositoryGenerator.class, "MSG_Progress_Now_Generating", FG_DARK_RED, BOLD, UNDERLINE));
        if (appConfigData.isMonolith() || appConfigData.isMicroservice()) {
            if (appConfigData.isCompleteApplication()) {
                if (jpa) {
                    generateAbstract(true, targetSource, appConfigData.getTargetPackage());
                    generateProducer(targetSource, appConfigData.getTargetPackage());
                }
                addMavenDependencies("repository/pom/pom.xml", targetProject);
            }
            generateRepository();
        }
        if (appConfigData.isGateway()) {
            if (jpa) {
                generateAbstract(true, gatewaySource, appConfigData.getGatewayPackage());
                generateProducer(gatewaySource, appConfigData.getGatewayPackage());
            }
            addMavenDependencies("repository/pom/pom.xml", gatewayProject);
        }

    }

    private void addMavenDependencies(String pom, Project project) {
        BuildManager.getInstance(project)
                .copy(TEMPLATE + pom)
                .setSourceVersion("1.8")
                .commit()
                .reload();
    }

    private Set<FileObject> generateRepository() throws IOException {
        final Set<FileObject> createdFiles = new HashSet<>();
        for (Entity entity : entityMapping.getGeneratedEntity().collect(toList())) {
            handler.progress(repositoryData.getRepositoryPrefixName() + entity.getClazz() + repositoryData.getRepositorySuffixName());
            createdFiles.add(generate(entity, true));
        }

        return createdFiles;
    }

    private FileObject generateProducer(SourceGroup source, String appPackage) throws IOException {
        String _package = appPackage + ".producer";
        FileObject targetFolder = getFolderForPackage(source, _package, true);
        String fileName = "EntityManagerProducer";
        FileObject afFO = targetFolder.getFileObject(fileName, JAVA_EXT);//skips here
        if (afFO == null) {
            Map<String, Object> param = new HashMap<>();
            param.put("PU", entityMapping.getPersistenceUnitName());
            param.put("package", _package);
            handler.progress(fileName);
            afFO = io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "service/producer/EntityManagerProducer.java.ftl", targetFolder, fileName + '.' + JAVA_EXT, param);
        }
        return afFO;
    }

    private FileObject generateAbstract(boolean overrideExisting, SourceGroup source, String appPackage) throws IOException {
        //create the abstract repository class
        FileObject targetFolder = getFolderForPackage(source, appPackage + '.' + repositoryData.getRepositoryPackage(), true);
        String fileName = repositoryData.getRepositoryPrefixName() + REPOSITORY_ABSTRACT + repositoryData.getRepositorySuffixName();
        FileObject afFO = targetFolder.getFileObject(fileName, JAVA_EXT);//skips here

        Map<String, Object> param = new HashMap<>();
        param.put("AbstractRepository", fileName);
        param.put("package", appPackage + '.' + repositoryData.getRepositoryPackage());
        param.put("cdi", repositoryData.isCDI());
        param.put("named", repositoryData.isNamed());

        if (afFO != null) {
            if (overrideExisting) {
                afFO.delete();
            } else {
                throw new IOException("File already exists exception: " + afFO.getPath());
            }
        }
        handler.progress(fileName);
        afFO = io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "repository/AbstractRepository.java.ftl", targetFolder, fileName + '.' + JAVA_EXT, param);

        return afFO;
    }

    /**
     * Generates the repository for the given entity class.
     *
     * @return the generated files.
     */
    private FileObject generate(final Entity entity, boolean overrideExisting) throws IOException {
        FileObject targetFolder = getFolderForPackage(appConfigData.getTargetSourceGroup(),
                entity.getAbsolutePackage(appConfigData.getTargetPackage() + '.' + repositoryData.getRepositoryPackage()), 
                true);
        String entityFQN = entity.getFQN();
        final String entitySimpleName = entity.getClazz();
        String abstractFileName = repositoryData.getRepositoryPrefixName() + REPOSITORY_ABSTRACT + repositoryData.getRepositorySuffixName();
        String repositoryName = repositoryData.getRepositoryPrefixName() + entitySimpleName + repositoryData.getRepositorySuffixName();
        // create the repository
        FileObject existingFO = targetFolder.getFileObject(repositoryName, JAVA_EXT);
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
        param.put("EntityClassPlural", pluralize(entityClass));
        param.put("EntityClass_FQN", entityFQN);
        param.put("entityInstance", entityInstance);
        param.put("entityInstancePlural", pluralize(entityInstance));

        param.put("AbstractRepository", abstractFileName);
        if (!entity.getAbsolutePackage(repositoryData.getRepositoryPackage()).equals(repositoryData.getRepositoryPackage())) { //if both EntityRepository and AbstractRepository are not in same package
            param.put("AbstractRepository_FQN", '.' + repositoryData.getRepositoryPackage() + "." + abstractFileName);
        } else {
            param.put("AbstractRepository_FQN", EMPTY);
        }
        param.put("EntityRepository", repositoryName);
        param.put("PU", entityMapping.getPersistenceUnitName());
        param.put("package", entity.getAbsolutePackage(appConfigData.getTargetPackage() + '.' + repositoryData.getRepositoryPackage()));
        param.put("cdi", repositoryData.isCDI());
        param.put("named", repositoryData.isNamed());
        param.put("appPackage", appConfigData.getTargetPackage());

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

        existingFO = expandTemplate(
                TEMPLATE + (Boolean.TRUE.equals(entity.getNoSQL()) ? "jnosql/" : "") + "repository/EntityRepository.java.ftl",
                targetFolder,
                repositoryName + '.' + JAVA_EXT,
                param
        );

        return existingFO;
    }

}
