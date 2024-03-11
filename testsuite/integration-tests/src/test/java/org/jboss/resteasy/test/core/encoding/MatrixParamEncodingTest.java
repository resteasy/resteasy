package org.jboss.resteasy.test.core.encoding;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.encoding.resource.MatrixParamEncodingResource;
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
 * @tpSubChapter Encoding
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-729
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MatrixParamEncodingTest {

    protected static final Logger logger = Logger.getLogger(MatrixParamEncodingTest.class.getName());

    protected static ResteasyClient client;

    @BeforeEach
    public void setup() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, MatrixParamEncodingTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(MatrixParamEncodingTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, MatrixParamEncodingResource.class);
    }

    @AfterEach
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
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("ac/dc", response.readEntity(String.class),
                "Wrong response");
        response.close();
    }

    /**
     * @tpTestDetails Check decoded request, one matrix param is not defined, do not use UriBuilder
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixParamNullRequestDecoded() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/decodedMultipleParam")).matrixParam("param1", "")
                .matrixParam("param2", "abc");
        Response response = target.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("null abc", response.readEntity(String.class),
                "Wrong response");
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
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("ac%2Fdc", response.readEntity(String.class),
                "Wrong response");
        response.close();
    }

    /**
     * @tpTestDetails Check decoded request, use UriBuilder
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixParamUriBuilderDecoded() throws Exception {
        UriBuilder uriBuilder = UriBuilder.fromUri(generateURL("/decoded"));
        uriBuilder.matrixParam("param", "ac/dc");
        ResteasyWebTarget target = client.target(uriBuilder.build().toString());
        logger.info("Sending request to " + uriBuilder.build().toString());
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("ac/dc", entity, "Wrong response");
        response.close();
    }

    /**
     * @tpTestDetails Check encoded request, use UriBuilder
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixParamUriBuilderEncoded() throws Exception {
        UriBuilder uriBuilder = UriBuilder.fromUri(generateURL("/encoded"));
        uriBuilder.matrixParam("param", "ac/dc");
        ResteasyWebTarget target = client.target(uriBuilder.build().toString());
        logger.info("Sending request to " + uriBuilder.build().toString());
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("ac%2Fdc", entity, "Wrong response");
        response.close();
    }
}
