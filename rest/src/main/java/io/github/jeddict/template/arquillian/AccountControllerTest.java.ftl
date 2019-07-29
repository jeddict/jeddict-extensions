package ${package};

import static ${appPackage}${ApplicationTest_FQN}.USERNAME;
import ${appPackage}${KeyAndPasswordVM_FQN};
import ${appPackage}${ManagedUserVM_FQN};
import ${appPackage}${PasswordChangeVM_FQN};
import ${appPackage}${UserDTO_FQN};
import static ${appPackage}${AuthoritiesConstants_FQN}.ADMIN;
import static ${appPackage}${AuthoritiesConstants_FQN}.USER;
import static java.util.Collections.singleton;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import static org.valid4j.matchers.http.HttpResponseMatchers.hasStatus;

/**
 * Test class for the ${AccountController} REST controller.
 *
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ${AccountControllerTest} extends ApplicationTest {

    private ${AccountController}Client client;

    @Deployment
    public static WebArchive createDeployment() {
        return buildApplication()
                .addClass(${AccountController}.class);
    }

    @Before
    public void buildClient() throws Exception {
        client = buildClient(${AccountController}Client.class);
    }

    @Test
    public void testGetExistingAccount() {
        Response response = client.getAccount();
        assertThat(response, hasStatus(OK));
        UserDTO user = response.readEntity(UserDTO.class);
        assertNotNull(user);
        assertThat(user.getLogin(), is(USERNAME));
    }

    @Test
    public void testGetUnknownAccount() throws Exception {
        logout();
        assertWebException(UNAUTHORIZED, () -> client.getAccount());
    }

    @Test
    public void testRegisterValid() throws Exception {
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

        Response response = client.registerAccount(validUser);
        assertThat(response, hasStatus(CREATED));
    }

<#--
    @Test
    @Ignore
    public void testRegisterInvalidLogin() throws Exception {
        ManagedUserVM invalidUser = new ManagedUserVM(
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

        Optional<User> user = ${userRepository}.findOneByEmail("funky@example.com");
        assertFalse(user.isPresent());
    }

    @Test
    @Ignore
    public void testRegisterInvalidEmail() throws Exception {
        ManagedUserVM invalidUser = new ManagedUserVM(
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

        Optional<User> user = ${userRepository}.findOneByLogin("bob");
        assertFalse(user.isPresent());

    }

    @Test
    @Ignore
    public void testRegisterInvalidPassword() throws Exception {
        ManagedUserVM invalidUser = new ManagedUserVM(
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

        Optional<User> user = ${userRepository}.findOneByLogin("bob");
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

        Optional<User> user = ${userRepository}.findOneByEmail("funky@example.com");
        assertFalse(user.isPresent());
    }
-->
    @Test
    public void testRegisterDuplicateLogin() throws Exception {
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
                validUser.getId(), validUser.getLogin(), validUser.getPassword(),
                validUser.getFirstName(), validUser.getLastName(),
                "alicejr@example.com", validUser.isActivated(), validUser.getLangKey(),
                validUser.getCreatedBy(), validUser.getCreatedDate(),
                validUser.getLastModifiedBy(), validUser.getLastModifiedDate(),
                validUser.getAuthorities()
        );

        // Good user
        Response response = client.registerAccount(validUser);
        assertThat(response, hasStatus(CREATED));

        // Duplicate login
        assertWebException(BAD_REQUEST, () -> client.registerAccount(duplicatedUser));
    }

    @Test
    public void testRegisterDuplicateEmail() throws Exception {
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
                validUser.getId(), "johnjr", validUser.getPassword(), 
                validUser.getFirstName(), validUser.getLastName(),
                validUser.getEmail(), true, validUser.getLangKey(), 
                validUser.getCreatedBy(), validUser.getCreatedDate(), 
                validUser.getLastModifiedBy(), validUser.getLastModifiedDate(),
                validUser.getAuthorities()
        );

        // Good user
        Response response = client.registerAccount(validUser);
        assertThat(response, hasStatus(CREATED));

        // Duplicate  e-mail
        assertWebException(BAD_REQUEST, () -> client.registerAccount(duplicatedUser));
    }

    @Test
    public void testRegisterAdminIsIgnored() throws Exception {
        ManagedUserVM validUser = new ManagedUserVM(
                null, // id
                "badguy", // login
                "password", // password
                "Bad", // firstName
                "Guy", // lastName
                "badguy@example.com", // e-mail
                true, // activated
                "en", // langKey
                null, // createdBy
                null, // createdDate
                null, // lastModifiedBy
                null, // lastModifiedDate
                singleton(ADMIN) // authorities
        );

        Response response = client.registerAccount(validUser);
        assertThat(response, hasStatus(CREATED));

<#--
        //Optional<User> userDup = ${userRepository}.findOneByLogin("badguy");
        //assertTrue(userDup.isPresent());
        //assertThat(userDup.get().getAuthorities().size(), is(1));
        //assertThat(userDup.get().getAuthorities(), hasItems(${authorityRepository}.find(AuthoritiesConstants.USER))); -->
    }

    @Test
    public void assertThatOnlyActivatedUserCanRequestPasswordReset() {
        ManagedUserVM user = new ManagedUserVM(
                null, // id
                "gaurav", // login
                "password", // password
                "Gaurav", // firstName
                "Gupta", // lastName
                "gaurav.gupta.jc@example.com", // e-mail
                true, // activated
                "en", // langKey
                null, // createdBy
                null, // createdDate
                null, // lastModifiedBy
                null, // lastModifiedDate
                singleton(USER) // authorities
        );

        Response response = client.registerAccount(user);
        assertThat(response, hasStatus(CREATED));

        assertWebException(BAD_REQUEST, () -> client.requestPasswordReset("gaurav.gupta.jc@example.com"));
    }

    @Test
    public void assertThatUserMustExistToResetPassword() {
        assertWebException(BAD_REQUEST, () -> client.requestPasswordReset("john.doe@example.com"));

        Response response = client.requestPasswordReset("admin@example.com");
        assertThat(response, hasStatus(OK));
    }

    @Test
    public void testfinishPasswordReset() {
        KeyAndPasswordVM keyAndPasswordVM = new KeyAndPasswordVM();
        keyAndPasswordVM.setKey(INVALID_RESET_KEY);
        keyAndPasswordVM.setNewPassword(PASSWORD);
        assertWebException(INTERNAL_SERVER_ERROR, () -> client.finishPasswordReset(keyAndPasswordVM));

        keyAndPasswordVM.setNewPassword(INCORRECT_PASSWORD);
        assertWebException(BAD_REQUEST, () -> client.finishPasswordReset(keyAndPasswordVM));
    }

    @Test
    public void testSaveAccount() throws Exception {
        Response response = client.getAccount();
        assertThat(response, hasStatus(OK));
        ManagedUserVM user = response.readEntity(ManagedUserVM.class);
        user.setLastName("Gupta");

        response = client.saveAccount(user);
        assertThat(response, hasStatus(OK));
    }

    @Test
    public void testSaveUserDuplicateEmail() throws Exception {
        Response response = client.getAccount();
        assertThat(response, hasStatus(OK));
        ManagedUserVM user = response.readEntity(ManagedUserVM.class);
        user.setEmail("user@example.com");
        assertWebException(BAD_REQUEST, () -> client.saveAccount(user));
    }

    @Test
    public void testChangePassword() throws Exception {
        Response response;
        PasswordChangeVM passwordChangeVM = new PasswordChangeVM();
        
        //Invalid password
        passwordChangeVM.setCurrentPassword(PASSWORD);
        passwordChangeVM.setNewPassword(INCORRECT_PASSWORD);
        assertWebException(BAD_REQUEST, () -> client.changePassword(passwordChangeVM));

        //Valid password
        passwordChangeVM.setNewPassword(NEW_PASSWORD);
        response = client.changePassword(passwordChangeVM);
        assertThat(response, hasStatus(OK));
        
        //Valid password
        passwordChangeVM.setCurrentPassword(NEW_PASSWORD);
        passwordChangeVM.setNewPassword(PASSWORD);
        response = client.changePassword(passwordChangeVM);
        assertThat(response, hasStatus(OK));
    }

    @Test
    public void testActivateAccountInvalidResetKey() throws Exception {
        //Invalid Reset Key
        assertWebException(INTERNAL_SERVER_ERROR, () -> client.activateAccount(INVALID_RESET_KEY));
    }

}
