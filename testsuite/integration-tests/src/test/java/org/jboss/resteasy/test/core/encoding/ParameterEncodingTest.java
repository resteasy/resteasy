package org.jboss.resteasy.test.core.encoding;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.core.encoding.resource.ParameterEncodingResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;

/**
 * @tpSubChapter Encoding
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-737
 * @tpSince EAP 7.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ParameterEncodingTest {

    protected ResteasyClient client;

    protected static final Logger logger = LogManager.getLogger(ParameterEncodingTest.class.getName());

    @Before
    public void setup() throws Exception {
        client = new ResteasyClientBuilder().build();
    }


    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ParameterEncodingTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ParameterEncodingTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ParameterEncodingResource.class);
    }

    @After
    public void shutdown() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check space encoding in URL
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testResteasy734() throws Exception {
        ResteasyWebTarget target = null;
        Response response = null;

        target = client.target(generateURL("/encoded/pathparam/bee bop"));
        response = target.request().get();
        String entity = response.readEntity(String.class);
        logger.info("Received encoded path param: " + entity);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee%20bop", entity);
        response.close();

        target = client.target(generateURL("/decoded/pathparam/bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info("Received decoded path param: " + entity);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee bop", entity);
        response.close();

        target = client.target(generateURL("/encoded/matrix;m=bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info("Received encoded matrix param: " + entity);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee%20bop", entity);
        response.close();

        target = client.target(generateURL("/decoded/matrix;m=bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info("Received decoded matrix param: " + entity);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee bop", entity);
        response.close();

        target = client.target(generateURL("/encoded/query?m=bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info("Received encoded query param: " + entity);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee%20bop", entity);
        response.close();

        target = client.target(generateURL("/decoded/query?m=bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info("Received decoded query param: " + entity);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee bop", entity);
        response.close();

        target = client.target(generateURL("/encoded/form"));
        Form form = new Form();
        form.param("f", "bee bop");
        response = target.request().post(Entity.form(form));
        entity = response.readEntity(String.class);
        logger.info("Received encoded form param: " + entity);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee+bop", entity);
        response.close();

        target = client.target(generateURL("/decoded/form"));
        form = new Form();
        form.param("f", "bee bop");
        response = target.request().post(Entity.form(form));
        entity = response.readEntity(String.class);
        logger.info("Received decoded form param: " + entity);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee bop", entity);
        response.close();

        target = client.target(generateURL("/encoded/segment/bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info("Received encoded path param from segment: " + entity);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee%20bop", entity);
        response.close();

        target = client.target(generateURL("/decoded/segment/bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info("Received decoded path param from segment: " + entity);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee bop", entity);
        response.close();

        target = client.target(generateURL("/encoded/segment/matrix/params;m=bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info("Received encoded matrix param from segment: " + entity);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee%20bop", entity);
        response.close();

        target = client.target(generateURL("/decoded/segment/matrix/params;m=bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        logger.info("Received decoded matrix param from segment: " + entity);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee bop", entity);
        response.close();
    }
}
