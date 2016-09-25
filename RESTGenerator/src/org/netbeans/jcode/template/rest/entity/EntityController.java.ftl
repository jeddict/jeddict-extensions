package ${package};

import ${EntityClass_FQN};
import ${EntityFacade_FQN};
import ${HeaderUtil_FQN};
import ${Secured_FQN};
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
<#if metrics>import com.codahale.metrics.annotation.Timed;</#if>
<#if docs>import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;</#if>

/**
 * REST controller for managing ${EntityClass}.
 */
<#if docs>@Api(value = "/api/${entityApiUrl}", description = "${controllerClassHumanized}")</#if>
@Path("/api/${entityApiUrl}")
@Secured
public class ${controllerClass} {

    private final Logger log = LoggerFactory.getLogger(${controllerClass}.class);

    @Inject
    private ${EntityFacade} ${entityFacade};

    /**
     * POST : Create a new ${entityInstance}.
     *
     * @param ${instanceName} the ${instanceName} to create
     * @return the Response with status 201 (Created) and with body the
     * new ${instanceName}, or with status 400 (Bad Request) if the ${entityInstance} has already
     * an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "create a new ${entityInstance}", notes = "Create a new ${entityInstance}")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created"),
        @ApiResponse(code = 400, message = "Bad Request")})</#if>
    @POST
    public Response create${EntityClass}(${instanceType} ${instanceName}) throws URISyntaxException {
        log.debug("REST request to save ${EntityClass} : {}", ${instanceName});
        ${entityFacade}.create(${instanceName});
        return HeaderUtil.createEntityCreationAlert(Response.created(new URI("/${applicationPath}/api/${entityApiUrl}/" + ${instanceName}.${pkGetter}())),
                "${entityInstance}", ${instanceName}.${pkGetter}().toString())
                .entity(${instanceName}).build();
    }

    /**
     * PUT : Updates an existing ${entityInstance}.
     *
     * @param ${instanceName} the ${instanceName} to update
     * @return the Response with status 200 (OK) and with body the updated ${instanceName},
     * or with status 400 (Bad Request) if the ${instanceName} is not valid,
     * or with status 500 (Internal Server Error) if the ${instanceName} couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "update ${entityInstance}", notes = "Updates an existing ${entityInstance}")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 400, message = "Bad Request"),
        @ApiResponse(code = 500, message = "Internal Server Error")})</#if>
    @PUT
    public Response update${EntityClass}(${instanceType} ${instanceName}) throws URISyntaxException {
        log.debug("REST request to update ${EntityClass} : {}", ${instanceName});
        ${entityFacade}.edit(${instanceName});
        return HeaderUtil.createEntityUpdateAlert(Response.ok(), "${entityInstance}", ${instanceName}.${pkGetter}().toString())
                .entity(${instanceName}).build();
    }

    /**
     * GET : get all the ${entityInstancePlural}.
     *<% if (pagination != 'no') {}
     * @param pageable the pagination information<% } if (fieldsContainNoOwnerOneToOne) {}
     * @param filter the filter of the request<% }}
     * @return the Response with status 200 (OK) and the list of ${entityInstancePlural} in body<% if (pagination != 'no') {}
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers<% }}
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "get all the ${entityInstancePlural}")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK")})</#if>
    @GET
    public List<${instanceType}> getAll${EntityClassPlural}() {
        log.debug("REST request to get all ${EntityClassPlural}");
        List<${EntityClass}> ${entityInstancePlural} = ${entityFacade}.findAll();
        return ${entityInstancePlural};
    }

    /**
     * GET /:${pkName} : get the "${pkName}" ${entityInstance}.
     *
     * @param ${pkName} the ${pkName} of the ${instanceName} to retrieve
     * @return the Response with status 200 (OK) and with body the ${instanceName}, or with status 404 (Not Found)
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "get the ${entityInstance}")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})</#if>
    @Path("/{${pkName}}")
    @GET
    public Response get${EntityClass}(@PathParam("${pkName}") ${pkType} ${pkName}) {
        log.debug("REST request to get ${EntityClass} : {}", ${pkName});
        ${instanceType} ${instanceName} = ${entityFacade}.find(${pkName});
        return Optional.ofNullable(${instanceName})
                .map(result -> Response.status(Response.Status.OK).entity(${instanceName}).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * DELETE /:${pkName} : remove the "${pkName}" ${entityInstance}.
     * 
     * @param ${pkName} the ${pkName} of the ${instanceName} to delete
     * @return the Response with status 200 (OK)
     */
    <#if metrics>@Timed</#if>
    <#if docs>@ApiOperation(value = "remove the ${entityInstance}" )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})</#if>
    @Path("/{${pkName}}")
    @DELETE
    public Response remove${EntityClass}(@PathParam("${pkName}") ${pkType} ${pkName}) {
        log.debug("REST request to delete ${EntityClass} : {}", ${pkName});
        ${entityFacade}.remove(${entityFacade}.find(${pkName}));
        return HeaderUtil.createEntityDeletionAlert(Response.ok(), "${entityInstance}", ${pkName}.toString()).build();
    }

}
