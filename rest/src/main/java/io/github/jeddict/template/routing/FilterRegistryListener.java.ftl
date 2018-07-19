package ${package};

import com.netflix.zuul.filters.FilterRegistry;
import com.netflix.zuul.monitoring.MonitoringHelper;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FilterRegistryListener implements ServletContextListener {

    @Inject 
    private ServiceDiscoveryFilter serviceDiscoveryFilter;

    @Inject 
    private RoutingFilter routingFilter;

    @Inject 
    private SendResponseFilter sendResponseFilter;
    
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        MonitoringHelper.initMocks();

        FilterRegistry r = FilterRegistry.instance();
        r.put("javaPreFilter", serviceDiscoveryFilter);
        r.put("javaRoutingFilter", routingFilter);
        r.put("javaPostFilter", sendResponseFilter);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
