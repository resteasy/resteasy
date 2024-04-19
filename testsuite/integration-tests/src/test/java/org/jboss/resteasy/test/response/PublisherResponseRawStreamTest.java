package org.jboss.resteasy.test.response;

import java.util.PropertyPermission;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.response.resource.AsyncResponseCallback;
import org.jboss.resteasy.test.response.resource.AsyncResponseException;
import org.jboss.resteasy.test.response.resource.AsyncResponseExceptionMapper;
import org.jboss.resteasy.test.response.resource.PublisherResponseRawStreamResource;
import org.jboss.resteasy.test.response.resource.SlowString;
import org.jboss.resteasy.test.response.resource.SlowStringWriter;
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
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter Publisher response type
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class PublisherResponseRawStreamTest {

    Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PublisherResponseRawStreamTest.class.getSimpleName());
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services, org.reactivestreams\n"));
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new PropertyPermission("*", "read"),
                new PropertyPermission("*", "write"),
                new RuntimePermission("modifyThread")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, PublisherResponseRawStreamResource.class,
                SlowStringWriter.class, SlowString.class,
                AsyncResponseCallback.class, AsyncResponseExceptionMapper.class, AsyncResponseException.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, PublisherResponseRawStreamTest.class.getSimpleName());
    }

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void close() {
        client.close();
        client = null;
    }

    /**
     * @tpTestDetails Resource method returns Publisher<String>.
     * @tpSince RESTEasy 4.0
     */
    @Test
    public void testChunked() throws Exception {
        Invocation.Builder request = client.target(generateURL("/chunked")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(entity.startsWith("0-21-22-2"));
        Assertions.assertTrue(entity.endsWith("29-2"));
    }

    /**
     * @tpTestDetails Resource method unsubscribes on close for infinite streams.
     * @tpSince RESTEasy 4.0
     */
    @Test
    public void testInfiniteStreamsChunked() throws Exception {
        Invocation.Builder request = client.target(generateURL("/chunked-infinite")).request();
        Future<Response> futureResponse = request.async().get();
        try {
            futureResponse.get(2, TimeUnit.SECONDS);
        } catch (TimeoutException x) {
        }
        close();
        setup();
        Thread.sleep(5000);
        request = client.target(generateURL("/infinite-done")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("true", entity);
    }

    @Test
    public void testSlowAsyncWriter() throws Exception {
        Invocation.Builder request = client.target(generateURL("/slow-async-io")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("onetwo", entity);
    }
}
