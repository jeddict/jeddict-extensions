package ${package};

import static ${appPackage}${AuthoritiesConstants_FQN}.ADMIN;
import static ${appPackage}${AuthoritiesConstants_FQN}.USER;
import javax.annotation.security.DeclareRoles;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;
import org.eclipse.microprofile.auth.LoginConfig;

@LoginConfig(
    authMethod = "MP-JWT",
    realmName = "MP-JWT"
)
@DeclareRoles({ADMIN, USER})
@ApplicationPath("${applicationPath}")
public class ApplicationTestConfig extends Application {

}
