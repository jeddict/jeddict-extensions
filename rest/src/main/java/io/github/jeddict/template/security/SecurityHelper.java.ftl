package ${package};

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;

/**
 * Utility class for Security.
 */
@RequestScoped
@Path("/api")
public class SecurityHelper {

    @Inject
    private SecurityContext securityContext;

    @Path(value = "/current-user")
    @GET
    public String getCurrentUserLogin() {
        if (securityContext == null || securityContext.getCallerPrincipal() == null) {
            return null;   
        }
        return securityContext.getCallerPrincipal().getName();
    }
}
