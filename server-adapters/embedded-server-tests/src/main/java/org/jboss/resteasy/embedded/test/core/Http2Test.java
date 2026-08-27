/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.embedded.test.core;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;
import dev.resteasy.junit.extension.annotations.SelfSignedCert;
import dev.resteasy.junit.extension.annotations.SslCert;
import dev.resteasy.junit.extension.api.SelfSignedCertificate;

/**
 * Tests that HTTP/2 is negotiated via ALPN, verified from the wire using the JDK's own
 * {@link HttpClient} rather than RESTEasy's servlet dispatcher reporting it back.
 */
@SelfSignedCert
@RestBootstrap(value = Http2Test.HelloResource.class)
@Tag("http2")
@Tag("ssl")
class Http2Test {

    @SslCert
    private SelfSignedCertificate certificate;

    @Test
    void negotiatesHttp2(@RestResource @RequestPath("hello") final URI uri) throws Exception {
        final HttpClient client = HttpClient.newBuilder()
                .sslContext(certificate.clientSslContext())
                .version(HttpClient.Version.HTTP_2)
                .build();
        final HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(HttpClient.Version.HTTP_2, response.version());
        Assertions.assertEquals("hello", response.body());
    }

    @Path("/hello")
    public static class HelloResource {

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String hello() {
            return "hello";
        }
    }
}
