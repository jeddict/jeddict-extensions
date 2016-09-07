<#if package??>package ${package};</#if>

import java.util.EnumSet;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
<#if metrics>import ${MetricsConfigurer_FQN};
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.MetricsServlet;</#if>
<#if metrics>import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner;
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader;
import com.wordnik.swagger.reader.ClassReaders;</#if>

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@WebListener
public class WebConfigurer implements ServletContextListener {

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    <#if metrics>
    @Inject
    private MetricsConfigurer metricsConfigurer;
    </#if>
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        <#if metrics>EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
        initMetrics(sce.getServletContext(), disps);</#if>
        <#if docs>initDocs(sce.getServletContext());</#if>
        log.info("Web application fully configured");
    }

    <#if metrics>
    /**
     * Initializes Metrics.
     */
    private void initMetrics(ServletContext servletContext, EnumSet<DispatcherType> disps) {
         MetricRegistry metricRegistry = metricsConfigurer.getMetricRegistry();
        log.debug("Initializing Metrics registries");
        servletContext.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE, metricRegistry);
        servletContext.setAttribute(MetricsServlet.METRICS_REGISTRY, metricRegistry);

        log.debug("Registering Metrics Filter");
        FilterRegistration.Dynamic metricsFilter = servletContext.addFilter("webappMetricsFilter",
                new InstrumentedFilter());

        metricsFilter.addMappingForUrlPatterns(disps, true, "/*");
        metricsFilter.setAsyncSupported(true);

        log.debug("Registering Metrics Servlet");
        ServletRegistration.Dynamic metricsAdminServlet
                = servletContext.addServlet("metricsServlet", new MetricsServlet());

        metricsAdminServlet.addMapping("/management/metrics/*");
        metricsAdminServlet.setAsyncSupported(true);
        metricsAdminServlet.setLoadOnStartup(2);
        
    }
    </#if>
    <#if docs>
    /**
     * Initializes Swagger.
     */
    private void initDocs(ServletContext servletContext) {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        ConfigFactory.setConfig(swaggerConfig);
        String contextPath = servletContext.getContextPath();
        swaggerConfig.setBasePath(contextPath + "/${applicationPath}");
        swaggerConfig.setApiVersion("1.0.0");
        ScannerFactory.setScanner(new DefaultJaxrsScanner());
        ClassReaders.setReader(new DefaultJaxrsApiReader());
    }
    </#if>
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
