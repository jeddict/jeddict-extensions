package ${package};

import ${entityPackage}.Authority;
import ${entityPackage}.User;
import ${appPackage}${TokenProvider_FQN};
import ${appPackage}${UserService_FQN};
import ${appPackage}${LoginDTO_FQN};
import static ${appPackage}${Constants_FQN}.BEARER_PREFIX;
import static java.util.stream.Collectors.toSet;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;<#if metrics>
import org.eclipse.microprofile.metrics.annotation.Timed;</#if><#if openAPI>
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;</#if>

@Path("/api")
public class ${AuthenticationController} {

    @Inject
    private TokenProvider tokenProvider;

    @Inject
    private UserService userService;

    /**
     * POST /authenticate : authenticate the credential.
     * <p>
     * Authenticate the user login and password.
     * </p>
     *
     * @param loginDTO the login details to authenticate
     * @return the Response with status 200 (OK) and with body the new jwt
     * token, or with status 401 (Unauthorized) if the authentication fails
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "authenticate the credential" )
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "401", description = "Unauthorized")</#if>
    @Path("/authenticate")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response login(@Valid LoginDTO loginDTO) {
        User user;
        try {
            user = userService.authenticate(loginDTO);
            String token = tokenProvider.createToken(
                    user.getLogin(),
                    user.getAuthorities().stream().map(Authority::getName).collect(toSet()), 
                    loginDTO.isRememberMe()
            );
            return Response.ok()
                    .header(AUTHORIZATION, BEARER_PREFIX + token)
                    .build();
        } catch (AuthenticationException ex) {
            return Response.status(UNAUTHORIZED)
                    .header(AuthenticationException.class.getName(), ex.getLocalizedMessage())
                    .build();
        }
    }

}
