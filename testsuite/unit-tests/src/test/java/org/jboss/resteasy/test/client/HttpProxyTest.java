/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.client;

import java.net.URI;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * Tests that requests are sent through the configured HTTP proxy. The server acts as the proxy: the request is sent to
 * a host which does not resolve, so it only reaches the server through the proxy.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RestBootstrap(HttpProxyTest.HostResource.class)
class HttpProxyTest {
    // The .invalid top-level domain never resolves (RFC 2606)
    private static final String PROXIED_HOST = "proxied.invalid";

    @RestResource
    private URI uri;

    /**
     * Checks that the proxy host and port properties send the request through the proxy.
     */
    @Test
    void proxyFromProperties() {
        try (
                Client client = ClientBuilder.newBuilder()
                        .property(ResteasyClientBuilder.PROPERTY_PROXY_HOST, uri.getHost())
                        .property(ResteasyClientBuilder.PROPERTY_PROXY_PORT, uri.getPort())
                        .build()) {
            assertProxied(client, uri);
        }
    }

    /**
     * Checks that a proxy set with {@link ResteasyClientBuilder#defaultProxy(String, int)} is used instead of the
     * proxy properties.
     */
    @Test
    void defaultProxyOverridesProperties() {
        final ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder()
                .property(ResteasyClientBuilder.PROPERTY_PROXY_HOST, "127.0.0.1")
                .property(ResteasyClientBuilder.PROPERTY_PROXY_PORT, 1);
        try (Client client = builder.defaultProxy(uri.getHost(), uri.getPort()).build()) {
            assertProxied(client, uri);
        }
    }

    /**
     * Checks that a client can be built with a proxy which has no port.
     */
    @Test
    void defaultProxyWithoutPort() {
        final ResteasyClientBuilder builder = ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                .defaultProxy("myproxy.example");
        try (Client client = Assertions.assertDoesNotThrow(builder::build)) {
            Assertions.assertNotNull(client);
            Assertions.assertEquals(-1, builder.getDefaultProxyPort());
        }
    }

    /**
     * Checks that a builder whose proxy properties are removed builds a client which does not use a proxy.
     */
    @Test
    void removedPropertiesUseNoProxy() {
        final ClientBuilder builder = ClientBuilder.newBuilder()
                .property(ResteasyClientBuilder.PROPERTY_PROXY_HOST, uri.getHost())
                .property(ResteasyClientBuilder.PROPERTY_PROXY_PORT, uri.getPort());
        try (Client client = builder.build()) {
            assertProxied(client, uri);
        }
        builder.property(ResteasyClientBuilder.PROPERTY_PROXY_HOST, null)
                .property(ResteasyClientBuilder.PROPERTY_PROXY_PORT, null);
        try (Client client = builder.build()) {
            Assertions.assertThrows(ProcessingException.class,
                    () -> client.target(proxiedUri(uri)).request().get(),
                    "The request was sent without a proxy, so the host should not resolve");
        }
    }

    private static void assertProxied(final Client client, final URI uri) {
        try (Response response = client.target(proxiedUri(uri)).request().get()) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo().toEnum());
            Assertions.assertEquals(PROXIED_HOST, response.readEntity(String.class));
        }
    }

    // The same request URI with a host which only the proxy can reach
    private static URI proxiedUri(final URI uri) {
        return UriBuilder.fromUri(uri)
                .host(PROXIED_HOST)
                .port(-1)
                .path("proxy/host")
                .build();
    }

    @Path("/proxy")
    public static class HostResource {

        @GET
        @Path("host")
        @Produces(MediaType.TEXT_PLAIN)
        public String host(@HeaderParam(HttpHeaders.HOST) final String host) {
            return host;
        }
    }
}
