<#if package??>package ${package};</#if>

import ${SecurityConfig_FQN};
import io.jsonwebtoken.*;
import java.util.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;

public class TokenProvider {

    @Inject
    private Logger log;

    private static final String AUTHORITIES_KEY = "auth";

    private String secretKey;

    private long tokenValidityInSeconds;

    private long tokenValidityInSecondsForRememberMe;

    @Inject
    private SecurityConfig securityConfig;

    @PostConstruct
    public void init() {
        this.secretKey
                = securityConfig.getSecret();

        this.tokenValidityInSeconds
                = 1000 * securityConfig.getTokenValidityInSeconds();
        this.tokenValidityInSecondsForRememberMe
                = 1000 * securityConfig.getTokenValidityInSecondsForRememberMe();
    }

    public String createToken(String username, Set<String> authorities, Boolean rememberMe) {
        long now = (new Date()).getTime();
        long validity = now + (rememberMe ? tokenValidityInSecondsForRememberMe : tokenValidityInSeconds);

        return Jwts.builder()
                .setSubject(username)
                .claim(AUTHORITIES_KEY, authorities.stream().collect(joining(",")))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setExpiration(new Date(validity))
                .compact();
    }

    public JWTCredential getCredential(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        Set<String> authorities
                = Arrays.asList(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .stream()
                        .collect(toSet());

        return new JWTCredential(claims.getSubject(), authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature: {0}", e.getMessage());
            return false;
        }
    }
}
