package ${package};

<#assign cid = (pkStrategy == "EmbeddedId" || pkStrategy == "IdClass") >
import ${appPackage}${EntityClass_FQN};
<#if cid || pagination != "no">
import jakarta.ws.rs.QueryParam;
</#if>
<#if !cid>
import jakarta.ws.rs.PathParam;
</#if>
import java.util.List;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("/api/${entityApiUrl}")
public interface ${controllerClass}Client {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create${EntityClass}(${instanceType} ${instanceName});

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update${EntityClass}(${instanceType} ${instanceName});

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    <#if pagination == "no">
    public List<${instanceType}> getAll${EntityClassPlural}();
    <#else>
    public Response getAll${EntityClassPlural}(@QueryParam("page") int page, @QueryParam("size") int size);
    </#if>

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    <#if cid>
    @Path("/${pkName}")
    public Response get${EntityClass}(${restParamList});
    <#else>
    @Path("/{${pkName}}")
    public Response get${EntityClass}(@PathParam("${pkName}") ${pkType} ${pkName});
    </#if>

    @DELETE
    <#if cid>
    public Response remove${EntityClass}(${restParamList});
    <#else>
    @Path("/{${pkName}}")
    public Response remove${EntityClass}(@PathParam("${pkName}") ${pkType} ${pkName});
    </#if>

}
