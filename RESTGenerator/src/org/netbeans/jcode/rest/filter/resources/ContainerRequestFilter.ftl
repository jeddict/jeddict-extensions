package ${package};

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

public class ${class} implements ContainerRequestFilter {

    @Inject
    private Logger logger;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        logger.log(Level.CONFIG, "HTTP Request method: {0}", requestContext.getMethod());
        logger.log(Level.CONFIG, "Base URI: {0}", requestContext.getUriInfo().getBaseUri());
    }

}