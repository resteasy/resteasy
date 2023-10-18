package org.jboss.resteasy.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.stream.Collectors;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.plugins.server.netty.NettyContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * RESTEASY-2300
 *
 * @author <a href="mailto:istudens@redhat.com">Ivo Studensky</a>
 */
public class HeaderEmptyHostTest {
    @Path("/emptyhost")
    public static class Resource {
        @Context
        UriInfo uriInfo;

        @GET
        @Path("/test")
        public String hello() {
            return "uriInfo: " + uriInfo.getRequestUri().toString();
        }
    }

    @BeforeAll
    public static void setup() throws Exception {
        NettyContainer.start().getRegistry().addPerRequestResource(Resource.class);
    }

    @AfterAll
    public static void end() throws Exception {
        NettyContainer.stop();
    }

    @Test
    public void testEmptyHostHeader() throws Exception {
        try (Socket client = new Socket(TestPortProvider.getHost(), TestPortProvider.getPort())) {
            try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                final String uri = "/emptyhost/test";
                out.printf("GET %s HTTP/1.1\r\n", uri);
                out.print("Host: \r\n");
                out.print("Connection: close\r\n");
                out.print("\r\n");
                out.flush();
                String response = new BufferedReader(new InputStreamReader(client.getInputStream())).lines()
                        .collect(Collectors.joining("\n"));
                Assertions.assertNotNull(response);
                Assertions.assertTrue(response.contains("HTTP/1.1 200 OK"));
                Assertions.assertTrue(response.contains("uriInfo: http://unknown" + uri));
            }
        }
    }
}
