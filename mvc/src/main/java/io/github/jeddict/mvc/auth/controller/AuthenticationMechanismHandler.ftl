package ${package};

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.auth.message.AuthException;
import jakarta.security.auth.message.AuthStatus;
import jakarta.security.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.authentication.mechanism.http.annotation.AutoApplySession;
import jakarta.security.identitystore.CredentialValidationResult;
import static jakarta.security.identitystore.CredentialValidationResult.Status.VALID;
import jakarta.security.identitystore.IdentityStore;
import jakarta.security.identitystore.credential.Password;
import jakarta.security.identitystore.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@AutoApplySession
@RequestScoped
public class AuthenticationMechanismHandler implements HttpAuthenticationMechanism {

    @Inject
    private IdentityStore identityStore;

    @Override
    public AuthStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthException {
        String name = request.getParameter("name");
        if (name != null && request.getParameter("password") != null) {

            Password password = new Password(request.getParameter("password"));

            CredentialValidationResult result = identityStore.validate(new UsernamePasswordCredential(name, password));

            if (result.getStatus() == VALID) {
                return httpMessageContext.notifyContainerAboutLogin(result.getCallerPrincipal(), result.getCallerGroups());
            } else {
                return httpMessageContext.responseUnAuthorized();
            }
        }

        return httpMessageContext.doNothing();
    }

}
