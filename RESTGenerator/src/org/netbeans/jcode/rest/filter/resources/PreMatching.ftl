package ${package};

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

public class ${class} implements ClientRequestFilter {

    @Inject
    private Logger logger;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        logger.log(Level.CONFIG, "{0} created", this.getClass().getSimpleName());

    }

}
