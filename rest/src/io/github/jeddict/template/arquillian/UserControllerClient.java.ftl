package ${package};

import ${appPackage}${ManagedUserVM_FQN};
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("/api")
public interface ${UserController}Client {

    @POST
    @Path(value = "/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(ManagedUserVM managedUserVM);

    @PUT
    @Path(value = "/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(ManagedUserVM managedUserVM);

    @GET
    @Path(value = "/users/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("login") String login);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/users")
    public Response getAllUsers(@QueryParam("page") int page, @QueryParam("size") int size);

    @GET
    @Path("/users/authorities")
    public List<String> getAuthorities();

    @DELETE
    @Path(value = "/users/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("login") String login);

}
