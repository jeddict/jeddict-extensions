package ${package};

import static ${appPackage}${AuthoritiesConstants_FQN}.ADMIN;
import static ${appPackage}${AuthoritiesConstants_FQN}.USER;
import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.auth.LoginConfig;
<#if microservices && registryType == "SNOOPEE">
import eu.agilejava.snoop.annotation.EnableSnoopClient;

@EnableSnoopClient(serviceName = "${contextPath}")</#if>
@LoginConfig(
    authMethod = "MP-JWT",
    realmName = "MP-JWT"
)
@DeclareRoles({ADMIN, USER})
@ApplicationPath("${applicationPath}")
public class ${applicationConfig} extends Application {
<#--
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        <#if metrics>resources.add(${appPackage}${DiagnosticFilter_FQN}.class);</#if>
        resources.add(${appPackage}${SecurityHelper_FQN}.class);
        resources.add(${appPackage}${CORSFilter_FQN}.class);
        <#if microservices>resources.add(${appPackage}${HealthController_FQN}.class);</#if>
        <#if gateway || monolith>
        resources.add(${appPackage}${AccountController_FQN}.class);
        resources.add(${appPackage}${AuthenticationController_FQN}.class);
        resources.add(${appPackage}${UserController_FQN}.class);</#if><#if gateway>
        resources.add(${appPackage}${GatewayController_FQN}.class);</#if>
        <#if log>resources.add(${appPackage}${LogsController_FQN}.class);</#if>
        <#if microservices || monolith><#list entityControllerList as entityController>
        resources.add(${entityController.package}.${entityController.name}.class);
        </#list></#if>
        return resources;
    }
-->
}
