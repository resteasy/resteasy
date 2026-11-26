/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.client;

import java.net.URI;
import java.net.http.HttpClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.config.HttpClientConfigurationOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;
import dev.resteasy.junit.extension.annotations.SelfSignedCert;
import dev.resteasy.junit.extension.annotations.SslCert;
import dev.resteasy.junit.extension.api.SelfSignedCertificate;

/**
 * Tests chunked requests over HTTPS with the HTTP version set on the client. The resource returns the protocol, the
 * {@code Transfer-Encoding} header, the {@code Content-Length} header and the entity it received.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@SelfSignedCert
@RestBootstrap(ChunkedHttpVersionTest.EchoResource.class)
class ChunkedHttpVersionTest {

    @SslCert
    private SelfSignedCertificate certificate;

    /**
     * Checks that a chunked request uses HTTP/2 when HTTP/2 is configured. HTTP/2 does not allow a
     * {@code Transfer-Encoding} header, so the entity is streamed without one and without a {@code Content-Length}.
     */
    @Test
    void chunkedHttp2(@RestResource @RequestPath("echo") final URI uri) {
        try (Client client = createClient(HttpClient.Version.HTTP_2)) {
            Assertions.assertEquals("HTTP/2.0 null null hello", post(client, uri));
        }
    }

    /**
     * Checks that a chunked request uses chunked transfer encoding when HTTP/1.1 is configured.
     */
    @Test
    void chunkedHttp11(@RestResource @RequestPath("echo") final URI uri) {
        try (Client client = createClient(HttpClient.Version.HTTP_1_1)) {
            Assertions.assertEquals("HTTP/1.1 chunked null hello", post(client, uri));
        }
    }

    private Client createClient(final HttpClient.Version version) {
        return ClientBuilder.newBuilder()
                .sslContext(certificate.clientSslContext())
                .property(HttpClientConfigurationOptions.HTTP_VERSION, version)
                .build();
    }

    private static String post(final Client client, final URI uri) {
        final ResteasyWebTarget target = (ResteasyWebTarget) client.target(uri);
        target.setChunked(true);
        return target.request().post(Entity.text("hello"), String.class);
    }

    @Path("/echo")
    public static class EchoResource {

        @POST
        @Consumes(MediaType.TEXT_PLAIN)
        @Produces(MediaType.TEXT_PLAIN)
        public String echo(@Context final HttpServletRequest request,
                @HeaderParam("Transfer-Encoding") final String transferEncoding,
                @HeaderParam("Content-Length") final String contentLength,
                final String entity) {
            return String.join(" ", request.getProtocol(), transferEncoding, contentLength, entity);
        }
    }
}
