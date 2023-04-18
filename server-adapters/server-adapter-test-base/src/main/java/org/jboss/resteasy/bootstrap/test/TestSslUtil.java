/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.bootstrap.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

import org.wildfly.security.SecurityFactory;
import org.wildfly.security.ssl.SSLContextBuilder;
import org.wildfly.security.x500.cert.SelfSignedX509CertificateAndSigningKey;

/**
 * This is only meant for use in tests and should not be used outside of tests.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class TestSslUtil {

    private static final AtomicBoolean SETUP = new AtomicBoolean(false);

    private static final String WORK_DIR = "./target/test-classes/";

    private static final Path SERVER_KEYSTORE_FILE = Path.of(WORK_DIR, "server.keystore");
    private static final Path SERVER_TRUSTSTORE_FILE = Path.of(WORK_DIR, "server.truststore");
    private static final Path CLIENT_KEYSTORE_FILE = Path.of(WORK_DIR, "client.keystore");
    private static final Path CLIENT_TRUSTSTORE_FILE = Path.of(WORK_DIR, "client.truststore");
    private static final String ALIAS = "self-signed";
    private static final String CLIENT_DNS_STRING = "CN=localhost, OU=Test, L=Test, ST=Test, C=Test";
    private static final String SERVER_DNS_STRING = "CN=localhost, OU=Unknown, L=Unknown, ST=Unknown, C=Unknown";
    private static final String KEYSTORE_PASSWORD = "change.it.12345";

    /**
     * Creates an {@link SSLContext} for a server.
     *
     * @return the SSL context
     *
     * @throws Exception if an error occurs creating the SSL context
     */
    public static SSLContext createServerSslContext() throws Exception {
        setupOnce();
        final SecurityFactory<SSLContext> ssl = new SSLContextBuilder()
                .setClientMode(false)
                .setKeyManager(getKeyManager(SERVER_KEYSTORE_FILE))
                .setTrustManager(getTrustManager())
                .build();
        return ssl.create();
    }

    /**
     * Creates an {@link SSLContext} for a client.
     *
     * @return the SSL context
     *
     * @throws Exception if an error occurs creating the SSL context
     */
    public static SSLContext createClientSslContext() throws Exception {
        setupOnce();
        final SecurityFactory<SSLContext> ssl = new SSLContextBuilder()
                .setClientMode(true)
                .setKeyManager(getKeyManager(CLIENT_KEYSTORE_FILE))
                .setTrustManager(getTrustManager())
                .build();
        return ssl.create();
    }

    private static void setupOnce() throws Exception {
        if (SETUP.compareAndSet(false, true)) {
            final KeyStore clientKeyStore = loadKeyStore();
            final KeyStore clientTrustStore = loadKeyStore();
            final KeyStore serverKeyStore = loadKeyStore();
            final KeyStore serverTrustStore = loadKeyStore();

            createKeyStoreTrustStore(clientKeyStore, serverTrustStore, CLIENT_DNS_STRING);
            createKeyStoreTrustStore(serverKeyStore, clientTrustStore, SERVER_DNS_STRING);

            createTemporaryKeyStoreFile(clientKeyStore, CLIENT_KEYSTORE_FILE);
            createTemporaryKeyStoreFile(clientTrustStore, CLIENT_TRUSTSTORE_FILE);
            createTemporaryKeyStoreFile(serverKeyStore, SERVER_KEYSTORE_FILE);
            createTemporaryKeyStoreFile(serverTrustStore, SERVER_TRUSTSTORE_FILE);
        }
    }

    private static X509ExtendedKeyManager getKeyManager(final Path ksFile) throws Exception {
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(loadKeyStore(ksFile), KEYSTORE_PASSWORD.toCharArray());

        for (KeyManager current : keyManagerFactory.getKeyManagers()) {
            if (current instanceof X509ExtendedKeyManager) {
                return (X509ExtendedKeyManager) current;
            }
        }
        throw new IllegalStateException("Unable to obtain X509ExtendedKeyManager.");
    }

    private static X509TrustManager getTrustManager() throws Exception {
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(loadKeyStore(CLIENT_TRUSTSTORE_FILE));

        for (TrustManager current : trustManagerFactory.getTrustManagers()) {
            if (current instanceof X509TrustManager) {
                return (X509TrustManager) current;
            }
        }
        throw new IllegalStateException("Unable to obtain X509TrustManager.");
    }

    private static KeyStore loadKeyStore(final Path ksFile) throws Exception {
        final KeyStore ks = KeyStore.getInstance("JKS");
        try (InputStream in = Files.newInputStream(ksFile)) {
            ks.load(in, KEYSTORE_PASSWORD.toCharArray());
        }
        return ks;
    }

    private static void createKeyStoreTrustStore(final KeyStore keyStore, final KeyStore trustStore, final String name)
            throws Exception {
        final X500Principal principal = new X500Principal(name);
        final SelfSignedX509CertificateAndSigningKey selfSignedX509CertificateAndSigningKey = SelfSignedX509CertificateAndSigningKey
                .builder()
                .setKeyAlgorithmName("RSA")
                .setSignatureAlgorithmName("SHA256withRSA")
                .setDn(principal)
                .setKeySize(2048)
                .build();
        final X509Certificate certificate = selfSignedX509CertificateAndSigningKey.getSelfSignedCertificate();

        keyStore.setKeyEntry(ALIAS, selfSignedX509CertificateAndSigningKey.getSigningKey(), KEYSTORE_PASSWORD.toCharArray(),
                new X509Certificate[] { certificate });
        trustStore.setCertificateEntry(ALIAS, certificate);
    }

    private static KeyStore loadKeyStore() throws Exception {
        final KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);
        return ks;
    }

    private static void createTemporaryKeyStoreFile(final KeyStore keyStore, final Path outputFile) throws Exception {
        try (OutputStream out = Files.newOutputStream(outputFile, StandardOpenOption.CREATE)) {
            keyStore.store(out, KEYSTORE_PASSWORD.toCharArray());
        }
    }
}
