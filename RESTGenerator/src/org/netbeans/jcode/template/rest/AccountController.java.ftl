<#if package??>package ${package};</#if>

import ${MailConfig_FQN};
import ${UserFacade_FQN};
import ${User_FQN};
import ${SecurityUtils_FQN};
import ${MailService_FQN};
import ${UserService_FQN};
import ${KeyAndPasswordDTO_FQN};
import ${ManagedUserDTO_FQN};
import ${UserDTO_FQN};
import ${HeaderUtil_FQN};
import ${Secured_FQN};
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import org.apache.commons.lang3.StringUtils;
<#if metrics>import com.codahale.metrics.annotation.Timed;</#if>
<#if docs>import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;</#if>

/**
 * REST controller for managing the current user's account.
 */
<#if docs>@Api(value = "/api/account", description = "Account ${restSuffix}")</#if>
@Path("/api")
public class ${restPrefix}Account${restSuffix} {

    private final Logger log = LoggerFactory.getLogger(${restPrefix}Account${restSuffix}.class);

    @Inject
    private ${UserFacade} ${userFacade};

    @Inject
    private UserService userService;

    @Inject
    private MailService mailService;

    @Inject
    private SecurityUtils securityUtils;

    @Inject
    private MailConfig mailConfig;

    /**
     * POST /register : register the user.
     *
     * @param managedUserDTO the managed user DTO
     * @param request the HTTP request
     * @return the Response with status 201 (Created) if the user is
     * registered or 400 (Bad Request) if the login or e-mail is already in use
     */
    @Context
    HttpServletRequest request;

    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "register the user" )
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created"),
        @ApiResponse(code = 400, message = "Bad Request")})</#if>
    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response registerAccount(@Valid ManagedUserDTO managedUserDTO) {

        return ${userFacade}.findOneByLogin(managedUserDTO.getLogin().toLowerCase())
                .map(user -> Response.status(BAD_REQUEST).type(TEXT_PLAIN).entity("login already in use").build())
                .orElseGet(() -> ${userFacade}.findOneByEmail(managedUserDTO.getEmail())
                        .map(user -> Response.status(BAD_REQUEST).type(TEXT_PLAIN).entity("e-mail address already in use").build())
                        .orElseGet(() -> {
                            User user = userService.createUserInformation(managedUserDTO.getLogin(), managedUserDTO.getPassword(),
                                    managedUserDTO.getFirstName(), managedUserDTO.getLastName(), managedUserDTO.getEmail().toLowerCase(),
                                    managedUserDTO.getLangKey());
                            String baseUrl = request.getScheme()
                                    + // "http"
                                    "://"
                                    + // "://"
                                    request.getServerName()
                                    + // "myhost"
                                    ":"
                                    + // ":"
                                    request.getServerPort()
                                    + // "80"
                                    request.getContextPath();
                                    // "/myContextPath" or "" if deployed in root context

                            if (mailConfig.isEnable()) {
                                mailService.sendActivationEmail(user, baseUrl);
                            } else {
                                activateAccount(user.getActivationKey());
                            }

                            return Response.status(CREATED).build();
                        })
                );
    }

    /**
     * GET /activate : activate the registered user.
     *
     * @param key the activation key
     * @return the Response with status 200 (OK) and the activated user in
     * body, or status 500 (Internal Server Error) if the user couldn't be
     * activated
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "activate the registered user" )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Internal Server Error")})</#if>
    @Path("/activate")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response activateAccount(@QueryParam("key") String key) {
        return userService.activateRegistration(key)
                .map(user -> Response.ok().build())
                .orElse(Response.status(INTERNAL_SERVER_ERROR).build());
    }

    /**
     * GET /authenticate : check if the user is authenticated, and return its
     * login.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "check if the user is authenticated" )</#if>
    @Path("/authenticate")
    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Secured
    public String isAuthenticated(@Context HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET /account : get the current user.
     *
     * @return the Response with status 200 (OK) and the current user in
     * body, or status 500 (Internal Server Error) if the user couldn't be
     * returned
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "get the current user" )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 500, message = "Internal Server Error")})</#if>
    @Path("/account")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Secured
    public Response getAccount() {
        return Optional.ofNullable(userService.getUserWithAuthorities())
                .map(user -> Response.ok(new UserDTO(user)).build())
                .orElse(Response.status(INTERNAL_SERVER_ERROR).build());
    }

    /**
     * POST /account : update the current user information.
     *
     * @param userDTO the current user information
     * @return the Response with status 200 (OK), or status 400 (Bad
     * Request) or 500 (Internal Server Error) if the user couldn't be updated
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "update the current user information" )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")})</#if>
    @Path("/account")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Secured
    public Response saveAccount(@Valid UserDTO userDTO) {
        Optional<User> existingUser = ${userFacade}.findOneByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userDTO.getLogin()))) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "user-management", "emailexists", "Email already in use").build();
        }
        return ${userFacade}
                .findOneByLogin(securityUtils.getCurrentUserLogin())
                .map(u -> {
                    userService.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                            userDTO.getLangKey());
                    return Response.ok().build();
                })
                .orElseGet(() -> Response.status(INTERNAL_SERVER_ERROR).build());
    }

    /**
     * POST /account/change_password : changes the current user's password
     *
     * @param password the new password
     * @return the Response with status 200 (OK), or status 400 (Bad
     * Request) if the new password is not strong enough
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "changes the current user's password" )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request")})</#if>
    @Path("/account/change_password")
    @POST
    <#--@Consumes({MediaType.TEXT_PLAIN}) should be TEXT_HTML -->
    @Produces({MediaType.TEXT_PLAIN})
    @Secured
    public Response changePassword(String password) {
        if (!checkPasswordLength(password)) {
            return Response.status(BAD_REQUEST).entity("Incorrect password").build();
        }
        userService.changePassword(password);
        return Response.ok().build();
    }

    /**
     * POST /account/reset_password/init : Send an e-mail to reset the password
     * of the user
     *
     * @param mail the mail of the user
     * @param request the HTTP request
     * @return the Response with status 200 (OK) if the e-mail was sent,
     * or status 400 (Bad Request) if the e-mail address is not registred
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "Send an e-mail to reset the password" )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request")})</#if>
    @Path("/account/reset_password/init")
    @POST
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.TEXT_PLAIN})
    public Response requestPasswordReset(String mail, @Context HttpServletRequest request) {
        return userService.requestPasswordReset(mail)
                .map(user -> {
                    String baseUrl = request.getScheme()
                            + "://"
                            + request.getServerName()
                            + ":"
                            + request.getServerPort()
                            + request.getContextPath();
                    mailService.sendPasswordResetMail(user, baseUrl);
                    return Response.ok("e-mail was sent").build();
                }).orElse(Response.status(BAD_REQUEST).entity("e-mail address not registered").build());
    }

    /**
     * POST /account/reset_password/finish : Finish to reset the password of the
     * user
     *
     * @param keyAndPassword the generated key and the new password
     * @return the Response with status 200 (OK) if the password has been
     * reset, or status 400 (Bad Request) or 500 (Internal Server Error) if the
     * password could not be reset
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "reset the password" )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")})</#if>
    @Path("/account/reset_password/finish")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public Response finishPasswordReset(KeyAndPasswordDTO keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            return Response.status(BAD_REQUEST).entity("Incorrect password").build();
        }
        return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
                .map(user -> Response.ok().build())
                .orElse(Response.status(INTERNAL_SERVER_ERROR).build());
    }

    private boolean checkPasswordLength(String password) {
        return (!StringUtils.isEmpty(password)
                && password.length() >= ManagedUserDTO.PASSWORD_MIN_LENGTH
                && password.length() <= ManagedUserDTO.PASSWORD_MAX_LENGTH);
    }
}
