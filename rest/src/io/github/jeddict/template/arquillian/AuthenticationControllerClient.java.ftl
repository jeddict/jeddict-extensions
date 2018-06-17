package ${package};

import ${appPackage}${LoginDTO_FQN};
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("/api")
public interface ${AuthenticationController}Client {

    @Path("/authenticate")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response login(LoginDTO loginDTO);

}
