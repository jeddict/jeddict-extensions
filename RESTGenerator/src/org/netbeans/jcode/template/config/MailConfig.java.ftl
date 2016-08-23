<#if package??>package ${package};</#if>

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.deltaspike.core.api.config.ConfigProperty;

@ApplicationScoped
public class MailConfig {

    @Inject
    @ConfigProperty(name = "service.mail.enable")
    private boolean enable;

    @Inject
    @ConfigProperty(name = "service.mail.host")
    private String host;

    @Inject
    @ConfigProperty(name = "service.mail.port")
    private int port;

    @Inject
    @ConfigProperty(name = "service.mail.auth.username")
    private String username;

    @Inject
    @ConfigProperty(name = "service.mail.auth.password")
    private String password;

    @Inject
    @ConfigProperty(name = "service.mail.from")
    private String from;

    /**
     * @return the enable
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

}
