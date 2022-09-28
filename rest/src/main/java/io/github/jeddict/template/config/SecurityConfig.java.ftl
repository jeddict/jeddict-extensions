package ${package};

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SecurityConfig {

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    private String issuer;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.validityInSeconds")
    private long tokenValidityInSeconds;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.validityInSecondsForRememberMe")
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
