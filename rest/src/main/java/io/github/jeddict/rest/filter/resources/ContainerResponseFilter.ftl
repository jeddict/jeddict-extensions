package ${package};

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

public class ${class} implements ContainerResponseFilter {

    @Inject
    private Logger logger;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        logger.log(Level.CONFIG, "Request URI: {0}", responseContext.getStatus());
    }

}