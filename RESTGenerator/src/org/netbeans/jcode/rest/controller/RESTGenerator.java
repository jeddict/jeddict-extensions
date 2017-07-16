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
import java.util.Collections;
import static java.util.Collections.EMPTY_MAP;
import static java.util.Collections.singletonMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang.StringUtils.EMPTY;
import org.jcode.docker.generator.DockerConfigData;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import static org.netbeans.bean.validation.constraints.ConstraintUtil.getAttributeDefaultValue;
import static org.netbeans.bean.validation.constraints.ConstraintUtil.getAttributeUpdateValue;
import org.netbeans.jcode.cdi.logger.LoggerProducerGenerator;
import org.netbeans.jcode.cdi.util.CDIUtil;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import static org.netbeans.jcode.core.util.AttributeType.isBoolean;
import static org.netbeans.jcode.core.util.AttributeType.isPrimitive;
import static org.netbeans.jcode.core.util.Constants.JAVA_EXT;
import org.netbeans.jcode.core.util.FileUtil;
import static org.netbeans.jcode.core.util.FileUtil.expandTemplate;
import org.netbeans.jcode.core.util.JavaIdentifiers;
import org.netbeans.jcode.core.util.POMManager;
import static org.netbeans.jcode.core.util.PersistenceUtil.addClasses;
import static org.netbeans.jcode.core.util.PersistenceUtil.addProperty;
import static org.netbeans.jcode.core.util.PersistenceUtil.getPersistenceUnit;
import static org.netbeans.jcode.core.util.PersistenceUtil.updatePersistenceUnit;
import org.netbeans.jcode.core.util.ProjectHelper;
import org.netbeans.jcode.rest.util.RestUtils;
import org.netbeans.jcode.core.util.SourceGroupSupport;
import static org.netbeans.jcode.core.util.SourceGroupSupport.getFolderForPackage;
import static org.netbeans.jcode.core.util.StringHelper.firstLower;
import static org.netbeans.jcode.core.util.StringHelper.firstUpper;
import static org.netbeans.jcode.core.util.StringHelper.getMethodName;
import static org.netbeans.jcode.core.util.StringHelper.kebabCase;
import static org.netbeans.jcode.core.util.StringHelper.pluralize;
import static org.netbeans.jcode.core.util.StringHelper.startCase;
import static org.netbeans.jcode.core.util.StringHelper.toConstant;
import org.netbeans.jcode.layer.ConfigData;
import org.netbeans.jcode.layer.Technology;
import static org.netbeans.jcode.layer.Technology.Type.CONTROLLER;
import org.netbeans.jcode.rest.filter.RESTFilterGenerator;
import org.netbeans.jcode.rest.util.RestApp;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.netbeans.modules.j2ee.persistence.dd.common.PersistenceUnit;
import org.netbeans.modules.j2ee.persistence.dd.common.Property;
import org.netbeans.modules.websvc.rest.spi.RestSupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.netbeans.jpa.modeler.spec.*;
import org.netbeans.jpa.modeler.spec.extend.Attribute;
import org.netbeans.jcode.layer.Generator;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.repository.RepositoryData;
import org.netbeans.jcode.repository.RepositoryGenerator;
import static org.netbeans.jcode.repository.RepositoryGenerator.REPOSITORY_ABSTRACT;

/**
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(type = CONTROLLER, label = "REST", panel = RESTPanel.class, 
        parents = {RepositoryGenerator.class}, listIndex = 1)
public class RESTGenerator implements Generator {

    private static final String TEMPLATE = "org/netbeans/jcode/template/";

    @ConfigData
    private RepositoryData repositoryData;

    @ConfigData
    private RESTData restData;

    @ConfigData
    private DockerConfigData dockerConfigData;

    @ConfigData
    private Project project;

    @ConfigData
    private SourceGroup source;

    @ConfigData
    private EntityMappings entityMapping;
    
    @ConfigData
    private ApplicationConfigData appConfigData;

    @ConfigData
    private ProgressHandler handler;

    private SourceGroup testSource;
    private String entityPackage;

    private List<Template> CONFIG_TEMPLATES, ENTITY_TEMPLATES,
            ENTITY_LISTENER_TEMPLATES, 
            REPOSITORY_TEMPLATES, SERVICE_TEMPLATES, 
            CONTROLLER_TEMPLATES, CONTROLLER_EXT_TEMPLATES, 
            METRICS_TEMPLATES, LOGGER_TEMPLATES, 
            TEST_CASE_TEMPLATES, TEST_CASE_CONTROLLER_TEMPLATES;

    private void registerTemplates() {

        CONFIG_TEMPLATES = new ArrayList<>();
        ENTITY_TEMPLATES = new ArrayList<>();
        ENTITY_LISTENER_TEMPLATES = new ArrayList<>();
        REPOSITORY_TEMPLATES = new ArrayList<>();
        SERVICE_TEMPLATES = new ArrayList<>();
        CONTROLLER_TEMPLATES = new ArrayList<>();
        CONTROLLER_EXT_TEMPLATES = new ArrayList<>();
        METRICS_TEMPLATES = new ArrayList<>();
        LOGGER_TEMPLATES = new ArrayList<>();
        TEST_CASE_TEMPLATES = new ArrayList<>();
        TEST_CASE_CONTROLLER_TEMPLATES = new ArrayList<>();

        ENTITY_TEMPLATES.add(new Template("entity/AbstractAuditingEntity.java.ftl", "AbstractAuditingEntity"));
        ENTITY_TEMPLATES.add(new Template("entity/Authority.java.ftl", "Authority"));
        ENTITY_TEMPLATES.add(new Template("entity/User.java.ftl", "User"));

        ENTITY_LISTENER_TEMPLATES.add(new Template("entity/AuditListner.java.ftl", "AuditListner"));

        REPOSITORY_TEMPLATES.add(new Template("repository/AuthorityRepository.java.ftl", "Authority"));
        REPOSITORY_TEMPLATES.add(new Template("repository/UserRepository.java.ftl", "User"));

        CONFIG_TEMPLATES.add(new Template("config/ConfigResource.java.ftl", "ConfigResource", "config"));
        CONFIG_TEMPLATES.add(new Template("config/Constants.java.ftl", "Constants", "config"));
        CONFIG_TEMPLATES.add(new Template("config/MailConfig.java.ftl", "MailConfig", "config"));
//        CONFIG_TEMPLATES.add(new Template("config/MessageResource.java.ftl", "MessageResource", "config"));
        CONFIG_TEMPLATES.add(new Template("config/SecurityConfig.java.ftl", "SecurityConfig", "config"));

        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/util/HeaderUtil.java.ftl", "HeaderUtil", "util"));
        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/util/Page.java.ftl", "Page", "util"));
        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/util/PaginationUtil.java.ftl", "PaginationUtil", "util"));

        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/dto/KeyAndPasswordDTO.java.ftl", "KeyAndPasswordDTO", "dto"));
        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/dto/LoginDTO.java.ftl", "LoginDTO", "dto"));
        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/dto/ManagedUserDTO.java.ftl", "ManagedUserDTO", "dto"));
        CONTROLLER_EXT_TEMPLATES.add(new Template("rest/dto/UserDTO.java.ftl", "UserDTO", "dto"));

        if (restData.isMetrics()) {
            METRICS_TEMPLATES.add(new Template("config/MetricsConfig.java.ftl", "MetricsConfig", "config"));
            METRICS_TEMPLATES.add(new Template("metrics/DiagnosticFilter.java.ftl", "DiagnosticFilter", "metrics"));
            METRICS_TEMPLATES.add(new Template("metrics/MetricsConfigurer.java.ftl", "MetricsConfigurer", "metrics"));//require  import MetricsConfig
        }
        
        if (restData.isLogger()) {
            LOGGER_TEMPLATES.add(new Template("logger/LoggerVM.java.ftl", "LoggerVM", "dto"));
            LOGGER_TEMPLATES.add(new Template("logger/LogsResource.java.ftl", "LogsResource"));
        }
        
        SERVICE_TEMPLATES.add(new Template("security/Secured.java.ftl", "Secured", "security"));
        SERVICE_TEMPLATES.add(new Template("security/AuthenticationException.java.ftl", "AuthenticationException", "security"));
        SERVICE_TEMPLATES.add(new Template("security/AuthoritiesConstants.java.ftl", "AuthoritiesConstants", "security"));
        SERVICE_TEMPLATES.add(new Template("security/PasswordEncoder.java.ftl", "PasswordEncoder", "security"));
        SERVICE_TEMPLATES.add(new Template("security/SecurityUtils.java.ftl", "SecurityUtils", "security"));
        SERVICE_TEMPLATES.add(new Template("security/UserAuthenticationToken.java.ftl", "UserAuthenticationToken", "security"));

        SERVICE_TEMPLATES.add(new Template("security/TokenProvider.java.ftl", "TokenProvider", "security.jwt"));
        SERVICE_TEMPLATES.add(new Template("security/JWTAuthenticationFilter.java.ftl", "JWTAuthenticationFilter", "security.jwt"));
        SERVICE_TEMPLATES.add(new Template("security/JWTToken.java.ftl", "JWTToken", "security.jwt"));

        SERVICE_TEMPLATES.add(new Template("service/mail/TemplateEngineProvider.java.ftl", "TemplateEngineProvider", "service.mail"));
        SERVICE_TEMPLATES.add(new Template("service/mail/MailService.java.ftl", "MailService", "service.mail"));
        SERVICE_TEMPLATES.add(new Template("util/RandomUtil.java.ftl", "RandomUtil", "util"));
        SERVICE_TEMPLATES.add(new Template("service/UserService.java.ftl", "UserService", "service"));

        if (restData.isMetrics() || restData.isDocsEnable()) {
            SERVICE_TEMPLATES.add(new Template("web/WebConfigurer.java.ftl", "WebConfigurer", "web"));
        }
        CONTROLLER_TEMPLATES.add(new Template("rest/AccountController.java.ftl", "Account"));
        CONTROLLER_TEMPLATES.add(new Template("rest/UserController.java.ftl", "User"));
        CONTROLLER_TEMPLATES.add(new Template("rest/UserJWTController.java.ftl", "UserJWT"));

        if (restData.isTestCase()) {
            TEST_CASE_TEMPLATES.add(new Template("arquillian/AbstractTest.java.ftl", "AbstractTest"));
            TEST_CASE_TEMPLATES.add(new Template("arquillian/ApplicationTest.java.ftl", "ApplicationTest"));
            TEST_CASE_CONTROLLER_TEMPLATES.add(new Template("arquillian/AccountControllerTest.java.ftl", "Account"));
            TEST_CASE_CONTROLLER_TEMPLATES.add(new Template("arquillian/UserControllerTest.java.ftl", "User"));
        }

    }

    @Override
    public void execute() throws IOException {
        testSource = SourceGroupSupport.getTestSourceGroup(project);
        handler.progress(Console.wrap(RESTGenerator.class, "MSG_Progress_Generating_REST", FG_RED, BOLD, UNDERLINE));
        entityPackage = entityMapping.getPackage();
        Map<String, Object> param = new HashMap<>();
            param.putAll(generateServerSideComponent());
        if (appConfigData.isCompleteApplication()) {
            generateUtil();
            CDIUtil.createDD(project);
            generateProducer(param);
            addMavenDependencies("rest/pom/_pom.xml");
            if (restData.isMetrics()) {
                addMavenDependencies("metrics/pom/_pom.xml");
            }
            if (restData.isLogger()) {
                addMavenDependencies("logger/pom/_pom.xml");
            }
            if (restData.isDocsEnable()) {
                addMavenDependencies("docs/pom/_pom.xml");
            }
            if (restData.isTestCase()) {
                addMavenDependencies("arquillian/pom/_pom.xml");
            }
        }
        for (Entity entity : entityMapping.getGeneratedEntity().collect(toList())) {
            generateEntityController(entity, param);
        }
    }

    private FileObject generateProducer(Map<String, Object> appParam) throws IOException {
        String _package = repositoryData.getPackage() + ".producer";
        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(source, _package, true);
        String fileName = "LoggerProducer";
        FileObject afFO = targetFolder.getFileObject(fileName, JAVA_EXT);
        if (afFO == null) {
            handler.progress(fileName);
            afFO = org.netbeans.jcode.core.util.FileUtil.expandTemplate("org/netbeans/jcode/template/service/producer/LoggerProducer.java.ftl", targetFolder, fileName + '.' + JAVA_EXT, Collections.singletonMap("package", _package));
        }
        return afFO;
    }

    private void addMavenDependencies(String pom) {
        if (POMManager.isMavenProject(project)) {
            POMManager pomManager = new POMManager(TEMPLATE + pom, project);
            pomManager.commit();
        } else {
            handler.warning(NbBundle.getMessage(RESTGenerator.class, "TITLE_Maven_Project_Not_Found"),
                    NbBundle.getMessage(RESTGenerator.class, "MSG_Maven_Project_Not_Found"));
        }
    }

    public FileObject generateEntityController(final Entity entity, Map<String, Object> appParam) throws IOException {
        FileObject controllerFO = null;
        String entityFQN = entity.getFQN();
        boolean overrideExisting = true, dto = false;
        final String entitySimpleName = entity.getClazz();

        String repositoryFileName = repositoryData.getPrefixName() + entitySimpleName + repositoryData.getSuffixName();
        String fqRepositoryFileName = entity.getAbsolutePackage(repositoryData.getPackage()) + '.' + repositoryFileName;

        String controllerFileName = restData.getPrefixName() + entitySimpleName + restData.getSuffixName();

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

        param.put("EntityRepository", repositoryFileName);
        param.put("entityRepository", firstLower(repositoryFileName));
        param.put("EntityRepository_FQN", fqRepositoryFileName);

        param.put("instanceType", dto ? entityClass + "DTO" : entityClass);
        param.put("instanceName", dto ? entityInstance + "DTO" : entityInstance);

        param.put("pagination", restData.isPagination() ? "yes" : "no");
        param.put("fieldsContainNoOwnerOneToOne", false);

        Attribute idAttribute = entity.getAttributes().getIdField();

        if (idAttribute != null) {
            String dataType = idAttribute.getDataTypeLabel();
            param.put("pkName", firstLower(idAttribute.getName()));
            param.put("pkGetter", "get" + getMethodName(idAttribute.getName()));
            param.put("pkSetter", "set" + getMethodName(idAttribute.getName()));
            param.put("pkType", dataType);
            param.put("isPKPrimitive", isPrimitive(dataType));
        }

        String restTemplate = "EntityController";
//        if(idAttribute instanceof Id){
        if (idAttribute instanceof EmbeddedId || idAttribute instanceof DefaultAttribute) {
            restTemplate = "CompositePKEntityController";
            param.put("EntityPKClass_FQN", entity.getRootPackage() + '.' + idAttribute.getDataTypeLabel());
            DefaultClass defaultClass = null;
            if (idAttribute instanceof EmbeddedId) {
                EmbeddedId embeddedId = (EmbeddedId) idAttribute;
                defaultClass = embeddedId.getConnectedClass();
            } else if (idAttribute instanceof DefaultAttribute) {
                defaultClass = entityMapping.findDefaultClass(((DefaultAttribute) idAttribute).getAttributeType()).orElse(null);
            }
            List<DefaultAttribute> attributes = defaultClass.getAttributes().getDefaultAttributes();
            StringBuilder restParamList = new StringBuilder();
            StringBuilder restParamNameList = new StringBuilder();
            StringBuilder restDocList = new StringBuilder();
            for (DefaultAttribute attribute : attributes) {
                restParamList.append(String.format("@QueryParam(\"%s\") %s %s,", attribute.getName(), attribute.getDataTypeLabel(), attribute.getName()));
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

        param.put("package", entity.getAbsolutePackage(restData.getPackage()));
        param.put("applicationPath", restData.getRestConfigData().getApplicationPath());
        param.put("metrics", appConfigData.isCompleteApplication() && restData.isMetrics());
        param.put("docs", appConfigData.isCompleteApplication() && restData.isDocsEnable());

        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(source, entity.getAbsolutePackage(restData.getPackage()), true);

        controllerFO = targetFolder.getFileObject(controllerFileName, JAVA_EXT);
        if (controllerFO != null) {
            if (overrideExisting) {
                controllerFO.delete();
            } else {
                throw new IOException("File already exists exception: " + controllerFO.getPath());
            }
        }
        //entity controller 
        handler.progress(controllerFileName);
        expandTemplate(TEMPLATE + "rest/entity/" + restTemplate + ".java.ftl", targetFolder, controllerFileName + '.' + JAVA_EXT, param);

        //entity controller test-case
        if (restData.isTestCase()) {
            Function<Attribute, Map<String, Object>> con = attr -> {
                Map<String, Object> attrConf = new HashMap<>();
                attrConf.put("name", attr.getName());
                attrConf.put("Name", firstUpper(attr.getName()));
                attrConf.put("NAME", toConstant(attr.getName()));
                attrConf.put("setter", "set" + firstUpper(attr.getName()));
                attrConf.put("getter", (isBoolean(attr.getDataTypeLabel()) ? "is" : "get") + firstUpper(attr.getName()));
                attrConf.put("dataType", attr.getDataTypeLabel());
                attrConf.put("defaultValue", getAttributeDefaultValue(attr.getDataTypeLabel(), attr.getAttributeConstraintsMap()));
                attrConf.put("updatedValue", getAttributeUpdateValue(attr.getDataTypeLabel(), attr.getAttributeConstraintsMap()));
//            attrConf.put("array", isArray(attr.getDataTypeLabel()));
//            attrConf.put("precision", isPrecision(attr.getDataTypeLabel()));
//            attrConf.put("precisionType", isDouble(attr.getDataTypeLabel())?'d':'f');
                return attrConf;
            };

            if (idAttribute instanceof DefaultAttribute) {
                param.put("pkStrategy", "IdClass");
            } else if (idAttribute instanceof EmbeddedId) {
                param.put("pkStrategy", "EmbeddedId");
            } else {
                param.put("pkStrategy", "Id");
            }

            List<Map<String, Object>> allIdAttributes = entity.getAttributes().getSuperId().stream()
                    .filter(attr -> getAttributeDefaultValue(attr.getDataTypeLabel()) != null).map(con).collect(toList());
            param.put("allIdAttributes", allIdAttributes);
            param.put("idAttributes", entity.getAttributes().getSuperId().stream().filter(id -> !id.isGeneratedValue())
                    .filter(attr -> getAttributeDefaultValue(attr.getDataTypeLabel()) != null).map(con).collect(toList()));

//            String matrixParam = allIdAttributes.stream().map(attrConf -> String.format("%s={%s}",attrConf.get("name"),attrConf.get("name"))).collect(Collectors.joining(";"));
//            param.put("matrixParam", matrixParam);
            List<Map<String, Object>> basicAttributes = entity.getAttributes().getSuperBasic().stream()
                    .filter(attr -> getAttributeDefaultValue(attr.getDataTypeLabel()) != null && !attr.isOptionalReturnType())
                    .map(con)
                    .collect(toList());
            param.put("attributes", basicAttributes);
            param.put("versionAttributes", entity.getAttributes().getSuperVersion().stream().map(con).collect(toList()));
            Set<String> connectedClasses = entity.getAttributes().getConnectedClass();
            param.put("connectedClasses", connectedClasses.stream().map(jc -> JavaIdentifiers.unqualify(jc)).collect(toList()));
            param.put("connectedFQClasses", connectedClasses);

            String controllerTestFileName = controllerFileName + "Test";
            FileObject targetTestFolder = SourceGroupSupport.getFolderForPackage(testSource, entity.getAbsolutePackage(restData.getPackage()), true);
            controllerFO = targetTestFolder.getFileObject(controllerTestFileName, JAVA_EXT);

            if (controllerFO != null) {
                if (overrideExisting) {
                    controllerFO.delete();
                } else {
                    throw new IOException("File already exists exception: " + controllerFO.getPath());
                }
            }
            handler.progress(controllerTestFileName);
            expandTemplate(TEMPLATE + "arquillian/EntityControllerTest.java.ftl", targetTestFolder, controllerTestFileName + '.' + JAVA_EXT, param);
        }
        return controllerFO;
    }

    private Map<String, Object> generateServerSideComponent() throws IOException {
        registerTemplates();

        Map<String, Object> param = new HashMap<>();
        String appPackage = restData.getAppPackage();

        final String abstractRepository = repositoryData.getPrefixName() + REPOSITORY_ABSTRACT + repositoryData.getSuffixName();
        param.put("cdi", repositoryData.isCDI());
        param.put("named", repositoryData.isNamed());
        
        param.put("AbstractRepository", abstractRepository);
        param.put("AbstractRepository_FQN", repositoryData.getPackage() + '.' + abstractRepository);
        param.put("EntityManagerProducer_FQN", repositoryData.getPackage() + ".producer.EntityManagerProducer");
        param.put("LoggerProducer_FQN", repositoryData.getPackage() + ".producer.LoggerProducer");

        param.put("entityPackage", entityPackage);
        param.put("PU", entityMapping.getPersistenceUnitName());
        param.put("applicationPath", restData.getRestConfigData().getApplicationPath());

        param.put("servicePackage", appPackage);
        param.put("repositoryPackage", repositoryData.getPackage());
        param.put("restPackage", restData.getPackage());

        param.put("beanPrefix", repositoryData.getPrefixName());
        param.put("beanSuffix", repositoryData.getSuffixName());

        param.put("restPrefix", restData.getPrefixName());
        param.put("restSuffix", restData.getSuffixName());
        param.put("metrics", restData.isMetrics());
        param.put("docs", restData.isDocsEnable());
        param.put("serverType", dockerConfigData.getServerType().name());
        param.put("serverFamily", dockerConfigData.getServerType().getFamily().name());

        //config
        expandServerSideComponent(source, appPackage, EMPTY, EMPTY, CONFIG_TEMPLATES, param);
        //entity
        expandServerSideComponent(source, entityPackage, EMPTY, EMPTY, ENTITY_TEMPLATES, param);
        //contoller ext
        expandServerSideComponent(source, restData.getPackage(), EMPTY, EMPTY, CONTROLLER_EXT_TEMPLATES, param);
        //repository
        expandServerSideComponent(source, repositoryData.getPackage(), repositoryData.getPrefixName(), repositoryData.getSuffixName(), REPOSITORY_TEMPLATES, param);
        //metrics
        expandServerSideComponent(source, appPackage, EMPTY, EMPTY, METRICS_TEMPLATES, param);
        //logger
        expandServerSideComponent(source, restData.getPackage(), EMPTY, EMPTY, LOGGER_TEMPLATES, param);
        //service
        expandServerSideComponent(source, appPackage, EMPTY, EMPTY, SERVICE_TEMPLATES, param);
        //entity
        expandServerSideComponent(source, entityPackage, EMPTY, EMPTY, ENTITY_LISTENER_TEMPLATES, param);
        //controller
        expandServerSideComponent(source, restData.getPackage(), restData.getPrefixName(), restData.getSuffixName(), CONTROLLER_TEMPLATES, param);
        //test-case

        if (restData.isTestCase()) {
            expandServerSideComponent(testSource, restData.getPackage(), EMPTY, EMPTY, TEST_CASE_TEMPLATES, param);
            expandServerSideComponent(testSource, restData.getPackage(), restData.getPrefixName(), restData.getSuffixName() + "Test", TEST_CASE_CONTROLLER_TEMPLATES, param);
        }

        if (appConfigData.isCompleteApplication()) {
            FileObject configRoot = ProjectHelper.getResourceDirectory(project);
            if (configRoot == null) {//non-maven project
                configRoot = source.getRootFolder();
            }
            expandTemplate(TEMPLATE + "config/resource/insert.sql.ftl", getFolderForPackage(configRoot, "META-INF.sql", true), "insert.sql", singletonMap("database", dockerConfigData.getDatabaseType() != null ? dockerConfigData.getDatabaseType() : "Derby"));
            FileUtil.copyStaticResource(TEMPLATE + "config/resource/config-resources.zip", configRoot, null, handler);
            updatePersistenceXml(Arrays.asList(entityPackage + ".User", entityPackage + ".Authority"));

            if (restData.isTestCase()) {
                configRoot = ProjectHelper.getTestResourceDirectory(project);
                expandTemplate(TEMPLATE + "arquillian/config/arquillian.xml.ftl", configRoot, "arquillian.xml", EMPTY_MAP);
//              expandTemplate(TEMPLATE + "arquillian/config/glassfish-resources.xml.ftl", configRoot, "glassfish-resources.xml", EMPTY_MAP);
                expandTemplate(TEMPLATE + "arquillian/config/web.xml.ftl", configRoot, "web.xml", EMPTY_MAP);
                expandTemplate(TEMPLATE + "arquillian/config/test-persistence.xml.ftl", configRoot, "test-persistence.xml", Collections.singletonMap("PU_NAME", entityMapping.getPersistenceUnitName()));
                expandTemplate(TEMPLATE + "config/resource/insert.sql.ftl", getFolderForPackage(configRoot, "META-INF.sql", true), "insert.sql", singletonMap("database", "Derby"));
            }
        }
        return param;
    }

    private void updatePersistenceXml(List<String> classNames) {
        String puName = entityMapping.getPersistenceUnitName();
        Optional<PersistenceUnit> punitOptional = getPersistenceUnit(project, puName);
        if (punitOptional.isPresent()) {
            PersistenceUnit punit = punitOptional.get();
            String SCHEMA_GEN_ACTION = "javax.persistence.schema-generation.database.action";
            String DROP_CREATE = "drop-and-create";
            String SQL_LOAD_SCRIPT = "javax.persistence.sql-load-script-source";
            for (Property property : punit.getProperties().getProperty2()) {
                if (property.getName() == null) {
                    punit.getProperties().removeProperty2(property);
                }
            }
            addProperty(punit, SCHEMA_GEN_ACTION, DROP_CREATE);
            addProperty(punit, SQL_LOAD_SCRIPT, "META-INF/sql/insert.sql");

            addClasses(project, punit, classNames);
            updatePersistenceUnit(project, punit);
        }

    }

    private void expandServerSideComponent(SourceGroup targetSourceGroup, String _package, String prefixName, String suffixName, List<Template> templates, Map<String, Object> param) {
        String fileName = null;
        try {
            if (templates != null) {
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
                    if (appConfigData.isCompleteApplication()) {
                        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(targetSourceGroup, (String) param.get("package"), true);
                        handler.progress(fileName);
                        expandTemplate(TEMPLATE + template.getPath(), targetFolder, fileName + '.' + JAVA_EXT, param);
                    }
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            System.out.println("InputResource : " + _package + '.' + fileName);
        }
    }

    public void generateUtil() throws IOException {
        FileObject targetFolder = SourceGroupSupport.getFolderForPackage(source, restData.getPackage(), true);
        List<RestApp> singletonClasses = new ArrayList<>();
        List<String> providerClasses = new ArrayList<>();
        singletonClasses.add(RestApp.JACKSON);
        if (restData.isMetrics()) {
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

        FileObject restAppPack = SourceGroupSupport.getFolderForPackage(source,
                restData.getRestConfigData().getPackage() == null
                ? restData.getPackage() : restData.getRestConfigData().getPackage(), true);

        try {
            if (restAppPack != null && restData.getRestConfigData().getApplicationClass() != null) {
                RestUtils.createApplicationConfigClass(restSupport, restData.getRestConfigData(),
                        restAppPack, restData.getAppPackage(), singletonClasses, providerClasses, handler);
            }
            RestUtils.disableRestServicesChangeListner(project);
            restSupport.configure("Jeddict - REST support");
        } catch (IOException iox) {
            Exceptions.printStackTrace(iox);
        } finally {
            RestUtils.enableRestServicesChangeListner(project);
        }
    }

    private final static Set<String> AUTO_GEN_ENITY = new HashSet<>(Arrays.asList("User", "Authority", "AbstractAuditingEntity", "AuditListner"));

    public static boolean isAutoGeneratedEntity(String entity) {
        return AUTO_GEN_ENITY.contains(entity);
    }

}
