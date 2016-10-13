<#if package??>package ${package};</#if>

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Utility class for Security.
 */
@SessionScoped
@Path("/api")
public class SecurityUtils implements Serializable {

    @Context
    private SecurityContext securityContext;

    @Path(value = "/current_user")
    @GET
    public String getCurrentUserLogin() {
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            return null;   
        }
        return securityContext.getUserPrincipal().getName();
    }
}
