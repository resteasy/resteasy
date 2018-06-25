package org.jboss.resteasy.test.providers.jackson2;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.category.ExpectedFailingOnWildFly13;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonJaxbCoexistenceJacksonResource;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonJaxbCoexistenceJacksonXmlResource;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonJaxbCoexistenceProduct;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonJaxbCoexistenceProduct2;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonJaxbCoexistenceXmlResource;
import org.jboss.resteasy.test.providers.jackson2.resource.JacksonJaxbCoexistenceXmlProduct;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

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
public class JacksonJaxbCoexistenceTest {

    static ResteasyClient client;
    protected static final Logger logger = Logger.getLogger(JacksonJaxbCoexistenceTest.class.getName());
    private static final String JETTISON_DEPLOYMENT = "jettison";

    @Deployment(name = "default")
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JacksonJaxbCoexistenceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, JacksonJaxbCoexistenceJacksonResource.class,
                JacksonJaxbCoexistenceJacksonXmlResource.class, JacksonJaxbCoexistenceXmlResource.class,
                JacksonJaxbCoexistenceProduct.class, JacksonJaxbCoexistenceProduct2.class,
                JacksonJaxbCoexistenceXmlProduct.class);
    }

    /**
     * Jettison is deprecated, so it needs to be added to EAP manually (see JBEAP-2856).
     */
    @Deployment(name = "jettison")
    public static Archive<?> deployJettison() {
        WebArchive war = TestUtil.prepareArchive(JETTISON_DEPLOYMENT);
        war.addAsManifestResource("jboss-deployment-structure-jackson-v2-jettison.xml", "jboss-deployment-structure.xml");
        return TestUtil.finishContainerPrepare(war, null, JacksonJaxbCoexistenceJacksonResource.class,
                JacksonJaxbCoexistenceJacksonXmlResource.class, JacksonJaxbCoexistenceXmlResource.class,
                JacksonJaxbCoexistenceProduct.class, JacksonJaxbCoexistenceProduct2.class,
                JacksonJaxbCoexistenceXmlProduct.class);
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
        return PortProviderUtil.generateURL(path, JacksonJaxbCoexistenceTest.class.getSimpleName());
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
     * @tpTestDetails Test that Jackson is picked
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @Category({ExpectedFailingOnWildFly13.class})
    public void testJacksonXmlString() throws Exception {
        WebTarget target = client.target(generateURL("/jxml/products/333"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response entity content doesn't match the expected",
                "{\"name\":\"Iphone\",\"id\":333}", entity);
        response.close();

        target = client.target(generateURL("/jxml/products"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The response entity content doesn't match the expected",
                "[{\"name\":\"Iphone\",\"id\":333},{\"name\":\"macbook\",\"id\":44}]", entity);
        response.close();
    }

    /**
     * @tpTestDetails Test that Jettison is picked
     * Jettison is deprecated, so it needs to be added to EAP manually (see JBEAP-2856).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testXmlString() throws Exception {
        WebTarget target = client.target(PortProviderUtil.generateURL("/xml/products/333", JETTISON_DEPLOYMENT));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(TestUtil.getErrorMessageForKnownIssue("JBEAP-2856"), HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertTrue(entity.startsWith("{\"product"));
        response.close();

        target = client.target(PortProviderUtil.generateURL("/xml/products", JETTISON_DEPLOYMENT));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(TestUtil.getErrorMessageForKnownIssue("JBEAP-2856"), HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertTrue(entity.startsWith("[{\"product"));
    }

    /**
     * @tpTestDetails Test that Jackson is picked and object can be returned from the resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testJackson() throws Exception {
        WebTarget target = client.target(generateURL("/products/333"));
        Response response = target.request().get();
        JacksonJaxbCoexistenceProduct p = response.readEntity(JacksonJaxbCoexistenceProduct.class);
        Assert.assertEquals("JacksonJaxbCoexistenceProduct id value doesn't match", 333, p.getId());
        Assert.assertEquals("JacksonJaxbCoexistenceProduct name value doesn't match", "Iphone", p.getName());
        response.close();

        target = client.target(generateURL("/products"));
        response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info(entity);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        target = client.target(generateURL("/products/333"));
        response = target.request().post(Entity.entity(p, "application/foo+json"));
        p = response.readEntity(JacksonJaxbCoexistenceProduct.class);
        Assert.assertEquals("JacksonJaxbCoexistenceProduct id value doesn't match", 333, p.getId());
        Assert.assertEquals("JacksonJaxbCoexistenceProduct name value doesn't match", "Iphone", p.getName());
    }
}
