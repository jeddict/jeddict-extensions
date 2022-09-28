package ${package};

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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
