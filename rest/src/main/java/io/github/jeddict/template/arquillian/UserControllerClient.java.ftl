package ${package};

import ${appPackage}${ManagedUserVM_FQN};
import java.net.URISyntaxException;
import java.util.List;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
