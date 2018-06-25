package org.jboss.resteasy.test.providers.jackson2;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.category.ExpectedFailingOnWildFly13;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2JAXBResource;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2Product;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2Resource;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2XmlProduct;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2XmlResource;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2XmlResourceWithJacksonAnnotation;
import org.jboss.resteasy.test.providers.jackson2.resource.Jackson2XmlResourceWithJAXB;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class Jackson2Test {

    protected static final Logger logger = Logger.getLogger(Jackson2Test.class.getName());
    private static final String JETTISON_DEPLOYMENT = "jettison";
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
        war.addAsResource(Jackson2Test.class.getPackage(), "javax.ws.rs.ext.Providers", "META-INF/services/javax.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, Jackson2Resource.class, Jackson2Product.class,
                Jackson2XmlResource.class, Jackson2XmlProduct.class, Jackson2JAXBResource.class,
                Jackson2XmlResourceWithJacksonAnnotation.class, Jackson2XmlResourceWithJAXB.class);
    }
    
    @Deployment(name = "JSONPenabled")
    public static Archive<?> deployJSONPenabled() {
        WebArchive war = TestUtil.prepareArchive(JSONP_ENABLED);
        war.addClass(Jackson2Test.class);
        war.addAsResource(Jackson2Test.class.getPackage(), "javax.ws.rs.ext.Providers", "META-INF/services/javax.ws.rs.ext.Providers");
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
        war.addAsResource(Jackson2Test.class.getPackage(), "javax.ws.rs.ext.Providers", "META-INF/services/javax.ws.rs.ext.Providers");
        Map<String, String> contextParam = new HashMap<>();
        contextParam.put("resteasy.jsonp.enable", "false");
        return TestUtil.finishContainerPrepare(war, contextParam, Jackson2Resource.class, Jackson2Product.class,
                Jackson2XmlResource.class, Jackson2XmlProduct.class, Jackson2JAXBResource.class,
                Jackson2XmlResourceWithJacksonAnnotation.class, Jackson2XmlResourceWithJAXB.class);
    }

    /**
     * Jettison is deprecated, so it needs to be added to EAP manually (see JBEAP-2856).
     */
    @Deployment(name = "jettison")
    public static Archive<?> deployJettison() {
        WebArchive war = TestUtil.prepareArchive(JETTISON_DEPLOYMENT);
        war.addClass(Jackson2Test.class);
        war.addAsManifestResource("jboss-deployment-structure-jackson-v2-jettison.xml", "jboss-deployment-structure.xml");
        return TestUtil.finishContainerPrepare(war, null, Jackson2Resource.class, Jackson2Product.class,
                Jackson2XmlResource.class, Jackson2XmlProduct.class, Jackson2JAXBResource.class,
                Jackson2XmlResourceWithJacksonAnnotation.class, Jackson2XmlResourceWithJAXB.class,
                org.jboss.resteasy.plugins.providers.jackson.Jackson2JsonpInterceptor.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, Jackson2Test.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends GET request for json annotated resource. In the first case it returns single json entity,
     * in the second case multiple json entities as String.
     * @tpPassCrit The resource returns json entities in correct format
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @Category({ExpectedFailingOnWildFly13.class})
    public void testJacksonString() throws Exception {
        WebTarget target = client.target(generateURL("/products/333"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response entity content doesn't match the expected",
                "{\"name\":\"Iphone\",\"id\":333}", entity);
        response.close();

        target = client.target(generateURL("/products"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response entity content doesn't match the expected",
                "[{\"name\":\"Iphone\",\"id\":333},{\"name\":\"macbook\",\"id\":44}]", entity);
        response.close();

    }

    /**
     * @tpTestDetails Client sends GET request for Json resource. The request url contains 'callback' keyword which should
     * trigger processing of the response in the format callbackvalue("key":"value")
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response entity content doesn't match the expected", "foo({\"name\":\"Iphone\",\"id\":333})", entity);
        response.close();
    }
    
    /**
     * @tpTestDetails Client sends GET request for Json resource. The request url contains 'callback' keyword which should
     * trigger processing of the response in the format callbackvalue("key":"value"). However, Jackson2JsonpInterceptor is disabled.
     * @tpPassCrit The resource returns json entities in correct format (without callback function wrapping)
     * @tpInfo RESTEASY-1486
     * @tpSince RESTEasy 3.0.20.Final
     */
    @Test
    @Category({NotForForwardCompatibility.class, ExpectedFailingOnWildFly13.class})
    public void testJacksonJsonpDisabled() throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/products/333?callback=foo", JSONP_DISABLED));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Jackson2JsonpInterceptor should be disabled", "{\"name\":\"Iphone\",\"id\":333}", entity);
        response.close();
    }
    
    /**
     * @tpTestDetails Client sends GET request for Json resource. The request url contains 'callback' keyword which should
     * trigger processing of the response in the format callbackvalue("key":"value")
     * @tpPassCrit The resource returns json entities in correct format (without callback function wrapping)
     * @tpInfo RESTEASY-1486
     * @tpSince RESTEasy 3.0.16 (as testJacksonJsonp() but Jackson2JsonpInterceptor would have been enabled)
     */
    @Test
    @Category({NotForForwardCompatibility.class, ExpectedFailingOnWildFly13.class})
    public void testJacksonJsonpDefault() throws Exception {
        WebTarget target = client.target(generateURL("/products/333?callback=foo"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Jackson2JsonpInterceptor should be disabled", "{\"name\":\"Iphone\",\"id\":333}", entity);
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for json annotated resource. The resource is annotated with @Formatted,
     * annotation available in Jackson 2 provider. It formats the response entity to look prettier. The test tests whether
     * response contains '\n' (new line) character, because the annotation inserts new lines between element fields.
     * @tpPassCrit The resource returns json entities in correct format
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @Category({ExpectedFailingOnWildFly13.class})
    public void testFormattedJacksonString() throws Exception {
        WebTarget target = client.target(generateURL("/products/formatted/333"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertTrue("Entity doesn't contain formatting", entity.contains("\n"));
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for json annotated resource. The resource is annotated with @BadgerFish
     * and @NoJackson annotations. The jettison provider should be triggered instead of jackson one.
     * Jettison is deprecated, so it needs to be added to EAP manually (see JBEAP-2856).
     * @tpPassCrit The resource returns json entities in correct format
     * @tpInfo JBEAP-2856
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlString() throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/xml/products/333", JETTISON_DEPLOYMENT));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(TestUtil.getErrorMessageForKnownIssue("JBEAP-2856"), HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertTrue("Entity doesn't have json format", entity.startsWith("{\"product"));
        response.close();

        target = client.target(PortProviderUtil.generateURL("/xml/products", JETTISON_DEPLOYMENT));
        Response response2 = target.request().get();
        String entity2 = response2.readEntity(String.class);
        logger.info(entity2);
        Assert.assertEquals(TestUtil.getErrorMessageForKnownIssue("JBEAP-2856"), HttpResponseCodes.SC_OK, response2.getStatus());
        Assert.assertTrue("Entity doesn't have json format", entity2.startsWith("[{\"product"));
        response2.close();
    }

    /**
     * @tpTestDetails Client sends GET and POST request for json annotated resource. For the response processing is used jackson
     * provider. There are three types of response in this test:
     * + The response entity is returned as instance of Jackson2Product class.
     * + The response entity is returned as instance of String class
     * + The response entity is returned as instance of Jackson2Product class and response is mediatype of 'application/foo+json'
     * @tpPassCrit The returned object contains expected values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJackson() throws Exception {
        WebTarget target = client.target(generateURL("/products/333"));
        Response response = target.request().get();
        Jackson2Product p = response.readEntity(Jackson2Product.class);
        Assert.assertEquals("Jackson2Product id value doesn't match", 333, p.getId());
        Assert.assertEquals("Jackson2Product name value doesn't match", "Iphone", p.getName());
        response.close();

        target = client.target(generateURL("/products"));
        response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        target = client.target(generateURL("/products/333"));
        response = target.request().post(Entity.entity(p, "application/foo+json"));
        p = response.readEntity(Jackson2Product.class);
        Assert.assertEquals("Jackson2Product id value doesn't match", 333, p.getId());
        Assert.assertEquals("Jackson2Product name value doesn't match", "Iphone", p.getName());
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
        Assert.assertEquals("Jackson2Product id value doesn't match", 1, p.getId());
        Assert.assertEquals("Jackson2Product name value doesn't match", "Stuff", p.getName());
    }

    /**
     * @tpTestDetails Client has both, JAXB and Jackson v.2 providers on the classpath. First it sends GET request for
     * JAXB annotated resource and verifies renaming of the Xml element attribute. Second it sends GET request for resource
     * with Jackson annotation and verifies that json response contains the renamed attribute.
     * @tpPassCrit The response contains the renamed attributes
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @Category({ExpectedFailingOnWildFly13.class})
    public void testJacksonJAXB() throws Exception {
        {
            WebTarget target = client.target(generateURL("/jaxb"));
            String response = target.request().get(String.class);
            logger.info(response);
            Assert.assertTrue("The response doesn't contain the renamed attribute", response.contains("attr_1"));
        }

        {
            WebTarget target = client.target(generateURL("/jaxb/json"));
            String response = target.request().get(String.class);
            logger.info(response);
            Assert.assertTrue("The response doesn't contain the renamed attribute", response.contains("attr_1"));
        }

    }
}
