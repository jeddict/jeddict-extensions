<#if package??>package ${package};</#if>

<#if metrics>import ${MetricsConfigurer_FQN};
import com.codahale.metrics.jersey2.InstrumentedResourceMethodApplicationListener;
import javax.inject.Inject;</#if>
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("${ApplicationPath}")
public class ${ApplicationConfig} extends Application {

    <#if metrics>@Inject
    private MetricsConfigurer metricsConfigurer;</#if>

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
        resources.add(${DiagnosticFilter_FQN}.class);</#if>
        resources.add(${SecurityHelper_FQN}.class);
        resources.add(${CORSFilter_FQN}.class);
        <#list entityControllerList as entityController>
        resources.add(${entityController.package}.${entityController.name}.class);
        </#list>
        resources.add(${AccountController_FQN}.class);
        resources.add(${AuthenticationController_FQN}.class);
        resources.add(${UserController_FQN}.class);<#if log>
        resources.add(${LogsResource_FQN}.class);</#if>
    }

    <#if metrics>@Override
    public Set<Object> getSingletons() {
        final Set<Object> instances = new HashSet<>();
        instances.add(new InstrumentedResourceMethodApplicationListener(metricsConfigurer.getMetricRegistry()));
        return instances;
    }
    </#if>
}
