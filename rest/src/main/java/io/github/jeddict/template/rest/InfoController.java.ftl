package ${package};

import java.util.List;
import java.util.Optional;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Controller for profile information.
 */
@Path("/api/info")
public class InfoController {

    @Inject
    @ConfigProperty(name = "display.ribbon")
    private Optional<String> displayRibbonOnProfiles;

    @Inject
    @ConfigProperty(name = "active.profiles")
    private List<String> activeProfiles;

    @GET
    public JsonObject getInfo() {
        JsonObjectBuilder buider = Json.createObjectBuilder()
                .add("activeProfiles", Json.createArrayBuilder(activeProfiles));
        displayRibbonOnProfiles.ifPresent(value -> buider.add("display-ribbon-on-profiles", value));
        return buider.build();
    }

}
