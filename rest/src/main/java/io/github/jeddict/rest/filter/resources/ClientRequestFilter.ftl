package ${package};

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

public class ${class} implements ClientRequestFilter {

    @Inject
    private Logger logger;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        logger.log(Level.CONFIG, "{0} created", this.getClass().getSimpleName());

    }

}
