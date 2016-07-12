package org.jboss.resteasy.test.providers.jackson;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jackson.resource.JacksonJAXBResource;
import org.jboss.resteasy.test.providers.jackson.resource.JacksonProduct;
import org.jboss.resteasy.test.providers.jackson.resource.JacksonXmlProduct;
import org.jboss.resteasy.test.providers.jackson.resource.JacksonXmlResource;
import org.jboss.resteasy.test.providers.jackson.resource.JacksonXmlResourceWithJacksonAnnotation;
import org.jboss.resteasy.test.providers.jackson.resource.JacksonXmlResourceWithJAXB;
import org.jboss.resteasy.test.providers.jackson.resource.JacksonResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.runner.RunWith;

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
 * @tpSubChapter Jackson provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JacksonTest {

    protected static final Logger logger = Logger.getLogger(JacksonTest.class.getName());
    private static final String JETTISON_DEPLOYMENT = "jettison";

    @Path("/products")
    public interface JacksonProxy {
        @GET
        @Produces("application/json")
        @Path("{id}")
        JacksonProduct getProduct();

        @GET
        @Produces("application/json")
        JacksonProduct[] getProducts();

        @POST
        @Produces("application/foo+json")
        @Consumes("application/foo+json")
        @Path("{id}")
        JacksonProduct post(@PathParam("id") int id, JacksonProduct p);
    }


    static ResteasyClient client;

    @Deployment(name = "default")
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JacksonTest.class.getSimpleName());
        war.addClass(JacksonTest.class);
        war.addAsManifestResource("jboss-deployment-structure-jackson-v1.xml", "jboss-deployment-structure.xml");
        return TestUtil.finishContainerPrepare(war, null, JacksonResource.class, JacksonProduct.class,
                JacksonXmlResource.class, JacksonXmlProduct.class, JacksonJAXBResource.class,
                JacksonXmlResourceWithJacksonAnnotation.class, JacksonXmlResourceWithJAXB.class);
    }

    /**
     * Jettison is deprecated, so it needs to be added to EAP manually (see JBEAP-2856).
     */
    @Deployment(name = "jettison")
    public static Archive<?> deployJettison() {
        WebArchive war = TestUtil.prepareArchive(JETTISON_DEPLOYMENT);
        war.addClass(JacksonTest.class);
        war.addAsManifestResource("jboss-deployment-structure-jackson-v1-jettison.xml", "jboss-deployment-structure.xml");
        return TestUtil.finishContainerPrepare(war, null, JacksonResource.class, JacksonProduct.class,
                JacksonXmlResource.class, JacksonXmlProduct.class, JacksonJAXBResource.class,
                JacksonXmlResourceWithJacksonAnnotation.class, JacksonXmlResourceWithJAXB.class);
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
        return PortProviderUtil.generateURL(path, JacksonTest.class.getSimpleName());
    }


    @AfterClass
    public static void shutdown() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client sends GET request for json annotated resource. In the first case it returns single json entity,
     * in the second case multiple json entities as String.
     * @tpPassCrit The resource returns json entities in correct format
     * @tpSince RESTEasy 3.0.16
     */
    @Test
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
         * + The response entity is returned as instance of JacksonProduct class.
         * + The response entity is returned as instance of String class
         * + The response entity is returned as instance of JacksonProduct class and response is mediatype of 'application/foo+json'
         * @tpPassCrit The returned object contains expected values
         * @tpSince RESTEasy 3.0.16
         */
    @Test
    public void testJackson() throws Exception {
        WebTarget target = client.target(generateURL("/products/333"));
        Response response = target.request().get();
        JacksonProduct p = response.readEntity(JacksonProduct.class);
        Assert.assertEquals("JacksonProduct id value doesn't match", 333, p.getId());
        Assert.assertEquals("JacksonProduct name value doesn't match", "Iphone", p.getName());
        response.close();

        target = client.target(generateURL("/products"));
        response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        target = client.target(generateURL("/products/333"));
        response = target.request().post(Entity.entity(p, "application/foo+json"));
        p = response.readEntity(JacksonProduct.class);
        Assert.assertEquals("JacksonProduct id value doesn't match", 333, p.getId());
        Assert.assertEquals("JacksonProduct name value doesn't match", "Iphone", p.getName());
        response.close();


    }

    /**
     * @tpTestDetails Client sends POST request with JacksonProduct entity using client proxy.
     * @tpPassCrit The returned object contains expected values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJacksonProxy() throws Exception {
        JacksonProxy proxy = client.target(generateURL("")).proxy(JacksonProxy.class);
        JacksonProduct p = new JacksonProduct(1, "Stuff");
        p = proxy.post(1, p);
        Assert.assertEquals("JacksonProduct id value doesn't match", 1, p.getId());
        Assert.assertEquals("JacksonProduct name value doesn't match", "Stuff", p.getName());
    }

    /**
     * @tpTestDetails Client has both, JAXB and Jackson v.1 providers on the classpath. First it sends GET request for
     * JAXB annotated resource and verifies renaming of the Xml element attribute. Second it sends GET request for resource
     * with Jackson annotation and verifies that json response contains the renamed attribute.
     * @tpPassCrit The response contains the renamed attributes
     * @tpSince RESTEasy 3.0.16
     */
    @Test
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
