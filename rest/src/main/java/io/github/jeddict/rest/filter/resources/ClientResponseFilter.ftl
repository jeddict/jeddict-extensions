package ${package};

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;

public class ${class} implements ClientResponseFilter {
    @Inject
    private Logger logger;

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
         logger.log(Level.CONFIG, "Response status: {0}", responseContext.getStatus());
    }
    
}