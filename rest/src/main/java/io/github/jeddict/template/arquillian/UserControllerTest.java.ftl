package ${package};

import static ${appPackage}${ApplicationTest_FQN}.USERNAME;
import ${appPackage}${ManagedUserVM_FQN};
import ${appPackage}${User_FQN};
import ${appPackage}${UserRepository_FQN};
import static ${appPackage}${AuthoritiesConstants_FQN}.USER;
import static java.util.Collections.singleton;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.valid4j.matchers.http.HttpResponseMatchers.hasHeader;
import static org.valid4j.matchers.http.HttpResponseMatchers.hasStatus;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import org.junit.Before;

/**
 * Test class for the ${UserController} REST controller.
 *
 */
@RunWith(Arquillian.class)
public class ${UserControllerTest} extends ApplicationTest {

    @Inject
    private ${UserRepository} ${userRepository};

    private ${UserController}Client client;

    @Deployment
    public static WebArchive createDeployment() {
        return buildApplication()
                .addClass(${UserController}.class)
                .addClass(${UserController}Client.class);
    }

    @Before
    public void buildClient() throws Exception {
        client = buildClient(${UserController}Client.class);
    }

    @Test
    public void testCreateUser() throws Exception {
        ManagedUserVM validUser = new ManagedUserVM(
                null, // id
                "joe", // login
                "password", // password
                "Joe", // firstName
                "Shmoe", // lastName
                "joe@example.com", // e-mail
                true, // activated
                "en", // langKey
                null, // createdBy
                null, // createdDate
                null, // lastModifiedBy
                null, // lastModifiedDate
                singleton(USER) // authorities
        );

        Response response = client.createUser(validUser);
        assertThat(response, hasStatus(CREATED));

        response = client.getUser("joe");
        assertThat(response, hasStatus(OK));
    }

    @Test
    public void testCreateUserDuplicateLogin() throws Exception {
        // Good
        ManagedUserVM validUser = new ManagedUserVM(
                null, // id
                "alice", // login
                "password", // password
                "Alice", // firstName
                "Something", // lastName
                "alice@example.com", // e-mail
                true, // activated
                "en", // langKey
                null, // createdBy
                null, // createdDate
                null, // lastModifiedBy
                null, // lastModifiedDate
                singleton(USER) // authorities
        );

        // Duplicate login, different e-mail
        ManagedUserVM duplicatedUser = new ManagedUserVM(
                validUser.getId(),
                validUser.getLogin(),
                validUser.getPassword(),
                validUser.getFirstName(),
                validUser.getLastName(),
                "alicejr@example.com",
                validUser.isActivated(),
                validUser.getLangKey(),
                validUser.getCreatedBy(),
                validUser.getCreatedDate(),
                validUser.getLastModifiedBy(),
                validUser.getLastModifiedDate(),
                validUser.getAuthorities()
        );

        // Good user
        Response response = client.createUser(validUser);
        assertThat(response, hasStatus(CREATED));

        // Duplicate login
        assertWebException(BAD_REQUEST, () -> client.createUser(duplicatedUser));

        Optional<User> userDup = ${userRepository}.findOneByEmail("alicejr@example.com");
        assertFalse(userDup.isPresent());
    }

    @Test
    public void testCreateUserDuplicateEmail() throws Exception {
        // Good
        ManagedUserVM validUser = new ManagedUserVM(
                null, // id
                "john", // login
                "password", // password
                "John", // firstName
                "Doe", // lastName
                "john@example.com", // e-mail
                true, // activated
                "en", // langKey
                null, // createdBy
                null, // createdDate
                null, // lastModifiedBy
                null, // lastModifiedDate
                singleton(USER) // authorities
        );

        // Duplicate e-mail, different login
        ManagedUserVM duplicatedUser = new ManagedUserVM(
                validUser.getId(),
                "johnjr",
                validUser.getPassword(),
                validUser.getFirstName(),
                validUser.getLastName(),
                validUser.getEmail(),
                validUser.isActivated(),
                validUser.getLangKey(),
                validUser.getCreatedBy(),
                validUser.getCreatedDate(),
                validUser.getLastModifiedBy(),
                validUser.getLastModifiedDate(),
                validUser.getAuthorities()
        );

        // Good user
        Response response = client.createUser(validUser);
        assertThat(response, hasStatus(CREATED));

        // Duplicate  e-mail
        assertWebException(BAD_REQUEST, () -> client.createUser(duplicatedUser));

        Optional<User> userDup = ${userRepository}.findOneByLogin("johnjr");
        assertFalse(userDup.isPresent());
    }

    @Test
    public void testGetExistingUser() throws Exception {
        Response response = client.getUser("admin");
        assertThat(response, hasStatus(OK));
        ManagedUserVM user = response.readEntity(ManagedUserVM.class);
        assertThat(user.getEmail(), is("admin@example.com"));
    }

    @Test
    public void testGetAllUser() throws Exception {
        Response response = client.getAllUsers(0, 5);
        assertThat(response, hasStatus(OK));
        List<ManagedUserVM> users = response.readEntity(List.class);
        assertThat(users.size(), is(${userRepository}.findAll().size()));
    }

    @Test
    public void testGetUnknownUser() throws Exception {
        assertWebException(NOT_FOUND, () -> client.getUser("unknown"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        Response response = client.getUser("user");
        assertThat(response, hasStatus(OK));
        ManagedUserVM user = response.readEntity(ManagedUserVM.class);
        user.setLastName("Gupta");

        response = client.updateUser(user);
        assertThat(response, hasStatus(OK));
    }

    @Test
    public void testDeleteUser() throws Exception {
        Response response = client.deleteUser("user");
        assertThat(response, hasStatus(OK));
    }

    @Test
    public void testValidLogin() throws Exception {
        Response response = login(USERNAME, PASSWORD);
        assertThat(response, hasStatus(OK));
        assertThat(response, hasHeader(AUTHORIZATION));
        String token = response.getHeaderString(AUTHORIZATION);
        assertNotNull(token);
    }

    @Test
    public void testInvalidLogin() throws Exception {
        assertWebException(UNAUTHORIZED, () -> login(USERNAME, INVALID_PASSWORD));
    }

}
