package ${package};

import ${appPackage}${UserRepository_FQN};
import ${appPackage}${User_FQN};
import ${appPackage}${MailService_FQN};
import ${appPackage}${UserService_FQN};
import ${appPackage}${ManagedUserVM_FQN};
import ${appPackage}${UserDTO_FQN};
import ${appPackage}${HeaderUtil_FQN};
import ${appPackage}${Page_FQN};
import ${appPackage}${PaginationUtil_FQN};
import static ${appPackage}${Constants_FQN}.EMAIL_ALREADY_USED_TYPE;
import static ${appPackage}${Constants_FQN}.LOGIN_ALREADY_USED_TYPE;
import static ${appPackage}${AuthoritiesConstants_FQN}.ADMIN;
import static ${appPackage}${AuthoritiesConstants_FQN}.USER;
import org.slf4j.Logger;
import jakarta.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.security.RolesAllowed;
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
import jakarta.ws.rs.core.Response.ResponseBuilder;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;<#if metrics>
import org.eclipse.microprofile.metrics.annotation.Timed;</#if>
import org.eclipse.microprofile.faulttolerance.Timeout;<#if openAPI>
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;</#if>

/**
 * REST controller for managing users.
 *
 * <p>
 * This class accesses the User entity, and needs to fetch its collection of
 * authorities.</p>
 */
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
     * @param managedUserVM the user to create
     * @return the Response with status 201 (Created) and with body the
     * new user, or with status 400 (Bad Request) if the login or email is
     * already in use
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "create a new user")
    @APIResponse(responseCode = "201", description = "Created")
    @APIResponse(responseCode = "400", description = "Bad Request")</#if>
    @Path(value = "/users")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public Response createUser(ManagedUserVM managedUserVM) throws URISyntaxException {
        log.debug("REST request to save User : {}", managedUserVM);

        //Lowercase the user login before comparing with database
        if (${userRepository}.findOneByLogin(managedUserVM.getLogin().toLowerCase()).isPresent()) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "userexists", LOGIN_ALREADY_USED_TYPE).build();
        } else if (${userRepository}.findOneByEmail(managedUserVM.getEmail()).isPresent()) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "emailexists", EMAIL_ALREADY_USED_TYPE).build();
        } else {
            User newUser = userService.createUser(managedUserVM);
            mailService.sendCreationEmail(newUser);
            return HeaderUtil.createAlert(Response.created(new URI("/${applicationPath}/api/users/" + newUser.getLogin())),
                    "userManagement.created", newUser.getLogin()).entity(new UserDTO(newUser)).build();
        }
    }

    /**
     * PUT /users : Updates an existing User.
     *
     * @param managedUserVM the user to update
     * @return the Response with status 200 (OK) and with body the updated
     * user, or with status 400 (Bad Request) if the login or email is already
     * in use, or with status 500 (Internal Server Error) if the user couldn't be
     * updated
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "update user")
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "400", description = "Bad Request")
    @APIResponse(responseCode = "500", description = "Internal Server Error")</#if>
    @Path(value = "/users")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public Response updateUser(ManagedUserVM managedUserVM) {
        log.debug("REST request to update User : {}", managedUserVM);
        Optional<User> existingUser = ${userRepository}.findOneByEmail(managedUserVM.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserVM.getId()))) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "emailexists", EMAIL_ALREADY_USED_TYPE).build();
        }
        existingUser = ${userRepository}.findOneByLogin(managedUserVM.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserVM.getId()))) {
            return HeaderUtil.createFailureAlert(Response.status(BAD_REQUEST), "userManagement", "userexists", LOGIN_ALREADY_USED_TYPE).build();
        }
        Optional<UserDTO> updatedUser = userService.updateUser(managedUserVM);
        
        return updatedUser.map(userDTO -> HeaderUtil.createAlert(Response.ok(userDTO),
                "userManagement.updated", managedUserVM.getLogin()).build())
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
    <#if openAPI>@Operation(summary = "get all the users")
    @APIResponse(responseCode = "200", description = "OK")</#if>
    @Path(value = "/users")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timeout
    @RolesAllowed(USER)
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
    <#if openAPI>@Operation(summary = "get roles")
    @APIResponse(responseCode = "200", description = "OK")</#if>
    @Path("/users/authorities")
    @GET
    @RolesAllowed(ADMIN)
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
    <#if openAPI>@Operation(summary = "get the user")
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "404", description = "Not Found")</#if>
    @Path(value = "/users/{login}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER)
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
    <#if openAPI>@Operation(summary = "remove the user" )
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "404", description = "Not Found")</#if>
    @Path(value = "/users/{login}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(ADMIN)
    public Response deleteUser(@PathParam("login") String login) {
        log.debug("REST request to delete User: {}", login);
        userService.deleteUser(login);
        return HeaderUtil.createAlert(Response.ok(), "userManagement.deleted", login).build();
    }
}
