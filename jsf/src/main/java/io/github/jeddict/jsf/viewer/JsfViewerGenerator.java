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
package io.github.jeddict.jsf.viewer;

import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.Generator;
import io.github.jeddict.jcode.annotation.ConfigData;
import io.github.jeddict.jcode.annotation.Technology;
import static io.github.jeddict.jcode.annotation.Technology.Type.VIEWER;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import io.github.jeddict.jcode.util.BuildManager;
import io.github.jeddict.jcode.util.FileUtil;
import io.github.jeddict.jcode.util.ProjectHelper;
import static io.github.jeddict.jcode.util.StringHelper.firstLower;
import io.github.jeddict.jpa.spec.Embeddable;
import io.github.jeddict.jpa.spec.EmbeddableAttributes;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jsf.controller.JsfControllerData;
import io.github.jeddict.jsf.controller.JsfControllerGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * Generates repository for entity classes.
 *
 * @author Shiwani Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(type = VIEWER,
        label = "JSF",
        panel = JsfViewerPanel.class,
        parents = JsfControllerGenerator.class
)
public final class JsfViewerGenerator implements Generator {

    private static final String TEMPLATE = "io/github/jeddict/template/";
    public static final String REPOSITORY_ABSTRACT = "Abstract";

    @ConfigData
    private JsfViewerData jsfViewerData;

    @ConfigData
    private JsfControllerData controllerData;

    @ConfigData
    private EntityMappings entityMapping;

    @ConfigData
    private ApplicationConfigData appConfigData;

    @ConfigData
    private ProgressHandler handler;

    @ConfigData
    private ProjectHelper projectHelper;

    private Project targetProject;

    private SourceGroup targetSource;

    private Project gatewayProject;

    private SourceGroup gatewaySource;
    private String webXmlData = "<context-param>\n"
            + "        <param-name>primefaces.THEME</param-name>\n"
            + "        <param-value>aristo</param-value>\n"
            + "    </context-param>\n"
            + "    <servlet>\n"
            + "        <servlet-name>Faces Servlet</servlet-name>\n"
            + "        <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>\n"
            + "        <load-on-startup>1</load-on-startup>\n"
            + "    </servlet>\n"
            + "    <servlet-mapping>\n"
            + "        <servlet-name>Faces Servlet</servlet-name>\n"
            + "        <url-pattern>/faces/*</url-pattern>\n"
            + "    </servlet-mapping>\n"
            + "     <session-config>\n"
            + "     <session-timeout>\n"
            + "           30\n"
            + "     </session-timeout>\n"
            + "     </session-config>\n"
            + "    <welcome-file-list>\n"
            + "        <welcome-file>faces/index.xhtml</welcome-file>\n"
            + "    </welcome-file-list>";

    @Override
    public void execute() throws IOException {
        targetProject = appConfigData.getTargetProject();
        targetSource = appConfigData.getTargetSourceGroup();
        gatewayProject = appConfigData.getGatewayProject();
        gatewaySource = appConfigData.getGatewaySourceGroup();
        addMavenDependencies("jsf/pom/_pom.xml", targetProject);
        appConfigData.addWebDescriptorContent(webXmlData, targetProject);
        generate();
        generateLayout();
        generateResources();
    }

    private void addMavenDependencies(String pom, Project project) {
        BuildManager.getInstance(project)
                .copy(TEMPLATE + pom)
                .setSourceVersion("1.8")
                .commit()
                .reload();
    }

    private void generate() throws IOException {
        List<Entity> entityList = entityMapping.getGeneratedEntity().collect(toList());
        for (Entity entity : entityList) {
            handler.progress(jsfViewerData.getPrefixName() + entity.getClazz() + jsfViewerData.getSuffixName());
            generateCRUD(entity, true);
        }
        generatePropertiesFile(entityList);
    }

    private void generatePropertiesFile(List<Entity> entityList) {
        final FileObject resourceFolder = projectHelper.getResourceDirectory(gatewayProject);
        FileObject rootFolder = projectHelper.getProjectWebRoot(targetProject);//getFolderForPackage(source, _package, true);

        String indexFilename = "index";
        String bundleFileName = "Bundle";
        List<String> entityNames = new ArrayList<>();
        for (Entity entity : entityList) {
            entityNames.add(entity.getName());
        }
        Map<String, Object> params = new HashMap<>();
        params.put("Entities", entityNames.toArray());

        try {
            io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "jsf/index.xhtml.ftl", rootFolder, indexFilename + '.' + "xhtml", params);
            FileUtil.expandTemplate(TEMPLATE + "util/Bundle.properties.ftl", resourceFolder, bundleFileName + '.' + "properties", params);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void generateLayout() throws IOException {
        FileObject targetFolder = projectHelper.getProjectWebRoot(targetProject);//getFolderForPackage(source, _package, true);
        String xmlTargetPath = "WEB-INF/";
        FileObject xmlTargetFolder = projectHelper.getFileObject(targetFolder, xmlTargetPath, "/");
        String fileName = "template";

        Map<String, Object> params = new HashMap<>();
        handler.progress(fileName);

        io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "jsf/template.xhtml.ftl", targetFolder, fileName + '.' + "xhtml", params);
        io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "jsf/beans.xml.ftl", xmlTargetFolder, "beans" + '.' + "xml", params);
        io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "jsf/faces-config.xml.ftl", xmlTargetFolder, "faces-config" + '.' + "xhtml", params);
    }

    public void generateCRUD(final Entity entity, boolean overrideExisting) throws IOException {
        String entityName = entity.getName();
        List<Attribute> attributes = entity.getAttributes().getAllAttribute();
        List<Embeddable> embeddables = entity.getRootElement().getEmbeddable();

        String entityInstance = firstLower(entityName);
        String targetPath = "app/entities/" + entityInstance;

        FileObject rootFolder = projectHelper.getProjectWebRoot(targetProject);//getFolderForPackage(source, _package, true);
        FileObject targetFolder = projectHelper.getFileObject(rootFolder, targetPath, "/");

        String createFileName = "create" + entityName;
        String viewFileName = "view" + entityName;
        String updateFileName = "update" + entityName;
        String listFileName = "list" + entityName;
        String controllerName = firstLower(controllerData.getPrefixName() + entityName + controllerData.getSuffixName());

        Map<String, Object> params = new HashMap<>();
        params.put("EntityController", controllerName);
        params.put("Entity", entityName);
        params.put("hash", "#");
        params.put("attributes", attributes);
        params.put("embeddables", embeddables);
        params.put("entityInstance", entityInstance);
        params.put("path", TEMPLATE + "jsf/");
        handler.progress(createFileName);
        io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "jsf/view.xhtml.ftl", targetFolder, viewFileName + '.' + "xhtml", params);
        io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "jsf/list.xhtml.ftl", targetFolder, listFileName + '.' + "xhtml", params);
        params.put("pageType", "Create");
        io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "jsf/createUpdate.xhtml.ftl", targetFolder, createFileName + '.' + "xhtml", params);
        params.put("pageType", "Update");
        io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "jsf/createUpdate.xhtml.ftl", targetFolder, updateFileName + '.' + "xhtml", params);
    }

    public void generateResources() throws IOException {
        FileObject targetFolder = projectHelper.getProjectWebRoot(targetProject);//getFolderForPackage(source, _package, true);
        String xmlTargetPath = "resources/";
        FileObject cssTargetFolder = projectHelper.getFileObject(targetFolder, xmlTargetPath + "css/", "/");
        FileObject jsTargetFolder = projectHelper.getFileObject(targetFolder, xmlTargetPath + "js/", "/");
        FileObject imagesTargetFolder = projectHelper.getFileObject(targetFolder, xmlTargetPath + "images/", "/");

        String fileName = "jsfcrud";
        Map<String, Object> params = new HashMap<>();
        handler.progress(fileName);
        io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "jsf/jsfcrud.css.ftl", cssTargetFolder, fileName + '.' + "css", params);
        io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "jsf/jsfcrud.js.ftl", jsTargetFolder, fileName + '.' + "js", params);
//        io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "jsf/images.js.ftl", jsTargetFolder, fileName + '.' + "js", params);

    }

}
