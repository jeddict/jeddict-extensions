package ${package};

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;<#if microservices && registryType == "SNOOPEE">
import eu.agilejava.snoop.annotation.EnableSnoopClient;

@EnableSnoopClient(serviceName = "${contextPath}")</#if>
@javax.ws.rs.ApplicationPath("${applicationPath}")
public class ${applicationConfig} extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();<#if docs>
        resources.add(com.wordnik.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider.class);
        resources.add(com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON.class);
        resources.add(com.wordnik.swagger.jaxrs.listing.ResourceListingProvider.class);</#if>
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {<#if metrics>
        resources.add(${appPackage}${DiagnosticFilter_FQN}.class);</#if>
        resources.add(${appPackage}${SecurityHelper_FQN}.class);
        resources.add(${appPackage}${CORSFilter_FQN}.class);<#if microservices || monolith><#list entityControllerList as entityController>
        resources.add(${entityController.package}.${entityController.name}.class);
        </#list></#if><#if microservices>
        resources.add(${appPackage}${HealthController_FQN}.class);</#if><#if gateway || monolith>
        resources.add(${appPackage}${AccountController_FQN}.class);
        resources.add(${appPackage}${AuthenticationController_FQN}.class);
        resources.add(${appPackage}${UserController_FQN}.class);</#if><#if log>
        resources.add(${appPackage}${LogsController_FQN}.class);</#if>
    }

}
