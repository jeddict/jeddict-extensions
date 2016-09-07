<#if package??>package ${package};</#if>

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.deltaspike.core.api.config.ConfigProperty;

@ApplicationScoped
public class MetricsConfig {

    @Inject
    @ConfigProperty(name = "metrics.jmx.enable")
    private boolean jmxEnable;
    
    @Inject
    @ConfigProperty(name = "metrics.logs.enable")
    private boolean logsEnable;

    /**
     * @return the jmxEnable
     */
    public boolean isJMXEnable() {
        return jmxEnable;
    }

    /**
     * @return the logsEnable
     */
    public boolean isLogsEnable() {
        return logsEnable;
    }
}
