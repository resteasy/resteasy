package org.jboss.resteasy.test.client;

import java.io.File;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.test.client.resource.ChunkedTransferEncodingResource;
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
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test facility for sending requests in chunked format
 * @tpSince RESTEasy 3.0.24
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ChunkedTransferEncodingTest {

    static ResteasyClient clientDefault;
    static ResteasyClient clientEngine43;
    static final String testFilePath;
    static long fileLength;
    static File file;

    static {
        testFilePath = TestUtil.getResourcePath(ChunkedTransferEncodingTest.class, "ChunkedTransferEncodingTestFile");
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ChunkedTransferEncodingTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ChunkedTransferEncodingResource.class);
    }

    @BeforeEach
    public void init() {
        file = new File(testFilePath);
        fileLength = file.length();
        clientDefault = (ResteasyClient) ClientBuilder.newClient();
        clientEngine43 = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(new ApacheHttpClient43Engine())
                .build();
    }

    @AfterEach
    public void after() throws Exception {
        clientDefault.close();
        clientEngine43.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ChunkedTransferEncodingTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests that chunked Transfer-encoding header is set on ResteasyWebTarget. Tests that Content-Length
     *                header is set only in case when chunked transfer encoding is set to false. Headers are tested with the
     *                default client,
     *                client with te underlying http engines ApacheHttpClient4Engine and ApacheHttpClient43Engine.
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void testTarget() throws Exception {
        doTestTarget(clientDefault, Boolean.TRUE, "chunked null");
        doTestTarget(clientDefault, Boolean.FALSE, "null " + fileLength);
        doTestTarget(clientDefault, null, "null " + fileLength);
        doTestTarget(clientEngine43, Boolean.TRUE, "chunked null");
        doTestTarget(clientEngine43, Boolean.FALSE, "null " + fileLength);
        doTestTarget(clientEngine43, null, "null " + fileLength);
    }

    public void doTestTarget(ResteasyClient client, Boolean b, String expected) throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test"));
        if (b == Boolean.TRUE || b == Boolean.FALSE) {
            target.setChunked(b.booleanValue());
        }
        Invocation.Builder request = target.request();
        Response response = request.post(Entity.entity(file, "text/plain"));
        String header = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(expected, header);
    }

    /**
     * @tpTestDetails Tests that chunked Transfer-encoding header is set on ClientInvocationBuilder. Tests that Content-Length
     *                header is set only in case when chunked transfer encoding is set to false. Headers are tested with the
     *                default client,
     *                client with te underlying http engines ApacheHttpClient4Engine and ApacheHttpClient43Engine.
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void testRequest() throws Exception {
        doTestRequest(clientDefault, Boolean.TRUE, "chunked null");
        doTestRequest(clientDefault, Boolean.FALSE, "null " + fileLength);
        doTestRequest(clientDefault, null, "null " + fileLength);
        doTestRequest(clientEngine43, Boolean.TRUE, "chunked null");
        doTestRequest(clientEngine43, Boolean.FALSE, "null " + fileLength);
        doTestRequest(clientEngine43, null, "null " + fileLength);
    }

    protected void doTestRequest(ResteasyClient client, Boolean b, String expected) throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test"));
        ClientInvocationBuilder request = (ClientInvocationBuilder) target.request();
        if (b != null) {
            request.setChunked(b);
        }
        Response response = request.post(Entity.entity(file, "text/plain"));
        String header = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(expected, header);
    }
}
