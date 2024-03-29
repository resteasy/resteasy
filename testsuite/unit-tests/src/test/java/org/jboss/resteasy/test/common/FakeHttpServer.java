package org.jboss.resteasy.test.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.xnio.streams.Streams;

import com.sun.net.httpserver.HttpServer;

/**
 * Tiny fake HTTP server providing a target for testing the RESTEasy client.
 */
public class FakeHttpServer {

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private HttpServer server;

    public FakeHttpServer() {
    }

    /**
     * Get the servers listen address.
     *
     * @return The address the server is listening on.
     */
    public InetSocketAddress getAddress() {
        return server.getAddress();
    }

    /**
     * Get the servers listen address in host:port format.
     *
     * @return The host and port the server is listening on.
     */
    public String getHostAndPort() {
        return server.getAddress().getHostString() + ":" + server.getAddress().getPort();
    }

    /**
     * Starts the server.
     */
    public void start() {
        start(FakeHttpServer::dummyMethods);
    }

    /**
     * Start the server.
     */
    public void start(final Consumer<HttpServer> configurator) {
        final HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        configurator.accept(server);

        server.setExecutor(null); // handle on dispatcher thread

        this.server = server;
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    public static void dummyMethods(HttpServer server) {
        // generic dummy methods
        server.createContext("/path", exchange -> {
            final byte[] response;
            final int length;
            final int status;
            switch (exchange.getRequestMethod().toUpperCase()) {
                case "HEAD":
                    response = EMPTY_BYTE_ARRAY;
                    length = exchange.getRequestURI().toString().getBytes(StandardCharsets.UTF_8).length;
                    status = 200;
                    break;

                case "GET":
                    response = exchange.getRequestURI().toString().getBytes(StandardCharsets.UTF_8);
                    length = response.length;
                    status = 200;
                    break;

                case "POST": {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    Streams.copyStream(exchange.getRequestBody(), buffer);
                    response = buffer.toByteArray();
                    length = response.length;
                    status = 200;
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
    }
}
