package ${package};

<#assign eid = pkStrategy == "EmbeddedId" >
import ${EntityRepository_FQN};
<#list connectedFQClasses as connectedFQClass>
import ${connectedFQClass};
</#list>
<#if eid || pkStrategy == "IdClass">
import java.util.HashMap;
import java.util.Map;
<#else>
import static java.util.Collections.singletonMap;
</#if>
<#if eid || pagination!= "no">
import java.util.HashMap;
import java.util.Map;
</#if>
import java.util.List;
import javax.inject.Inject;
import static javax.ws.rs.client.Entity.json;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jboss.arquillian.container.test.api.Deployment;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.valid4j.matchers.http.HttpResponseMatchers.hasContentType;
import static org.valid4j.matchers.http.HttpResponseMatchers.hasStatus;

/**
 * Test class for the ${controllerClass} REST controller.
 *
 */
public class ${controllerClass}Test extends ApplicationTest {

<#if eid>
<#list allIdAttributes as attribute>
    private static final ${attribute.dataType} DEFAULT_${attribute.NAME} = ${attribute.defaultValue};
</#list>
<#else>
<#list idAttributes as attribute>
    private static final ${attribute.dataType} DEFAULT_${attribute.NAME} = ${attribute.defaultValue};
</#list>
</#if>
<#list attributes as attribute>
    private static final ${attribute.dataType} DEFAULT_${attribute.NAME} = ${attribute.defaultValue};
    private static final ${attribute.dataType} UPDATED_${attribute.NAME} = ${attribute.updatedValue};
</#list>
    private static final String RESOURCE_PATH = "api/${entityApiUrl}";

    @Deployment
    public static WebArchive createDeployment() {
        return buildApplication()<#list connectedClasses as connectedClass>.addClass(${connectedClass}.class)</#list>.addClass(${EntityRepository}.class).addClass(${controllerClass}.class);
    }

    private static ${instanceType} ${instanceName};

    @Inject
    private ${EntityRepository} ${entityRepository};

    @Test
    @InSequence(1)
    public void create${EntityClass}() throws Exception {

        int databaseSizeBeforeCreate = ${entityRepository}.findAll().size();

        // Create the ${instanceType}
        ${instanceName} = new ${instanceType}();
<#if eid>
        ${pkType} ${pkName} = new ${pkType}();
<#list allIdAttributes as attribute>
        ${pkName}.${attribute.setter}(DEFAULT_${attribute.NAME});
</#list>
        ${instanceName}.${pkSetter}(${pkName});
<#else>
<#list idAttributes as attribute>
        ${instanceName}.${attribute.setter}(DEFAULT_${attribute.NAME});
</#list>
</#if>
<#list attributes as attribute>
        ${instanceName}.${attribute.setter}(DEFAULT_${attribute.NAME});
</#list>
        Response response = target(RESOURCE_PATH).post(json(${instanceName}));
        assertThat(response, hasStatus(Status.CREATED));
        ${instanceName} = response.readEntity(${instanceType}.class);

        // Validate the ${EntityClass} in the database
        List<${EntityClass}> ${entityInstancePlural} = ${entityRepository}.findAll();
        assertThat(${entityInstancePlural}.size(), is(databaseSizeBeforeCreate + 1));
        ${EntityClass} test${EntityClass} = ${entityInstancePlural}.get(${entityInstancePlural}.size() - 1);
<#list idAttributes as attribute>
        assertThat(test${EntityClass}<#if eid>.${pkGetter}()</#if>.${attribute.getter}(), is(DEFAULT_${attribute.NAME}));
</#list>
<#list attributes as attribute>
        assertThat(test${EntityClass}.${attribute.getter}(), is(DEFAULT_${attribute.NAME}));
</#list>
    }

    @Test
    @InSequence(2)
    public void getAll${EntityClassPlural}() throws Exception {

        int databaseSize = ${entityRepository}.findAll().size();
<#if pagination!= "no">
        Map<String, Object> params = new HashMap<>();
        params.put("page", 0);
        params.put("size", 10);
</#if>
        // Get all the ${entityInstancePlural}
        Response response = target(RESOURCE_PATH<#if pagination!= "no">, params</#if>).get();
        assertThat(response, hasStatus(Status.OK));
        assertThat(response, hasContentType(MediaType.APPLICATION_JSON_TYPE));

        List<${instanceType}> ${entityInstancePlural} = response.readEntity(List.class);
        assertThat(${entityInstancePlural}.size(), is(databaseSize));
    }

    @Test
    @InSequence(3)
    public void get${EntityClass}() throws Exception {
    
        // Get the ${instanceName}
<#if allIdAttributes?size=1>
        Response response = target(RESOURCE_PATH + "/{${pkName}}", singletonMap("${pkName}", ${instanceName}.${pkGetter}())).get();
<#else>
        Map<String, Object> params = new HashMap<>();
<#list allIdAttributes as attribute>
        params.put("${attribute.name}", ${instanceName}<#if eid>.${pkGetter}()</#if>.${attribute.getter}());
</#list>
        Response response = target(RESOURCE_PATH + "/${pkName}", params).get();
</#if>
        ${instanceType} test${instanceType} = response.readEntity(${instanceType}.class);
        assertThat(response, hasStatus(Status.OK));
        assertThat(response, hasContentType(MediaType.APPLICATION_JSON_TYPE));
<#list allIdAttributes as attribute>
        assertThat(test${instanceType}<#if eid>.${pkGetter}()</#if>.${attribute.getter}(), is(${instanceName}<#if eid>.${pkGetter}()</#if>.${attribute.getter}()));
</#list>
<#list attributes as attribute>
        assertThat(test${instanceType}.${attribute.getter}(), is(DEFAULT_${attribute.NAME}));
</#list>
    }

    @Test
    @InSequence(4)
    public void getNonExisting${EntityClass}() throws Exception {

        // Get the ${instanceName}
<#if allIdAttributes?size=1>
        Response response = target(RESOURCE_PATH + "/{${pkName}}", singletonMap("${pkName}", Long.MAX_VALUE)).get();
<#else>
        Map<String, Object> params = new HashMap<>();
<#list allIdAttributes as attribute>
        params.put("${attribute.name}", Long.MAX_VALUE);
</#list>
        Response response = target(RESOURCE_PATH + "/${pkName}", params).get();
</#if>
        assertThat(response, hasStatus(Status.NOT_FOUND));
    }

    @Test
    @InSequence(5)
    public void update${EntityClass}() throws Exception {

        int databaseSizeBeforeUpdate = ${entityRepository}.findAll().size();

        // Update the ${instanceName}
        ${instanceType} updated${instanceType} = new ${instanceType}();
<#if eid>
        updated${instanceType}.${pkSetter}(${instanceName}.${pkGetter}());
<#else>
<#list allIdAttributes as attribute>
        updated${instanceType}.${attribute.setter}(${instanceName}.${attribute.getter}());
</#list>
</#if>
<#list versionAttributes as attribute>
        updated${instanceType}.${attribute.setter}(${instanceName}.${attribute.getter}());
</#list>
<#list attributes as attribute>
        updated${instanceType}.${attribute.setter}(UPDATED_${attribute.NAME});
</#list>

        Response response = target(RESOURCE_PATH).put(json(updated${instanceType}));
        assertThat(response, hasStatus(Status.OK));

        // Validate the ${EntityClass} in the database
        List<${EntityClass}> ${entityInstancePlural} = ${entityRepository}.findAll();
        assertThat(${entityInstancePlural}.size(), is(databaseSizeBeforeUpdate));
        ${EntityClass} test${EntityClass} = ${entityInstancePlural}.get(${entityInstancePlural}.size() - 1);
<#list idAttributes as attribute>
        assertThat(test${EntityClass}<#if eid>.${pkGetter}()</#if>.${attribute.getter}(), is(${instanceName}<#if eid>.${pkGetter}()</#if>.${attribute.getter}()));
</#list>
<#list attributes as attribute>
        assertThat(test${EntityClass}.${attribute.getter}(), is(UPDATED_${attribute.NAME}));
</#list>
    }

    @Test
    @InSequence(6)
    public void remove${EntityClass}() throws Exception {

        int databaseSizeBeforeDelete = ${entityRepository}.findAll().size();

        // Delete the ${instanceName}
<#if allIdAttributes?size=1>
        Response response = target(RESOURCE_PATH + "/{${pkName}}", singletonMap("${pkName}", ${instanceName}.${pkGetter}())).delete();
<#else>
        Map<String, Object> params = new HashMap<>();
<#list allIdAttributes as attribute>
        params.put("${attribute.name}", ${instanceName}<#if eid>.${pkGetter}()</#if>.${attribute.getter}());
</#list>
        Response response = target(RESOURCE_PATH, params).delete();
</#if>
        assertThat(response, hasStatus(Status.OK));

        // Validate the database is empty
        List<${EntityClass}> ${entityInstancePlural} = ${entityRepository}.findAll();
        assertThat(${entityInstancePlural}.size(), is(databaseSizeBeforeDelete - 1));
    }

}
