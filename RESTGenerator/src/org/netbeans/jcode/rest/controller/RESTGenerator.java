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
package org.netbeans.jcode.rest.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang.StringUtils.EMPTY;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.cdi.logger.LoggerProducerGenerator;
import org.netbeans.jcode.cdi.util.CDIUtil;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import static org.netbeans.jcode.core.util.AttributeType.isPrimitive;
import static org.netbeans.jcode.core.util.Constants.JAVA_EXT;
import org.netbeans.jcode.core.util.FileUtil;
import org.netbeans.jcode.core.util.POMManager;
import org.netbeans.jcode.core.util.PersistenceUtil;
import static org.netbeans.jcode.core.util.PersistenceUtil.addProperty;
import org.netbeans.jcode.core.util.ProjectHelper;
import org.netbeans.jcode.ejb.facade.SessionBeanData;
import org.netbeans.jcode.rest.util.RestUtils;
import org.netbeans.jcode.core.util.SourceGroupSupport;
import static org.netbeans.jcode.core.util.StringHelper.firstLower;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import static org.netbeans.jcode.core.util.StringHelper.getMethodName;
import static org.netbeans.jcode.core.util.StringHelper.kebabCase;
import static org.netbeans.jcode.core.util.StringHelper.startCase;
import org.netbeans.jcode.ejb.facade.EjbFacadeGenerator;
import static org.netbeans.jcode.generator.internal.util.Util.pluralize;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Technology;
import static org.netbeans.jcode.layer.Technology.Type.CONTROLLER;
import org.netbeans.jcode.rest.filter.RESTFilterGenerator;
import org.netbeans.jcode.rest.util.RestApp;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.modules.j2ee.persistence.dd.common.PersistenceUnit;
import org.netbeans.modules.j2ee.persistence.dd.common.Property;
import org.netbeans.modules.j2ee.persistence.provider.InvalidPersistenceXmlException;
import org.netbeans.modules.j2ee.persistence.provider.ProviderUtil;
import org.netbeans.modules.j2ee.persistence.unit.PUDataObject;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.netbeans.jpa.modeler.spec.*;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
//import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.layer.Generator;
/**
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(type = CONTROLLER, label = "REST", panel = RESTPanel.class, parents = {EjbFacadeGenerator.class})

public class RESTGenerator implements Generator {

    private static final String TEMPLATE = "org/netbeans/jcode/template/";

    @ConfigData
    private ApplicationConfigData applicationConfigData;

    @ConfigData
    private SessionBeanData beanData;

    @ConfigData
    private RESTData restData;

    @ConfigData
    private Project project;

    @ConfigData
    private SourceGroup source;

    @ConfigData
    private EntityMappings entityMapping;
    
    @ConfigData
    private ProgressHandler handler;
    
    private String entityPackage;
    
    private List<Template> CONFIG_TEMPLATES, ENTITY_TEMPLATES,
            ENTITY_LISTENER_TEMPLATES, FACADE_TEMPLATES,
            SERVICE_TEMPLATES, CONTROLLER_TEMPLATES,
            CONTROLLER_EXT_TEMPLATES, METRICS_TEMPLATES;
    
    private void registerTemplates() {
        
        CONFIG_TEMPLATES = new ArrayList<>();
        ENTITY_TEMPLATES = new ArrayList<>();
        ENTITY_LISTENER_TEMPLATES = new ArrayList<>();
        FACADE_TEMPLATES = new ArrayList<>();
        SERVICE_TEMPLATES = new ArrayList<>();
        CONTROLLER_TEMPLATES = new ArrayList<>();
        CONTROLLER_EXT_TEMPLATES = new ArrayList<>();
        METRICS_TEMPLATES = new ArrayList<>();

        ENTITY_TEMPLATES.add(new Template("entity/AbstractAuditingEntity.java.ftl", "AbstractAuditingEntity"));
        ENTITY_TEMPLATES.add(new Template("entity/Authority.java.ftl", "Authority"));
        ENTITY_TEMPLATES.add(new Template("entity/User.java.ftl", "User"));
        
        ENTITY_LISTENER_TEMPLATES.add(new Template("entity/AuditListner.java.ftl", "AuditListner"));

        FACADE_TEMPLATES.add(new Template("facade/AuthorityFacade.java.ftl", "Authority"));
        FACADE_TEMPLATES.add(new Template("facade/UserFacade.java.ftl", "User"));

        CONFIG_TEMPLATES.add(new Template("config/ConfigResource.java.ftl", "ConfigResource", "config"));
        CONFIG_TEMPLATES.add(new Template("config/Constants.java.ftl", "Constants", "config"));
        CONFIG_TEMPLATES.add(new Template("config/MailConfig.java.ftl", "MailConfig", "config"));
        CONFIG_TEMPLATES.add(new Template("config/MessageResource.java.ftl", "MessageResource", "config"));
        CONFIG_TEMPLATES.add(new Template("config/SecurityConfig.java.ftl", "SecurityConfig", "config"));

        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/util/HeaderUtil.java.ftl", "HeaderUtil", "util"));
        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/util/Page.java.ftl", "Page", "util"));
        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/util/PaginationUtil.java.ftl", "PaginationUtil", "util"));

        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/dto/KeyAndPasswordDTO.java.ftl", "KeyAndPasswordDTO", "dto"));
        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/dto/LoginDTO.java.ftl", "LoginDTO", "dto"));
        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/dto/ManagedUserDTO.java.ftl", "ManagedUserDTO", "dto"));
        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/dto/UserDTO.java.ftl", "UserDTO", "dto"));

        if(restData.isMetrics()){
        METRICS_TEMPLATES.add(new Template("config/MetricsConfig.java.ftl", "MetricsConfig", "config"));
        METRICS_TEMPLATES.add(new Template("metrics/DiagnosticFilter.java.ftl", "DiagnosticFilter", "metrics"));
        METRICS_TEMPLATES.add(new Template("metrics/MetricsConfigurer.java.ftl", "MetricsConfigurer", "metrics"));//require  import MetricsConfig
        }
        
        SERVICE_TEMPLATES.add(new Template("security/Secured.java.ftl", "Secured", "security"));
        SERVICE_TEMPLATES.add(new Template("security/AuthenticationException.java.ftl", "AuthenticationException", "security"));
        SERVICE_TEMPLATES.add(new Template("security/AuthoritiesConstants.java.ftl", "AuthoritiesConstants", "security"));
        SERVICE_TEMPLATES.add(new Template("security/JWTAuthenticationFilter.java.ftl", "JWTAuthenticationFilter", "security"));
        SERVICE_TEMPLATES.add(new Template("security/JWTToken.java.ftl", "JWTToken", "security"));
        SERVICE_TEMPLATES.add(new Template("security/PasswordEncoder.java.ftl", "PasswordEncoder", "security"));
        SERVICE_TEMPLATES.add(new Template("security/SecurityUtils.java.ftl", "SecurityUtils", "security"));
        SERVICE_TEMPLATES.add(new Template("security/TokenProvider.java.ftl", "TokenProvider", "security"));
        SERVICE_TEMPLATES.add(new Template("security/UserAuthenticationToken.java.ftl", "UserAuthenticationToken", "security"));

        SERVICE_TEMPLATES.add(new Template("service/mail/TemplateEngineProvider.java.ftl", "TemplateEngineProvider", "service.mail"));
        SERVICE_TEMPLATES.add(new Template("service/mail/MailService.java.ftl", "MailService", "service.mail"));
        SERVICE_TEMPLATES.add(new Template("util/RandomUtil.java.ftl", "RandomUtil", "util"));
        SERVICE_TEMPLATES.add(new Template("service/UserService.java.ftl", "UserService", "service"));
        
        if(restData.isMetrics() || restData.isDocsEnable()){
            SERVICE_TEMPLATES.add(new Template("web/WebConfigurer.java.ftl", "WebConfigurer", "web"));
        }
        CONTROLLER_TEMPLATES.add(new Template("rest/AccountController.java.ftl", "Account"));
        CONTROLLER_TEMPLATES.add(new Template("rest/UserController.java.ftl", "User"));
        CONTROLLER_TEMPLATES.add(new Template("rest/UserJWTController.java.ftl", "UserJWT"));
        
}

    @Override
    public void execute() throws IOException {
        handler.progress(Console.wrap(RESTGenerator.class, "MSG_Progress_Now_Generating", FG_RED, BOLD, UNDERLINE));
        generateUtil();
        entityPackage = entityMapping.getPackage();
        Map<String, Object> param = generateServerSideComponent();
        
        for(Entity entity: entityMapping.getConcreteEntity()) {
            generateEntityController(entity, param);
        }
        CDIUtil.createDD(project);
        addMavenDependencies("pom/rest/_pom.xml");
        if(restData.isMetrics()){
          addMavenDependencies("pom/metrics/_pom.xml"); 
        }
        if(restData.isDocsEnable()){
          addMavenDependencies("pom/docs/_pom.xml"); 
        }
    }

    
    private void addMavenDependencies(String pom) {
        if(POMManager.isMavenProject(project)){
            POMManager pomManager = new POMManager(TEMPLATE + pom, project);
            pomManager.setSourceVersion("1.8");
            pomManager.execute();
            pomManager.commit();
        } else {
            handler.warning(NbBundle.getMessage(RESTGenerator.class, "TITLE_Maven_Project_Not_Found"),
                    NbBundle.getMessage(RESTGenerator.class, "MSG_Maven_Project_Not_Found"));
        }
    }
    
    public FileObject generateEntityController(final Entity entity, Map<String, Object> appParam) throws IOException {
        FileObject controllerFO = null;
        String entityFQN = entity.getFQN(entityMapping.getPackage());
        boolean overrideExisting = true, dto = false;
        final String entitySimpleName = entity.getClazz();

        String facadeFileName = beanData.getPrefixName() + entitySimpleName + beanData.getSuffixName();
        String fqFacadeFileName = entity.getPackage(beanData.getPackage()) + '.' + facadeFileName;

        String controllerFileName = restData.getPrefixName() + entitySimpleName + restData.getSuffixName();
        handler.progress(controllerFileName);

        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(source, entity.getPackage(restData.getPackage()), true);

        controllerFO = targetFolder.getFileObject(controllerFileName, JAVA_EXT);

        if (controllerFO != null) {
            if (overrideExisting) {
                controllerFO.delete();
            } else {
                throw new IOException("File already exists exception: " + controllerFO.getPath());
            }
        }
        String entityClass = firstUpper(entitySimpleName);
        String entityInstance = firstLower(entitySimpleName);
        String entityNameSpinalCased = kebabCase(entityInstance);

        Map<String, Object> param = new HashMap<>(appParam);
        param.put("EntityClass", entityClass);
        param.put("EntityClassPlural", pluralize(firstUpper(entitySimpleName)));
        param.put("EntityClass_FQN", entityFQN);
        param.put("entityInstance", entityInstance);
        param.put("entityInstancePlural", pluralize(entityInstance));

        param.put("controllerClass", controllerFileName);
        param.put("controllerClassHumanized", startCase(controllerFileName));
        param.put("entityApiUrl", entityNameSpinalCased);

        param.put("EntityFacade", facadeFileName);
        param.put("entityFacade", firstLower(facadeFileName));
        param.put("EntityFacade_FQN", fqFacadeFileName);

        param.put("instanceType", dto ? entityClass + "DTO" : entityClass);
        param.put("instanceName", dto ? entityInstance + "DTO" : entityInstance);

        param.put("pagination", restData.isPagination()?"yes":"no");
        param.put("fieldsContainNoOwnerOneToOne", false);

        Attribute idAttribute = entity.getAttributes().getIdField();
        
        if(idAttribute!=null){
            String dataType_FQN = idAttribute.getDataTypeLabel();
            param.put("pkName", firstLower(idAttribute.getName()));
            param.put("pkGetter", "get"+ getMethodName(idAttribute.getName()));
            param.put("pkType", dataType_FQN);
            param.put("isPKPrimitive", isPrimitive(dataType_FQN));
        } 
        
        String restTemplate = "EntityController" ;
//        if(idAttribute instanceof Id){
        if(idAttribute instanceof EmbeddedId || idAttribute instanceof DefaultAttribute){
            restTemplate = "CompositePKEntityController";
            param.put("EntityPKClass_FQN", entity.getPackage(entityMapping.getPackage()) + '.' + idAttribute.getDataTypeLabel());
            DefaultClass defaultClass = null;
            if (idAttribute instanceof EmbeddedId) {
                EmbeddedId embeddedId = (EmbeddedId) idAttribute;
                defaultClass = embeddedId.getConnectedClass();
            } else if (idAttribute instanceof DefaultAttribute) {
                 defaultClass = entityMapping.findDefaultClass(((DefaultAttribute)idAttribute).getAttributeType());
            }
             List<DefaultAttribute> attributes = defaultClass.getAttributes();
                StringBuilder restParamList = new StringBuilder();
                StringBuilder restParamNameList = new StringBuilder();
                StringBuilder restDocList = new StringBuilder();
                for (DefaultAttribute attribute : attributes) {
                    restParamList.append(String.format("@MatrixParam(\"%s\") %s %s,", attribute.getName(), attribute.getDataTypeLabel(), attribute.getName()));
                    restParamNameList.append(attribute.getName()).append(',');
                    restDocList.append(String.format("@param %s the %s of the %s", attribute.getName(), attribute.getName(), param.get("instanceName"))).append('\n');
                }
                restParamList.setLength(restParamList.length() - 1);
                restParamNameList.setLength(restParamNameList.length() - 1);
                restDocList.setLength(restDocList.length() - 1);
                param.put("restParamList", restParamList);
                param.put("restParamNameList", restParamNameList);
                param.put("restDocList", restDocList);
        }
        
        param.put("package", entity.getPackage(restData.getPackage()));
        param.put("applicationPath", restData.getRestConfigData().getApplicationPath());
        param.put("metrics", restData.isMetrics());
        param.put("docs", restData.isDocsEnable());

        
        FileUtil.expandTemplate(TEMPLATE + "rest/entity/"+ restTemplate +".java.ftl", targetFolder, controllerFileName + '.' + JAVA_EXT, param);
        return controllerFO;
    }

    private Map<String, Object> generateServerSideComponent() throws IOException {
        registerTemplates();
        
        Map<String, Object> param = new HashMap<>();
        String appPackage = restData.getAppPackage();

        param.put("entityPackage", entityPackage);
        param.put("PU", applicationConfigData.getPersistenceUnitName());
        param.put("applicationPath", restData.getRestConfigData().getApplicationPath());

        param.put("servicePackage", appPackage);
        param.put("facadePackage", beanData.getPackage());
        param.put("restPackage", restData.getPackage());

        param.put("beanPrefix", beanData.getPrefixName());
        param.put("beanSuffix", beanData.getSuffixName());

        param.put("restPrefix", restData.getPrefixName());
        param.put("restSuffix", restData.getSuffixName());
        param.put("metrics", restData.isMetrics());
        param.put("docs", restData.isDocsEnable());

        //config
        expandServerSideComponent(source, appPackage, EMPTY, EMPTY, CONFIG_TEMPLATES, param);
        //entity
        expandServerSideComponent(source, entityPackage, EMPTY, EMPTY, ENTITY_TEMPLATES, param);
        //contoller ext
        expandServerSideComponent(source, restData.getPackage(), EMPTY, EMPTY, CONTROLLER_EXT_TEMPLATES, param);
        //facade
        expandServerSideComponent(source, beanData.getPackage(), beanData.getPrefixName(), beanData.getSuffixName(), FACADE_TEMPLATES, param);
        //metrics
        expandServerSideComponent(source, appPackage, EMPTY, EMPTY, METRICS_TEMPLATES, param);
        //service
        expandServerSideComponent(source, appPackage, EMPTY, EMPTY, SERVICE_TEMPLATES, param);
        //entity
        expandServerSideComponent(source, entityPackage, EMPTY, EMPTY, ENTITY_LISTENER_TEMPLATES, param);
        //controller
        expandServerSideComponent(source, restData.getPackage(), restData.getPrefixName(), restData.getSuffixName(), CONTROLLER_TEMPLATES, param);
        
        FileObject configRoot = ProjectHelper.getResourceDirectory(project);
        if(configRoot==null){//non-maven project
            configRoot = source.getRootFolder();
        }
        FileUtil.copyStaticResource(TEMPLATE + "config/config-resources.zip", configRoot, null, handler);

        updatePersistenceXml(Arrays.asList(entityPackage + ".User", entityPackage + ".Authority"));

        return param;
    }

    private void updatePersistenceXml(List<String> classNames) {
        try {
            String puName = applicationConfigData.getPersistenceUnitName();
            PUDataObject pud = ProviderUtil.getPUDataObject(project);
            Optional<PersistenceUnit> punitOptional = PersistenceUtil.getPersistenceUnit(project, puName);
            if (punitOptional.isPresent()) {
                PersistenceUnit punit = punitOptional.get();
                String SCHEMA_GEN_ACTION = "javax.persistence.schema-generation.database.action";
                String DROP_CREATE = "drop-and-create";
                String SQL_LOAD_SCRIPT = "javax.persistence.sql-load-script-source";
                for(Property property : punit.getProperties().getProperty2()){
                    if(property.getName() == null){
                        punit.getProperties().removeProperty2(property);
                    }
                }
                addProperty(punit, SCHEMA_GEN_ACTION, DROP_CREATE);
                addProperty(punit, SQL_LOAD_SCRIPT, "META-INF/sql/insert.sql");

                classNames.stream().forEach((entityClass) -> {
                    pud.addClass(punit, entityClass, false);
                });
                pud.save();
            }
        } catch (InvalidPersistenceXmlException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    


    private void expandServerSideComponent(SourceGroup targetSourceGroup, String _package, String prefixName, String suffixName, List<Template> templates, Map<String, Object> param) {
        String fileName = null;
        try {
            if(templates!=null){
                for (Template template : templates) {
                    String templatePackage = _package;
                    fileName = prefixName + template.getFileName() + suffixName;
                    if (StringUtils.isNotBlank(template.getPackageSuffix())) {
                        templatePackage = templatePackage + '.' + template.getPackageSuffix();
                    } 
                    param.put("package", templatePackage);
                    String templateFile = template.getPath().substring(template.getPath().lastIndexOf('/') + 1, template.getPath().indexOf('.'));
                    param.put(templateFile, fileName);
                    if (prefixName != null || suffixName != null) {
                        param.put(firstLower(templateFile), firstLower(fileName));
                    }
                    param.put(templateFile + "_FQN", templatePackage + '.' + fileName);
                    FileObject targetFolder = SourceGroupSupport.getFolderForPackage(targetSourceGroup, (String) param.get("package"), true);
                    FileUtil.expandTemplate(TEMPLATE + template.getPath(), targetFolder, fileName + '.' + JAVA_EXT, param);
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            System.out.println("InputResource : " + _package + '.' + fileName);
        }
    }

    public void generateUtil() throws IOException {
        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(source, restData.getPackage(), true);
        List<RestApp> singletonClasses = new ArrayList<>();
        List<String> providerClasses = new ArrayList<>();
        singletonClasses.add(RestApp.JACKSON);
        if(restData.isMetrics()){
            singletonClasses.add(RestApp.METRICS);    
        }
        if (restData.isDocsEnable()) {
            providerClasses.add("com.wordnik.swagger.jaxrs.listing.ApiListingResource");//not req
            providerClasses.add("com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider");
            providerClasses.add("com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON");
            providerClasses.add("com.wordnik.swagger.jaxrs.listing.ResourceListingProvider");
        }
        
        generateApplicationConfig(singletonClasses, providerClasses);
        if (!restData.getFilterTypes().isEmpty()) {
            String UTIL_PACKAGE = "util";
            FileObject utilFolder = SourceGroupSupport.getFolderForPackage(targetFolder, UTIL_PACKAGE, true);
            LoggerProducerGenerator.generate(utilFolder, handler);
            RESTFilterGenerator.generate(project, source, utilFolder, restData.getFilterTypes(), handler);
        }

    }

    public void generateApplicationConfig(List<RestApp> singletonClasses, List<String> providerClasses) throws IOException {
        if (restData.getRestConfigData() == null) {
            return;
        }
        final RestSupport restSupport = project.getLookup().lookup(RestSupport.class);
        RestSupport.RestConfig.IDE.setAppClassName(restData.getRestConfigData().getPackage() + "." + restData.getRestConfigData().getApplicationClass());
        if (restSupport != null) {
            try {
                restSupport.ensureRestDevelopmentReady(RestSupport.RestConfig.IDE);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        FileObject restAppPack = null;
        try {
            restAppPack = SourceGroupSupport.getFolderForPackage(source, restData.getRestConfigData().getPackage(), true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        try {
            if (restAppPack != null && restData.getRestConfigData().getApplicationClass() != null) {
                RestUtils.createApplicationConfigClass(restSupport, restData.getRestConfigData(), 
                        restAppPack, restData.getAppPackage(), singletonClasses, providerClasses, handler);
            }
            RestUtils.disableRestServicesChangeListner(project);
            restSupport.configure("JPA Modeler - REST support");
        } catch (Exception iox) {
            Exceptions.printStackTrace(iox);
        } finally {
            RestUtils.enableRestServicesChangeListner(project);
        }
    }


}
