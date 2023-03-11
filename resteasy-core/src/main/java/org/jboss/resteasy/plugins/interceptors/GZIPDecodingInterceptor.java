package org.jboss.resteasy.plugins.interceptors;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.interception.jaxrs.ServerReaderInterceptorContext;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyConfiguration;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Priority(Priorities.ENTITY_CODER)
public class GZIPDecodingInterceptor implements ReaderInterceptor {
    private static final int DEFAULT_MAX_SIZE = 10000000;
    private int maxSize;

    public GZIPDecodingInterceptor(final int maxSize) {
        this.maxSize = maxSize;
    }

    public GZIPDecodingInterceptor() {
        this.maxSize = -1;
    }

    public static class FinishableGZIPInputStream extends GZIPInputStream {
        private int maxSize;
        private int count;
        private boolean server;

        public FinishableGZIPInputStream(final InputStream is) throws IOException {
            this(is, true, DEFAULT_MAX_SIZE);
        }

        public FinishableGZIPInputStream(final InputStream is, final boolean server) throws IOException {
            this(is, server, DEFAULT_MAX_SIZE);
        }

        public FinishableGZIPInputStream(final InputStream is, final boolean server, final int maxSize) throws IOException {
            super(is);
            this.server = server;
            this.maxSize = maxSize;
        }

        public int read(byte[] buf, int off, int len) throws IOException {
            LogMessages.LOGGER.debugf("Interceptor : %s,  Method : read", getClass().getName());
            int n = super.read(buf, off, len);
            if (n > -1) {
                count += n;
            }
            if (count > maxSize) {
                finish();
                close();
                if (server) {
                    throw new WebApplicationException(Response.status(Status.REQUEST_ENTITY_TOO_LARGE)
                            .entity(Messages.MESSAGES.gzipExceedsMaxSize(maxSize)).build());
                } else {
                    throw new ProcessingException(Messages.MESSAGES.gzipExceedsMaxSize(maxSize));
                }
            }
            return n;
        }

        public void finish() {
            inf.end(); // make sure on finish the inflater's end() is called to release the native code pointer
        }
    }

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        LogMessages.LOGGER.debugf("Interceptor : %s,  Method : aroundReadFrom", getClass().getName());
        Object encoding = context.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);
        if (encoding != null && encoding.toString().equalsIgnoreCase("gzip")) {
            InputStream old = context.getInputStream();
            FinishableGZIPInputStream is = new FinishableGZIPInputStream(old, context instanceof ServerReaderInterceptorContext,
                    getMaxSize());
            context.setInputStream(is);
            try {
                return context.proceed();
            } finally {
                // Don't finish() an InputStream - TODO this still will require a garbage collect to finish the stream
                // see RESTEASY-554 for more details
                if (!context.getType().equals(InputStream.class))
                    is.finish();
                context.setInputStream(old);
            }
        } else {
            return context.proceed();
        }
    }

    private int getMaxSize() {
        if (maxSize != -1) {
            return maxSize;
        }

        int size = -1;
        ResteasyConfiguration context = ResteasyContext.getContextData(ResteasyConfiguration.class);
        if (context != null) {
            String s = context.getParameter(ResteasyContextParameters.RESTEASY_GZIP_MAX_INPUT);
            if (s != null) {
                try {
                    size = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    LogMessages.LOGGER.invalidFormat(ResteasyContextParameters.RESTEASY_GZIP_MAX_INPUT,
                            Integer.toString(DEFAULT_MAX_SIZE));
                }
            }
        }
        if (size == -1) {
            size = DEFAULT_MAX_SIZE;
        }

        return size;
    }
}
