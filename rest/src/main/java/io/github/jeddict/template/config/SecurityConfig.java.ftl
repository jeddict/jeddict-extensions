package ${package};

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SecurityConfig {

    @Inject
    @ConfigProperty(name = "security.jwt.issuer")
    private String issuer;

    @Inject
    @ConfigProperty(name = "security.jwt.validityInSeconds")
    private long tokenValidityInSeconds;

    @Inject
    @ConfigProperty(name = "security.jwt.validityInSecondsForRememberMe")
    private long tokenValidityInSecondsForRememberMe;

    /**
     * @return the issuer
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * @return the tokenValidityInSeconds
     */
    public long getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
    }

    /**
     * @return the tokenValidityInSecondsForRememberMe
     */
    public long getTokenValidityInSecondsForRememberMe() {
        return tokenValidityInSecondsForRememberMe;
    }

}
