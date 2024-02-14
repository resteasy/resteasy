package org.jboss.resteasy.test.providers.jackson2;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2JAXBResource;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2Product;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2Resource;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2XmlProduct;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2XmlResource;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2XmlResourceWithJAXB;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2XmlResourceWithJacksonAnnotation;
import org.jboss.resteasy.test.providers.jackson2.resource.JaxbJsonObjectMapperProvider;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class Jackson2Test {

    protected static final Logger logger = Logger.getLogger(Jackson2Test.class.getName());
    private static final String JSONP_ENABLED = "JSONP_enabled";
    private static final String JSONP_DISABLED = "JSONP_disabled";

    @Path("/products")
    public interface Jackson2Proxy {
        @GET
        @Produces("application/json")
        @Path("{id}")
        Jackson2Product getProduct();

        @GET
        @Produces("application/json")
        Jackson2Product[] getProducts();

        @POST
        @Produces("application/foo+json")
        @Consumes("application/foo+json")
        @Path("{id}")
        Jackson2Product post(@PathParam("id") int id, Jackson2Product p);
    }

    static ResteasyClient client;

    @Deployment(name = "default")
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(Jackson2Test.class.getSimpleName());
        war.addClass(Jackson2Test.class);
        war.addAsResource(Jackson2Test.class.getPackage(), "jakarta.ws.rs.ext.Providers",
                "META-INF/services/jakarta.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, Jackson2Resource.class, Jackson2Product.class,
                Jackson2XmlResource.class, Jackson2XmlProduct.class, Jackson2JAXBResource.class,
                Jackson2XmlResourceWithJacksonAnnotation.class, Jackson2XmlResourceWithJAXB.class,
                JaxbJsonObjectMapperProvider.class);
    }

    @Deployment(name = "JSONPenabled")
    public static Archive<?> deployJSONPenabled() {
        WebArchive war = TestUtil.prepareArchive(JSONP_ENABLED);
        war.addClass(Jackson2Test.class);
        war.addAsResource(Jackson2Test.class.getPackage(), "jakarta.ws.rs.ext.Providers",
                "META-INF/services/jakarta.ws.rs.ext.Providers");
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.jsonp.enable", "true");
        return TestUtil.finishContainerPrepare(war, contextParam, Jackson2Resource.class, Jackson2Product.class,
                Jackson2XmlResource.class, Jackson2XmlProduct.class, Jackson2JAXBResource.class,
                Jackson2XmlResourceWithJacksonAnnotation.class, Jackson2XmlResourceWithJAXB.class);
    }

    @Deployment(name = "JSONPdisabled")
    public static Archive<?> deployJSONPdisabled() {
        WebArchive war = TestUtil.prepareArchive(JSONP_DISABLED);
        war.addClass(Jackson2Test.class);
        war.addAsResource(Jackson2Test.class.getPackage(), "jakarta.ws.rs.ext.Providers",
                "META-INF/services/jakarta.ws.rs.ext.Providers");
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.jsonp.enable", "false");
        return TestUtil.finishContainerPrepare(war, contextParam, Jackson2Resource.class, Jackson2Product.class,
                Jackson2XmlResource.class, Jackson2XmlProduct.class, Jackson2JAXBResource.class,
                Jackson2XmlResourceWithJacksonAnnotation.class, Jackson2XmlResourceWithJAXB.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, Jackson2Test.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends GET request for json annotated resource. In the first case it returns single json entity,
     *                in the second case multiple json entities as String.
     * @tpPassCrit The resource returns json entities in correct format
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJacksonString() throws Exception {
        WebTarget target = client.target(generateURL("/products/333"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("{\"name\":\"Iphone\",\"id\":333}", entity,
                "The response entity content doesn't match the expected");
        response.close();

        target = client.target(generateURL("/products"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info(entity);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("[{\"name\":\"Iphone\",\"id\":333},{\"name\":\"macbook\",\"id\":44}]", entity,
                "The response entity content doesn't match the expected");
        response.close();

    }

    /**
     * @tpTestDetails Client sends GET request for Json resource. The request url contains 'callback' keyword which should
     *                trigger processing of the response in the format callbackvalue("key":"value")
     * @tpPassCrit The resource returns json entities in correct format (with callback function wrapping)
     * @tpInfo This test fails, see RESTEASY-1168. This should be fixed in 3.0.12 release.
     * @tpSince RESTEasy 3.0.16 as testJacksonJsonp() (but Jackson2JsonpInterceptor didn't need to be enabled)
     */
    @Test
    public void testJacksonJsonpEnabled() throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/products/333?callback=foo", JSONP_ENABLED));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("foo({\"name\":\"Iphone\",\"id\":333})", entity,
                "The response entity content doesn't match the expected");
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for Json resource. The request url contains 'callback' keyword which should
     *                trigger processing of the response in the format callbackvalue("key":"value"). However,
     *                Jackson2JsonpInterceptor is disabled.
     * @tpPassCrit The resource returns json entities in correct format (without callback function wrapping)
     * @tpInfo RESTEASY-1486
     * @tpSince RESTEasy 3.1.0.Final
     */
    @Test
    public void testJacksonJsonpDisabled() throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/products/333?callback=foo", JSONP_DISABLED));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("{\"name\":\"Iphone\",\"id\":333}", entity,
                "Jackson2JsonpInterceptor should be disabled");
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for Json resource. The request url contains 'callback' keyword which should
     *                trigger processing of the response in the format callbackvalue("key":"value")
     * @tpPassCrit The resource returns json entities in correct format (without callback function wrapping)
     * @tpInfo RESTEASY-1486
     * @tpSince RESTEasy 3.0.16 (as testJacksonJsonp() but Jackson2JsonpInterceptor would have been enabled)
     */
    @Test
    public void testJacksonJsonpDefault() throws Exception {
        WebTarget target = client.target(generateURL("/products/333?callback=foo"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("{\"name\":\"Iphone\",\"id\":333}", entity,
                "Jackson2JsonpInterceptor should be disabled");
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for json annotated resource. The resource is annotated with @Formatted,
     *                annotation available in Jackson 2 provider. It formats the response entity to look prettier. The test
     *                tests whether
     *                response contains '\n' (new line) character, because the annotation inserts new lines between element
     *                fields.
     * @tpPassCrit The resource returns json entities in correct format
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormattedJacksonString() throws Exception {
        WebTarget target = client.target(generateURL("/products/formatted/333"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertTrue(entity.contains("\n"), "Entity doesn't contain formatting");
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET and POST request for json annotated resource. For the response processing is used jackson
     *                provider. There are three types of response in this test:
     *                + The response entity is returned as instance of Jackson2Product class.
     *                + The response entity is returned as instance of String class
     *                + The response entity is returned as instance of Jackson2Product class and response is mediatype of
     *                'application/foo+json'
     * @tpPassCrit The returned object contains expected values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJackson() throws Exception {
        WebTarget target = client.target(generateURL("/products/333"));
        Response response = target.request().get();
        Jackson2Product p = response.readEntity(Jackson2Product.class);
        Assertions.assertEquals(333, p.getId(), "Jackson2Product id value doesn't match");
        Assertions.assertEquals("Iphone", p.getName(), "Jackson2Product name value doesn't match");
        response.close();

        target = client.target(generateURL("/products"));
        response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        target = client.target(generateURL("/products/333"));
        response = target.request().post(Entity.entity(p, "application/foo+json"));
        p = response.readEntity(Jackson2Product.class);
        Assertions.assertEquals(333, p.getId(), "Jackson2Product id value doesn't match");
        Assertions.assertEquals("Iphone", p.getName(), "Jackson2Product name value doesn't match");
        response.close();

    }

    /**
     * @tpTestDetails Client sends POST request with Jackson2Product entity using client proxy.
     * @tpPassCrit The returned object contains expected values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJacksonProxy() throws Exception {
        Jackson2Proxy proxy = client.target(generateURL("")).proxy(Jackson2Proxy.class);
        Jackson2Product p = new Jackson2Product(1, "Stuff");
        p = proxy.post(1, p);
        Assertions.assertEquals(1, p.getId(), "Jackson2Product id value doesn't match");
        Assertions.assertEquals("Stuff", p.getName(), "Jackson2Product name value doesn't match");
    }

    /**
     * @tpTestDetails Client has both, JAXB and Jackson v.2 providers on the classpath. First it sends GET request for
     *                JAXB annotated resource and verifies renaming of the Xml element attribute. Second it sends GET request
     *                for resource
     *                with Jackson annotation and verifies that json response contains the renamed attribute.
     * @tpPassCrit The response contains the renamed attributes
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJacksonJAXB() throws Exception {
        {
            WebTarget target = client.target(generateURL("/jaxb"));
            String response = target.request().get(String.class);
            logger.info(response);
            Assertions.assertTrue(response.contains("attr_1"), "The response doesn't contain the renamed attribute");
        }

        {
            WebTarget target = client.target(generateURL("/jaxb/json"));
            String response = target.request().get(String.class);
            logger.info(response);
            Assertions.assertTrue(response.contains("attr_1"), "The response doesn't contain the renamed attribute");
        }

    }
}
