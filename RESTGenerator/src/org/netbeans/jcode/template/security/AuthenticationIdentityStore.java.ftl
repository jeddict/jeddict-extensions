<#if package??>package ${package};</#if>

import ${UserService_FQN};
import ${Authority_FQN};
import ${User_FQN};
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;
import javax.security.enterprise.identitystore.IdentityStore;

@RequestScoped
public class AuthenticationIdentityStore implements IdentityStore {

    @Inject
    private UserService userService;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        CredentialValidationResult result;

        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePassword = (UsernamePasswordCredential) credential;
            User user;
            try {
                user = userService.authenticate(usernamePassword);
                result = new CredentialValidationResult(user.getLogin(),
                        user.getAuthorities()
                                .stream()
                                .map(Authority::getName)
                                .collect(Collectors.toSet())
                );
            } catch (AuthenticationException ex) {
                result = INVALID_RESULT;
            }
        } else {
            result = NOT_VALIDATED_RESULT;
        }
        return result;
    }

}
