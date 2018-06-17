package ${package};

<#assign cid = (pkStrategy == "EmbeddedId" || pkStrategy == "IdClass") >
import ${appPackage}${EntityClass_FQN};
<#if cid || pagination != "no">
import javax.ws.rs.QueryParam;
</#if>
<#if !cid>
import javax.ws.rs.PathParam;
</#if>
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
