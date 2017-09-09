<#if package??>package ${package};</#if>

import static ${Constants_FQN}.AUTHORIZATION_HEADER;
import static ${Constants_FQN}.BEARER_PREFIX;
import ${LoginDTO_FQN};
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import static ${AuthenticationController_FQN}.AUTHENTICATION_ENDPOINT;

@ApplicationScoped
public class JWTAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    private Logger log;

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Inject
    private TokenProvider tokenProvider;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context) {

        String token;
        LoginDTO loginDTO;
        if (AUTHENTICATION_ENDPOINT.equals(context.getRequest().getServletPath()) 
                && (loginDTO = extractLoginCredential(context)) != null) {
            // validation of the credential using the identity store
            CredentialValidationResult result = identityStoreHandler.validate(
                    new UsernamePasswordCredential(loginDTO.getUsername(), loginDTO.getPassword()));
            if (result.getStatus() == CredentialValidationResult.Status.VALID) {
                // create the token and notify the container about authenticated user
                return createToken(result, loginDTO.isRememberMe(), context);
            }
            // if the authentication failed, we return the unauthorized status in the http response
            return context.responseUnauthorized();
        } else if ((token = extractToken(context)) != null) {
            // validation of the jwt credential
            return validateToken(token, context);
        } else if (context.isProtected()) {
            // if there are no credentials and the resource is protected, we response with unauthorized status
            return context.responseUnauthorized();
        }
        // there are no credentials and the resource is not protected, so notify the container to "do nothing"
        return context.doNothing();
    }

    /**
     * To extract the login credential from http request
     *
     * @param context
     * @return The login credential
     */
    private LoginDTO extractLoginCredential(HttpMessageContext context) {
        LoginDTO loginDTO = null;
        try {
            Jsonb jsonb = JsonbBuilder.create();
            loginDTO = jsonb.fromJson(context.getRequest().getInputStream(), LoginDTO.class);
        } catch (IOException | JsonbException ex) {
            log.error(ex.getMessage(), ex);
        }
        return loginDTO;
    }

    /**
     * To validate the JWT token e.g Signature check, JWT claims
     * check(expiration) etc
     *
     * @param token The JWT access tokens
     * @param context
     * @return the AuthenticationStatus to notify the container
     */
    private AuthenticationStatus validateToken(String token, HttpMessageContext context) {
        try {
            if (tokenProvider.validateToken(token)) {
                JWTCredential credential = tokenProvider.getCredential(token);
                return context.notifyContainerAboutLogin(credential.getPrincipal(), credential.getAuthorities());
            }
            // if token invalid, response with unauthorized status
            return context.responseUnauthorized();
        } catch (ExpiredJwtException eje) {
            log.info("Security exception for user {0} - {1}", new Object[]{eje.getClaims().getSubject(), eje.getMessage()});
            return context.responseUnauthorized();
        }
    }

    /**
     * Create the JWT using CredentialValidationResult received from
     * IdentityStoreHandler
     *
     * @param result the result from validation of UsernamePasswordCredential
     * @param context
     * @return the AuthenticationStatus to notify the container
     */
    private AuthenticationStatus createToken(CredentialValidationResult result, boolean rememberMe, HttpMessageContext context) {
        String token = tokenProvider.createToken(
                result.getCallerPrincipal().getName(),
                result.getCallerGroups(),
                rememberMe
        );
        context.getResponse().setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        return context.notifyContainerAboutLogin(result);
    }

    /**
     * To extract the JWT from Authorization HTTP header
     *
     * @param context
     * @return The JWT access tokens
     */
    private String extractToken(HttpMessageContext context) {
        String authorizationHeader = context.getRequest().getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length(), authorizationHeader.length());
            return token;
        }
        return null;
    }

}
