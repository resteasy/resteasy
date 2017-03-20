package org.jboss.resteasy.test.providers.jettison;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jettison.resource.ContentTypeMatchingError;
import org.jboss.resteasy.test.providers.jettison.resource.ContentTypeMatchingErrorException;
import org.jboss.resteasy.test.providers.jettison.resource.ContentTypeMatchingErrorExceptionMapper;
import org.jboss.resteasy.test.providers.jettison.resource.ContentTypeMatchingMapperResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jettison provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails This tests automatically picking content type based on Accept header and/or @Produces
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ContentTypeMatchingTest {

    protected final Logger logger = Logger.getLogger(ContentTypeMatchingTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ContentTypeMatchingTest.class.getSimpleName());
        war.addAsManifestResource("jboss-deployment-structure-no-jackson.xml", "jboss-deployment-structure.xml");
        return TestUtil.finishContainerPrepare(war, null, ContentTypeMatchingError.class, ContentTypeMatchingErrorException.class,
                ContentTypeMatchingErrorExceptionMapper.class, ContentTypeMatchingMapperResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ContentTypeMatchingTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test that media type is chosen from resource method
     * @tpPassCrit The response returned with expected http response code
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProduces() throws Exception {
        WebTarget target = client.target(generateURL("/mapper/produces"));
        Response response = target.request().get();
        Assert.assertEquals("Unexpected http response code was returned", 412, response.getStatus());
        Assert.assertEquals("Wrong response content-type returned",
                "application/xml;charset=UTF-8", response.getStringHeaders().getFirst("Content-Type"));
        String error = response.readEntity(String.class);
        logger.info(error);
        Assert.assertTrue("Incorrect exception mapper was used",
                error.contains("<contentTypeMatchingError><name>foo</name></contentTypeMatchingError>"));

    }

    /**
     * @tpTestDetails Test that media type is chosen from resource method and accepts
     * @tpPassCrit The response returned with expected http response code
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAcceptsProduces() throws Exception {
        WebTarget target = client.target(generateURL("/mapper/accepts-produces"));
        {
            Response response = target.request().accept("application/json").get();
            Assert.assertEquals(412, response.getStatus());
            Assert.assertEquals("application/json", response.getStringHeaders().getFirst("Content-Type"));
            String error = response.readEntity(String.class);
            logger.info(error);
            Assert.assertTrue("Incorrect exception mapper was used", error.contains("{\"name\":\"foo\"}"));
        }

        {
            Response response = target.request().accept("application/xml").get();
            Assert.assertEquals(412, response.getStatus());
            Assert.assertEquals("application/xml;charset=UTF-8", response.getStringHeaders().getFirst("Content-Type"));
            String error = response.readEntity(String.class);
            logger.info(error);
            Assert.assertTrue("Incorrect exception mapper was used",
                    error.contains("<contentTypeMatchingError><name>foo</name></contentTypeMatchingError>"));
        }
    }

    /**
     * @tpTestDetails Test that media type is chosen from accepts
     * @tpPassCrit The response returned with expected http response code
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAccepts() throws Exception {
        WebTarget target = client.target(generateURL("/mapper/accepts"));
        {

            Response response = target.request().accept("application/json").get();
            Assert.assertEquals(412, response.getStatus());
            Assert.assertEquals("application/json", response.getStringHeaders().getFirst("Content-Type"));
            String error = response.readEntity(String.class);
            logger.info(error);
            Assert.assertTrue("Incorrect exception mapper was used", error.contains("{\"name\":\"foo\"}"));
        }

        {
            Response response = target.request().accept("application/xml").get();
            Assert.assertEquals(412, response.getStatus());
            Assert.assertEquals("application/xml;charset=UTF-8", response.getStringHeaders().getFirst("Content-Type"));
            String error = response.readEntity(String.class);
            logger.info(error);
            Assert.assertTrue("Incorrect exception mapper was used",
                    error.contains("<contentTypeMatchingError><name>foo</name></contentTypeMatchingError>"));
        }
    }

    /**
     * @tpTestDetails Test that media type is chosen from accepts when returning an entity
     * @tpPassCrit The response returned with expected http response code
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAcceptsEntity() throws Exception {
        WebTarget target = client.target(generateURL("/mapper/accepts-entity"));
        {
            Response response = target.request().accept("application/json").get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals("application/json", response.getStringHeaders().getFirst("Content-Type"));
            String error = response.readEntity(String.class);
            logger.info(error);
            Assert.assertTrue("Incorrect exception mapper was used", error.contains("{\"name\":\"foo\"}"));
        }

        {
            Response response = target.request().accept("application/xml").get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals("application/xml;charset=UTF-8", response.getStringHeaders().getFirst("Content-Type"));
            String error = response.readEntity(String.class);
            logger.info(error);
            Assert.assertTrue("Incorrect exception mapper was used",
                    error.contains("<contentTypeMatchingError><name>foo</name></contentTypeMatchingError>"));
        }
    }
}
