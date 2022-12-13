package ${package};

import org.slf4j.Logger;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import org.eclipse.microprofile.metrics.MetricRegistry;

@Provider
public class DiagnosticFilter implements ContainerRequestFilter {

    @Inject
    private Logger log;

    @Inject
    private MetricRegistry metricRegistry;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String path = containerRequestContext.getUriInfo().getAbsolutePath().getPath();
        log.info("Invoking request {}", path);
        metricRegistry.counter(path).inc();
        log.info("Finished request {}", path);
    }
}
