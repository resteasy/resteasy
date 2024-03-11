package org.jboss.resteasy.test.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Consumer;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.test.common.FakeHttpServer;
import org.jboss.resteasy.test.common.TestServer;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xnio.streams.Streams;

import com.sun.net.httpserver.HttpServer;

/**
 * @author <a href="mailto:rsigal@redhat.com">Ron Sigal</a>
 * @version $Revision: 1 $
 * @tpSubChapter Resteasy-client
 * @tpChapter Unit tests
 * @tpTestCaseDetails Verify request is sent in chunked format
 * @tpSince RESTEasy 3.1.4
 */
public class ChunkedTransferEncodingUnitTest {
    private static final String testFilePath;

    static {
        testFilePath = TestUtil.getResourcePath(ChunkedTransferEncodingUnitTest.class, "ChunkedTransferEncodingUnitTestFile");
    }

    private final Consumer<HttpServer> configurator = (server) -> {

        FakeHttpServer.dummyMethods(server);

        // for ChunkedTransferEncodingUnitTest
        server.createContext("/chunked", exchange -> {
            final byte[] response;
            final int length;
            final int status;
            switch (exchange.getRequestMethod().toUpperCase()) {
                case "POST": {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    Streams.copyStream(exchange.getRequestBody(), buffer);

                    String transferEncoding = exchange.getRequestHeaders().getFirst("Transfer-Encoding");
                    if ("chunked".equalsIgnoreCase(transferEncoding)
                            && Arrays.equals(buffer.toByteArray(), "file entity".getBytes())) {
                        response = "ok".getBytes();
                        status = 200;
                    } else {
                        response = "not ok".getBytes();
                        status = 400;
                    }

                    length = response.length;
                    break;
                }

                default:
                    response = "Method Not Allowed".getBytes(StandardCharsets.UTF_8);
                    length = response.length;
                    status = 405;
                    break;
            }

            exchange.sendResponseHeaders(status, length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        });
    };

    @TestServer
    public FakeHttpServer fakeHttpServer;

    @Test
    public void testChunkedTarget() throws Exception {
        fakeHttpServer.start(configurator);

        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target("http://" + fakeHttpServer.getHostAndPort() + "/chunked");
        target.setChunked(true);
        ClientInvocationBuilder request = (ClientInvocationBuilder) target.request();
        File file = new File(testFilePath);
        Response response = request.post(Entity.entity(file, "text/plain"));
        String header = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ok", header);
        response.close();
        client.close();
    }

    @Test
    public void testChunkedRequest() throws Exception {
        fakeHttpServer.start(configurator);

        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target("http://" + fakeHttpServer.getHostAndPort() + "/chunked");
        ClientInvocationBuilder request = (ClientInvocationBuilder) target.request();
        request.setChunked(true);
        File file = new File(testFilePath);
        Response response = request.post(Entity.entity(file, "text/plain"));
        String header = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("ok", header);
        response.close();
        client.close();
    }
}
