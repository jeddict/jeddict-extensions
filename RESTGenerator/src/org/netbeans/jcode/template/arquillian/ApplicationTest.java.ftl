<#if package??>package ${package};</#if>

import ${SecurityConfig_FQN};
import ${Constants_FQN};
import ${SecurityHelper_FQN};
import ${TemplateEngineProducer_FQN};
import ${MailService_FQN};
import ${RandomUtil_FQN};
import ${UserService_FQN};
import ${LoginDTO_FQN};
import ${UserDTO_FQN};
import ${AbstractAuditingEntity_FQN};
import ${AuditListner_FQN};
import ${Authority_FQN};
import ${User_FQN};
import ${AuthorityRepository_FQN};
import ${UserRepository_FQN};
import static ${package}.AbstractTest.buildArchive;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
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
    private static final String AUTH_RESOURCE_PATH = "api/authenticate";

    protected String tokenId;

    public static WebArchive buildApplication() {
        return buildArchive()
                .addPackages(true, 
                        SecurityConfig.class.getPackage(), 
                        MailService.class.getPackage(), 
                        UserDTO.class.getPackage(), 
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
                .addAsResource(new ClassLoaderAsset("META-INF/microprofile-config.properties"), "META-INF/microprofile-config.properties")
                .addAsResource(new ClassLoaderAsset("i18n/messages.properties"), "i18n/messages.properties");
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
        tokenId = response.getHeaderString(Constants.AUTHORIZATION_HEADER);
        return response;
    }

    protected void logout() {
        tokenId = null;
    }

    @Override
    protected Invocation.Builder target(String path) {
        return super.target(path).header(Constants.AUTHORIZATION_HEADER, tokenId);
    }

    @Override
    protected Invocation.Builder target(String path, Map<String, Object> params) {
        return super.target(path, params).header(Constants.AUTHORIZATION_HEADER, tokenId);
    }

}
