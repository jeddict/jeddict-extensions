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

/**
 * REST controller for managing ${EntityClass}.
 */
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
     * @return the ResponseEntity with status 201 (Created) and with body the
     * new ${instanceName}, or with status 400 (Bad Request) if the ${entityInstance} has already
     * an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    
    @POST
    public Response create${EntityClass}(${instanceType} ${instanceName}) throws URISyntaxException {
        log.debug("REST request to save ${EntityClass} : {}", ${instanceName});
        ${instanceName} = ${entityFacade}.edit(${instanceName});
        return HeaderUtil.createEntityCreationAlert(Response.created(new URI("/${applicationPath}/api/${entityApiUrl}/" + ${instanceName}.getId())),
                "${entityInstance}", ${instanceName}.getId().toString())
                .entity(${instanceName}).build();
    }

    /**
     * PUT : Updates an existing ${entityInstance}.
     *
     * @param ${instanceName} the ${instanceName} to update
     * @return the Response with status 200 (OK) and with body the updated ${instanceName},
     * or with status 400 (Bad Request) if the ${instanceName} is not valid,
     * or with status 500 (Internal Server Error) if the ${instanceName} couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    
    @PUT
    public Response update${EntityClass}(${instanceType} ${instanceName}) throws URISyntaxException {
        log.debug("REST request to update ${EntityClass} : {}", ${instanceName});
        ${entityFacade}.edit(${instanceName});
        return HeaderUtil.createEntityUpdateAlert(Response.ok(), "${entityInstance}", ${instanceName}.getId().toString())
                .entity(${instanceName}).build();
    }

    /**
     * GET : get all the ${entityInstancePlural}.
     *<% if (pagination != 'no') {}
     * @param pageable the pagination information<% } if (fieldsContainNoOwnerOneToOne) {}
     * @param filter the filter of the request<% }}
     * @return the ResponseEntity with status 200 (OK) and the list of ${entityInstancePlural} in body<% if (pagination != 'no') {}
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers<% }}
     */
    
    @GET
    public List<${instanceType}> getAll${EntityClassPlural}() {
        log.debug("REST request to get all ${EntityClassPlural}");
        List<${EntityClass}> ${entityInstancePlural} = ${entityFacade}.findAll();
        return ${entityInstancePlural};
    }

    /**
     * GET /:${pkName} : get the "${pkName} ${entityInstance}.
     *
     * @param ${pkName} the ${pkName} of the ${instanceName} to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ${instanceName}, or with status 404 (Not Found)
     */
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
     *     *
     * @param id the id of the ${instanceName} to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @Path("/{${pkName}}")
    @DELETE
    public Response remove${EntityClass}(@PathParam("${pkName}") ${pkType} ${pkName}) {
        log.debug("REST request to delete ${EntityClass} : {}", ${pkName});
        ${entityFacade}.remove(${entityFacade}.find(${pkName}));
        return HeaderUtil.createEntityDeletionAlert(Response.ok(), "${entityInstance}", ${pkName}.toString()).build();
    }

}
