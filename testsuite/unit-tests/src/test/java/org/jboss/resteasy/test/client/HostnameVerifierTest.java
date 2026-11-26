/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;
import dev.resteasy.junit.extension.api.ConfigurationProvider;

/**
 * Tests {@link HostnameVerifier} support against an HTTPS server whose certificate is issued for {@code wrong.example},
 * so the standard {@code HTTPS} host name verification always fails.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RestBootstrap(value = HostnameVerifierTest.HelloResource.class, configFactory = HostnameVerifierTest.WrongHostnameConfiguration.class)
class HostnameVerifierTest {
    private static final char[] PASSWORD = "changeit".toCharArray();
    private static KeyStore keyStore;

    @BeforeAll
    static void loadKeyStore() {
        keyStore = loadWrongHostnameKeyStore();
    }

    /**
     * Checks that a verifier which accepts the host name overrides the host name mismatch when the trust store is
     * configured on the builder.
     */
    @Test
    void trustStoreWithAcceptingVerifier(@RestResource @RequestPath("hello") final URI uri) {
        try (Client client = ClientBuilder.newBuilder()
                .trustStore(keyStore)
                .hostnameVerifier((hostname, session) -> true)
                .build()) {
            Assertions.assertEquals("ok", client.target(uri).request().get(String.class));
        }
    }

    /**
     * Checks that a verifier which accepts the host name overrides the host name mismatch when the user supplies an
     * {@link SSLContext} whose trust manager is not an {@link javax.net.ssl.X509ExtendedTrustManager}.
     */
    @Test
    void sslContextWithAcceptingVerifier(@RestResource @RequestPath("hello") final URI uri) throws Exception {
        final SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[] { new TrustAllManager() }, null);
        try (Client client = ClientBuilder.newBuilder()
                .sslContext(context)
                .hostnameVerifier((hostname, session) -> true)
                .build()) {
            Assertions.assertEquals("ok", client.target(uri).request().get(String.class));
        }
    }

    /**
     * Checks that the verifier receives the host of the request URI and a session with the peer certificates.
     */
    @Test
    void verifierReceivesHostAndPeerCertificates(@RestResource @RequestPath("hello") final URI uri) {
        final RecordingVerifier verifier = new RecordingVerifier(true);
        try (Client client = ClientBuilder.newBuilder()
                .trustStore(keyStore)
                .hostnameVerifier(verifier)
                .build()) {
            client.target(uri).request().get(String.class);
        }
        Assertions.assertEquals(uri.getHost(), verifier.host);
        Assertions.assertNotNull(verifier.peerCertificates, "The session did not contain the peer certificates");
        Assertions.assertEquals(1, verifier.peerCertificates.length);
    }

    /**
     * Checks that a verifier which rejects the host name fails the request before the request is sent to the server.
     */
    @Test
    void rejectingVerifierFailsBeforeRequestIsSent(@RestResource @RequestPath("hello") final URI uri) {
        final int invocations = HelloResource.INVOCATIONS.get();
        final RecordingVerifier verifier = new RecordingVerifier(false);
        try (Client client = ClientBuilder.newBuilder()
                .trustStore(keyStore)
                .hostnameVerifier(verifier)
                .build()) {
            final ProcessingException e = Assertions.assertThrows(ProcessingException.class,
                    () -> client.target(uri).request().get(String.class));
            Assertions.assertTrue(hasCause(e, SSLPeerUnverifiedException.class),
                    () -> "Expected an SSLPeerUnverifiedException as a cause of " + e);
        }
        Assertions.assertEquals(uri.getHost(), verifier.host, "The verifier was not invoked");
        Assertions.assertEquals(invocations, HelloResource.INVOCATIONS.get(),
                "The request was sent to a host the verifier rejected");
    }

    /**
     * Checks that the standard {@code HTTPS} host name verification still applies when no verifier is set.
     */
    @Test
    void noVerifierAppliesHttpsHostnameVerification(@RestResource @RequestPath("hello") final URI uri) {
        try (Client client = ClientBuilder.newBuilder()
                .trustStore(keyStore)
                .build()) {
            final ProcessingException e = Assertions.assertThrows(ProcessingException.class,
                    () -> client.target(uri).request().get(String.class));
            Assertions.assertTrue(hasCause(e, CertificateException.class),
                    () -> "Expected a CertificateException as a cause of " + e);
        }
    }

    private static boolean hasCause(final Throwable error, final Class<? extends Throwable> type) {
        Throwable current = error;
        while (current != null) {
            if (type.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static KeyStore loadWrongHostnameKeyStore() {
        try (InputStream in = HostnameVerifierTest.class.getResourceAsStream("/wrong-hostname.p12")) {
            // KeyStore.load() creates an empty key store for a null stream
            if (in == null) {
                throw new IllegalStateException("Could not find wrong-hostname.p12");
            }
            final KeyStore result = KeyStore.getInstance("PKCS12");
            result.load(in, PASSWORD);
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Starts the server on HTTPS with the certificate issued for {@code wrong.example}.
     */
    public static class WrongHostnameConfiguration implements ConfigurationProvider {
        @Override
        public SeBootstrap.Configuration getConfiguration(final ExtensionContext context) {
            try {
                final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(loadWrongHostnameKeyStore(), PASSWORD);
                final SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(kmf.getKeyManagers(), null, null);
                return SeBootstrap.Configuration.builder()
                        .protocol("https")
                        .sslContext(sslContext)
                        .build();
            } catch (GeneralSecurityException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Path("/hello")
    public static class HelloResource {
        static final AtomicInteger INVOCATIONS = new AtomicInteger();

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        public String hello() {
            INVOCATIONS.incrementAndGet();
            return "ok";
        }
    }

    private static class RecordingVerifier implements HostnameVerifier {
        private final boolean accept;
        private volatile String host;
        private volatile Certificate[] peerCertificates;

        private RecordingVerifier(final boolean accept) {
            this.accept = accept;
        }

        @Override
        public boolean verify(final String hostname, final SSLSession session) {
            host = hostname;
            try {
                peerCertificates = session.getPeerCertificates();
            } catch (SSLPeerUnverifiedException e) {
                peerCertificates = null;
            }
            return accept;
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
