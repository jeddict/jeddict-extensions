package ${package};

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;

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
