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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.script.ScriptException;
import org.apache.commons.io.IOUtils;

import org.netbeans.jpa.modeler.spec.*;
import org.netbeans.api.project.Project;
import static org.netbeans.jcode.core.util.FileUtil.getSimpleFileName;
import static org.netbeans.jcode.core.util.JavaSourceHelper.getSimpleClassName;
import org.netbeans.jcode.core.util.JavaUtil;
import static org.netbeans.jcode.core.util.ProjectHelper.getProjectWebRoot;
import org.netbeans.jcode.layer.ConfigData;
import static org.netbeans.jcode.ng.main.AngularUtil.copyDynamicResource;
import org.netbeans.jcode.ng.main.domain.NGApplicationConfig;
import org.netbeans.jcode.ng.main.domain.ApplicationSourceFilter;
import org.netbeans.jcode.ng.main.domain.EntityConfig;
import org.netbeans.jcode.ng.main.domain.NGEntity;
import org.netbeans.jcode.ng.main.domain.NGField;
import org.netbeans.jcode.ng.main.domain.NGRelationship;
import org.netbeans.jcode.parser.ejs.EJSParser;
import org.netbeans.jcode.parser.ejs.FileTypeStream;
import org.netbeans.jcode.rest.controller.RESTData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.jpa.modeler.spec.ElementCollection;
import org.netbeans.jpa.modeler.spec.Embedded;
import org.netbeans.jpa.modeler.spec.Entity;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.netbeans.jpa.modeler.spec.EntityMappings;
import org.netbeans.jpa.modeler.spec.Id;
import org.netbeans.jpa.modeler.spec.Transient;
import org.netbeans.jpa.modeler.spec.Version;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jpa.modeler.spec.extend.BaseAttribute;
import org.netbeans.jpa.modeler.spec.extend.EnumTypeHandler;
import org.netbeans.jpa.modeler.spec.extend.RelationAttribute;
import org.netbeans.jcode.layer.Generator;

/**
 *
 * @author Gaurav Gupta
 */
public abstract class AngularGenerator implements Generator {

    @ConfigData
    protected EntityMappings entityMapping;

    @ConfigData
    protected AngularData ngData;

    @ConfigData
    protected RESTData restData;

    @ConfigData
    protected Project project;

    @ConfigData
    protected ProgressHandler handler;

    protected static final List<String> PARSER_FILE_TYPE = Arrays.asList("html", "js", "css", "scss", "json", "ts", "ejs", "txt");
//    private static final List<String> BINARY_FILE_TYPE = Arrays.asList("gif", "ico", "png", "jpeg", "jpg");

    protected Consumer<FileTypeStream> getParserManager(EJSParser parser) {
        return getParserManager(parser, null);
    }

    protected Consumer<FileTypeStream> getParserManager(EJSParser parser, List<String> skipFile) {
        return (fileTypeStream) -> {
            try {

                if (PARSER_FILE_TYPE.contains(fileTypeStream.getFileType()) && (skipFile == null || !skipFile.contains(fileTypeStream.getFileName()))) {
                    IOUtils.write(parser.parse(fileTypeStream.getInputStream()), fileTypeStream.getOutputStream());
                } else {
                    IOUtils.copy(fileTypeStream.getInputStream(), fileTypeStream.getOutputStream());
                }
            } catch (ScriptException | IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        };
    }

    protected List<String> entityScriptFiles;
    protected List<String> scriptFiles;
    public abstract String getTemplatePath();

    @Override
    public void execute() throws IOException {
        entityScriptFiles = new ArrayList<>();
        scriptFiles = new ArrayList<>();
        generateClientSideComponent();
    }

    protected abstract ApplicationSourceFilter getApplicationSourceFilter(NGApplicationConfig applicationConfig);

    protected abstract void generateClientSideComponent();

    protected void generateNgEntityi18nResource(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter, NGEntity entity) throws IOException {
        FileObject webRoot = getProjectWebRoot(project);

        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(entity);

        Function<String, String> pathResolver = (templatePath) -> {
            String simpleFileName = getSimpleFileName(templatePath);
            String[] pathSplitter = simpleFileName.split("_");
            String type = pathSplitter[1];
            String lang = pathSplitter[2];
            if (!"entity".equals(type) || !applicationConfig.getLanguages().contains(lang)) {
                return null;
            }

            templatePath = String.format("i18n/%s/%s.json", lang, entity.getEntityTranslationKey());
            return templatePath;
        };
        copyDynamicResource(getParserManager(parser), getTemplatePath() + "entity-resource-i18n.zip", webRoot, pathResolver, handler);
    }

    protected void generateNgApplicationi18nResource(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter) throws IOException {
        FileObject webRoot = getProjectWebRoot(project);

        Map<String, Object> data = new HashMap();//todo remove

        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(data);

        Function<String, String> pathResolver = (templatePath) -> {
            String[] paths = templatePath.split("/");
            String lang = paths[1]; //"i18n/en/password.json" 
            String file = paths[2];
            if (!applicationConfig.getLanguages().contains(lang)) { //if lang selected by dev 
                return null;
            }
            if (!fileFilter.isEnable(file)) {
                return null;
            }
            if (templatePath.contains("/_")) {
                templatePath = templatePath.replaceAll("/_", "/");
            }
            //path modification not required
            return templatePath;
        };
        copyDynamicResource(getParserManager(parser), getTemplatePath() + "web-resources-i18n.zip", webRoot, pathResolver, handler);
    }

    protected EntityConfig getEntityConfig() {
        EntityConfig entityConfig = new EntityConfig();
        entityConfig.setPagination(ngData.getPagination().getKeyword());
        return entityConfig;
    }

    protected abstract String getClientFramework();

    protected NGApplicationConfig getAppConfig() {
        NGApplicationConfig applicationConfig = new NGApplicationConfig();
        applicationConfig.setAngularAppName(ngData.getModule());
        applicationConfig.setEnableTranslation(true);
        applicationConfig.setJhiPrefix("j");
        applicationConfig.setBuildTool("maven");
        applicationConfig.setBaseName(ngData.getApplicationTitle());
        applicationConfig.setApplicationPath(restData.getRestConfigData().getApplicationPath());
        applicationConfig.setEnableMetrics(restData.isMetrics());
        applicationConfig.setRestPackage(restData.getPackage());
        applicationConfig.setEnableDocs(restData.isDocsEnable());
        applicationConfig.setClientFramework(getClientFramework());
        return applicationConfig;
    }

    protected NGEntity getEntity(Entity entity) {
        Attribute idAttribute = entity.getAttributes().getIdField();
        if (idAttribute instanceof EmbeddedId || idAttribute instanceof DefaultAttribute) {
            handler.error(NbBundle.getMessage(AngularGenerator.class, "TITLE_Composite_Key_Not_Supported"),
                    NbBundle.getMessage(AngularGenerator.class, "MSG_Composite_Key_Not_Supported", entity.getClazz()));
            return null;
        } else if (!"id".equals(idAttribute.getName())) {
            handler.error(NbBundle.getMessage(AngularGenerator.class, "TITLE_PK_Field_Named_Id_Missing"),
                    NbBundle.getMessage(AngularGenerator.class, "MSG_PK_Field_Named_Id_Missing", entity.getClazz()));
            return null;
        }
        NGEntity ngEntity = new NGEntity(entity.getClazz(), "");
        ngEntity.setPkType(entity.getAttributes().getIdField().getDataTypeLabel());
        List<Attribute> attributes = entity.getAttributes().getAllAttribute();
//      Uncomment for inheritance support
//        if(entitySpec.getSubclassList().size() > 1){
//            return null;
//        }
//        attributes.addAll(entitySpec.getSuperclassAttributes());
        for (Attribute attribute : attributes) {
            if (attribute instanceof Id && ((Id) attribute).isGeneratedValue()) {
                continue;
            }
            if (!attribute.getIncludeInUI()) {//system attribute
                continue;
            }
            if (attribute.isOptionalReturnType()) {//todo dto
                continue;
            }

            if (attribute instanceof RelationAttribute) {
                RelationAttribute relationAttribute = (RelationAttribute) attribute;
                NGRelationship relationship = new NGRelationship(entity, relationAttribute);
                Entity mappedEntity = relationAttribute.getConnectedEntity();
                if (mappedEntity.getLabelAttribute() == null || mappedEntity.getLabelAttribute().getName().equals("id")) {
                    handler.warning(NbBundle.getMessage(AngularGenerator.class, "TITLE_Entity_Label_Missing"),
                            NbBundle.getMessage(AngularGenerator.class, "MSG_Entity_Label_Missing", mappedEntity.getClazz()));
                } else {
                    relationship.setOtherEntityField(mappedEntity.getLabelAttribute().getName());
                }
                if (entity == mappedEntity) {
                    handler.warning(NbBundle.getMessage(AngularGenerator.class, "TITLE_Self_Relation_Not_Supported"),
                            NbBundle.getMessage(AngularGenerator.class, "MSG_Self_Relation_Not_Supported", attribute.getName(), ngEntity.getName()));
                    continue;
                }
                ngEntity.addRelationship(relationship);
            } else if (attribute instanceof BaseAttribute) {
                if (attribute instanceof EnumTypeHandler && ((EnumTypeHandler) attribute).getEnumerated() != null) {
                    handler.warning(NbBundle.getMessage(AngularGenerator.class, "TITLE_Enum_Type_Not_Supported"),
                            NbBundle.getMessage(AngularGenerator.class, "MSG_Enum_Type_Not_Supported", attribute.getName(), ngEntity.getName()));
                    continue;
                }
                if (attribute instanceof Embedded) {
                    handler.warning(NbBundle.getMessage(AngularGenerator.class, "TITLE_Embedded_Type_Not_Supported"),
                            NbBundle.getMessage(AngularGenerator.class, "MSG_Embedded_Type_Not_Supported", attribute.getName(), ngEntity.getName()));
                    continue;
                }
                if (attribute instanceof ElementCollection) {
                    handler.warning(NbBundle.getMessage(AngularGenerator.class, "TITLE_ElementCollection_Type_Not_Supported"),
                            NbBundle.getMessage(AngularGenerator.class, "MSG_ElementCollection_Type_Not_Supported", attribute.getName(), ngEntity.getName()));
                    continue;
                }
                if (attribute instanceof Version || attribute instanceof Transient) {
                    continue;
                }
                NGField field = new NGField((BaseAttribute) attribute);
                Class<?> primitiveType = JavaUtil.getPrimitiveType(attribute.getDataTypeLabel());
                if (primitiveType != null) {
                    field.setFieldType(primitiveType.getSimpleName());//todo short, byte, char not supported in ui template
                } else {
                    field.setFieldType(getSimpleClassName(attribute.getDataTypeLabel()));
                }

                ngEntity.addField(field);
            }
        }
        return ngEntity;
    }

}
