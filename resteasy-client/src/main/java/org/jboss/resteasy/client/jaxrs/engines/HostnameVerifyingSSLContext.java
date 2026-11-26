/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.client.jaxrs.engines;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.List;
import java.util.function.BiFunction;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLContextSpi;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.jboss.resteasy.client.jaxrs.i18n.LogMessages;

/**
 * An {@link SSLContext} whose engines verify the host name of the server with a {@link HostnameVerifier} after each
 * TLS handshake.
 * <p>
 * The {@link java.net.http.HttpClient} sets the {@code HTTPS} endpoint identification algorithm on every engine it
 * creates, and it has no option for a {@link HostnameVerifier}. The engines created by this context remove the
 * endpoint identification algorithm and invoke the {@link HostnameVerifier} when the handshake finishes. If the
 * verifier rejects the host name, the handshake fails before any application data is written.
 * </p>
 * <p>
 * The socket factories are not supported, because sockets created from them would not be verified.
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
final class HostnameVerifyingSSLContext extends SSLContext {

    /**
     * Creates a context which delegates to the {@code delegate} and verifies host names with the {@code verifier}.
     *
     * @param delegate the initialized context used for the TLS handshake, cannot be {@code null}
     * @param verifier the verifier invoked after each handshake, cannot be {@code null}
     */
    HostnameVerifyingSSLContext(final SSLContext delegate, final HostnameVerifier verifier) {
        super(new VerifyingSpi(delegate, verifier), delegate.getProvider(), delegate.getProtocol());
    }

    private static class VerifyingSpi extends SSLContextSpi {
        private final SSLContext delegate;
        private final HostnameVerifier verifier;

        private VerifyingSpi(final SSLContext delegate, final HostnameVerifier verifier) {
            this.delegate = delegate;
            this.verifier = verifier;
        }

        @Override
        protected void engineInit(final KeyManager[] km, final TrustManager[] tm, final SecureRandom sr) {
            throw LogMessages.LOGGER.unsupportedSslContextOperation("init");
        }

        @Override
        protected SSLSocketFactory engineGetSocketFactory() {
            throw LogMessages.LOGGER.unsupportedSslContextOperation("getSocketFactory");
        }

        @Override
        protected SSLServerSocketFactory engineGetServerSocketFactory() {
            throw LogMessages.LOGGER.unsupportedSslContextOperation("getServerSocketFactory");
        }

        @Override
        protected SSLEngine engineCreateSSLEngine() {
            return new VerifyingEngine(delegate.createSSLEngine(), verifier);
        }

        @Override
        protected SSLEngine engineCreateSSLEngine(final String host, final int port) {
            return new VerifyingEngine(delegate.createSSLEngine(host, port), verifier);
        }

        @Override
        protected SSLSessionContext engineGetServerSessionContext() {
            return delegate.getServerSessionContext();
        }

        @Override
        protected SSLSessionContext engineGetClientSessionContext() {
            return delegate.getClientSessionContext();
        }

        @Override
        protected SSLParameters engineGetDefaultSSLParameters() {
            return delegate.getDefaultSSLParameters();
        }

        @Override
        protected SSLParameters engineGetSupportedSSLParameters() {
            return delegate.getSupportedSSLParameters();
        }
    }

    /**
     * An engine which invokes the {@link HostnameVerifier} once per handshake.
     * <p>
     * The verifier is invoked when {@link #wrap} or {@link #unwrap} reports the
     * {@link SSLEngineResult.HandshakeStatus#FINISHED} status. The {@link java.net.http.HttpClient} can also treat a
     * handshake as complete when {@link #getHandshakeStatus()} returns
     * {@link SSLEngineResult.HandshakeStatus#NOT_HANDSHAKING} after delegated tasks, without a {@code FINISHED}
     * status. To fail closed in that case, {@link #wrap} invokes the verifier before it encrypts application data on
     * a session which has not been verified.
     * </p>
     */
    static class VerifyingEngine extends SSLEngine {
        private static final String NULL_CIPHER_SUITE = "SSL_NULL_WITH_NULL_NULL";
        private final SSLEngine delegate;
        private final HostnameVerifier verifier;
        private volatile SSLSession verifiedSession;

        VerifyingEngine(final SSLEngine delegate, final HostnameVerifier verifier) {
            super(delegate.getPeerHost(), delegate.getPeerPort());
            this.delegate = delegate;
            this.verifier = verifier;
        }

        @Override
        public SSLEngineResult wrap(final ByteBuffer[] srcs, final int offset, final int length, final ByteBuffer dst)
                throws SSLException {
            if (hasRemaining(srcs, offset, length)
                    && delegate.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
                final SSLSession session = delegate.getSession();
                if (session != verifiedSession && !NULL_CIPHER_SUITE.equals(session.getCipherSuite())) {
                    verify(session);
                }
            }
            return verifyOnFinish(delegate.wrap(srcs, offset, length, dst));
        }

        @Override
        public SSLEngineResult unwrap(final ByteBuffer src, final ByteBuffer[] dsts, final int offset, final int length)
                throws SSLException {
            return verifyOnFinish(delegate.unwrap(src, dsts, offset, length));
        }

        @Override
        public Runnable getDelegatedTask() {
            return delegate.getDelegatedTask();
        }

        @Override
        public void closeInbound() throws SSLException {
            delegate.closeInbound();
        }

        @Override
        public boolean isInboundDone() {
            return delegate.isInboundDone();
        }

        @Override
        public void closeOutbound() {
            delegate.closeOutbound();
        }

        @Override
        public boolean isOutboundDone() {
            return delegate.isOutboundDone();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }

        @Override
        public String[] getEnabledCipherSuites() {
            return delegate.getEnabledCipherSuites();
        }

        @Override
        public void setEnabledCipherSuites(final String[] suites) {
            delegate.setEnabledCipherSuites(suites);
        }

        @Override
        public String[] getSupportedProtocols() {
            return delegate.getSupportedProtocols();
        }

        @Override
        public String[] getEnabledProtocols() {
            return delegate.getEnabledProtocols();
        }

        @Override
        public void setEnabledProtocols(final String[] protocols) {
            delegate.setEnabledProtocols(protocols);
        }

        @Override
        public SSLSession getSession() {
            return delegate.getSession();
        }

        @Override
        public SSLSession getHandshakeSession() {
            return delegate.getHandshakeSession();
        }

        @Override
        public void beginHandshake() throws SSLException {
            delegate.beginHandshake();
        }

        @Override
        public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
            return delegate.getHandshakeStatus();
        }

        @Override
        public void setUseClientMode(final boolean mode) {
            delegate.setUseClientMode(mode);
        }

        @Override
        public boolean getUseClientMode() {
            return delegate.getUseClientMode();
        }

        @Override
        public void setNeedClientAuth(final boolean need) {
            delegate.setNeedClientAuth(need);
        }

        @Override
        public boolean getNeedClientAuth() {
            return delegate.getNeedClientAuth();
        }

        @Override
        public void setWantClientAuth(final boolean want) {
            delegate.setWantClientAuth(want);
        }

        @Override
        public boolean getWantClientAuth() {
            return delegate.getWantClientAuth();
        }

        @Override
        public void setEnableSessionCreation(final boolean flag) {
            delegate.setEnableSessionCreation(flag);
        }

        @Override
        public boolean getEnableSessionCreation() {
            return delegate.getEnableSessionCreation();
        }

        @Override
        public SSLParameters getSSLParameters() {
            return delegate.getSSLParameters();
        }

        @Override
        public void setSSLParameters(final SSLParameters params) {
            delegate.setSSLParameters(params);
            // The HTTPS endpoint identification algorithm would reject the host name before the verifier is invoked.
            // Copy the parameters from the delegate, so the caller's parameters are not modified. JSSE ignores a null
            // algorithm and keeps the previous one, but an empty algorithm disables the endpoint identification.
            final SSLParameters withoutIdentification = delegate.getSSLParameters();
            withoutIdentification.setEndpointIdentificationAlgorithm("");
            delegate.setSSLParameters(withoutIdentification);
        }

        @Override
        public String getApplicationProtocol() {
            return delegate.getApplicationProtocol();
        }

        @Override
        public String getHandshakeApplicationProtocol() {
            return delegate.getHandshakeApplicationProtocol();
        }

        @Override
        public void setHandshakeApplicationProtocolSelector(final BiFunction<SSLEngine, List<String>, String> selector) {
            delegate.setHandshakeApplicationProtocolSelector(selector);
        }

        @Override
        public BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector() {
            return delegate.getHandshakeApplicationProtocolSelector();
        }

        private SSLEngineResult verifyOnFinish(final SSLEngineResult result) throws SSLException {
            if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
                verify(delegate.getSession());
            }
            return result;
        }

        private void verify(final SSLSession session) throws SSLException {
            final String host = getPeerHost();
            if (!verifier.verify(host, session)) {
                // Close the outbound side, so no application data can be written on this engine
                delegate.closeOutbound();
                throw LogMessages.LOGGER.hostnameVerificationFailed(host);
            }
            verifiedSession = session;
        }

        private static boolean hasRemaining(final ByteBuffer[] srcs, final int offset, final int length) {
            for (int i = offset; i < offset + length; i++) {
                if (srcs[i].hasRemaining()) {
                    return true;
                }
            }
            return false;
        }
    }
}
