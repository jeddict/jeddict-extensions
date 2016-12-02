<#if package??>package ${package};</#if>

import ${Constants_FQN};
import ${UserAuthenticationToken_FQN};
import ${Secured_FQN};
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import java.security.Principal;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;

@Priority(Priorities.AUTHENTICATION)
@Provider
@Secured
public class JWTAuthenticationFilter implements ContainerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Inject
    private TokenProvider tokenProvider;

    @Inject
    HttpServletRequest httpServletRequest;
    
    @Context
    private ResourceInfo resourceInfo;
    

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String jwt = resolveToken(httpServletRequest);
        if (StringUtils.isNotBlank(jwt)) {
            try {
                if (tokenProvider.validateToken(jwt)) {
                    UserAuthenticationToken authenticationToken = this.tokenProvider.getAuthentication(jwt);
                    if(!isAllowed(authenticationToken)){
                        requestContext.setProperty("auth-failed", true);
                        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                    }
                    final SecurityContext securityContext = requestContext.getSecurityContext();
                    requestContext.setSecurityContext(new SecurityContext() {
                        @Override
                        public Principal getUserPrincipal() {
                            return authenticationToken::getPrincipal;
                        }

                        @Override
                        public boolean isUserInRole(String role) {
                            return securityContext.isUserInRole(role);
                        }

                        @Override
                        public boolean isSecure() {
                            return securityContext.isSecure();
                        }

                        @Override
                        public String getAuthenticationScheme() {
                            return securityContext.getAuthenticationScheme();
                        }
                    });
                }
            } catch (ExpiredJwtException eje) {
                log.info("Security exception for user {} - {}", eje.getClaims().getSubject(), eje.getMessage());
                requestContext.setProperty("auth-failed", true);
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }

        } else {
            log.info("No JWT token found");
            requestContext.setProperty("auth-failed", true);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }

    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(Constants.AUTHORIZATION_HEADER);
        if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String jwt = bearerToken.substring(7, bearerToken.length());
            return jwt;
        }
        return null;
    }
    

    private boolean isAllowed(UserAuthenticationToken authenticationToken) {
        Secured secured = resourceInfo.getResourceMethod().getAnnotation(Secured.class);
        if (secured == null) {
            secured = resourceInfo.getResourceClass().getAnnotation(Secured.class);
        }
        for (String role : secured.value()) {
            if (!authenticationToken.getAuthorities().contains(role)) {
                return false;
            } 
        }
        return true;
    }
}
