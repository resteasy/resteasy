package org.jboss.resteasy.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test for RESTEASY-3553
 * When the Netty HttpObjectDecoder transitions into a BAD_MESSAGE state, a connection close response header should be sent.
 */
public class ResteasySmugglingRequestTest {
    private static final List<String> SMUGGLING_REQUEST = List.of(
            "POST /invalid/path HTTP/1.1",
            "X: X\u0001Transfer-Encoding: chunked",
            "Host: any-host",
            "User-Agent: any-user-agent",
            "Content-type: application/x-www-form-urlencoded; charset=UTF-8",
            "Content-Length: 6",
            "",
            "0",
            "",
            "X");
    private static final List<String> LEGIT_REQUEST = List.of(
            "GET /test HTTP/1.1",
            "Host: any-host",
            "User-Agent: any-user-agent",
            "",
            "");
    private static NettyJaxrsServer server;

    @Path("/test")
    public static class Resource {
        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String get() {
            return "hello world";
        }
    }

    @BeforeAll
    public static void init() {
        server = new NettyJaxrsServer();
        server.setPort(TestPortProvider.getPort());
        server.setRootResourcePath("");
        server.setSecurityDomain(null);
        server.getDeployment().getScannedResourceClasses().add(Resource.class.getName());
        server.start();
    }

    @AfterAll
    public static void stop() {
        server.stop();
    }

    @Test
    void testSmugglingRequest() throws IOException, InterruptedException {
        try (Socket clientSocket = new Socket(TestPortProvider.getHost(), TestPortProvider.getPort())) {
            clientSocket.setSoTimeout(5_000);

            sendRequest(clientSocket.getOutputStream(), LEGIT_REQUEST);
            Assertions.assertTrue(readResponse(clientSocket.getInputStream()).contains("hello world"));

            sendRequest(clientSocket.getOutputStream(), SMUGGLING_REQUEST);
            Assertions.assertTrue(readResponse(clientSocket.getInputStream()).contains("connection: close"));

            sendRequest(clientSocket.getOutputStream(), LEGIT_REQUEST);
            Assertions.assertTrue(readResponse(clientSocket.getInputStream()).isEmpty());
        }
    }

    private void sendRequest(OutputStream output, List<String> requestLines) throws IOException {
        String request = String.join("\r\n", requestLines);
        output.write(request.getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    private String readResponse(InputStream input) throws IOException, InterruptedException {
        ByteBuffer responseBuffer = ByteBuffer.allocate(4096);
        int nextByte;
        do {
            try {
                nextByte = input.read();
            } catch (SocketException e) {
                // Connection reset
                break;
            }
            if (nextByte == -1) {
                break;
            }
            responseBuffer.put((byte) nextByte);
        } while (!Character.isISOControl(nextByte));
        Thread.sleep(100);
        int available = input.available();
        if (available > 0) {
            responseBuffer.put(input.readNBytes(available));
        }
        return new String(responseBuffer.array(), 0, responseBuffer.position(), StandardCharsets.UTF_8);
    }
}
