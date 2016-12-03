<#if package??>package ${package};</#if>

import ${Constants_FQN};
import ${AuthoritiesConstants_FQN};
import ${UserService_FQN};
import ${ManagedUserDTO_FQN};
import ${User_FQN};
import java.util.Arrays;
import static java.util.Collections.singletonMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import static javax.ws.rs.client.Entity.json;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.valid4j.matchers.http.HttpResponseMatchers.hasHeader;
import static org.valid4j.matchers.http.HttpResponseMatchers.hasStatus;
import ${UserFacade_FQN};

/**
 * Test class for the ${restPrefix}User${restSuffix} REST controller.
 *
 */
public class ${restPrefix}User${restSuffix}Test extends ApplicationTest {

    @Inject
    private ${UserFacade} ${userFacade};

    @Deployment
    public static WebArchive createDeployment() {
        return buildApplication().addClass(${restPrefix}User${restSuffix}.class);
    }

    @Test
    public void testCreateUser() throws Exception {

        ManagedUserDTO validUser = new ManagedUserDTO(
                null, // id
                "joe", // login
                "password", // password
                "Joe", // firstName
                "Shmoe", // lastName
                "joe@example.com", // e-mail
                true, // activated
                "en", // langKey
                new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
                null, // createdDate
                null, // lastModifiedBy
                null // lastModifiedDate
        );

        Response response = target("api/users").post(json(validUser));
        assertThat(response, hasStatus(CREATED));

        response = target("api/users/joe").get();
        assertThat(response, hasStatus(Response.Status.OK));
    }

    @Test
    public void testCreateUserDuplicateLogin() throws Exception {

        // Good
        ManagedUserDTO validUser = new ManagedUserDTO(
                null, // id
                "alice", // login
                "password", // password
                "Alice", // firstName
                "Something", // lastName
                "alice@example.com", // e-mail
                true, // activated
                "en", // langKey
                new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
                null, // createdDate
                null, // lastModifiedBy
                null // lastModifiedDate
        );

        // Duplicate login, different e-mail
        ManagedUserDTO duplicatedUser = new ManagedUserDTO(validUser.getId(), validUser.getLogin(), validUser.getPassword(), validUser.getLogin(), validUser.getLastName(),
                "alicejr@example.com", true, validUser.getLangKey(), validUser.getAuthorities(), validUser.getCreatedDate(), validUser.getLastModifiedBy(), validUser.getLastModifiedDate());

        // Good user
        Response response = target("api/users").post(json(validUser));
        assertThat(response, hasStatus(CREATED));

        // Duplicate login
        Response errorResponse = target("api/users").post(json(duplicatedUser));
        assertThat(errorResponse, hasStatus(BAD_REQUEST));

        Optional<User> userDup = ${userFacade}.findOneByEmail("alicejr@example.com");
        assertFalse(userDup.isPresent());
    }

    @Test
    @InSequence(1)
    public void testCreateUserDuplicateEmail() throws Exception {
        // Good
        ManagedUserDTO validUser = new ManagedUserDTO(
                null, // id
                "john", // login
                "password", // password
                "John", // firstName
                "Doe", // lastName
                "john@example.com", // e-mail
                true, // activated
                "en", // langKey
                new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
                null, // createdDate
                null, // lastModifiedBy
                null // lastModifiedDate
        );

        // Duplicate e-mail, different login
        ManagedUserDTO duplicatedUser = new ManagedUserDTO(validUser.getId(), "johnjr", validUser.getPassword(), validUser.getLogin(), validUser.getLastName(),
                validUser.getEmail(), true, validUser.getLangKey(), validUser.getAuthorities(), validUser.getCreatedDate(), validUser.getLastModifiedBy(), validUser.getLastModifiedDate());

        // Good user
        Response response = target("api/users").post(json(validUser));
        assertThat(response, hasStatus(CREATED));

        // Duplicate  e-mail
        Response errorResponse = target("api/users").post(json(duplicatedUser));
        assertThat(errorResponse, hasStatus(BAD_REQUEST));

        Optional<User> userDup = ${userFacade}.findOneByLogin("johnjr");
        assertFalse(userDup.isPresent());
    }

    @Test
    @InSequence(2)
    public void testGetExistingUser() throws Exception {
        Response response = target("api/users/admin").get();
        assertThat(response, hasStatus(Response.Status.OK));
        ManagedUserDTO user = response.readEntity(ManagedUserDTO.class);
        assertEquals(user.getEmail(), "admin@example.com");
    }

    @Test
    @InSequence(4)
    public void testGetAllUser() throws Exception {
        Response response = target("api/users", singletonMap("size", 5)).get();
        assertThat(response, hasStatus(Response.Status.OK));
        List<ManagedUserDTO> users = response.readEntity(List.class);
        assertEquals(users.size(), ${userFacade}.findAll().size());
    }

    @Test
    @InSequence(3)
    public void testGetUnknownUser() throws Exception {
        Response response = target("api/users/unknown").get();
        assertThat(response, hasStatus(Response.Status.NOT_FOUND));
    }

    @Test
    @InSequence(5)
    public void testUpdateUser() throws Exception {
        Response response = target("api/users/user").get();
        assertThat(response, hasStatus(Response.Status.OK));
        ManagedUserDTO user = response.readEntity(ManagedUserDTO.class);
        user.setLastName("Gupta");

        response = target("api/users").put(json(user));
        assertThat(response, hasStatus(OK));
    }

    @Test
    @InSequence(6)
    public void testDeleteUser() throws Exception {
        Response response = target("api/users/{login}", singletonMap("login", "user")).delete();
        assertThat(response, hasStatus(OK));
    }

    @Test
    public void testValidLogin() throws Exception {
        Response response = login(USERNAME, PASSWORD);
        assertThat(response, hasStatus(Response.Status.OK));
        assertThat(response, hasHeader(Constants.AUTHORIZATION_HEADER));
        String token = response.getHeaderString(Constants.AUTHORIZATION_HEADER);
        assertNotNull(token);
    }

    @Test
    public void testInvalidLogin() throws Exception {
        Response response = login(USERNAME, INVALID_PASSWORD);
        assertThat(response, hasStatus(Response.Status.UNAUTHORIZED));
    }

}
