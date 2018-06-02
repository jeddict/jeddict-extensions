package ${package};

import ${appPackage}${LoggerVM_FQN};
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
<#if metrics>import org.eclipse.microprofile.metrics.annotation.Timed;</#if>
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

/**
 * Controller for view and managing Log Level at runtime.
 */

@Path("/api")
public class ${LogsController} {

    @Path("/logs")
    @GET<#if metrics>
    @Timed</#if>
    public List<LoggerVM> getList() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        return context.getLoggerList()
            .stream()
            .map(LoggerVM::new)
            .collect(Collectors.toList());
    }
    
    @Path("/logs")
    @PUT<#if metrics>
    @Timed</#if>
    public void changeLevel(LoggerVM jsonLogger) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
    }
}
