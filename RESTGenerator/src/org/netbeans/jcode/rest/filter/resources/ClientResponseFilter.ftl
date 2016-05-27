package ${package};

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

public class ${class} implements ClientResponseFilter {
    @Inject
    private Logger logger;

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
         logger.log(Level.CONFIG, "Response status: {0}", responseContext.getStatus());
    }
    
}