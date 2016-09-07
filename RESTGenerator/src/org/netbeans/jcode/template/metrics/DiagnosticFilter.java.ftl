<#if package??>package ${package};</#if>

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class DiagnosticFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiagnosticFilter.class);

    @Inject
    private MetricsConfigurer metricsConfigurer;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String path = containerRequestContext.getUriInfo().getAbsolutePath().getPath();
        LOGGER.info("Invoking request {}", path);
        metricsConfigurer.getMetricRegistry().counter(path).inc();
        LOGGER.info("Finished request {}", path);
    }
}
