package ${package};

import ${appPackage}${KeyAndPasswordVM_FQN};
import ${appPackage}${ManagedUserVM_FQN};
import ${appPackage}${PasswordChangeVM_FQN};
import ${appPackage}${UserDTO_FQN};
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("/api")
public interface ${AccountController}Client {

    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response registerAccount(ManagedUserVM managedUserVM);

    @Path("/activate")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response activateAccount(@QueryParam("key") String key);

    @Path("/authenticate")
    @GET
    @Produces({MediaType.TEXT_PLAIN})
    public String isAuthenticated();

    @Path("/account")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAccount();

    @Path("/account")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response saveAccount(UserDTO userDTO);

    @Path("/account/change-password")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public Response changePassword(PasswordChangeVM passwordChangeVM);

    @Path("/account/reset-password/init")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response requestPasswordReset(String mail);

    @Path("/account/reset-password/finish")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public Response finishPasswordReset(KeyAndPasswordVM keyAndPassword);

}
