package ${package};

import ${appPackage}${HeaderUtil_FQN};
import ${appPackage}${AbstractRepository_FQN};
import ${appPackage}${EntityManagerProducer_FQN};
import ${appPackage}${LoggerProducer_FQN};
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.function.Supplier;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import junit.framework.AssertionFailedError;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.valid4j.matchers.http.HttpResponseMatchers.hasStatus;

/**
 * Abstract class for base application packaging.
 *
 */
public abstract class AbstractTest {

    @ArquillianResource
    protected static URL deploymentUrl;

    private final static MavenResolverSystem RESOLVER = Maven.resolver();

    public static WebArchive buildArchive() {
        File[] libs
                = RESOLVER.loadPomFromFile("pom.xml")
                        .importCompileAndRuntimeDependencies()
                        .resolve()
                        .withTransitivity()
                        .asFile();

        return ShrinkWrap.create(WebArchive.class)
                .addPackage(HeaderUtil.class.getPackage())
                .addClasses(
                        <#if microservices>${applicationConfig}.class,</#if>
                        ${AbstractRepository}.class,
                        EntityManagerProducer.class,
                        LoggerProducer.class)
                .addAsLibraries(libs)
                .addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
                <#if microservices>.addAsWebInfResource("glassfish-web.xml")</#if>
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .setWebXML("web.xml");
    }

    protected WebTarget buildWebTarget() {
        try {
            return ClientBuilder.newClient().target(deploymentUrl.toURI().toString() + "resources/");
        } catch (URISyntaxException ex) {
            throw new AssertionFailedError(ex.getMessage());
        }
    }

    protected <T> T buildClient(Class<? extends T> type) throws Exception {
        RestClientBuilder builder = RestClientBuilder.newBuilder();
        return builder.baseUrl(new URL(deploymentUrl.toURI().toString() + "resources/"))
                .build(type);
    }

    protected Invocation.Builder target(String path) {
        return buildWebTarget().path(path).request();
    }

    protected Invocation.Builder target(String path, Map<String, Object> params) {
        WebTarget target = buildWebTarget().path(path);
        for (String key : params.keySet()) {
            if (path.contains(String.format("{%s}", key))) {
                target = target.resolveTemplate(key, params.get(key));
            } else {
                target = target.queryParam(key, params.get(key));
            }
        }
        return target.request();
    }

    public <T> void assertWebException(Response.Status status, Supplier supplier) {
        try {
            supplier.get();
            fail("WebApplicationException not thrown");
        } catch (WebApplicationException wae) {
            assertThat(wae.getResponse(), hasStatus(status));
        } catch (Throwable throwable) {
            throw new AssertionFailedError("Unexpected exception type thrown : " + throwable.getClass());
        }
    }

}
