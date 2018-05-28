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
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

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
    private static final String AUTH_RESOURCE_PATH = "api/authenticate";

    protected String tokenId;

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
                .addClass(TemplateEngineProducer.class)
                .addClass(User.class)
                .addClass(Authority.class)
                .addClass(AbstractAuditingEntity.class)
                .addClass(AuditListner.class)
                .addClass(${UserRepository}.class)
                .addClass(${AuthorityRepository}.class)
                .addClass(${UserService}.class)
                .addClass(${AuthenticationController}.class)
                .addClass(AbstractTest.class)
                .addClass(ApplicationTest.class)
                .addClass(ApplicationTestConfig.class)
                .addAsResource("META-INF/sql/insert.sql")
                .addAsResource("META-INF/microprofile-config.properties")
                .addAsResource("i18n/messages.properties")
                .addAsResource("payara-mp-jwt.properties")
                .addAsResource("publicKey.pem")
                .addAsResource("privateKey.pem");
    }

    @Before
    public void setUp() throws Exception {
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
        Response response = target(AUTH_RESOURCE_PATH).post(Entity.json(loginDTO));
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

}
