package org.jboss.resteasy.plugins.server.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.ResteasyContext.CloseableContext;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletResponseWrapper implements HttpResponse {
    public abstract class AsyncOperation {
        final CompletableFuture<Void> future;
        final OutputStream stream;
        private final long id;

        @Deprecated
        public AsyncOperation(final OutputStream stream) {
            this(stream, new CompletableFuture<>(),
                    (stream instanceof DeferredOutputStream ? ((DeferredOutputStream) stream).getId() : -1));
        }

        private AsyncOperation(final DeferredOutputStream stream) {
            this(stream, new CompletableFuture<>(), stream.getId());
        }

        private AsyncOperation(final OutputStream stream, final CompletableFuture<Void> future, final long id) {
            this.stream = stream;
            this.future = future;
            this.id = id;
        }

        public void work(ServletOutputStream sos) {
            try (CloseableContext c = ResteasyContext.addCloseableContextDataLevel(contextDataMap)) {
                doWork(sos);
            }
        }

        protected abstract void doWork(ServletOutputStream sos);

        protected void requeue(final AsyncOperation op) {
            if (op.future.isDone()) {
                return;
            }
            if (stream instanceof DeferredOutputStream) {
                ((DeferredOutputStream) stream).queue(op);
            }
        }

        protected void queueComplete(final AsyncOperation op) {
            if (op.future.isDone()) {
                return;
            }
            if (stream instanceof DeferredOutputStream) {
                final AsyncOperation requeue = (op instanceof CompletionOperation ? op : new CompletionOperation(op));
                ((DeferredOutputStream) stream).queue(requeue);
            }
        }
    }

    public class WriteOperation extends AsyncOperation {

        private final byte[] bytes;
        private final int offset;
        private final int length;

        @Deprecated
        public WriteOperation(final OutputStream stream, final byte[] bytes, final int offset, final int length) {
            super(stream);
            this.bytes = bytes;
            this.offset = offset;
            this.length = length;
        }

        private WriteOperation(final DeferredOutputStream stream, final byte[] bytes, final int offset, final int length) {
            super(stream);
            this.bytes = bytes;
            this.offset = offset;
            this.length = length;
        }

        @Override
        protected void doWork(ServletOutputStream sos) {
            try {
                // we only are complete if isReady says we're good to write, otherwise
                // we will be complete in the next onWritePossible or onError
                if (sos == null) {
                    stream.write(bytes, offset, length);
                    future.complete(null);
                } else {
                    // Check if the stream is ready and if so write the data
                    if (sos.isReady()) {
                        stream.write(bytes, offset, length);
                        // Recheck before we complete the future as the write above may still be in process
                        if (sos.isReady()) {
                            future.complete(null);
                        } else {
                            queueComplete(this);
                        }
                    } else {
                        // The stream is not ready, requeue ourself
                        requeue(this);
                    }
                }
            } catch (IOException e) {
                future.completeExceptionally(e);
            }
        }

        @Override
        public String toString() {
            return "[write: " + new String(bytes) + "]";
        }
    }

    public class FlushOperation extends AsyncOperation {
        @Deprecated
        public FlushOperation(final OutputStream os) {
            super(os);
        }

        public FlushOperation(final DeferredOutputStream os) {
            super(os);
        }

        @Override
        protected void doWork(ServletOutputStream sos) {
            try {
                // we only are complete if isReady says we're good to write, otherwise
                // we will be complete in the next onWritePossible or onError
                if (sos == null) {
                    stream.flush();
                    future.complete(null);
                } else {
                    // The stream is ready, flush the output
                    if (sos.isReady()) {
                        stream.flush();
                        // Recheck before we complete the future as the flush above may still be in process
                        if (sos.isReady()) {
                            future.complete(null);
                        } else {
                            queueComplete(this);
                        }
                    } else {
                        // The stream is not ready, requeue ourself
                        requeue(this);
                    }
                }
            } catch (IOException e) {
                future.completeExceptionally(e);
            }
        }

        @Override
        public String toString() {
            return "[flush]";
        }
    }

    private class CompletionOperation extends AsyncOperation {

        CompletionOperation(final AsyncOperation op) {
            super(op.stream, op.future, op.id);
        }

        @Override
        protected void doWork(final ServletOutputStream sos) {
            if (sos == null || sos.isReady()) {
                if (!future.isDone()) {
                    future.complete(null);
                }
            } else {
                // We need to requeue
                queueComplete(this);
            }
        }
    }

    protected final HttpServletResponse response;
    protected int status = 200;
    protected MultivaluedMap<String, Object> outputHeaders;
    protected final ResteasyProviderFactory factory;
    private OutputStream outputStream;
    protected volatile boolean suppressExceptionDuringChunkedTransfer = true;
    protected final HttpServletRequest request;
    protected final Map<Class<?>, Object> contextDataMap;

    // RESTEASY-1784
    @Override
    public void setSuppressExceptionDuringChunkedTransfer(boolean suppressExceptionDuringChunkedTransfer) {
        this.suppressExceptionDuringChunkedTransfer = suppressExceptionDuringChunkedTransfer;
    }

    @Override
    public boolean suppressExceptionDuringChunkedTransfer() {
        return suppressExceptionDuringChunkedTransfer;
    }

    /**
     * RESTEASY-684 wants to defer access to outputstream until a write happens
     *
     * <p>
     * Note that all locking is on {@code this} and should remain that way to avoid deadlocks on consumers of this
     * stream.
     * </p>
     *
     */
    protected class DeferredOutputStream extends AsyncOutputStream implements WriteListener {
        // Guarded by this
        private final Queue<AsyncOperation> asyncQueue;
        private final AtomicBoolean asyncRegistered;
        // Guarded by this
        private volatile boolean asyncListenerCalled;
        private volatile ServletOutputStream lazyOut;
        // Guarded by this
        private long idCounter;
        // Track if stream is closed due to session invalidation or other errors
        private final AtomicBoolean streamClosed;

        DeferredOutputStream() throws IOException {
            asyncQueue = new PriorityQueue<>(AsyncOperationComparator.INSTANCE);
            asyncRegistered = new AtomicBoolean();
            streamClosed = new AtomicBoolean(false);
        }

        @Override
        public void write(int i) throws IOException {
            getServletOutputStream().write(i);
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            getServletOutputStream().write(bytes);
        }

        @Override
        public void write(byte[] bytes, int i, int i1) throws IOException {
            getServletOutputStream().write(bytes, i, i1);
        }

        @Override
        public void flush() throws IOException {
            getServletOutputStream().flush();
        }

        @Override
        public void close() throws IOException {
            //NOOP (RESTEASY-1650)
        }

        @Override
        public CompletionStage<Void> asyncFlush() {
            AsyncOperation op = new FlushOperation(this);
            queue(op);
            return op.future;
        }

        @Override
        public CompletionStage<Void> asyncWrite(byte[] bytes, int offset, int length) {
            AsyncOperation op = new WriteOperation(this, bytes, offset, length);
            queue(op);
            return op.future;
        }

        /**
         * Checks if the request has a valid {@link HttpSession}. If no session exists, we still consider this valid.
         * A session is only invalid if {@link HttpSession#invalidate()} has been invoked.
         */
        private boolean isSessionInvalidated(final AsyncOperation op) {
            try {
                HttpSession session = request.getSession(false);
                // The session does not exist, consider it valid
                if (session != null) {
                    // Try to access session to check if it's still valid
                    session.getLastAccessedTime();
                    return false;
                }
                // No session means we can't check, assume valid
                return false;
            } catch (IllegalStateException e) {
                streamClosed.set(true);
                // Session has been invalidated
                if (op != null) {
                    op.future.completeExceptionally(Messages.MESSAGES.invalidSession());
                }
                onError(Messages.MESSAGES.invalidSession());
                return true;
            }
        }

        private void queue(AsyncOperation op) {
            // Check if stream should be considered closed
            if (streamClosed.get()) {
                op.future.completeExceptionally(new IOException(Messages.MESSAGES.streamIsClosed()));
                onError(new IOException(Messages.MESSAGES.streamIsClosed()));
                return;
            }

            // fetch it from the context directly to avoid having to restore the context just in case we're invoked on a context-less thread
            HttpRequest resteasyRequest = (HttpRequest) contextDataMap.get(HttpRequest.class);
            if (request.isAsyncStarted() && !resteasyRequest.getAsyncContext().isOnInitialRequest()) {
                boolean flush = false;
                final ServletOutputStream out;
                try {
                    out = getServletOutputStream();
                } catch (IOException e) {
                    // We can't write to the stream, consider it closed
                    streamClosed.set(true);
                    op.future.completeExceptionally(e);
                    onError(e);
                    return;
                }
                if (asyncRegistered.compareAndSet(false, true)) {
                    out.setWriteListener(this);
                }
                synchronized (this) {
                    if (asyncListenerCalled && out.isReady()) {
                        // it's possible that we startAsync and queue, then queue another event and the stream becomes ready before
                        // onWritePossible is called, which means we need to flush the queue here to guarantee ordering if that happens
                        asyncQueue.add(op);
                        flush = true;
                    } else {
                        // just queue
                        asyncQueue.add(op);
                    }
                }
                // Invoked outside the lock to avoid deadlocks, the flushQueue itself locks on this
                if (flush) {
                    flushQueue();
                }
            } else {
                op.work(null);
            }
        }

        private void flushQueue() {
            // Re-validate session before processing queue to avoid race condition
            // between queue() check and async execution
            if (isSessionInvalidated(null)) {
                return;
            }
            synchronized (this) {
                final ServletOutputStream out;
                try {
                    out = getServletOutputStream();
                } catch (IOException e) {
                    onError(e);
                    return;
                }
                AsyncOperation op;
                while (out.isReady() && (op = asyncQueue.poll()) != null) {
                    op.work(out);
                }
            }
        }

        @Override
        public void onWritePossible() {
            asyncListenerCalled = true;
            flushQueue();
        }

        @Override
        public void onError(Throwable t) {
            synchronized (this) {
                asyncListenerCalled = true;
                streamClosed.set(true);
                AsyncOperation op;
                while ((op = asyncQueue.poll()) != null) {
                    if (!op.future.isDone())
                        op.future.completeExceptionally(t);
                }
            }
        }

        private ServletOutputStream getServletOutputStream() throws IOException {
            if (lazyOut == null) {
                synchronized (this) {
                    if (lazyOut == null) {
                        lazyOut = new WrappedServletOutputStream(response.getOutputStream());
                    }
                }
            }
            return lazyOut;
        }

        private long getId() {
            synchronized (this) {
                if (idCounter == Long.MAX_VALUE) {
                    // This should never happen, but we will be safe and just reset the id in case it does.
                    idCounter = 0;
                }
                return idCounter++;
            }
        }
    }

    public HttpServletResponseWrapper(final HttpServletResponse response, final HttpServletRequest request,
            final ResteasyProviderFactory factory) {
        this.response = response;
        this.request = request;
        outputHeaders = new HttpServletResponseHeaders(response, factory);
        this.factory = factory;
        this.contextDataMap = ResteasyContext.getContextDataMap();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        this.response.setStatus(status);
    }

    public MultivaluedMap<String, Object> getOutputHeaders() {
        return outputHeaders;
    }

    public synchronized OutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = new DeferredOutputStream();
        }
        return outputStream;
    }

    @Override
    public synchronized void setOutputStream(OutputStream os) {
        this.outputStream = os;
    }

    public void addNewCookie(NewCookie cookie) {
        outputHeaders.add(jakarta.ws.rs.core.HttpHeaders.SET_COOKIE, cookie);
    }

    public void sendError(int status) throws IOException {
        response.sendError(status);
    }

    public void sendError(int status, String message) throws IOException {
        response.sendError(status, message);
    }

    public boolean isCommitted() {
        return response.isCommitted();
    }

    public void reset() {
        response.reset();
        outputHeaders = new HttpServletResponseHeaders(response, factory);
    }

    @Override
    public void flushBuffer() throws IOException {
        response.flushBuffer();
    }

    /**
     * The Undertow {@code ServletRequestContext} requires access when getting the current request context. This wraps
     * each action in the delegate in a privileged action if the security manager is installed.
     */
    private static class WrappedServletOutputStream extends ServletOutputStream {
        private final ServletOutputStream delegate;
        private final boolean usePrivilegedAction;

        private WrappedServletOutputStream(final ServletOutputStream delegate) {
            this.delegate = delegate;
            usePrivilegedAction = System.getSecurityManager() != null;
        }

        @Override
        public void print(final String s) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.print(s));
            } else {
                delegate.print(s);
            }
        }

        @Override
        public void print(final boolean b) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.print(b));
            } else {
                delegate.print(b);
            }
        }

        @Override
        public void print(final char c) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.print(c));
            } else {
                delegate.print(c);
            }
        }

        @Override
        public void print(final int i) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.print(i));
            } else {
                delegate.print(i);
            }
        }

        @Override
        public void print(final long l) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.print(l));
            } else {
                delegate.print(l);
            }
        }

        @Override
        public void print(final float f) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.print(f));
            } else {
                delegate.print(f);
            }
        }

        @Override
        public void print(final double d) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.print(d));
            } else {
                delegate.print(d);
            }
        }

        @Override
        public void println() throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(delegate::println);
            } else {
                delegate.println();
            }
        }

        @Override
        public void println(final String s) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.println(s));
            } else {
                delegate.println(s);
            }
        }

        @Override
        public void println(final boolean b) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.println(b));
            } else {
                delegate.println(b);
            }
        }

        @Override
        public void println(final char c) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.println(c));
            } else {
                delegate.println(c);
            }
        }

        @Override
        public void println(final int i) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.println(i));
            } else {
                delegate.println(i);
            }
        }

        @Override
        public void println(final long l) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.println(l));
            } else {
                delegate.println(l);
            }
        }

        @Override
        public void println(final float f) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.println(f));
            } else {
                delegate.println(f);
            }
        }

        @Override
        public void println(final double d) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.println(d));
            } else {
                delegate.println(d);
            }
        }

        @Override
        public boolean isReady() {
            return delegate.isReady();
        }

        @Override
        public void setWriteListener(final WriteListener writeListener) {
            delegate.setWriteListener(writeListener);
        }

        @Override
        public void write(final int b) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.write(b));
            } else {
                delegate.write(b);
            }
        }

        @Override
        public void write(final byte[] b) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.write(b));
            } else {
                delegate.write(b);
            }
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(() -> delegate.write(b, off, len));
            } else {
                delegate.write(b, off, len);
            }
        }

        @Override
        public void flush() throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(delegate::flush);
            } else {
                delegate.flush();
            }
        }

        @Override
        public void close() throws IOException {
            if (usePrivilegedAction) {
                doPrivileged(delegate::close);
            } else {
                delegate.close();
            }
        }

        private void doPrivileged(final IoInvoker invoker) throws IOException {
            try {
                AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                    try {
                        invoker.invoke();
                        return null;
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        }

        @FunctionalInterface
        private interface IoInvoker {
            void invoke() throws IOException;
        }
    }

    private static class AsyncOperationComparator implements Comparator<AsyncOperation> {
        static final AsyncOperationComparator INSTANCE = new AsyncOperationComparator();

        @Override
        public int compare(final AsyncOperation o1, final AsyncOperation o2) {
            return Long.compare(o1.id, o2.id);
        }
    }
}
