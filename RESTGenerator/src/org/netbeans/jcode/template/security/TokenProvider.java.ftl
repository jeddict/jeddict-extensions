<#if package??>package ${package};</#if>

import ${SecurityConfig_FQN};
import ${User_FQN};
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.*;

public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

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

    public String createToken(User user, Boolean rememberMe) {
        String authorities = user.getAuthorities().stream()
                .map(authority -> authority.getName())
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInSecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInSeconds);
        }

        return Jwts.builder()
                .setSubject(user.getLogin())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setExpiration(validity)
                .compact();
    }

    public UserAuthenticationToken getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        Set<String> authorities
                = Arrays.asList(claims.get(AUTHORITIES_KEY).toString().split(",")).stream()
                .collect(Collectors.toSet());

        return new UserAuthenticationToken(claims.getSubject(), "", authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature: " + e.getMessage());
            return false;
        }
    }
}
