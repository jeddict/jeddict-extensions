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
package io.github.jeddict.rest.controller;

import io.github.jeddict.bv.constraints.Constraint;
import static io.github.jeddict.bv.constraints.ConstraintUtil.getAttributeDefaultValue;
import static io.github.jeddict.bv.constraints.ConstraintUtil.getAttributeUpdateValue;
import static io.github.jeddict.bv.constraints.ConstraintUtil.getAttributeUpdateValue2;
import static io.github.jeddict.bv.constraints.ConstraintUtil.isAllowedConstraint;
import io.github.jeddict.cdi.CDIUtil;
import io.github.jeddict.docker.generator.DockerConfigData;
import io.github.jeddict.jcode.ApplicationConfigData;
import io.github.jeddict.jcode.Generator;
import static io.github.jeddict.jcode.RegistryType.CONSUL;
import static io.github.jeddict.jcode.RegistryType.SNOOPEE;
import io.github.jeddict.jcode.annotation.ConfigData;
import io.github.jeddict.jcode.annotation.Technology;
import static io.github.jeddict.jcode.annotation.Technology.Type.CONTROLLER;
import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.BOLD;
import static io.github.jeddict.jcode.console.Console.FG_DARK_RED;
import static io.github.jeddict.jcode.console.Console.UNDERLINE;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import static io.github.jeddict.jcode.util.AttributeType.isBoolean;
import static io.github.jeddict.jcode.util.AttributeType.isPrimitive;
import io.github.jeddict.jcode.util.BuildManager;
import static io.github.jeddict.jcode.util.Constants.JAVA_EXT;
import io.github.jeddict.jcode.util.FileUtil;
import static io.github.jeddict.jcode.util.FileUtil.expandTemplate;
import static io.github.jeddict.jcode.util.JavaUtil.getMethodName;
import static io.github.jeddict.jcode.util.PersistenceUtil.addClasses;
import static io.github.jeddict.jcode.util.PersistenceUtil.addProperty;
import static io.github.jeddict.jcode.util.PersistenceUtil.getPersistenceUnit;
import static io.github.jeddict.jcode.util.PersistenceUtil.updatePersistenceUnit;
import io.github.jeddict.jcode.util.ProjectHelper;
import static io.github.jeddict.jcode.util.ProjectHelper.getFolderForPackage;
import static io.github.jeddict.jcode.util.ProjectHelper.getTestSourceGroup;
import static io.github.jeddict.jcode.util.StringHelper.camelCase;
import static io.github.jeddict.jcode.util.StringHelper.firstLower;
import static io.github.jeddict.jcode.util.StringHelper.firstUpper;
import static io.github.jeddict.jcode.util.StringHelper.kebabCase;
import static io.github.jeddict.jcode.util.StringHelper.pluralize;
import static io.github.jeddict.jcode.util.StringHelper.startCase;
import static io.github.jeddict.jcode.util.StringHelper.toConstant;
import io.github.jeddict.jpa.spec.DefaultAttribute;
import io.github.jeddict.jpa.spec.DefaultClass;
import io.github.jeddict.jpa.spec.EmbeddedId;
import io.github.jeddict.jpa.spec.Entity;
import io.github.jeddict.jpa.spec.EntityMappings;
import io.github.jeddict.jpa.spec.extend.Attribute;
import io.github.jeddict.jpa.spec.extend.PaginationType;
import io.github.jeddict.repository.RepositoryData;
import io.github.jeddict.repository.RepositoryGenerator;
import static io.github.jeddict.repository.RepositoryGenerator.REPOSITORY_ABSTRACT;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static java.util.Collections.EMPTY_MAP;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import org.apache.commons.lang.StringUtils;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.j2ee.core.api.support.java.JavaIdentifiers;
import org.netbeans.modules.j2ee.persistence.dd.common.PersistenceUnit;
import org.netbeans.modules.j2ee.persistence.dd.common.Property;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Gaurav Gupta
 */
@ServiceProvider(service = Generator.class)
@Technology(type = CONTROLLER,
        label = "REST",
        panel = RESTPanel.class,
        parents = {RepositoryGenerator.class},
        microservice = true,
        listIndex = 1
)
public class RESTGenerator implements Generator {

    private static final String TEMPLATE = "io/github/jeddict/template/";

    @ConfigData
    private RepositoryData repositoryData;

    @ConfigData
    private RESTData restData;

    @ConfigData
    private DockerConfigData infraConfig;

    @ConfigData
    private EntityMappings entityMapping;

    @ConfigData
    private ApplicationConfigData appConfigData;

    @ConfigData
    private ProgressHandler handler;

    private Project targetProject;

    private SourceGroup targetSource;

    private SourceGroup targetTestSource;

    private Project gatewayProject;

    private SourceGroup gatewaySource;

    private SourceGroup gatewayTestSource;

    private String entityPackage;
    private final List<RestEntityInfo> restEntityInfo = new ArrayList<>();

    private List<Template> CONFIG_TEMPLATES, ENTITY_TEMPLATES,
            ENTITY_LISTENER_TEMPLATES,
            REPOSITORY_TEMPLATES, SERVICE_TEMPLATES, POST_SERVICE_TEMPLATES,
            CONTROLLER_TEMPLATES, CONTROLLER_UTIL_TEMPLATES, DTO_TEMPLATES,
            METRICS_TEMPLATES, VM_TEMPLATES,
            TEST_CASE_TEMPLATES, TEST_CASE_CONTROLLER_TEMPLATES, TEST_CASE_CONTROLLER_CLINET_TEMPLATES, 
            MICROSERVICES_TEMPLATES, MICROSERVICES_CONTROLLER_TEMPLATES, 
            GATEWAY_TEMPLATES, GATEWAY_CONTROLLER_TEMPLATES, GATEWAY_VM_TEMPLATES;
    
    private Template LOGGER_VM = new Template("logger/LoggerVM.java.ftl", "LoggerVM", "vm");
  
    private void registerTemplates() {

        CONFIG_TEMPLATES = new ArrayList<>();
        ENTITY_TEMPLATES = new ArrayList<>();
        ENTITY_LISTENER_TEMPLATES = new ArrayList<>();
        REPOSITORY_TEMPLATES = new ArrayList<>();
        SERVICE_TEMPLATES = new ArrayList<>();
        POST_SERVICE_TEMPLATES = new ArrayList<>();
        CONTROLLER_TEMPLATES = new ArrayList<>();
        CONTROLLER_UTIL_TEMPLATES = new ArrayList<>();
        DTO_TEMPLATES = new ArrayList<>();
        METRICS_TEMPLATES = new ArrayList<>();
        VM_TEMPLATES = new ArrayList<>();
        TEST_CASE_TEMPLATES = new ArrayList<>();
        TEST_CASE_CONTROLLER_TEMPLATES = new ArrayList<>();
        TEST_CASE_CONTROLLER_CLINET_TEMPLATES = new ArrayList<>();
        MICROSERVICES_TEMPLATES = new ArrayList<>();
        MICROSERVICES_CONTROLLER_TEMPLATES = new ArrayList<>();
        GATEWAY_TEMPLATES = new ArrayList<>();
        GATEWAY_CONTROLLER_TEMPLATES = new ArrayList<>();
        GATEWAY_VM_TEMPLATES = new ArrayList<>();

        ENTITY_TEMPLATES.add(new Template("entity/AbstractAuditingEntity.java.ftl", "AbstractAuditingEntity"));
        ENTITY_TEMPLATES.add(new Template("entity/Authority.java.ftl", "Authority"));
        ENTITY_TEMPLATES.add(new Template("entity/User.java.ftl", "User"));

        ENTITY_LISTENER_TEMPLATES.add(new Template("entity/AuditListner.java.ftl", "AuditListner"));

        REPOSITORY_TEMPLATES.add(new Template("repository/AuthorityRepository.java.ftl", "Authority"));
        REPOSITORY_TEMPLATES.add(new Template("repository/UserRepository.java.ftl", "User"));

        CONFIG_TEMPLATES.add(new Template("config/Constants.java.ftl", "Constants", "config"));
        CONFIG_TEMPLATES.add(new Template("config/MailConfig.java.ftl", "MailConfig", "config"));
        CONFIG_TEMPLATES.add(new Template("config/SecurityConfig.java.ftl", "SecurityConfig", "config"));

        CONTROLLER_UTIL_TEMPLATES.add(new Template("rest/util/HeaderUtil.java.ftl", "HeaderUtil", "util"));
        CONTROLLER_UTIL_TEMPLATES.add(new Template("rest/util/Page.java.ftl", "Page", "util"));
        CONTROLLER_UTIL_TEMPLATES.add(new Template("rest/util/PaginationUtil.java.ftl", "PaginationUtil", "util"));

        DTO_TEMPLATES.add(new Template("service/dto/UserDTO.java.ftl", "UserDTO", "service.dto"));
        DTO_TEMPLATES.add(new Template("service/dto/LoginDTO.java.ftl", "LoginDTO", "service.dto"));
        
        VM_TEMPLATES.add(new Template("rest/vm/PasswordChangeVM.java.ftl", "PasswordChangeVM", "vm"));
        VM_TEMPLATES.add(new Template("rest/vm/ManagedUserVM.java.ftl", "ManagedUserVM", "vm"));
        VM_TEMPLATES.add(new Template("rest/vm/KeyAndPasswordVM.java.ftl", "KeyAndPasswordVM", "vm"));

        if (restData.isMetrics()) {
//            METRICS_TEMPLATES.add(new Template("config/MetricsConfig.java.ftl", "MetricsConfig", "config"));
//             METRICS_TEMPLATES.add(new Template("metrics/MetricsConfigurer.java.ftl", "MetricsConfigurer", "metrics"));
            METRICS_TEMPLATES.add(new Template("metrics/DiagnosticFilter.java.ftl", "DiagnosticFilter", "metrics"));
            METRICS_TEMPLATES.add(new Template("metrics/InstrumentedFilter.java.ftl", "InstrumentedFilter", "metrics"));
        }
               
        if (restData.isLogger()) {
            VM_TEMPLATES.add(LOGGER_VM);
            CONTROLLER_TEMPLATES.add(new Template("logger/LogsController.java.ftl", "Logs"));
        }

        SERVICE_TEMPLATES.add(new Template("security/AuthoritiesConstants.java.ftl", "AuthoritiesConstants", "security"));
        SERVICE_TEMPLATES.add(new Template("security/PasswordEncoder.java.ftl", "PasswordEncoder", "security"));
        SERVICE_TEMPLATES.add(new Template("security/SecurityHelper.java.ftl", "SecurityHelper", "security"));
        SERVICE_TEMPLATES.add(new Template("security/TokenProvider.java.ftl", "TokenProvider", "security"));
        SERVICE_TEMPLATES.add(new Template("producer/TemplateEngineProducer.java.ftl", "TemplateEngineProducer", "producer"));
        SERVICE_TEMPLATES.add(new Template("util/RandomUtil.java.ftl", "RandomUtil", "util"));
        SERVICE_TEMPLATES.add(new Template("service/UserService.java.ftl", "UserService", "service"));
        if (restData.isMetrics()) {
            SERVICE_TEMPLATES.add(new Template("web/WebConfigurer.java.ftl", "WebConfigurer", "web"));
        }
        SERVICE_TEMPLATES.add(new Template("web/CORSFilter.java.ftl", "CORSFilter", "web"));

        SERVICE_TEMPLATES.add(new Template("mail/MailEvent.java.ftl", "MailEvent", "mail"));
        SERVICE_TEMPLATES.add(new Template("mail/MailNotifier.java.ftl", "MailNotifier", "mail"));
        SERVICE_TEMPLATES.add(new Template("mail/MailService.java.ftl", "MailService", "service"));

        CONTROLLER_TEMPLATES.add(new Template("rest/AccountController.java.ftl", "Account"));
        CONTROLLER_TEMPLATES.add(new Template("rest/UserController.java.ftl", "User"));
        CONTROLLER_TEMPLATES.add(new Template("rest/AuthenticationController.java.ftl", "Authentication"));
        CONTROLLER_TEMPLATES.add(new Template("rest/HealthController.java.ftl", "Health"));
        CONTROLLER_TEMPLATES.add(new Template("rest/InfoController.java.ftl", "Info"));

        if (restData.isTestCase()) {
            TEST_CASE_TEMPLATES.add(new Template("arquillian/AbstractTest.java.ftl", "AbstractTest"));
            if (appConfigData.isMonolith() || appConfigData.isGateway()) {
                TEST_CASE_TEMPLATES.add(new Template("arquillian/ApplicationTest.java.ftl", "ApplicationTest"));
//                TEST_CASE_TEMPLATES.add(new Template("arquillian/ApplicationTestConfig.java.ftl", "ApplicationTestConfig"));
            
                TEST_CASE_CONTROLLER_TEMPLATES.add(new Template("arquillian/AccountControllerTest.java.ftl", "Account"));
                TEST_CASE_CONTROLLER_TEMPLATES.add(new Template("arquillian/UserControllerTest.java.ftl", "User"));
                
                TEST_CASE_CONTROLLER_CLINET_TEMPLATES.add(new Template("arquillian/AuthenticationControllerClient.java.ftl", "Authentication"));
                TEST_CASE_CONTROLLER_CLINET_TEMPLATES.add(new Template("arquillian/AccountControllerClient.java.ftl", "Account"));
                TEST_CASE_CONTROLLER_CLINET_TEMPLATES.add(new Template("arquillian/UserControllerClient.java.ftl", "User"));
            }
        }

        MICROSERVICES_TEMPLATES.add(new Template("web/CORSFilter.java.ftl", "CORSFilter", "web"));
        MICROSERVICES_TEMPLATES.add(new Template("security/AuthoritiesConstants.java.ftl", "AuthoritiesConstants", "security"));
        MICROSERVICES_TEMPLATES.add(new Template("security/SecurityHelper.java.ftl", "SecurityHelper", "security"));
        MICROSERVICES_TEMPLATES.add(new Template("security/TokenProvider.java.ftl", "TokenProvider", "security"));        
        if(appConfigData.getRegistryType() == CONSUL){
            MICROSERVICES_TEMPLATES.add(new Template("registry/RegistryService.java.ftl", "RegistryService", "registry"));
        }
        MICROSERVICES_CONTROLLER_TEMPLATES.add(new Template("rest/HealthController.java.ftl", "Health"));
        MICROSERVICES_CONTROLLER_TEMPLATES.add(new Template("logger/LogsController.java.ftl", "Logs"));
    
        GATEWAY_TEMPLATES.add(new Template("routing/FilterRegistryListener.java.ftl", "FilterRegistryListener", "routing"));
        GATEWAY_TEMPLATES.add(new Template("routing/ServiceDiscoveryFilter.java.ftl", "ServiceDiscoveryFilter", "routing"));
        GATEWAY_TEMPLATES.add(new Template("routing/RoutingFilter.java.ftl", "RoutingFilter", "routing"));
        GATEWAY_TEMPLATES.add(new Template("routing/SendResponseFilter.java.ftl", "SendResponseFilter", "routing"));
        GATEWAY_VM_TEMPLATES.add(new Template("gateway/RouteVM.java.ftl", "RouteVM", "vm"));
        GATEWAY_VM_TEMPLATES.add(new Template("gateway/ServiceInstanceVM.java.ftl", "ServiceInstanceVM", "vm"));
        GATEWAY_CONTROLLER_TEMPLATES.add(new Template("gateway/GatewayController.java.ftl", "Gateway"));
    }

    @Override
    public void execute() throws IOException {
        targetProject = appConfigData.getTargetProject();
        targetSource = appConfigData.getTargetSourceGroup();
        gatewayProject = appConfigData.getGatewayProject();
        gatewaySource = appConfigData.getGatewaySourceGroup();
        targetTestSource = getTestSourceGroup(targetProject);
        gatewayTestSource = getTestSourceGroup(gatewayProject);

        handler.progress(Console.wrap(RESTGenerator.class, "MSG_Progress_Generating_REST", FG_DARK_RED, BOLD, UNDERLINE));
        entityPackage = entityMapping.getEntityPackage();
        Map<String, Object> params = new HashMap<>();
        params.put("gateway", appConfigData.isGateway());
        params.put("microservices", appConfigData.isMicroservice());
        params.put("monolith", appConfigData.isMonolith());
        reloadTargetPackage(params);
        params.put("applicationConfig_FQN", appConfigData.isGateway() ? appConfigData.getGatewayPackage() : appConfigData.getTargetPackage() + '.' + restData.getPackage() + '.' + restData.getRestConfigData().getApplicationClass());
        params.put("applicationConfig", restData.getRestConfigData().getApplicationClass());
        params.put("applicationPath", restData.getRestConfigData().getApplicationPath());
        params.put("beanPrefix", repositoryData.getPrefixName());
        params.put("beanSuffix", repositoryData.getSuffixName());
        params.put("restPrefix", restData.getPrefixName());
        params.put("restSuffix", restData.getSuffixName());
        params.put("security", restData.getSecurityType().name());
        params.put("metrics", restData.isMetrics());
        params.put("log", restData.isLogger());
        params.put("openAPI", restData.isOpenAPI());
        if (appConfigData.isMonolith() || appConfigData.isMicroservice()) {
            if (appConfigData.isCompleteApplication()) {
                generateServerSideComponent(params);
                CDIUtil.createDD(targetProject);
                generateLoggerProducer(targetSource, appConfigData.getTargetPackage());
                addRestMavenDependencies(targetProject);
            }
        }
        if (appConfigData.isGateway()) {
            generateServerSideComponent(params);
            CDIUtil.createDD(gatewayProject);
            generateLoggerProducer(gatewaySource, appConfigData.getGatewayPackage());
            addRestMavenDependencies(gatewayProject);
        }
        if (appConfigData.isMonolith() || appConfigData.isMicroservice()) {
            for (Entity entity : entityMapping.getGeneratedEntity().collect(toList())) {
                generateEntityController(entity, params);
            }
        }
        
        if(appConfigData.isGateway()) {
            appConfigData.addWebDescriptorContent(
                            expandTemplate("/io/github/jeddict/template/routing/descriptor/web.xml.ftl", params), gatewayProject);
        }
        

        
        if (appConfigData.isCompleteApplication()) {
            Map<String, Object> appParams = new HashMap<>(params);
            if (appConfigData.isGateway()) {
                reloadGatewayPackage(appParams);
                generateApplicationConfig(appParams,
                        appConfigData.getGatewayContextPath(),
                        gatewaySource,
                        appConfigData.getGatewayPackage());
            } else if (appConfigData.isMicroservice()) {
                reloadTargetPackage(appParams);
                generateApplicationConfig(appParams,
                        appConfigData.getTargetContextPath(),
                        targetSource,
                        appConfigData.getTargetPackage());
            } else if (appConfigData.isMonolith()) {
                generateApplicationConfig(appParams,
                        appConfigData.getTargetContextPath(),
                        targetSource,
                        appConfigData.getTargetPackage());
            }
        }
    }

    private void reloadTargetPackage(Map<String, Object> params) {
        params.put("entityPackage", appConfigData.getTargetPackage() + "." + entityPackage);
        params.put("appPackage", appConfigData.getTargetPackage());
        params.put("repositoryPackage", appConfigData.getTargetPackage() + "." + repositoryData.getPackage());
        params.put("restPackage", appConfigData.getTargetPackage() + "." + restData.getPackage());
    }

    private void reloadGatewayPackage(Map<String, Object> params) {
        params.put("entityPackage", appConfigData.getGatewayPackage() + "." + entityPackage);
        params.put("appPackage", appConfigData.getGatewayPackage());
        params.put("repositoryPackage", appConfigData.getGatewayPackage() + "." + repositoryData.getPackage());
        params.put("restPackage", appConfigData.getGatewayPackage() + "." + restData.getPackage());
    }

    public FileObject generateEntityController(final Entity entity, Map<String, Object> params) throws IOException {
        FileObject controllerFO;
        boolean overrideExisting = true, dto = false;
        final String entitySimpleName = entity.getClazz();

        String repositoryFileName = repositoryData.getPrefixName() + entitySimpleName + repositoryData.getSuffixName();
        String fqRepositoryFileName = entity.getAbsolutePackage(repositoryData.getPackage()) + '.' + repositoryFileName;

        String controllerFileName = restData.getPrefixName() + entitySimpleName + restData.getSuffixName();

        String entityClass = firstUpper(entitySimpleName);
        String entityInstance = firstLower(entitySimpleName);
        String entityNameSpinalCased = kebabCase(entityInstance);
        String clientRootFolder = appConfigData.isMicroservice() ?appConfigData.getTargetContextPath() : null;

        Map<String, Object> contollerParams = new HashMap<>(params);
        reloadTargetPackage(contollerParams);
        contollerParams.put("EntityClass", entityClass);
        contollerParams.put("EntityClassPlural", pluralize(firstUpper(entitySimpleName)));
        contollerParams.put("EntityClass_FQN", '.' + entity.getRelativeFQN());
        contollerParams.put("entityInstance", entityInstance);
        contollerParams.put("entityInstancePlural", pluralize(entityInstance));
        contollerParams.put("entityTranslationKey", isNotEmpty(clientRootFolder) ? camelCase(clientRootFolder + "-" + entityInstance) : entityInstance);

        contollerParams.put("controllerClass", controllerFileName);
        contollerParams.put("controllerClassHumanized", startCase(controllerFileName));
        contollerParams.put("entityApiUrl", entityNameSpinalCased);

        contollerParams.put("EntityRepository", repositoryFileName);
        contollerParams.put("entityRepository", firstLower(repositoryFileName));
        contollerParams.put("EntityRepository_FQN", '.' + fqRepositoryFileName);

        contollerParams.put("instanceType", dto ? entityClass + "DTO" : entityClass);
        contollerParams.put("instanceName", dto ? entityInstance + "DTO" : entityInstance);

        contollerParams.put("pagination", entity.getPaginationType() == PaginationType.NO ? "no" : "yes");
        contollerParams.put("fieldsContainNoOwnerOneToOne", false);

        Attribute idAttribute = entity.getAttributes().getIdField();

        if (idAttribute != null) {
            String dataType = idAttribute.getDataTypeLabel();
            contollerParams.put("pkName", firstLower(idAttribute.getName()));
            contollerParams.put("pkGetter", getMethodName("get", idAttribute.getName()));
            contollerParams.put("pkSetter", getMethodName("set", idAttribute.getName()));
            contollerParams.put("pkType", dataType);
            contollerParams.put("isPKPrimitive", isPrimitive(dataType));
        }

        String restTemplate = "EntityController";
        if (idAttribute instanceof EmbeddedId || idAttribute instanceof DefaultAttribute) {
            restTemplate = "CompositePKEntityController";
            contollerParams.put("EntityPKClass_FQN", '.' + entity.getRelativeRootPackage() + '.' + idAttribute.getDataTypeLabel());
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
                restDocList.append(String.format("@param %s the %s of the %s", attribute.getName(), attribute.getName(), contollerParams.get("instanceName"))).append('\n');
            }
            restParamList.setLength(restParamList.length() - 1);
            restParamNameList.setLength(restParamNameList.length() - 1);
            restDocList.setLength(restDocList.length() - 1);
            contollerParams.put("restParamList", restParamList);
            contollerParams.put("restParamNameList", restParamNameList);
            contollerParams.put("restDocList", restDocList);
        }

        contollerParams.put("package", entity.getAbsolutePackage(appConfigData.getTargetPackage() + '.' + restData.getPackage()));
        contollerParams.put("applicationPath", restData.getRestConfigData().getApplicationPath());
        contollerParams.put("metrics", appConfigData.isCompleteApplication() && restData.isMetrics());
        contollerParams.put("openAPI", appConfigData.isCompleteApplication() && restData.isOpenAPI());

        FileObject targetFolder = getFolderForPackage(targetSource, entity.getAbsolutePackage(appConfigData.getTargetPackage() + '.' + restData.getPackage()), true);

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
        expandTemplate(TEMPLATE + "rest/entity/" + restTemplate + ".java.ftl", targetFolder, controllerFileName + '.' + JAVA_EXT, contollerParams);

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
                attrConf.put("updatedValue2", getAttributeUpdateValue2(attr.getDataTypeLabel(), attr.getAttributeConstraintsMap()));
//            attrConf.put("array", isArray(attr.getDataTypeLabel()));
//            attrConf.put("precision", isPrecision(attr.getDataTypeLabel()));
//            attrConf.put("precisionType", isDouble(attr.getDataTypeLabel())?'d':'f');
                return attrConf;
            };

            if (idAttribute instanceof DefaultAttribute) {
                contollerParams.put("pkStrategy", "IdClass");
            } else if (idAttribute instanceof EmbeddedId) {
                contollerParams.put("pkStrategy", "EmbeddedId");
            } else {
                contollerParams.put("pkStrategy", "Id");
            }

            List<Map<String, Object>> allIdAttributes = entity.getAttributes().getSuperId()
                    .stream()
                    .filter(attr -> getAttributeDefaultValue(attr.getDataTypeLabel()) != null)
                    .map(con)
                    .collect(toList());
            contollerParams.put("allIdAttributes", allIdAttributes);
            contollerParams.put("idAttributes", entity.getAttributes().getSuperId()
                    .stream()
                    .filter(id -> !id.isGeneratedValue())
                    .filter(attr -> getAttributeDefaultValue(attr.getDataTypeLabel()) != null)
                    .map(con)
                    .collect(toList()));

//            String matrixParam = allIdAttributes.stream().map(attrConf -> String.format("%s={%s}",attrConf.get("name"),attrConf.get("name"))).collect(Collectors.joining(";"));
//            param.put("matrixParam", matrixParam);
            List<Map<String, Object>> basicAttributes = entity.getAttributes().getSuperBasic()
                    .stream()
                    .filter(attr -> !attr.isOptionalReturnType())
                    .filter(attr -> !attr.getJsonbTransient())
                    .filter(attr -> getAttributeDefaultValue(attr.getDataTypeLabel()) != null)
                    .filter(attr -> isAllowedConstraint(
                                        attr.getAttributeConstraints()
                                                .stream()
                                                .filter(Constraint::getSelected)
                                                .map(Constraint::getClass)
                                                .collect(toSet())
                    ))
                    .map(con)
                    .collect(toList());
            contollerParams.put("attributes", basicAttributes);
            contollerParams.put("versionAttributes", entity.getAttributes().getSuperVersion().stream().map(con).collect(toList()));
            Set<String> connectedClasses = entity.getAttributes().getConnectedClass();
            contollerParams.put("connectedClasses", connectedClasses.stream().map(jc -> JavaIdentifiers.unqualify(jc)).collect(toList()));
            contollerParams.put("connectedFQClasses", connectedClasses);

            FileObject targetTestFolder = getFolderForPackage(targetTestSource,
                    entity.getAbsolutePackage(appConfigData.getTargetPackage() + '.' + restData.getPackage()),
                    true);
            
            String controllerTestFileName = controllerFileName + "Test";
            controllerFO = targetTestFolder.getFileObject(controllerTestFileName, JAVA_EXT);
            if (controllerFO != null) {
                if (overrideExisting) {
                    controllerFO.delete();
                } else {
                    throw new IOException("File already exists exception: " + controllerFO.getPath());
                }
            }
            handler.progress(controllerTestFileName);
            expandTemplate(TEMPLATE + "arquillian/EntityControllerTest.java.ftl", targetTestFolder, controllerTestFileName + '.' + JAVA_EXT, contollerParams);
            
            String controllerClientFileName = controllerFileName + "Client";
            controllerFO = targetTestFolder.getFileObject(controllerClientFileName, JAVA_EXT);
            if (controllerFO != null) {
                if (overrideExisting) {
                    controllerFO.delete();
                } else {
                    throw new IOException("File already exists exception: " + controllerFO.getPath());
                }
            }
            handler.progress(controllerClientFileName);
            expandTemplate(TEMPLATE + "arquillian/EntityControllerClient.java.ftl", targetTestFolder, controllerClientFileName + '.' + JAVA_EXT, contollerParams);
        }

        restEntityInfo.add(new RestEntityInfo(entity.getAbsolutePackage(appConfigData.getTargetPackage() + '.' + restData.getPackage()), controllerFileName, entityNameSpinalCased));
        return controllerFO;
    }

    private Map<String, Object> generateServerSideComponent(Map<String, Object> params) throws IOException {
        registerTemplates();

        final String abstractRepository = repositoryData.getPrefixName() + REPOSITORY_ABSTRACT + repositoryData.getSuffixName();
        params.put("cdi", repositoryData.isCDI());
        params.put("named", repositoryData.isNamed());

        params.put("AbstractRepository", abstractRepository);
        params.put("AbstractRepository_FQN", '.' + repositoryData.getPackage() + '.' + abstractRepository);
        params.put("EntityManagerProducer_FQN", ".producer.EntityManagerProducer");
        params.put("LoggerProducer_FQN", ".producer.LoggerProducer");
        params.put("PU", entityMapping.getPersistenceUnitName());

        params.put("runtime", infraConfig.getRuntime().name());
        params.put("frontendAppName", restData.getFrontendAppName());
        params.put("registryType", appConfigData.getRegistryType().name());
        

        if (appConfigData.isGateway() || appConfigData.isMonolith()) {
            reloadGatewayPackage(params);
            //config
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), null, EMPTY, EMPTY, CONFIG_TEMPLATES, params);
            //entity
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), entityPackage, EMPTY, EMPTY, ENTITY_TEMPLATES, params);
            //contoller util
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), restData.getPackage(), EMPTY, EMPTY, CONTROLLER_UTIL_TEMPLATES, params);
            //contoller ext
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), null, EMPTY, EMPTY, DTO_TEMPLATES, params);
            //repository
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), repositoryData.getPackage(), repositoryData.getPrefixName(), repositoryData.getSuffixName(), REPOSITORY_TEMPLATES, params);
            //metrics
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), null, EMPTY, EMPTY, METRICS_TEMPLATES, params);
            //vm
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), restData.getPackage(), EMPTY, EMPTY, VM_TEMPLATES, params);
            //service
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), null, EMPTY, EMPTY, SERVICE_TEMPLATES, params);
            //entity
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), entityPackage, EMPTY, EMPTY, ENTITY_LISTENER_TEMPLATES, params);
            //controller
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), restData.getPackage(), restData.getPrefixName(), restData.getSuffixName(), CONTROLLER_TEMPLATES, params);
            //post service depends on controller
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), null, EMPTY, EMPTY, POST_SERVICE_TEMPLATES, params);
        }
        
        if (appConfigData.isGateway()) {
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), null, EMPTY, EMPTY, GATEWAY_TEMPLATES, params);
            //vm
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), restData.getPackage(), EMPTY, EMPTY, GATEWAY_VM_TEMPLATES, params);
            //controller
            expandServerSideComponent(gatewaySource, appConfigData.getGatewayPackage(), restData.getPackage(), restData.getPrefixName(), restData.getSuffixName(), GATEWAY_CONTROLLER_TEMPLATES, params);
            
        }

        if (appConfigData.isMicroservice()) {
            reloadTargetPackage(params);
            //config
            expandServerSideComponent(targetSource, appConfigData.getTargetPackage(), null, EMPTY, EMPTY, CONFIG_TEMPLATES, params);
            //contoller util
            expandServerSideComponent(targetSource, appConfigData.getTargetPackage(), restData.getPackage(), EMPTY, EMPTY, CONTROLLER_UTIL_TEMPLATES, params);
            //metrics
            expandServerSideComponent(targetSource, appConfigData.getTargetPackage(), null, EMPTY, EMPTY, METRICS_TEMPLATES, params);
            //logger
            expandServerSideComponent(targetSource, appConfigData.getTargetPackage(), restData.getPackage(), EMPTY, EMPTY, 
                    singletonList(LOGGER_VM), params);
            //microservices
            expandServerSideComponent(targetSource, appConfigData.getTargetPackage(), null, EMPTY, EMPTY, MICROSERVICES_TEMPLATES, params);
            expandServerSideComponent(targetSource, appConfigData.getTargetPackage(), restData.getPackage(), restData.getPrefixName(), restData.getSuffixName(), MICROSERVICES_CONTROLLER_TEMPLATES, params);
        }

        if (restData.isTestCase()) {
            if (appConfigData.isGateway() || appConfigData.isMonolith()) {
                //test-case
                reloadGatewayPackage(params);
                expandServerSideComponent(gatewayTestSource, appConfigData.getGatewayPackage(), restData.getPackage(), EMPTY, EMPTY, TEST_CASE_TEMPLATES, params);
                expandServerSideComponent(gatewayTestSource, appConfigData.getGatewayPackage(), restData.getPackage(), restData.getPrefixName(), restData.getSuffixName() + "Test", TEST_CASE_CONTROLLER_TEMPLATES, params);
                expandServerSideComponent(gatewayTestSource, appConfigData.getGatewayPackage(), restData.getPackage(), restData.getPrefixName(), restData.getSuffixName() + "Client", TEST_CASE_CONTROLLER_CLINET_TEMPLATES, params);
            }
            if (appConfigData.isMicroservice()) {
                reloadTargetPackage(params);
                expandServerSideComponent(targetTestSource, appConfigData.getTargetPackage(), restData.getPackage(), EMPTY, EMPTY, TEST_CASE_TEMPLATES, params);
            }
        }

        final FileObject configGatwayRoot = ProjectHelper.getResourceDirectory(gatewayProject);
        final FileObject configTargetRoot = ProjectHelper.getResourceDirectory(targetProject);
        FileObject webTargetRoot = ProjectHelper.getProjectWebRoot(targetProject);
        FileObject webinfTargetRoot = webTargetRoot.getFileObject("WEB-INF");
        if (webinfTargetRoot == null) {
            webinfTargetRoot = webTargetRoot.createFolder("WEB-INF");
        }

        Map<String, Object> commonConfig = new HashMap<>(params);
        commonConfig.putAll(appConfigData.getCommonConfig());
        expandTemplate(TEMPLATE + "config/resources/config/application-common.properties.ftl",
                getFolderForPackage(appConfigData.isMicroservice() ? configTargetRoot : configGatwayRoot, "config", true),
                "application-common.properties",
                commonConfig);

        Map<String, Object> devConfig = new HashMap<>(params);
        devConfig.putAll(appConfigData.getDevConfig());
        expandTemplate(TEMPLATE + "config/resources/config/application-dev.properties.ftl",
                getFolderForPackage(appConfigData.isMicroservice() ? configTargetRoot : configGatwayRoot, "config", true),
                "application-dev.properties",
                devConfig);

        Map<String, Object> prodConfig = new HashMap<>(params);
        prodConfig.putAll(appConfigData.getProdConfig());
        expandTemplate(TEMPLATE + "config/resources/config/application-prod.properties.ftl",
                getFolderForPackage(appConfigData.isMicroservice() ? configTargetRoot : configGatwayRoot, "config", true),
                "application-prod.properties",
                prodConfig);

        FileUtil.copyFile(TEMPLATE + "config/resource/publicKey.pem", 
                appConfigData.isMicroservice() ? configTargetRoot : configGatwayRoot, 
                "publicKey.pem");
        if (appConfigData.isGateway() || appConfigData.isMonolith()) {
            expandTemplate(TEMPLATE + "config/resource/META-INF/sql/insert.sql.ftl",
                    getFolderForPackage(configGatwayRoot, "META-INF.sql", true),
                    "insert.sql",
                    singletonMap("database", infraConfig.getDatabaseType()));
            expandTemplate(TEMPLATE + "config/resource/i18n/messages.properties.ftl",
                    getFolderForPackage(configGatwayRoot, "i18n", true),
                    "messages.properties",
                    EMPTY_MAP);
            FileUtil.copyFile(TEMPLATE + "config/resource/privateKey.pem", configGatwayRoot, "privateKey.pem");
            FileUtil.copyStaticResource(TEMPLATE + "config/resource/mail-resources.zip", configGatwayRoot, null, handler);
            updatePersistenceXml(Arrays.asList(
                    appConfigData.getGatewayPackage() + "." + entityPackage + ".User",
                    appConfigData.getGatewayPackage() + "." + entityPackage + ".Authority"),
                    gatewayProject);
        }
        if (appConfigData.isMicroservice()) {
            Map<String, Object> descriptorParams = new HashMap<>();
            descriptorParams.put("applicationPath", restData.getRestConfigData().getApplicationPath());
            descriptorParams.put("contextPath", appConfigData.getTargetContextPath());
            if (appConfigData.getRegistryType() == SNOOPEE) {
                expandTemplate(TEMPLATE + "registry/resource/snoop.yml.ftl",
                        configTargetRoot,
                        "snoop.yml",
                        descriptorParams);
            }
            expandTemplate(TEMPLATE + "web/descriptor/glassfish-web.xml.ftl",
                    webinfTargetRoot,
                    "glassfish-web.xml",
                    descriptorParams);
        }

        if (restData.isTestCase()) {
        final FileObject testConfigGatwayRoot = ProjectHelper.getTestResourceDirectory(gatewayProject);
        final FileObject testConfigTargetRoot = ProjectHelper.getTestResourceDirectory(targetProject);
            Map<String, Object> param = new HashMap<>(params);
            param.put("PU_NAME", entityMapping.getPersistenceUnitName());
            expandTemplate(TEMPLATE + "arquillian/config/test-persistence.xml.ftl",
                    appConfigData.isMicroservice() ? testConfigTargetRoot : testConfigGatwayRoot,
                    "test-persistence.xml",
                    param);
            expandTemplate(TEMPLATE + "arquillian/config/arquillian.xml.ftl",
                    appConfigData.isMicroservice() ? testConfigTargetRoot : testConfigGatwayRoot,
                    "arquillian.xml",
                    EMPTY_MAP);
            if (appConfigData.isMicroservice()) {
                expandTemplate(TEMPLATE + "arquillian/config/glassfish-web.xml.ftl",
                        testConfigTargetRoot,
                        "glassfish-web.xml",
                        param);
            }
            
            if (appConfigData.isGateway() || appConfigData.isMonolith()) {
                expandTemplate(TEMPLATE + "config/resource/META-INF/sql/insert.sql.ftl",
                        getFolderForPackage(testConfigGatwayRoot, "META-INF.sql", true),
                        "insert.sql",
                        singletonMap("database", infraConfig.getRuntime().embeddedDB()));
            }
        }

        return params;
    }

    private void updatePersistenceXml(List<String> classNames, Project project) {
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

    private void expandServerSideComponent(SourceGroup targetSourceGroup, String appPackage,
            String _package, String prefixName, String suffixName, List<Template> templates,
            Map<String, Object> param) {
        if (templates != null) {
            for (Template template : templates) {
                try {
                    String templatePackage = _package;
                    String fileName = prefixName + template.getFileName() + suffixName;
                    if (StringUtils.isNotBlank(template.getPackageSuffix())) {
                        templatePackage = StringUtils.isNotBlank(templatePackage)
                                ? templatePackage + '.' + template.getPackageSuffix()
                                : template.getPackageSuffix();
                    }
                    if (StringUtils.isNotBlank(templatePackage)) {
                        templatePackage = '.' + templatePackage;
                    }
                    param.put("package", appPackage + templatePackage);
                    String templateFile = template.getPath().substring(template.getPath().lastIndexOf('/') + 1, template.getPath().indexOf('.'));
                    param.put(templateFile, fileName);

                    if (prefixName != null || suffixName != null) {
                        param.put(firstLower(templateFile), firstLower(fileName));
                    }
                    param.put(templateFile + "_FQN", templatePackage + '.' + fileName);
                    if (appConfigData.isCompleteApplication()) {
                        FileObject targetFolder = getFolderForPackage(targetSourceGroup, (String) param.get("package"), true);
                        handler.progress(fileName);
                        expandTemplate(TEMPLATE + template.getPath(), targetFolder, fileName + '.' + JAVA_EXT, param);
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                    System.out.println("InputResource : " + template.getPath() + '.' + template.getFileName());
                }
            }
        }

    }

    private void generateApplicationConfig(Map<String, Object> params, String contextPath, SourceGroup source, String appPackage) throws IOException {
        FileObject targetFolder = getFolderForPackage(source, appPackage + '.' + restData.getPackage(), true);
        Map<String, Object> appParams = new HashMap<>(params);
        appParams.put("entityControllerList", restEntityInfo);
        appParams.put("package", appPackage + '.' + restData.getPackage());
        appParams.put("contextPath", contextPath);
        expandTemplate(TEMPLATE + "rest/ApplicationConfig.java.ftl", targetFolder, restData.getRestConfigData().getApplicationClass() + '.' + JAVA_EXT, appParams);
    }

    private void addRestMavenDependencies(Project project) {
        addMavenDependencies("rest/pom/_pom.xml", project);
        if (restData.isMetrics()) {
            addMavenDependencies("metrics/pom/_pom.xml", project);
        }
        if (restData.isLogger()) {
            addMavenDependencies("logger/pom/_pom.xml", project);
        }
        if (restData.isTestCase()) {
            infraConfig.getRuntimeProvider().addTestDependency(infraConfig.isDockerActivated());
        }
        if (appConfigData.isGateway()) {
            addMavenDependencies("routing/pom/_pom.xml", project);
            addMavenDependencies("routing/pom/"+appConfigData.getRegistryType().name().toLowerCase()+"_pom.xml", project);
        } else if (appConfigData.isMicroservice()) {
            addMavenDependencies("registry/pom/"+appConfigData.getRegistryType().name().toLowerCase()+"_pom.xml", project);
        }
    }

    private FileObject generateLoggerProducer(SourceGroup source, String appPackage) throws IOException {
        String _package = appPackage + ".producer";
        FileObject targetFolder = getFolderForPackage(source, _package, true);
        String fileName = "LoggerProducer";
        FileObject afFO = targetFolder.getFileObject(fileName, JAVA_EXT);
        if (afFO == null) {
            handler.progress(fileName);
            afFO = io.github.jeddict.jcode.util.FileUtil.expandTemplate(TEMPLATE + "producer/LoggerProducer.java.ftl", targetFolder, fileName + '.' + JAVA_EXT, Collections.singletonMap("package", _package));
        }
        return afFO;
    }

    private void addMavenDependencies(String pom, Project project) {
        BuildManager.getInstance(project)
                .copy(TEMPLATE + pom)
                .setExtensionOverrideFilter((source, target) -> {
            if ("fileset".equalsIgnoreCase(source.getName())) {
                return false;
            }
            if ("concat".equals(source.getName())) {
                target.getExtensibilityElements()
                        .forEach(target::removeExtensibilityElement);
                return true;
            }
            return true;
                })
                .commit();
    }

}