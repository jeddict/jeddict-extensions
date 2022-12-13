package ${package};

import ${appPackage}${SecurityConfig_FQN};
import ${appPackage}${SecurityHelper_FQN};
import ${appPackage}${TemplateEngineProducer_FQN};
import ${appPackage}${MailService_FQN};
import ${appPackage}${MailNotifier_FQN};
import ${appPackage}${RandomUtil_FQN};
import ${appPackage}${UserService_FQN};
import ${appPackage}${LoginDTO_FQN};
import ${appPackage}${UserDTO_FQN};
import ${appPackage}${ManagedUserVM_FQN};
import ${appPackage}${AbstractAuditingEntity_FQN};
import ${appPackage}${AuditListner_FQN};
import ${appPackage}${Authority_FQN};
import ${appPackage}${User_FQN};
import ${appPackage}${AuthorityRepository_FQN};
import ${appPackage}${UserRepository_FQN};
import static ${package}.AbstractTest.buildArchive;
import java.io.File;
import java.net.URL;
import java.util.Map;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.Invocation;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import jakarta.ws.rs.core.Response;
import junit.framework.AssertionFailedError;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;

/**
 * Abstract class for application packaging.
 *
 */
public abstract class ApplicationTest extends AbstractTest {

    protected static final String USERNAME = "admin";
    protected static final String PASSWORD = "admin";
    protected static final String INVALID_PASSWORD = "invalid_password";
    protected static final String INCORRECT_PASSWORD = "pw";
    protected static final String NEW_PASSWORD = "newpw";
    protected static final String INVALID_RESET_KEY = "invalid_reset_key";

    protected String tokenId;

    protected AuthenticationControllerClient authClient;

    public static WebArchive buildApplication() {
        return buildArchive()
                .addPackages(true, 
                        SecurityConfig.class.getPackage(), 
                        MailService.class.getPackage(),
                        MailNotifier.class.getPackage(),
                        UserDTO.class.getPackage(),
                        ManagedUserVM.class.getPackage(),
                        SecurityHelper.class.getPackage(), 
                        RandomUtil.class.getPackage())
                .addClasses(
                        TemplateEngineProducer.class,
                        User.class,
                        Authority.class,
                        AbstractAuditingEntity.class,
                        AuditListner.class,
                        ${UserRepository}.class,
                        ${AuthorityRepository}.class,
                        ${UserService}.class,
                        ${AuthenticationController}.class,
                        ${applicationConfig}.class)
                .addAsResource("META-INF/sql/insert.sql")
                .addAsResource(new File("src/main/resources/config/application-common.properties"), "META-INF/microprofile-config.properties")
                .addAsResource("i18n/messages.properties")
                .addAsResource("publicKey.pem")
                .addAsResource("privateKey.pem");
    }

    @Before
    public void setUp() throws Exception {
        try {
            authClient = buildClient(AuthenticationControllerClient.class);
        } catch (Exception ex) {
            throw new AssertionFailedError(ex.getMessage());
        }
        login(USERNAME, PASSWORD);
    }

    @After
    public void tearDown() {
        logout();
    }

    protected Response login(String username, String password) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(password);

        Response response = authClient.login(loginDTO);
        tokenId = response.getHeaderString(AUTHORIZATION);
        return response;
    }

    protected void logout() {
        tokenId = null;
    }

    @Override
    protected Invocation.Builder target(String path) {
        return super.target(path).header(AUTHORIZATION, tokenId);
    }

    @Override
    protected Invocation.Builder target(String path, Map<String, Object> params) {
        return super.target(path, params).header(AUTHORIZATION, tokenId);
    }

    @Override
    protected <T> T buildClient(Class<? extends T> type) throws Exception {
        RestClientBuilder builder = RestClientBuilder.newBuilder();
        if (tokenId != null) {
            builder.register((ClientRequestFilter) context -> context.getHeaders().add(AUTHORIZATION, tokenId));
        }
        return builder.baseUrl(new URL(deploymentUrl.toURI().toString() + "resources/"))
                .build(type);
    }

}
