package ${package};

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("health")
public class ${HealthController} {

    @GET
    public Response health() {
        return Response.ok().build();
    }
}
