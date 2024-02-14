package org.jboss.resteasy.test.providers.jackson2.jsonfilter;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.Jackson2Product;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.Jackson2Resource;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.JsonFilterModifierConditionalWriterInterceptor;
import org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource.ObjectFilterModifierConditional;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Filters fields from json object. Sets ObjectWriterModifier in the interceptor.
 *                    The filter filters field of Jackson2Product pojo upon value if its 'id' field. Pojo with id value < 0 is
 *                    filtered
 *                    out and not returned in the response. See http://www.baeldung.com/jackson-serialize-field-custom-criteria
 * @tpSince RESTEasy 3.1.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class JsonFilterWithInterceptorConditionalFilterTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JsonFilterWithInterceptorConditionalFilterTest.class.getSimpleName());
        war.addClasses(Jackson2Product.class, ObjectFilterModifierConditional.class);
        war.addAsManifestResource(
                new StringAsset(
                        "Manifest-Version: 1.0\n" + "Dependencies: com.fasterxml.jackson.jaxrs.jackson-jaxrs-json-provider\n"),
                "MANIFEST.MF");
        return TestUtil.finishContainerPrepare(war, null, Jackson2Resource.class,
                JsonFilterModifierConditionalWriterInterceptor.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JsonFilterWithInterceptorConditionalFilterTest.class.getSimpleName());
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Json field id is filtered out
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testJacksonConditionalStringPropertyFiltered() throws Exception {
        WebTarget target = client.target(generateURL("/products/-1"));
        Response response = target.request().get();
        response.bufferEntity();
        Assertions.assertTrue(!response.readEntity(String.class).contains("id") &&
                response.readEntity(String.class).contains("name"), "Conditional filter doesn't work");
    }

    /**
     * @tpTestDetails Json field id is not filtered
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testJacksonConditionalStringPropertyNotFiltered() throws Exception {
        WebTarget target = client.target(generateURL("/products/333"));
        Response response = target.request().get();
        response.bufferEntity();
        Assertions.assertTrue(response.readEntity(String.class).contains("id") &&
                response.readEntity(String.class).contains("name"), "Conditional filter doesn't work");
    }
}
