<#if package??>package ${package};</#if>

import ${AuthoritiesConstants_FQN};
import ${AuthorityFacade_FQN};
import ${UserFacade_FQN};
import ${KeyAndPasswordDTO_FQN};
import ${ManagedUserDTO_FQN};
import ${UserDTO_FQN};
import ${User_FQN};
import java.util.Arrays;
import static java.util.Collections.singletonMap;
import java.util.HashSet;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.client.Entity.text;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.valid4j.matchers.http.HttpResponseMatchers.hasStatus;

/**
 * Test class for the ${restPrefix}Account${restSuffix} REST controller.
 *
 */
public class ${restPrefix}Account${restSuffix}Test extends ApplicationTest {

    @Inject
    private ${UserFacade} ${userFacade};

    @Inject
    private ${AuthorityFacade} ${authorityFacade};

    @Deployment
    public static WebArchive createDeployment() {
        return buildApplication().addClass(${restPrefix}Account${restSuffix}.class);
    }

    @Test
    public void testGetExistingAccount() throws Exception {
        Response response = target("api/account").get();
        assertThat(response, hasStatus(OK));
        UserDTO user = response.readEntity(UserDTO.class);
        assertNotNull(user);
        assertEquals(user.getLogin(), USERNAME);
    }

    @Test
    public void testGetUnknownAccount() throws Exception {
        logout();
        Response response = target("api/account").get();
        assertThat(response, hasStatus(UNAUTHORIZED));
    }

    @Test
    public void testRegisterValid() throws Exception {
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

        Response response = target("api/register").post(json(validUser));
        assertThat(response, hasStatus(CREATED));

        Optional<User> user = ${userFacade}.findOneByLogin("joe");
        assertTrue(user.isPresent());
    }

<#--
    @Test
    @Ignore
    public void testRegisterInvalidLogin() throws Exception {
        ManagedUserDTO invalidUser = new ManagedUserDTO(
                null, // id
                "funky-log!n", // login <-- invalid
                "password", // password
                "Funky", // firstName
                "One", // lastName
                "funky@example.com", // e-mail
                true, // activated
                "en", // langKey
                new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
                null, // createdDate
                null, // lastModifiedBy
                null // lastModifiedDate
        );

        Response response = target("api/register").post(json(invalidUser));
        assertThat(response, hasStatus(BAD_REQUEST));

        Optional<User> user = ${userFacade}.findOneByEmail("funky@example.com");
        assertFalse(user.isPresent());
    }

    @Test
    @Ignore
    public void testRegisterInvalidEmail() throws Exception {
        ManagedUserDTO invalidUser = new ManagedUserDTO(
                null, // id
                "bob", // login
                "password", // password
                "Bob", // firstName
                "Green", // lastName
                "invalid", // e-mail <-- invalid
                true, // activated
                "en", // langKey
                new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
                null, // createdDate
                null, // lastModifiedBy
                null // lastModifiedDate
        );

        Response response = target("api/register").post(json(invalidUser));
        assertThat(response, hasStatus(BAD_REQUEST));

        Optional<User> user = ${userFacade}.findOneByLogin("bob");
        assertFalse(user.isPresent());

    }

    @Test
    @Ignore
    public void testRegisterInvalidPassword() throws Exception {
        ManagedUserDTO invalidUser = new ManagedUserDTO(
                null, // id
                "bob", // login
                "123", // password with only 3 digits
                "Bob", // firstName
                "Green", // lastName
                "bob@example.com", // e-mail
                true, // activated
                "en", // langKey
                new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
                null, // createdDate
                null, // lastModifiedBy
                null // lastModifiedDate
        );

        Response response = target("api/register").post(json(invalidUser));
        assertThat(response, hasStatus(BAD_REQUEST));

        Optional<User> user = ${userFacade}.findOneByLogin("bob");
        assertFalse(user.isPresent());

    }

    @Test
    @Ignore
    public void testSaveInvalidLogin() throws Exception {
        UserDTO invalidUser = new UserDTO(
                "funky-log!n", // login <-- invalid
                "Funky", // firstName
                "One", // lastName
                "funky@example.com", // e-mail
                true, // activated
                "en", // langKey
                new HashSet<>(Arrays.asList(AuthoritiesConstants.USER))
        );

        Response response = target("api/account").post(json(invalidUser));
        assertThat(response, hasStatus(BAD_REQUEST));

        Optional<User> user = ${userFacade}.findOneByEmail("funky@example.com");
        assertFalse(user.isPresent());
    }
-->
    @Test
    public void testRegisterDuplicateLogin() throws Exception {
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
        Response response = target("api/register").post(json(validUser));
        assertThat(response, hasStatus(CREATED));

        // Duplicate login
        Response errorResponse = target("api/register").post(json(duplicatedUser));
        assertThat(errorResponse, hasStatus(BAD_REQUEST));

        Optional<User> userDup = ${userFacade}.findOneByEmail("alicejr@example.com");
        assertFalse(userDup.isPresent());
    }

    @Test
    public void testRegisterDuplicateEmail() throws Exception {
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
        Response response = target("api/register").post(json(validUser));
        assertThat(response, hasStatus(CREATED));

        // Duplicate  e-mail
        Response errorResponse = target("api/register").post(json(duplicatedUser));
        assertThat(errorResponse, hasStatus(BAD_REQUEST));

        Optional<User> userDup = ${userFacade}.findOneByLogin("johnjr");
        assertFalse(userDup.isPresent());
    }

    @Test
    public void testRegisterAdminIsIgnored() throws Exception {
        ManagedUserDTO validUser = new ManagedUserDTO(
                null, // id
                "badguy", // login
                "password", // password
                "Bad", // firstName
                "Guy", // lastName
                "badguy@example.com", // e-mail
                true, // activated
                "en", // langKey
                new HashSet<>(Arrays.asList(AuthoritiesConstants.ADMIN)),
                null, // createdDate
                null, // lastModifiedBy
                null // lastModifiedDate
        );

        Response response = target("api/register").post(json(validUser));
        assertThat(response, hasStatus(CREATED));

        Optional<User> userDup = ${userFacade}.findOneByLogin("badguy");
        assertTrue(userDup.isPresent());
        assertThat(userDup.get().getAuthorities().size(), is(1));
        assertThat(userDup.get().getAuthorities(), hasItems(${authorityFacade}.find(AuthoritiesConstants.USER)));

    }

    @Test
    public void assertThatOnlyActivatedUserCanRequestPasswordReset() {
        ManagedUserDTO user = new ManagedUserDTO(
                null, // id
                "gaurav", // login
                "password", // password
                "Gaurav", // firstName
                "Gupta", // lastName
                "gaurav.gupta.jc@example.com", // e-mail
                true, // activated
                "en", // langKey
                new HashSet<>(Arrays.asList(AuthoritiesConstants.USER)),
                null, // createdDate
                null, // lastModifiedBy
                null // lastModifiedDate
        );

        Response response = target("api/register").post(json(user));
        assertThat(response, hasStatus(CREATED));

        Response resetResponse = target("api/account/reset_password/init").post(Entity.text("gaurav.gupta.jc@example.com"));
        assertThat(resetResponse, hasStatus(BAD_REQUEST));
    }

    @Test
    public void assertThatUserMustExistToResetPassword() {
        Response resetResponse = target("api/account/reset_password/init").post(Entity.text("john.doe@example.com"));
        assertThat(resetResponse, hasStatus(BAD_REQUEST));

        resetResponse = target("api/account/reset_password/init").post(Entity.text("admin@example.com"));
        assertThat(resetResponse, hasStatus(OK));
    }

    @Test
    public void testfinishPasswordReset() {
        KeyAndPasswordDTO dto = new KeyAndPasswordDTO();
        dto.setKey("invalid_reset_key");
        dto.setNewPassword(PASSWORD);

        Response response = target("api/account/reset_password/finish").post(json(dto));
        assertThat(response, hasStatus(INTERNAL_SERVER_ERROR));

        dto.setNewPassword(INCORRECT_PASSWORD);
        response = target("api/account/reset_password/finish").post(json(dto));
        assertThat(response, hasStatus(BAD_REQUEST));
    }

    @Test
    public void testSaveAccount() throws Exception {
        Response response = target("api/account").get();
        assertThat(response, hasStatus(Response.Status.OK));
        ManagedUserDTO user = response.readEntity(ManagedUserDTO.class);
        user.setLastName("Gupta");

        response = target("api/account").post(json(user));
        assertThat(response, hasStatus(OK));
    }

    @Test
    public void testSaveUserDuplicateEmail() throws Exception {
        Response response = target("api/account").get();
        assertThat(response, hasStatus(Response.Status.OK));
        ManagedUserDTO user = response.readEntity(ManagedUserDTO.class);
        user.setEmail("user@example.com");

        response = target("api/account").post(json(user));
        assertThat(response, hasStatus(BAD_REQUEST));
    }

    @Test
    public void testChangePassword() throws Exception {
        //Invalid password
        Response response = target("api/account/change_password").post(text(INCORRECT_PASSWORD));
        assertThat(response, hasStatus(BAD_REQUEST));

        //Valid password
        response = target("api/account/change_password").post(text(PASSWORD));
        assertThat(response, hasStatus(OK));
    }

    @Test
    public void testActivateAccountInvalidResetKey() throws Exception {
        //Invalid Reset Key
        Response response = target("api/activate", singletonMap("key", "invalid_reset_key")).get();
        assertThat(response, hasStatus(INTERNAL_SERVER_ERROR));
    }

}
