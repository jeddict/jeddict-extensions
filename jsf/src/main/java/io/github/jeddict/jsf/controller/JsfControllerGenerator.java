/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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
package io.github.jeddict.jsf.controller;

import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.Generator;
import io.github.jeddict.jcode.annotation.ConfigData;
import io.github.jeddict.jcode.annotation.Technology;
import static io.github.jeddict.jcode.annotation.Technology.Type.CONTROLLER;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT;
import static io.github.jeddict.jcode.util.ProjectHelper.getFolderForPackage;
import static io.github.jeddict.jcode.util.StringHelper.firstLower;
import static io.github.jeddict.jcode.util.StringHelper.firstUpper;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.repository.RepositoryData;
import io.github.jeddict.repository.RepositoryGenerator;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;
import io.github.jeddict.jcode.util.FileUtil;
import io.github.jeddict.jpa.spec.extend.Attribute;
import java.util.List;

/**
 * Generates repository for entity classes.
 *
 * @author Shiwani Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(type = CONTROLLER,
        label = "JSF Controller",
        panel = JsfControllerPanel.class,
        parents = RepositoryGenerator.class
)
public final class JsfControllerGenerator implements Generator {

    private static final String TEMPLATE = "io/github/jeddict/template/";
    public static final String REPOSITORY_ABSTRACT = "Abstract";

    @ConfigData
    private JsfControllerData controllerData;

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

    private String targetPackage;

    private Project gatewayProject;

    private SourceGroup gatewaySource;

    @Override
    public void execute() throws IOException {
        targetProject = appConfigData.getTargetProject();
        targetSource = appConfigData.getTargetSourceGroup();
        gatewayProject = appConfigData.getGatewayProject();
        gatewaySource = appConfigData.getGatewaySourceGroup();
        targetPackage = appConfigData.getTargetPackage();
        generate();
        generatePagination();
        generateJsfUtil();

    }

    private Set<FileObject> generate() throws IOException {

        final Set<FileObject> createdFiles = new HashSet<>();
        for (Entity entity : entityMapping.getGeneratedEntity().collect(toList())) {
            handler.progress(controllerData.getPrefixName() + entity.getClazz() + controllerData.getSuffixName());
            createdFiles.add(generateController(entity, true));

        }
        return createdFiles;
    }

    private void generatePagination() throws IOException {
        String _package = targetPackage + ".util";
        FileObject targetFolder = getFolderForPackage(targetSource, _package, true);
        String fileName = "PaginationHelper";
        Map<String, Object> param = new HashMap<>();
        param.put("package", _package);
        handler.progress(fileName);
        FileUtil.expandTemplate(TEMPLATE + "util/PaginationHelper.java.ftl", targetFolder, fileName + '.' + JAVA_EXT, param);
    }

    private void generateJsfUtil() throws IOException {
        String _package = targetPackage + ".util";
        FileObject targetFolder = getFolderForPackage(targetSource, _package, true);
        String fileName = "JsfUtil";
        Map<String, Object> param = new HashMap<>();
        param.put("package", _package);
        handler.progress(fileName);
        FileUtil.expandTemplate(TEMPLATE + "util/JsfUtil.java.ftl", targetFolder, fileName + '.' + JAVA_EXT, param);
    }

    private FileObject generateController(final Entity entity, boolean overrideExisting) throws IOException {
        FileObject targetFolder = getFolderForPackage(targetSource,
                entity.getAbsolutePackage(targetPackage + '.' + controllerData.getPackage()),
                true);

        final String entitySimpleName = entity.getClazz();
        String controllerName = controllerData.getPrefixName() + entitySimpleName + controllerData.getSuffixName();
        String entityClass = firstUpper(entitySimpleName);
        String entityInstance = firstLower(entitySimpleName);
        String entityFQN = entity.getFQN();
        String embeddableFQN = entityFQN.substring(0, entityFQN.lastIndexOf("."));

        String repositoryName = repositoryData.getRepositoryPrefixName() + entitySimpleName + repositoryData.getRepositorySuffixName();
        String repositoryInstance = firstLower(repositoryName);
        String repositoryFQN = entity.getAbsolutePackage(repositoryData.getRepositoryPackage()) + '.' + repositoryName;;

        List<Attribute> attributes = entity.getAttributes().getAllAttribute();
        Map<String, Object> params = new HashMap<>();
        params.put("entityInstance", entityInstance);
        params.put("Entity", entityClass);
        params.put("EntityController", controllerName);
        params.put("named", controllerData.isNamed());
        params.put("EntityRepository", repositoryName);
        params.put("RepositoryInstance", repositoryInstance);
        params.put("EntityClass_FQN", entityFQN);
        params.put("attributes", attributes);
        params.put("embeddableFQN", embeddableFQN);
        params.put("RepositoryClass_FQN", targetPackage + '.' + repositoryFQN);
        params.put("JsfUtil_FQN", targetPackage + ".util.JsfUtil");
        params.put("PaginationHelper_FQN", targetPackage + ".util.PaginationHelper");
        params.put("package", targetPackage + '.' + entity.getAbsolutePackage(controllerData.getPackage()));

        return FileUtil.expandTemplate(TEMPLATE + "controller/EntityController.java.ftl", targetFolder, controllerName + '.' + JAVA_EXT, params);
    }
}
