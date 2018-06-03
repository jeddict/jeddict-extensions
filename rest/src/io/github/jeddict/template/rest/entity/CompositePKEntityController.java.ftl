package ${package};

import ${appPackage}${EntityClass_FQN};
import ${appPackage}${EntityPKClass_FQN};
import ${appPackage}${EntityRepository_FQN};
import ${appPackage}${HeaderUtil_FQN};
import static ${appPackage}${AuthoritiesConstants_FQN}.USER;
import org.slf4j.Logger;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;<#if pagination != "no">
import javax.ws.rs.core.Response.ResponseBuilder;
import ${appPackage}${Page_FQN};
import ${appPackage}${PaginationUtil_FQN};</#if><#if metrics>
import org.eclipse.microprofile.metrics.annotation.Timed;</#if>
import org.eclipse.microprofile.faulttolerance.Timeout;<#if openAPI>
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;</#if>

/**
 * REST controller for managing ${EntityClass}.
 */
@Path("/api/${entityApiUrl}")
@RolesAllowed(USER)
public class ${controllerClass} {

    @Inject
    private Logger log;

    @Inject
    private ${EntityRepository} ${entityRepository};

    private static final String ENTITY_NAME = "${entityTranslationKey}";

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
    <#if openAPI>@Operation(summary = "create a new ${entityInstance}", notes = "Create a new ${entityInstance}")
    @APIResponse(responseCode = "201", description = "Created")
    @APIResponse(responseCode = "400", description = "Bad Request")</#if>
    @POST
    public Response create${EntityClass}(${instanceType} ${instanceName}) throws URISyntaxException {
        log.debug("REST request to save ${EntityClass} : {}", ${instanceName});
        ${entityRepository}.create(${instanceName});
        return HeaderUtil.createEntityCreationAlert(Response.created(new URI("/${applicationPath}/api/${entityApiUrl}")),
                ENTITY_NAME, ${entityRepository}.getIdentifier(${instanceName}).toString())
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
    <#if openAPI>@Operation(summary = "update ${entityInstance}", notes = "Updates an existing ${entityInstance}")
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "400", description = "Bad Request")
    @APIResponse(responseCode = "500", description = "Internal Server Error")</#if>
    @PUT
    public Response update${EntityClass}(${instanceType} ${instanceName}) throws URISyntaxException {
        log.debug("REST request to update ${EntityClass} : {}", ${instanceName});
        ${entityRepository}.edit(${instanceName});
        return HeaderUtil.createEntityUpdateAlert(Response.ok(), ENTITY_NAME, ${entityRepository}.getIdentifier(${instanceName}).toString())
                .entity(${instanceName}).build();
    }

    /**
     * GET : get all the ${entityInstancePlural}.
     <#if pagination!= "no">* @param page the pagination information
     * @param size the pagination size information
     <#elseif fieldsContainNoOwnerOneToOne>* @param filter the filter of the request</#if>
     * @return the Response with status 200 (OK) and the list of ${entityInstancePlural} in body
     <#if pagination!= "no">* @throws URISyntaxException if there is an error to generate the pagination HTTP headers</#if>
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "get all the ${entityInstancePlural}")
    @APIResponse(responseCode = "200", description = "OK")</#if>
    @GET
    @Timeout
    <#if pagination == "no">
    public List<${instanceType}> getAll${EntityClassPlural}() {
        log.debug("REST request to get all ${EntityClassPlural}");
        List<${EntityClass}> ${entityInstancePlural} = ${entityRepository}.findAll();
        return ${entityInstancePlural};
    }
    <#else>
    public Response getAll${EntityClassPlural}(@QueryParam("page") int page, @QueryParam("size") int size) throws URISyntaxException {
        log.debug("REST request to get all ${EntityClassPlural}");
        List<${EntityClass}> ${entityInstancePlural} = ${entityRepository}.findRange(page * size, size);
        ResponseBuilder builder = Response.ok(${entityInstancePlural});
        PaginationUtil.generatePaginationHttpHeaders(builder, new Page(page, size, ${entityRepository}.count()), "/${applicationPath}/api/${entityApiUrl}");
        return builder.build();
    }
    </#if>

    /**
     * GET /:${pkName} : get the "${pkName}" ${entityInstance}.
     *
     * ${restDocList}
     * @return the Response with status 200 (OK) and with body the ${instanceName}, or with status 404 (Not Found)
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "get the ${entityInstance}")
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "404", description = "Not Found")</#if>
    @Path("/${pkName}")
    @GET
    public Response get${EntityClass}(${restParamList}) {
        ${pkType} ${pkName} = new ${pkType}(${restParamNameList});
        log.debug("REST request to get ${EntityClass} : {}", ${pkName});
        ${instanceType} ${instanceName} = ${entityRepository}.find(${pkName});
        return Optional.ofNullable(${instanceName})
                .map(result -> Response.status(Response.Status.OK).entity(${instanceName}).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * DELETE /:${pkName} : remove the "${pkName}" ${entityInstance}.
     * 
     * ${restDocList}
     * @return the Response with status 200 (OK)
     */
    <#if metrics>@Timed</#if>
    <#if openAPI>@Operation(summary = "remove the ${entityInstance}" )
    @APIResponse(responseCode = "200", description = "OK")
    @APIResponse(responseCode = "404", description = "Not Found")</#if>
    @DELETE
    public Response remove${EntityClass}(${restParamList}) {
        ${pkType} ${pkName} = new ${pkType}(${restParamNameList});
        log.debug("REST request to delete ${EntityClass} : {}", ${pkName});
        ${entityRepository}.remove(${entityRepository}.find(${pkName}));
        return HeaderUtil.createEntityDeletionAlert(Response.ok(), ENTITY_NAME, ${pkName}.toString()).build();
    }

}
