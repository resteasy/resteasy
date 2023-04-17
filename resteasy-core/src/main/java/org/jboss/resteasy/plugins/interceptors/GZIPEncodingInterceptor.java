package org.jboss.resteasy.plugins.interceptors;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.zip.GZIPOutputStream;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.AsyncWriterInterceptor;
import org.jboss.resteasy.spi.AsyncWriterInterceptorContext;
import org.jboss.resteasy.spi.BlockingAsyncOutputStream;
import org.jboss.resteasy.util.CommitHeaderAsyncOutputStream;
import org.jboss.resteasy.util.CommitHeaderOutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Priority(Priorities.ENTITY_CODER)
public class GZIPEncodingInterceptor implements AsyncWriterInterceptor {
    public static class EndableGZIPOutputStream extends GZIPOutputStream {
        public EndableGZIPOutputStream(final OutputStream os) throws IOException {
            super(os);
        }

        @Override
        public void finish() throws IOException {
            super.finish();
            def.end(); // make sure on finish the deflater's end() is called to release the native code pointer
        }
    }

    public static class CommittedGZIPOutputStream extends CommitHeaderAsyncOutputStream {
        protected CommittedGZIPOutputStream(final OutputStream delegate,
                final CommitHeaderOutputStream.CommitCallback headers) {
            this(new BlockingAsyncOutputStream(delegate), headers);
        }

        protected CommittedGZIPOutputStream(final AsyncOutputStream delegate,
                final CommitHeaderOutputStream.CommitCallback headers) {
            super(delegate, headers);
        }

        protected GZIPOutputStream gzip;

        public GZIPOutputStream getGzip() {
            return gzip;
        }

        @Override
        public synchronized void commit() {
            if (isHeadersCommitted)
                return;
            isHeadersCommitted = true;
            try {
                // GZIPOutputStream constructor writes to underlying OS causing headers to be written.
                // so we swap gzip OS in when we are ready to write.
                gzip = new EndableGZIPOutputStream(delegate);
                delegate = new BlockingAsyncOutputStream(gzip);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        synchronized void finish() throws IOException {
            if (gzip != null) {
                gzip.finish();
            }
        }

        CompletionStage<Void> asyncFinish() {
            try {
                finish();
            } catch (IOException e) {
                CompletableFuture<Void> ret = new CompletableFuture<>();
                ret.completeExceptionally(e);
                return ret;
            }
            return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        LogMessages.LOGGER.debugf("Interceptor : %s,  Method : aroundWriteTo", getClass().getName());
        if (isGzipEnconding(context.getHeaders())) {
            OutputStream old = context.getOutputStream();
            removeContentLengthHeader(context.getHeaders());
            CommittedGZIPOutputStream gzipOutputStream = replaceOutputStream(new BlockingAsyncOutputStream(old),
                    context::setOutputStream);
            try {
                context.proceed();
            } finally {
                gzipOutputStream.finish();
                context.setOutputStream(old);
            }
        } else {
            context.proceed();
        }
    }

    @Override
    public CompletionStage<Void> asyncAroundWriteTo(AsyncWriterInterceptorContext context) {
        LogMessages.LOGGER.debugf("Interceptor : %s,  Method : asyncAroundWriteTo", getClass().getName());
        if (isGzipEnconding(context.getHeaders())) {
            AsyncOutputStream old = context.getAsyncOutputStream();
            removeContentLengthHeader(context.getHeaders());
            CommittedGZIPOutputStream gzipOutputStream = replaceOutputStream(old, context::setAsyncOutputStream);
            return context.asyncProceed()
                    .handle((v, e) -> gzipOutputStream.asyncFinish()
                            .thenAccept(f -> context.setAsyncOutputStream(old)))
                    .thenCompose(x -> x);
        } else {
            return context.asyncProceed();
        }
    }

    private boolean isGzipEnconding(MultivaluedMap<String, Object> headers) {
        Object encoding = headers.getFirst(HttpHeaders.CONTENT_ENCODING);
        return encoding != null && encoding.toString().equalsIgnoreCase("gzip");
    }

    private void removeContentLengthHeader(MultivaluedMap<String, Object> headers) {
        // Any content length set will be obsolete
        headers.remove(HttpHeaders.CONTENT_LENGTH);
    }

    private CommittedGZIPOutputStream replaceOutputStream(AsyncOutputStream originalStream,
            Consumer<AsyncOutputStream> contextSetter) {
        // GZIPOutputStream constructor writes to underlying OS causing headers to be written.
        CommittedGZIPOutputStream gzipOutputStream = new CommittedGZIPOutputStream(originalStream, null);
        contextSetter.accept(gzipOutputStream);
        return gzipOutputStream;
    }
}
