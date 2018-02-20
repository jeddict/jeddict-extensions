package ${package};

<#if docs>import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.ScannerFactory;
import com.wordnik.swagger.config.SwaggerConfig;
import com.wordnik.swagger.jaxrs.config.DefaultJaxrsScanner;
import com.wordnik.swagger.jaxrs.reader.DefaultJaxrsApiReader;
import com.wordnik.swagger.reader.ClassReaders;</#if>
import java.util.EnumSet;
import javax.inject.Inject;
import static javax.servlet.DispatcherType.ASYNC;
import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.REQUEST;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.slf4j.Logger;
<#if metrics>import ${appPackage}${InstrumentedFilter_FQN};</#if>

/**
 * Configuration of web application
 */
@WebListener
public class WebConfigurer implements ServletContextListener {

    @Inject
    private Logger log;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        <#if metrics>initMetrics(sce.getServletContext());</#if>
        <#if docs>initDocs(sce.getServletContext());</#if>
        log.info("Web application fully configured");
    }

    <#if metrics>
    /**
     * Initializes Metrics.
     */
    private void initMetrics(ServletContext servletContext) {
        log.debug("Registering Metrics Filter");
        FilterRegistration.Dynamic metricsFilter = servletContext.addFilter("Instrumented Metrics Filter",InstrumentedFilter.class);
        metricsFilter.addMappingForUrlPatterns(EnumSet.of(REQUEST, FORWARD, ASYNC), true, "/*");
        metricsFilter.setAsyncSupported(true);
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
