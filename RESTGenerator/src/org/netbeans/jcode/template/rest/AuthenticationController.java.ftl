package ${package};

<#if security == "JAXRS_JWT">import static ${appPackage}${Constants_FQN}.*;
import ${entityPackage}.User;
import ${entityPackage}.Authority;
import javax.inject.Inject;
import ${appPackage}${LoginDTO_FQN};
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import ${appPackage}${TokenProvider_FQN};
import ${appPackage}${UserService_FQN};
import javax.validation.Valid;
import static java.util.stream.Collectors.toSet;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.credential.UsernamePasswordCredential;
<#if metrics>import com.codahale.metrics.annotation.Timed;</#if>
<#if docs>import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;</#if>

<#if docs>@Api(value = "/api")</#if>
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
    <#if docs>@ApiOperation(value = "authenticate the credential" )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "Unauthorized")})</#if>
    @Path("/authenticate")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response login(@Valid LoginDTO loginDTO) {

        UsernamePasswordCredential credential = new UsernamePasswordCredential(loginDTO.getUsername(), loginDTO.getPassword());
        try {
            User user = userService.authenticate(credential);
            boolean rememberMe = (loginDTO.isRememberMe() == null) ? false : loginDTO.isRememberMe();
            String token = tokenProvider.createToken(user.getLogin(),
                    user.getAuthorities().stream().map(Authority::getName).collect(toSet()), 
                    rememberMe);
            return Response.ok()
                    .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                    .build();
        } catch (AuthenticationException exception) {
            return Response.status(Status.UNAUTHORIZED).header("AuthenticationException", exception.getLocalizedMessage()).build();
        }
    }

}<#elseif security == "SECURITY_JWT">
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static ${appPackage}${AuthenticationController_FQN}.AUTHENTICATION_ENDPOINT;

@WebServlet(name = "authenticate", urlPatterns = {AUTHENTICATION_ENDPOINT})
public class ${AuthenticationController} extends HttpServlet {
        
    public static final String AUTHENTICATION_ENDPOINT = "/${applicationPath}/api/authenticate";
    /**
     * Authenticate the credential using JWTAuthenticationMechanism
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // authentication completed
    }

}</#if>

