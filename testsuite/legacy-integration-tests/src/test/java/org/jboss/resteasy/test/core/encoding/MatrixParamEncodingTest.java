package org.jboss.resteasy.test.core.encoding;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.test.core.encoding.resource.MatrixParamEncodingResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * @tpSubChapter Encoding
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-729
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class MatrixParamEncodingTest {

    protected static final Logger logger = LogManager.getLogger(MatrixParamEncodingTest.class.getName());

    protected static ResteasyClient client;

    @Before
    public void setup() throws Exception {
        client = new ResteasyClientBuilder().build();
    }


    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MatrixParamEncodingTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(MatrixParamEncodingTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, MatrixParamEncodingResource.class);
    }

    @After
    public void shutdown() throws Exception {
        client.close();
        client = null;
    }

    /**
     * @tpTestDetails Check decoded request, do not use UriBuilder
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixParamRequestDecoded() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/decoded")).matrixParam("param", "ac/dc");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong response", "ac/dc", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails Check decoded request, one matrix param is not defined, do not use UriBuilder
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixParamNullRequestDecoded() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/decodedMultipleParam")).matrixParam("param1", "").matrixParam("param2", "abc");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong response", "null abc", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails Check encoded request, do not use UriBuilder
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixParamRequestEncoded() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/encoded")).matrixParam("param", "ac/dc");
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong response", "ac%2Fdc", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails Check decoded request, use UriBuilder
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixParamUriBuilderDecoded() throws Exception {
        UriBuilder uriBuilder = ResteasyUriBuilder.fromUri(generateURL("/decoded"));
        uriBuilder.matrixParam("param", "ac/dc");
        ResteasyWebTarget target = client.target(uriBuilder.build().toString());
        logger.info("Sending request to " + uriBuilder.build().toString());
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong response", "ac/dc", entity);
        response.close();
    }

    /**
     * @tpTestDetails Check encoded request, use UriBuilder
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixParamUriBuilderEncoded() throws Exception {
        UriBuilder uriBuilder = ResteasyUriBuilder.fromUri(generateURL("/encoded"));
        uriBuilder.matrixParam("param", "ac/dc");
        ResteasyWebTarget target = client.target(uriBuilder.build().toString());
        logger.info("Sending request to " + uriBuilder.build().toString());
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong response", "ac%2Fdc", entity);
        response.close();
    }
}
