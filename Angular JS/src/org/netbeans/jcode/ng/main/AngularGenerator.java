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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import javax.script.ScriptException;
import org.apache.commons.io.IOUtils;

import org.netbeans.jpa.modeler.spec.*;
import org.netbeans.api.project.Project;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import org.netbeans.jcode.core.util.FileUtil;
import static org.netbeans.jcode.core.util.FileUtil.getFileExt;
import static org.netbeans.jcode.core.util.FileUtil.getSimpleFileName;
import static org.netbeans.jcode.core.util.FileUtil.getSimpleFileNameWithExt;
import static org.netbeans.jcode.core.util.JavaSourceHelper.getSimpleClassName;
import org.netbeans.jcode.core.util.JavaUtil;
import static org.netbeans.jcode.core.util.ProjectHelper.getProjectWebRoot;
import org.netbeans.jcode.layer.ConfigData;
import static org.netbeans.jcode.ng.main.AngularUtil.copyDynamicFile;
import static org.netbeans.jcode.ng.main.AngularUtil.copyDynamicResource;
import static org.netbeans.jcode.ng.main.AngularUtil.getResource;
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

    protected static final List<String> PARSER_FILE_TYPE = Arrays.asList("html", "js", "css", "scss", "json");
//    private static final List<String> BINARY_FILE_TYPE = Arrays.asList("gif", "ico", "png", "jpeg", "jpg");

    private Consumer<FileTypeStream> getParserManager(EJSParser parser, Map<String, String> extTemplate) {
        return (fileTypeStream) -> {
            try {
                if (PARSER_FILE_TYPE.contains(fileTypeStream.getFileType())) {
                    IOUtils.write(parser.parse(fileTypeStream.getInputStream(), extTemplate), fileTypeStream.getOutputStream());
                } else {
                    IOUtils.copy(fileTypeStream.getInputStream(), fileTypeStream.getOutputStream());
                }
            } catch (ScriptException | IOException ex) {
                Exceptions.printStackTrace(ex);
            } 
        };
    }

    private List<String> entityScriptFiles;
    private List<String> scriptFiles;
    private final static String MODULE_JS = "app/app.module.js";
    
    public abstract String getTemplatePath();
    
    @Override
    public void execute() throws IOException {
        entityScriptFiles = new ArrayList<>();
        scriptFiles = new ArrayList<>();
        generateClientSideComponent();
    }
    
    protected void generateClientSideComponent() {
        try {
            
            NGApplicationConfig applicationConfig = getAppConfig();
            ApplicationSourceFilter fileFilter = new ApplicationSourceFilter(applicationConfig);
            
            handler.append(Console.wrap(AngularGenerator.class, "MSG_Copying_Entity_Files", FG_RED, BOLD, UNDERLINE));
            Map<String, String> templateLib = getResource(getTemplatePath() + "entity-include-resources.zip");
            List<NGEntity> entities = new ArrayList<>();
            for (Entity entity : entityMapping.getConcreteEntity().collect(toList())) {
                NGEntity ngEntity = getEntity(entity);
                if (ngEntity != null) {
                    entities.add(ngEntity);
                    generateNgEntity(applicationConfig, fileFilter, getEntityConfig(), ngEntity, templateLib);
                    generateNgEntityi18nResource(applicationConfig, fileFilter, ngEntity);
                }
            }
            applicationConfig.setEntities(entities);
            
            generateNgApplication(applicationConfig, fileFilter);
            generateNgApplicationi18nResource(applicationConfig, fileFilter);
            generateNgLocaleResource(applicationConfig, fileFilter);
            generateNgHome(applicationConfig, fileFilter);
            
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected void generateNgHome(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter) throws IOException {
        FileObject webRoot = getProjectWebRoot(project);

        Map<String, Object> data = new HashMap();
        data.put("entityScriptFiles", entityScriptFiles);
        scriptFiles.remove(MODULE_JS);
        scriptFiles.add(0, MODULE_JS);
        data.put("scriptFiles", scriptFiles);

        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(data);

        Function<String, String> pathResolver = (templatePath) -> {
            return templatePath.substring(templatePath.lastIndexOf("/_")+2);// "_index.html" ->  "index.html"
        };

        copyDynamicFile(getParserManager(parser, null),getTemplatePath() +  "_index.html", webRoot, pathResolver, handler);
        copyDynamicFile(getParserManager(parser, null), getTemplatePath() + "_bower.json", project.getProjectDirectory(), pathResolver, handler);
        handler.append(Console.wrap(AngularGenerator.class, "MSG_Copying_Bower_Lib_Files", FG_RED, BOLD));
        FileUtil.copyStaticResource(getTemplatePath() + "bower_components.zip", webRoot, null, handler);

    }

    protected void generateNgApplication(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter) throws IOException {
        handler.append(Console.wrap(AngularGenerator.class, "MSG_Copying_Application_Files", FG_RED, BOLD, UNDERLINE));
        FileObject webRoot = getProjectWebRoot(project);

        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);

        Function<String, String> pathResolver = (templatePath) -> {
            String simpleFileName = getSimpleFileNameWithExt(templatePath);
            String ext = getFileExt(templatePath);
            
            if (!templatePath.startsWith("app")) {
                if (!fileFilter.isEnable(templatePath)) {
                    return null;
                }
            } else if (!fileFilter.isEnable(simpleFileName)) {
                return null;
            }
            if (templatePath.contains("/_")) {
                templatePath = templatePath.replaceAll("/_", "/");
            }
            if ("js".equals(ext)) {
                scriptFiles.add(templatePath);
            }
            return templatePath;
        };
        copyDynamicResource(getParserManager(parser, null), getTemplatePath() + "web-resources.zip", webRoot, pathResolver, handler);
    }

    protected void generateNgEntity(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter,
            EntityConfig config, NGEntity entity, Map<String, String> templateLib) throws IOException {
        FileObject webRoot = getProjectWebRoot(project);
        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(entity);
        parser.addContext(config);

        Function<String, String> pathResolver = (templatePath) -> {
            String simpleFileName = getSimpleFileNameWithExt(templatePath);
            String ext = templatePath.substring(templatePath.lastIndexOf('.') + 1);
            if (!fileFilter.isEnable(simpleFileName)) {
                return null;
            }
            if (templatePath.contains("_entity-management" + ".html")) {
                templatePath = templatePath.replace("_entity-management", entity.getEntityFolderName() + '/' + entity.getEntityPluralFileName());
            } else if (templatePath.contains("services/_entity.service.js")) {
                templatePath = templatePath.replace("services/_entity.service.js",
                        "entities/" + entity.getEntityFolderName() + '/' + entity.getEntityServiceFileName() + ".service.js");
            } else if (templatePath.contains("services/_entity-search.service.js")) {
                templatePath = templatePath.replace("services/_entity-search.service.js",
                        "entities/" + entity.getEntityFolderName() + '/' + entity.getEntityServiceFileName() + ".search.service.js");
            } else {
                templatePath = templatePath.replace("_entity-management", entity.getEntityFolderName() + '/' + entity.getEntityFileName());
            }

            if ("js".equals(ext)) {
                entityScriptFiles.add(templatePath);
            }
            return templatePath;
        };
        copyDynamicResource(getParserManager(parser, templateLib), getTemplatePath() + "entity-resources.zip", webRoot, pathResolver, handler);
    }

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
        copyDynamicResource(getParserManager(parser, null), getTemplatePath() + "entity-resource-i18n.zip", webRoot, pathResolver, handler);
    }

    protected void generateNgApplicationi18nResource(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter) throws IOException {
        FileObject webRoot = getProjectWebRoot(project);

        Map<String, Object> data = new HashMap();//todo remove

        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(data);

        Function<String, String> pathResolver = (templatePath) -> {
            String lang = templatePath.split("/")[1]; //"i18n/en/password.json" 
            if (!applicationConfig.getLanguages().contains(lang)) { //if lang selected by dev 
                return null;
            }
            if (templatePath.contains("/_")) {
                templatePath = templatePath.replaceAll("/_", "/");
            }
            //path modification not required
            return templatePath;
        };
        copyDynamicResource(getParserManager(parser, null), getTemplatePath() + "web-resources-i18n.zip", webRoot, pathResolver, handler);
    }
    
    protected void generateNgLocaleResource(NGApplicationConfig applicationConfig, ApplicationSourceFilter fileFilter) throws IOException {
        FileObject webRoot = getProjectWebRoot(project);

        Map<String, Object> data = new HashMap();//todo remove

        EJSParser parser = new EJSParser();
        parser.addContext(applicationConfig);
        parser.addContext(data);

        Function<String, String> pathResolver = (templatePath) -> {
            String lang = templatePath.substring(templatePath.indexOf('_')+1, templatePath.lastIndexOf('.')); //angular-locale_en.js 
            if (!applicationConfig.getLanguages().contains(lang)) { //if lang selected by dev 
                return null;
            }
            //path modification not required
            return templatePath;
        };
        copyDynamicResource(getParserManager(parser, null), getTemplatePath() + "angular-locale.zip", webRoot, pathResolver, handler);
    }

    protected EntityConfig getEntityConfig() {
        EntityConfig entityConfig = new EntityConfig();
        entityConfig.setPagination(ngData.getPagination().getKeyword());
        return entityConfig;
    }

    protected NGApplicationConfig getAppConfig() {
        NGApplicationConfig applicationConfig = new NGApplicationConfig();
        applicationConfig.setAngularAppName(ngData.getModule());
        applicationConfig.setEnableTranslation(true);
        applicationConfig.setJhiPrefix("jhi");
        applicationConfig.setBaseName(ngData.getApplicationTitle());
        applicationConfig.setApplicationPath(restData.getRestConfigData().getApplicationPath());
        applicationConfig.setEnableMetrics(restData.isMetrics());
        applicationConfig.setRestPackage(restData.getPackage());
        applicationConfig.setEnableDocs(restData.isDocsEnable());
        return applicationConfig;
    }

    private NGEntity getEntity(Entity entity) {
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
        List<Attribute> attributes = entity.getAttributes().getAllAttribute();
//Uncomment for inheritance support
//        if(entitySpec.getSubclassList().size() > 1){
//            return null;
//        }
//        attributes.addAll(entitySpec.getSuperclassAttributes());
        for(Attribute attribute : attributes){
            if(attribute instanceof Id && ((Id)attribute).isGeneratedValue()){
                continue;
            }
            if(!attribute.getIncludeInUI()){//system attribute
                continue;
            }
            
            if(attribute instanceof RelationAttribute){
                RelationAttribute relationAttribute = (RelationAttribute)attribute;
                NGRelationship relationship = new NGRelationship(entity, relationAttribute);
                Entity mappedEntity = relationAttribute.getConnectedEntity();
                if(mappedEntity.getLabelAttribute()==null || mappedEntity.getLabelAttribute().getName().equals("id")){
                    handler.warning(NbBundle.getMessage(AngularGenerator.class, "TITLE_Entity_Label_Missing"),
                    NbBundle.getMessage(AngularGenerator.class, "MSG_Entity_Label_Missing", ngEntity.getName()));
                } else {
                    relationship.setOtherEntityField(mappedEntity.getLabelAttribute().getName());
                }
                if(entity == mappedEntity){
                    handler.warning(NbBundle.getMessage(AngularGenerator.class, "TITLE_Self_Relation_Not_Supported"),
                    NbBundle.getMessage(AngularGenerator.class, "MSG_Self_Relation_Not_Supported", attribute.getName(), ngEntity.getName()));
                    continue;
                }
                ngEntity.addRelationship(relationship);
            } else if(attribute instanceof BaseAttribute){
                if(attribute instanceof EnumTypeHandler && ((EnumTypeHandler)attribute).getEnumerated()!=null){
                    handler.warning(NbBundle.getMessage(AngularGenerator.class, "TITLE_Enum_Type_Not_Supported"),
                    NbBundle.getMessage(AngularGenerator.class, "MSG_Enum_Type_Not_Supported", attribute.getName(), ngEntity.getName()));
                    continue;
                }
                if(attribute instanceof Embedded){
                    handler.warning(NbBundle.getMessage(AngularGenerator.class, "TITLE_Embedded_Type_Not_Supported"),
                    NbBundle.getMessage(AngularGenerator.class, "MSG_Embedded_Type_Not_Supported", attribute.getName(), ngEntity.getName()));
                    continue;
                }
                if(attribute instanceof ElementCollection){
                    handler.warning(NbBundle.getMessage(AngularGenerator.class, "TITLE_ElementCollection_Type_Not_Supported"),
                    NbBundle.getMessage(AngularGenerator.class, "MSG_ElementCollection_Type_Not_Supported", attribute.getName(), ngEntity.getName()));
                    continue;
                }
                if(attribute instanceof Version || attribute instanceof Transient){
                    continue;
                }
                NGField field = new NGField((BaseAttribute)attribute);
                Class<?> primitiveType = JavaUtil.getPrimitiveType(attribute.getDataTypeLabel());
                if(primitiveType!=null){
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
