/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.client.jaxrs.engines;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLContextSpi;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.jboss.resteasy.client.jaxrs.spi.ClientConfigProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests which {@link SSLContext} the {@link ClientConfigProviderSSLContext} creates each engine from.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class ClientConfigProviderSSLContextTest {
    private final List<URI> targets = new CopyOnWriteArrayList<>();
    private RecordingSSLContext firstContext;
    private RecordingSSLContext secondContext;
    private RecordingSSLContext defaultContext;
    private SSLContext context;

    @BeforeEach
    void createContext() throws Exception {
        firstContext = RecordingSSLContext.create();
        secondContext = RecordingSSLContext.create();
        defaultContext = RecordingSSLContext.create();
        final Map<String, SSLContext> contexts = Map.of(
                "first.example", firstContext,
                "second.example", secondContext);
        context = new ClientConfigProviderSSLContext(new TestClientConfigProvider(targets, contexts), defaultContext);
    }

    /**
     * Checks that the engine for a host is created from the context the provider returns for that host.
     */
    @Test
    void hostUsesProviderContext() {
        context.createSSLEngine("first.example", 443);
        Assertions.assertEquals(List.of("first.example:443"), firstContext.engines());
        Assertions.assertEquals(List.of(), secondContext.engines());
        Assertions.assertEquals(List.of(), defaultContext.engines());
    }

    /**
     * Checks that a second host, such as the target of a redirect, uses its own context.
     */
    @Test
    void secondHostUsesItsOwnContext() {
        context.createSSLEngine("first.example", 443);
        context.createSSLEngine("second.example", 443);
        Assertions.assertEquals(List.of("first.example:443"), firstContext.engines());
        Assertions.assertEquals(List.of("second.example:443"), secondContext.engines());
    }

    /**
     * Checks that the default context is used when the provider has no context for the host.
     */
    @Test
    void unknownHostUsesDefaultContext() {
        context.createSSLEngine("unknown.example", 443);
        Assertions.assertEquals(List.of("unknown.example:443"), defaultContext.engines());
    }

    /**
     * Checks that the provider receives a URI without a port when the port is 443.
     */
    @Test
    void defaultPortIsOmitted() {
        context.createSSLEngine("first.example", 443);
        Assertions.assertEquals(List.of(URI.create("https://first.example")), targets);
    }

    /**
     * Checks that the provider receives a URI with the port when the port is not 443.
     */
    @Test
    void otherPortIsIncluded() {
        context.createSSLEngine("first.example", 8443);
        Assertions.assertEquals(List.of(URI.create("https://first.example:8443")), targets);
    }

    /**
     * Checks that the provider receives an IPv6 address enclosed in brackets.
     */
    @Test
    void ipv6HostIsBracketed() {
        context.createSSLEngine("::1", 8443);
        Assertions.assertEquals(List.of(URI.create("https://[::1]:8443")), targets);
    }

    /**
     * Checks that the default parameters have no protocols and no cipher suites, so the parameters the
     * {@link java.net.http.HttpClient} sets on an engine do not replace those of the context which created it.
     */
    @Test
    void defaultParametersKeepEngineSettings() {
        Assertions.assertNull(context.getDefaultSSLParameters().getProtocols());
        Assertions.assertNull(context.getDefaultSSLParameters().getCipherSuites());
    }

    private static class TestClientConfigProvider implements ClientConfigProvider {
        private final List<URI> targets;
        private final Map<String, SSLContext> contexts;

        private TestClientConfigProvider(final List<URI> targets, final Map<String, SSLContext> contexts) {
            this.targets = targets;
            this.contexts = contexts;
        }

        @Override
        public String getUsername(final URI uri) {
            return null;
        }

        @Override
        public String getPassword(final URI uri) {
            return null;
        }

        @Override
        public String getBearerToken(final URI uri) {
            return null;
        }

        @Override
        public SSLContext getSSLContext(final URI uri) {
            targets.add(uri);
            return contexts.get(uri.getHost());
        }
    }

    /**
     * A context which records the host and port of each engine it creates.
     */
    private static class RecordingSSLContext extends SSLContext {
        private final List<String> engines;

        private RecordingSSLContext(final SSLContext delegate, final List<String> engines) {
            super(new RecordingSpi(delegate, engines), delegate.getProvider(), delegate.getProtocol());
            this.engines = engines;
        }

        static RecordingSSLContext create() throws NoSuchAlgorithmException {
            return new RecordingSSLContext(SSLContext.getDefault(), new CopyOnWriteArrayList<>());
        }

        List<String> engines() {
            return engines;
        }
    }

    private static class RecordingSpi extends SSLContextSpi {
        private final SSLContext delegate;
        private final List<String> engines;

        private RecordingSpi(final SSLContext delegate, final List<String> engines) {
            this.delegate = delegate;
            this.engines = engines;
        }

        @Override
        protected void engineInit(final KeyManager[] km, final TrustManager[] tm, final SecureRandom sr) {
            throw new UnsupportedOperationException("init");
        }

        @Override
        protected SSLSocketFactory engineGetSocketFactory() {
            throw new UnsupportedOperationException("getSocketFactory");
        }

        @Override
        protected SSLServerSocketFactory engineGetServerSocketFactory() {
            throw new UnsupportedOperationException("getServerSocketFactory");
        }

        @Override
        protected SSLEngine engineCreateSSLEngine() {
            engines.add("none");
            return delegate.createSSLEngine();
        }

        @Override
        protected SSLEngine engineCreateSSLEngine(final String host, final int port) {
            engines.add(host + ":" + port);
            return delegate.createSSLEngine(host, port);
        }

        @Override
        protected SSLSessionContext engineGetServerSessionContext() {
            return delegate.getServerSessionContext();
        }

        @Override
        protected SSLSessionContext engineGetClientSessionContext() {
            return delegate.getClientSessionContext();
        }
    }
}
