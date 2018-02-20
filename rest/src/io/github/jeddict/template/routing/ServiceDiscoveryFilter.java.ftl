package ${package};

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;<#if registryType == "CONSUL">
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.health.Service;<#elseif registryType == "SNOOPEE">
import eu.agilejava.snoop.client.SnoopConfig;
import eu.agilejava.snoop.client.SnoopServiceUnavailableException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response;</#if>
import java.net.MalformedURLException;
import java.net.URL;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;

@ApplicationScoped
public class ServiceDiscoveryFilter extends ZuulFilter {

    @Inject
    private Logger log;
    
    @Inject
    @ConfigProperty(name = "registry.url")
    private String registryUrl;
        
    @Override
    public int filterOrder() {
        return 5;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public boolean shouldFilter() {
        return "/ms".equals(RequestContext.getCurrentContext().getRequest().getServletPath());
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        String serviceName = context.getRequest().getPathInfo().substring(1);
        serviceName = serviceName.substring(0, serviceName.indexOf('/'));

        try {
            context.setRouteHost(new URL(getHost(serviceName)));
        } catch (MalformedURLException ex) {
            log.error(null, ex);
        }

        // sets custom header to send to the origin
        context.addOriginResponseHeader("cache-control", "max-age=3600");
        return null;
    }

    private String getHost(String serviceName) {
    <#if registryType == "CONSUL">
        Consul consul = Consul.builder().withUrl(registryUrl).build();
        Service service = consul.agentClient().getServices().get(serviceName);
        return service.getAddress() + ":" + service.getPort();
    <#elseif registryType == "SNOOPEE">
        try {
            Response response = ClientBuilder.newClient()
                    .target(registryUrl)
                    .path("api")
                    .path("services")
                    .path(serviceName)
                    .request(APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                SnoopConfig snoopConfig = response.readEntity(SnoopConfig.class);
                return snoopConfig.getServiceHome();
            } else {
                throw new SnoopServiceUnavailableException("Response from \"" + registryUrl + "\"=" + response.getStatus());
            }
        } catch (ProcessingException e) {
            throw new SnoopServiceUnavailableException(e);
        }
    </#if>
    }

}
