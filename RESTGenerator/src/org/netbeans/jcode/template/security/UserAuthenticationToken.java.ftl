<#if package??>package ${package};</#if>

import ${entityPackage}.Authority;
import java.util.Collection;

public class UserAuthenticationToken {

    private final String credentials;
    private final String principal;

    private final Collection<Authority> authorities;
    private boolean authenticated = false;

    public UserAuthenticationToken(String principal, String credentials, Collection<Authority> authorities) {
        this.principal = principal;
        this.credentials = credentials;
        this.authorities = authorities;
    }

    public UserAuthenticationToken(String principal, String credentials) {
        this(principal, credentials, null);
    }

    /**
     * @return the credentials
     */
    public String getCredentials() {
        return credentials;
    }

    /**
     * @return the principal
     */
    public String getPrincipal() {
        return principal;
    }

    /**
     * @return the authorities
     */
    public Collection<Authority> getAuthorities() {
        return authorities;
    }

    /**
     * @return the authenticated
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * @param authenticated the authenticated to set
     */
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

}
