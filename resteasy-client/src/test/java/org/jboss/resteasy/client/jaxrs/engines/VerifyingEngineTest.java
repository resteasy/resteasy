/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.client.jaxrs.engines;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link HostnameVerifyingSSLContext.VerifyingEngine} when the handshake completes without the engine
 * reporting the {@link javax.net.ssl.SSLEngineResult.HandshakeStatus#FINISHED} status.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class VerifyingEngineTest {
    private static final char[] PASSWORD = "changeit".toCharArray();
    private static final ByteBuffer EMPTY = ByteBuffer.allocate(0);

    /**
     * Checks that {@code wrap} invokes the verifier before it encrypts application data, when the handshake was
     * completed on the delegate and the wrapping engine never saw the {@code FINISHED} status.
     */
    @Test
    void wrapVerifiesBeforeApplicationDataWithoutFinishedStatus() throws Exception {
        final SSLEngine client = clientContext().createSSLEngine("127.0.0.1", 443);
        client.setUseClientMode(true);
        final SSLEngine server = serverContext().createSSLEngine();
        server.setUseClientMode(false);
        final AtomicReference<String> verifiedHost = new AtomicReference<>();
        final HostnameVerifyingSSLContext.VerifyingEngine engine = new HostnameVerifyingSSLContext.VerifyingEngine(client,
                (hostname, session) -> {
                    verifiedHost.set(hostname);
                    return false;
                });

        // Drive the handshake on the delegate, so the wrapping engine does not see the FINISHED status
        handshake(client, server);

        final ByteBuffer request = ByteBuffer.wrap("GET / HTTP/1.1\r\n\r\n".getBytes(StandardCharsets.US_ASCII));
        final ByteBuffer out = ByteBuffer.allocate(client.getSession().getPacketBufferSize());
        Assertions.assertThrows(SSLPeerUnverifiedException.class, () -> engine.wrap(request, out));
        Assertions.assertEquals("127.0.0.1", verifiedHost.get(), "The verifier was not invoked");
        Assertions.assertEquals(0, out.position(), "Application data was encrypted for a rejected host");
    }

    private static SSLContext serverContext() throws Exception {
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = VerifyingEngineTest.class.getResourceAsStream("/wrong-hostname.p12")) {
            keyStore.load(in, PASSWORD);
        }
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, PASSWORD);
        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), null, null);
        return context;
    }

    private static SSLContext clientContext() throws Exception {
        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[] { new TrustAllManager() }, null);
        return context;
    }

    private static void handshake(final SSLEngine client, final SSLEngine server) throws SSLException {
        final ByteBuffer clientToServer = ByteBuffer.allocate(client.getSession().getPacketBufferSize());
        final ByteBuffer serverToClient = ByteBuffer.allocate(server.getSession().getPacketBufferSize());
        final ByteBuffer clientApp = ByteBuffer.allocate(client.getSession().getApplicationBufferSize());
        final ByteBuffer serverApp = ByteBuffer.allocate(server.getSession().getApplicationBufferSize());
        client.beginHandshake();
        server.beginHandshake();
        int steps = 0;
        while (isHandshaking(client) || isHandshaking(server)) {
            Assertions.assertTrue(++steps < 1000, "The handshake did not complete");
            step(client, clientToServer, serverToClient, clientApp);
            step(server, serverToClient, clientToServer, serverApp);
        }
    }

    private static boolean isHandshaking(final SSLEngine engine) {
        return engine.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
    }

    private static void step(final SSLEngine engine, final ByteBuffer outbound, final ByteBuffer inbound,
            final ByteBuffer app) throws SSLException {
        switch (engine.getHandshakeStatus()) {
            case NEED_WRAP -> engine.wrap(EMPTY, outbound);
            case NEED_UNWRAP, NEED_UNWRAP_AGAIN -> {
                inbound.flip();
                engine.unwrap(inbound, app);
                inbound.compact();
            }
            case NEED_TASK -> {
                Runnable task;
                while ((task = engine.getDelegatedTask()) != null) {
                    task.run();
                }
            }
            default -> {
                // Not handshaking
            }
        }
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
