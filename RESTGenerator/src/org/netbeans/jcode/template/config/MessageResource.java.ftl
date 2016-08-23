<#if package??>package ${package};</#if>

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.api.config.PropertyFileConfig;

@ApplicationScoped
public class MessageResource implements PropertyFileConfig {

    @Inject
    @ConfigProperty(name = "email.activation.title")
    private String activationTitle;

    @Inject
    @ConfigProperty(name = "email.creation.title")
    private String creationTitle;

    @Inject
    @ConfigProperty(name = "email.reset.title")
    private String resetTitle;

    @Override
    public String getPropertyFileName() {
        return "i18n/messages.properties";
    }

    @Override
    public boolean isOptional() {
        return false;
    }

    /**
     * @return the activationTitle
     */
    public String getActivationTitle() {
        return activationTitle;
    }

    /**
     * @return the creationTitle
     */
    public String getCreationTitle() {
        return creationTitle;
    }

    /**
     * @return the resetTitle
     */
    public String getResetTitle() {
        return resetTitle;
    }

}
