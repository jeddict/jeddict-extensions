package ${package};

import ${appPackage}${UserRepository_FQN};
import ${appPackage}${User_FQN};
import ${appPackage}${SecurityHelper_FQN};
import ${appPackage}${MailService_FQN};
import ${appPackage}${UserService_FQN};
import ${appPackage}${KeyAndPasswordVM_FQN};
import ${appPackage}${ManagedUserVM_FQN};
import ${appPackage}${PasswordChangeVM_FQN};
import ${appPackage}${UserDTO_FQN};
import ${appPackage}${HeaderUtil_FQN};<#if security == "JAXRS_JWT">
import ${appPackage}${Secured_FQN};</#if>
import static ${appPackage}${Constants_FQN}.EMAIL_ALREADY_USED_TYPE;
import static ${appPackage}${Constants_FQN}.EMAIL_NOT_FOUND_TYPE;
import static ${appPackage}${Constants_FQN}.INVALID_PASSWORD_TYPE;
import static ${appPackage}${Constants_FQN}.LOGIN_ALREADY_USED_TYPE;
import static ${appPackage}${Constants_FQN}.PASSWORD_MAX_LENGTH;
import static ${appPackage}${Constants_FQN}.PASSWORD_MIN_LENGTH;
import static ${appPackage}${AuthoritiesConstants_FQN}.USER;
import java.util.*;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
import org.slf4j.Logger;<#if metrics>
import org.eclipse.microprofile.metrics.annotation.Timed;</#if><#if openAPI>
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;</#if>

/**
 * REST controller for managing the current user's account.
 */
@Path("/api")
public class ${AccountController} {

    @Inject
    private Logger log;

    @Inject
    private ${UserRepository} ${userRepository};

    @Inject
    private UserService userService;

    @Inject
    private MailService mailService;

    @Inject
    private SecurityHelper securityHelper;

    @Context
    private HttpServletRequest request;

    /**
     * POST /register : register the user.
     *
     * @param managedUserVM the managed user DTO
     * @return the Response with status 201 (Created) if the user is
     * registered or 400 (Bad Request) if the login or e-mail is already in use
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "register the user" )
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Created"),
        @APIResponse(responseCode = "400", description = "Bad Request")})</#if>
    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response registerAccount(@Valid ManagedUserVM managedUserVM) {
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            return Response.status(BAD_REQUEST).entity(INVALID_PASSWORD_TYPE).build();
        }
        return ${userRepository}.findOneByLogin(managedUserVM.getLogin().toLowerCase())
                .map(user -> Response.status(BAD_REQUEST).type(TEXT_PLAIN).entity(LOGIN_ALREADY_USED_TYPE).build())
                .orElseGet(() -> ${userRepository}.findOneByEmail(managedUserVM.getEmail())
                        .map(user -> Response.status(BAD_REQUEST).type(TEXT_PLAIN).entity(EMAIL_ALREADY_USED_TYPE).build())
                        .orElseGet(() -> {
                            User user = userService.createUser(managedUserVM.getLogin(), managedUserVM.getPassword(),
                                    managedUserVM.getFirstName(), managedUserVM.getLastName(),
                                    managedUserVM.getEmail().toLowerCase(), managedUserVM.getLangKey());
                                mailService.sendActivationEmail(user);
                            return Response.status(CREATED).build();
                        })
                );
    }

    /**
     * GET /activate : activate the registered user.
     *
     * @param key the activation key
     * @return the Response with status 200 (OK) and the activated user in body,
     * or status 500 (Internal Server Error) if the user couldn't be activated
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "activate the registered user" )
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "OK"),
        @APIResponse(responseCode = "500", description = "Internal Server Error")})</#if>
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
     * @return the login if the user is authenticated
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "check if the user is authenticated" )</#if>
    @Path("/authenticate")
    @GET
    @Produces({MediaType.TEXT_PLAIN})<#if security == "JAXRS_JWT">
    @Secured</#if>
    @RolesAllowed(USER)
    public String isAuthenticated() {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET /account : get the current user.
     *
     * @return the Response with status 200 (OK) and the current user in body,
     * or status 500 (Internal Server Error) if the user couldn't be returned
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "get the current user" )
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "OK"),
        @APIResponse(responseCode = "500", description = "Internal Server Error")})</#if>
    @Path("/account")
    @GET
    @Produces({MediaType.APPLICATION_JSON})<#if security == "JAXRS_JWT">
    @Secured</#if>
    @RolesAllowed(USER)
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
    <#if openAPI>@Operation(summary = "update the current user information" )
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "OK"),
        @APIResponse(responseCode = "400", description = "Bad Request"),
        @APIResponse(responseCode = "500", description = "Internal Server Error")})</#if>
    @Path("/account")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})<#if security == "JAXRS_JWT">
    @Secured</#if>
    @RolesAllowed(USER)
    public Response saveAccount(@Valid UserDTO userDTO) {
        final String userLogin = securityHelper.getCurrentUserLogin();
        Optional<User> existingUser = ${userRepository}.findOneByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "user-management", "emailexists", "Email already in use").build();
        }
        return ${userRepository}
                .findOneByLogin(userLogin)
                .map(user -> {
                    userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                            userDTO.getLangKey());
                    return Response.ok().build();
                })
                .orElseGet(() -> Response.status(INTERNAL_SERVER_ERROR).build());
    }

    /**
     * POST /account/change-password : changes the current user's password
     *
     * @param passwordChangeVM current and new password
     * @return the Response with status 200 (OK), or status 400 (Bad Request) 
     * if the new password is not strong enough
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "changes the current user's password" )
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "OK"),
        @APIResponse(responseCode = "400", description = "Bad Request")})</#if>
    @Path("/account/change-password")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})<#if security == "JAXRS_JWT">
    @Secured</#if>
    @RolesAllowed(USER)
    public Response changePassword(PasswordChangeVM passwordChangeVM) {
        if (!checkPasswordLength(passwordChangeVM.getNewPassword())) {
            return Response.status(BAD_REQUEST).entity(INVALID_PASSWORD_TYPE).build();
        }
        userService.changePassword(passwordChangeVM.getCurrentPassword(),passwordChangeVM.getNewPassword());
        return Response.ok().build();
    }

    /**
     * POST /account/reset-password/init : Send an e-mail to reset the password
     * of the user
     *
     * @param mail the mail of the user
     * @return the Response with status 200 (OK) if the e-mail was sent,
     * or status 400 (Bad Request) if the e-mail address is not registred
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "Send an e-mail to reset the password" )
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "OK"),
        @APIResponse(responseCode = "400", description = "Bad Request")})</#if>
    @Path("/account/reset-password/init")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response requestPasswordReset(String mail) {
        return userService.requestPasswordReset(mail)
                .map(user -> {
                    mailService.sendPasswordResetMail(user);
                    return Response.ok().build();
                }).orElse(Response.status(BAD_REQUEST).entity(EMAIL_NOT_FOUND_TYPE).build());
    }

    /**
     * POST /account/reset-password/finish : Finish to reset the password of the
     * user
     *
     * @param keyAndPassword the generated key and the new password
     * @return the Response with status 200 (OK) if the password has been
     * reset, or status 400 (Bad Request) or 500 (Internal Server Error) if the
     * password could not be reset
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "reset the password" )
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "OK"),
        @APIResponse(responseCode = "400", description = "Bad Request"),
        @APIResponse(responseCode = "500", description = "Internal Server Error")})</#if>
    @Path("/account/reset-password/finish")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public Response finishPasswordReset(KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            return Response.status(BAD_REQUEST).entity(INVALID_PASSWORD_TYPE).build();
        }
        return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
                .map(user -> Response.ok().build())
                .orElse(Response.status(INTERNAL_SERVER_ERROR).build());
    }

    private boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password)
                && password.length() >= PASSWORD_MIN_LENGTH
                && password.length() <= PASSWORD_MAX_LENGTH;
    }
}
