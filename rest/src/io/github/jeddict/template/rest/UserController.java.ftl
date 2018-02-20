package ${package};

import ${appPackage}${UserRepository_FQN};
import ${appPackage}${User_FQN};
import ${appPackage}${MailService_FQN};
import ${appPackage}${UserService_FQN};
import ${appPackage}${ManagedUserDTO_FQN};
import ${appPackage}${UserDTO_FQN};
import ${appPackage}${HeaderUtil_FQN};
import ${appPackage}${Page_FQN};
import ${appPackage}${PaginationUtil_FQN};
import static ${appPackage}${Constants_FQN}.EMAIL_ALREADY_USED_TYPE;
import static ${appPackage}${Constants_FQN}.LOGIN_ALREADY_USED_TYPE;
<#if security == "JAXRS_JWT">import ${appPackage}${Secured_FQN};
import ${appPackage}${AuthoritiesConstants_FQN};</#if>
import org.slf4j.Logger;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import static java.util.stream.Collectors.toList;
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
import javax.ws.rs.core.Response.ResponseBuilder;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;<#if metrics>
import org.eclipse.microprofile.metrics.annotation.Timed;</#if>
import org.eclipse.microprofile.faulttolerance.Timeout;<#if docs>
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;</#if>

/**
 * REST controller for managing users.
 *
 * <p>
 * This class accesses the User entity, and needs to fetch its collection of
 * authorities.</p>
 */
<#if docs>@Api(value = "/api")</#if>
@Path("/api")
public class ${UserController} {

    @Inject
    private Logger log;

    @Inject
    private ${UserRepository} ${userRepository};

    @Inject
    private MailService mailService;

    @Inject
    private UserService userService;

    /**
     * POST /users : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends
     * an mail with an activation link. The user needs to be activated on
     * creation.
     * </p>
     *
     * @param managedUserDTO the user to create
     * @return the Response with status 201 (Created) and with body the
     * new user, or with status 400 (Bad Request) if the login or email is
     * already in use
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "create a new user")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created"),
        @ApiResponse(code = 400, message = "Bad Request")})</#if>
    @Path(value = "/users")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)<#if security == "JAXRS_JWT">
    @Secured(AuthoritiesConstants.ADMIN)</#if>
    public Response createUser(ManagedUserDTO managedUserDTO) throws URISyntaxException {
        log.debug("REST request to save User : {}", managedUserDTO);

        //Lowercase the user login before comparing with database
        if (${userRepository}.findOneByLogin(managedUserDTO.getLogin().toLowerCase()).isPresent()) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "userexists", LOGIN_ALREADY_USED_TYPE).build();
        } else if (${userRepository}.findOneByEmail(managedUserDTO.getEmail()).isPresent()) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "emailexists", EMAIL_ALREADY_USED_TYPE).build();
        } else {
            User newUser = userService.createUser(managedUserDTO);
            mailService.sendCreationEmail(newUser);
            return HeaderUtil.createAlert(Response.created(new URI("/${applicationPath}/api/users/" + newUser.getLogin())),
                    "userManagement.created", newUser.getLogin()).entity(new UserDTO(newUser)).build();
        }
    }

    /**
     * PUT /users : Updates an existing User.
     *
     * @param managedUserDTO the user to update
     * @return the Response with status 200 (OK) and with body the updated
     * user, or with status 400 (Bad Request) if the login or email is already
     * in use, or with status 500 (Internal Server Error) if the user couldn't be
     * updated
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "update user")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")})</#if>
    @Path(value = "/users")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)<#if security == "JAXRS_JWT">
    @Secured(AuthoritiesConstants.ADMIN)</#if>
    public Response updateUser(ManagedUserDTO managedUserDTO) {
        log.debug("REST request to update User : {}", managedUserDTO);
        Optional<User> existingUser = ${userRepository}.findOneByEmail(managedUserDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserDTO.getId()))) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "emailexists", EMAIL_ALREADY_USED_TYPE).build();
        }
        existingUser = ${userRepository}.findOneByLogin(managedUserDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserDTO.getId()))) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "userexists", LOGIN_ALREADY_USED_TYPE).build();
        }
        Optional<UserDTO> updatedUser = userService.updateUser(managedUserDTO);
        
        return updatedUser.map(userDTO -> HeaderUtil.createAlert(Response.ok(userDTO),
                "userManagement.updated", managedUserDTO.getLogin()).build())
                .orElseGet(() -> Response.status(NOT_FOUND).build());
    }

    /**
     * GET /users : get all users.
     *
     * @param page the pagination information
     * @param size the pagination size information
     * @return the Response with status 200 (OK) and with body all users
     * @throws URISyntaxException if the pagination headers couldn't be generated
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "get all the users")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK")})</#if>
    @Path(value = "/users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)<#if security == "JAXRS_JWT">
    @Secured</#if>
    @Timeout
    public Response getAllUsers(@QueryParam("page") int page, @QueryParam("size") int size) throws URISyntaxException {
        List<User> userList = ${userRepository}.getUsersWithAuthorities(page * size, size);
        List<UserDTO> userDTOs = userList.stream()
                .map(UserDTO::new)
                .collect(toList());

        ResponseBuilder builder = Response.ok(userDTOs);
        PaginationUtil.generatePaginationHttpHeaders(builder, new Page(page, size, ${userRepository}.count()), "/${applicationPath}/api/users");
        return builder.build();
    }

    /**
     * @return a string list of the all of the roles
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "get roles")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK")})</#if>
    @Path("/users/authorities")
    @GET<#if security == "JAXRS_JWT">
    @Secured(AuthoritiesConstants.ADMIN)</#if>
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }
    
    /**
     * GET /users/:login : get the "login" user.
     *
     * @param login the login of the user to find
     * @return the Response with status 200 (OK) and with body the "login"
     * user, or with status 404 (Not Found)
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "get the user")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})</#if>
    @Path(value = "/users/{login}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)<#if security == "JAXRS_JWT">
    @Secured</#if>
    public Response getUser(@PathParam("login") String login) {
        log.debug("REST request to get User : {}", login);
        return userService.getUserWithAuthoritiesByLogin(login)
                .map(UserDTO::new)
                .map(userDTO -> Response.ok(userDTO).build())
                .orElse(Response.status(NOT_FOUND).build());
    }

    /**
     * DELETE /users/:login : delete the "login" User.
     *
     * @param login the login of the user to delete
     * @return the Response with status 200 (OK)
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "remove the user" )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})</#if>
    @Path(value = "/users/{login}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)<#if security == "JAXRS_JWT">
    @Secured(AuthoritiesConstants.ADMIN)</#if>
    public Response deleteUser(@PathParam("login") String login) {
        log.debug("REST request to delete User: {}", login);
        userService.deleteUser(login);
        return HeaderUtil.createAlert(Response.ok(), "userManagement.deleted", login).build();
    }
}
