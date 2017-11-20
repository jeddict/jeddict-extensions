package ${package};

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
<#if security == "JAXRS_JWT">import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;<#assign type = "User" ><#elseif security == "SECURITY_JWT">
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;<#assign type = "Caller" ></#if>

/**
 * Utility class for Security.
 */
@RequestScoped
@Path("/api")
public class SecurityHelper {

    <#if security == "JAXRS_JWT">@Context<#elseif security == "SECURITY_JWT">@Inject</#if>
    private SecurityContext securityContext;

    @Path(value = "/current-user")
    @GET
    public String getCurrentUserLogin() {
        if (securityContext == null || securityContext.get${type}Principal() == null) {
            return null;   
        }
        return securityContext.get${type}Principal().getName();
    }
}
