package ${package};

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import org.slf4j.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import static com.orbitz.consul.model.agent.Registration.RegCheck.http;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Singleton
@Startup
public class RegistryService {

    @Inject
    private Logger log;
    
    @Inject
    @ConfigProperty(name = "registry.url")
    private String registryUrl;

    @Inject
    @ConfigProperty(name = "web.host")
    private String webHost;

    @Inject
    @ConfigProperty(name = "web.port")
    private String webPort;

    @Inject
    @ConfigProperty(name = "context.path")
    private String registryService;

    private AgentClient agentClient;

    @PostConstruct
    protected void registerService() {

        log.info("Consul host and port " + registryUrl);

        Consul consul = Consul.builder().withUrl(registryUrl).build();
        agentClient = consul.agentClient();

        final ImmutableRegistration registration = ImmutableRegistration.builder()
                .id(registryService)
                .name(registryService)
                .address(webHost)
                .port(Integer.parseInt(webPort))
                .check(http(webHost + ":" + webPort + "/"+  registryService + "/resources/health", 5))
                .build();
        agentClient.register(registration);

        log.info(registryService + " is registered in consul on " + webHost + ":" + webPort);
    }

    @PreDestroy
    protected void unregisterService() {
        agentClient.deregister(registryService);
        log.info(registryService + " is un-registered from consul");
    }
}
