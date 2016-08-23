<#if package??>package ${package};</#if>

import ${JWTToken_FQN};
import ${Constants_FQN};
import ${entityPackage}.User;
import javax.inject.Inject;
import ${LoginDTO_FQN};
import javax.servlet.ServletException;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import ${AuthenticationException_FQN};
import ${TokenProvider_FQN};
import ${UserAuthenticationToken_FQN};
import ${UserService_FQN};
import javax.validation.Valid;

@Path("/api")
public class UserJWTController {

    @Inject
    private TokenProvider tokenProvider;

    @Inject
    private UserService userService;

    @Path("/authenticate")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response login(@Valid LoginDTO loginDTO) throws ServletException {

        UserAuthenticationToken authenticationToken = new UserAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        try {
            User user = userService.authenticate(authenticationToken);
            boolean rememberMe = (loginDTO.isRememberMe() == null) ? false : loginDTO.isRememberMe();
            String jwt = tokenProvider.createToken(user, rememberMe);
            return Response.ok(new JWTToken(jwt)).header(Constants.AUTHORIZATION_HEADER, "Bearer " + jwt).build();
        } catch (AuthenticationException exception) {
            return Response.status(Status.UNAUTHORIZED).header("AuthenticationException", exception.getLocalizedMessage()).build();
        }
    }

}
