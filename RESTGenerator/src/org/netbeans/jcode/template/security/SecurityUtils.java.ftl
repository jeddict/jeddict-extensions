<#if package??>package ${package};</#if>

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Utility class for Security.
 */
@SessionScoped
public class SecurityUtils implements Serializable {

    @Context
    private SecurityContext securityContext;

    public String getCurrentUserLogin() {
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            return null;
        }
        return securityContext.getUserPrincipal().getName();
    }
}
