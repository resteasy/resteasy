package org.jboss.resteasy.plugins.server.reactor.netty;

import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.AbstractExecutionContext;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.BaseHttpRequest;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;
import org.jboss.resteasy.spi.RunnableWithException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.http.server.HttpServerRequest;

import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * This is the 1-way bridge from reactor-netty's {@link HttpServerRequest} and
 * RestEasy.  Headers come via direct call.  RestEasy will access the request
 * body via {@link #getInputStream}.
 */
class ReactorNettyHttpRequest extends BaseHttpRequest {
    private static final Logger log = LoggerFactory.getLogger(ReactorNettyHttpRequest.class);

    private final HttpServerRequest req;
    private InputStream in;
    private final NettyExecutionContext executionContext;
    private final Map<String, Object> attributes = new HashMap<String, Object>();
    private Duration timeout;

    ReactorNettyHttpRequest(
        final ResteasyUriInfo uri,
        final HttpServerRequest req,
        final InputStream body,
        final ReactorNettyHttpResponse response,
        final SynchronousDispatcher dispatcher
        ) {
        super(uri);
        this.req = requireNonNull(req);
        this.in = requireNonNull(body);
        this.executionContext = new NettyExecutionContext(this, response, dispatcher);
    }

    @Override
    public HttpHeaders getHttpHeaders() {
        final MultivaluedMap<String, String> map = new MultivaluedMapImpl<>();
        req.requestHeaders().forEach(e -> map.putSingle(e.getKey(), e.getValue()));
        return new ResteasyHttpHeaders(map);
    }

    @Override
    public MultivaluedMap<String, String> getMutableHeaders() {
        // TODO
        return null;
    }

    @Override
    public InputStream getInputStream() {
        return in;
    }

    @Override
    public void setInputStream(InputStream stream) {
        // I guess this somehow 'wraps' the original InputStream..
        this.in = stream;
    }

    @Override
    public String getHttpMethod() {
        return req.method().name();
    }

    @Override
    public void setHttpMethod(String method) {
        // TODO
    }

    @Override
    public Enumeration<String> getAttributeNames()
    {
        Enumeration<String> en = new Enumeration<String>()
        {
            private Iterator<String> it = attributes.keySet().iterator();
            @Override
            public boolean hasMoreElements()
            {
                return it.hasNext();
            }

            @Override
            public String nextElement()
            {
                return it.next();
            }
        };
        return en;
    }

    @Override
    public Object getAttribute(String attribute)
    {
        return attributes.get(attribute);
    }

    @Override
    public void setAttribute(String name, Object value)
    {
        attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name)
    {
        attributes.remove(name);
    }

    @Override
    public ResteasyAsynchronousContext getAsyncContext() {
        return executionContext;
    }

    @Override
    public void forward(String path) {
        // TODO
    }

    @Override
    public boolean wasForwarded() {
        // TODO
        return false;
    }

    @Override
    public String getRemoteAddress() {
        return req.remoteAddress().getAddress().getHostAddress();
    }

    @Override
    public String getRemoteHost() {
        return req.remoteAddress().getHostName();
    }

    Duration timeout() {
        return this.timeout;
    }

    class NettyExecutionContext extends AbstractExecutionContext {
        protected final ReactorNettyHttpRequest request;
        protected final ReactorNettyHttpResponse response;
        protected volatile boolean done;
        protected volatile boolean cancelled;
        protected volatile boolean wasSuspended;
        protected NettyExecutionContext.NettyHttpAsyncResponse asyncResponse;

        NettyExecutionContext(final ReactorNettyHttpRequest request, final ReactorNettyHttpResponse response, final SynchronousDispatcher dispatcher)
        {
            super(dispatcher, request, response);
            this.request = request;
            this.response = response;
            this.asyncResponse = new NettyExecutionContext.NettyHttpAsyncResponse(dispatcher, request, response);
        }

        @Override
        public boolean isSuspended() {
            return wasSuspended;
        }

        @Override
        public ResteasyAsynchronousResponse getAsyncResponse() {
            return asyncResponse;
        }

        @Override
        public ResteasyAsynchronousResponse suspend() throws IllegalStateException {
            return suspend(-1);
        }

        @Override
        public ResteasyAsynchronousResponse suspend(long millis) throws IllegalStateException {
            return suspend(millis, TimeUnit.MILLISECONDS);
        }

        @Override
        public ResteasyAsynchronousResponse suspend(long time, TimeUnit unit) throws IllegalStateException {
            if (wasSuspended)
            {
                throw new IllegalStateException("oh nos!");
            }
            wasSuspended = true;
            return asyncResponse;
        }

        @Override
        public void complete() {
            if (wasSuspended) {
                asyncResponse.complete();
            }
        }

        @Override
        public CompletionStage<Void> executeAsyncIo(CompletionStage<Void> f) {
            // check if this CF is already resolved
            CompletableFuture<Void> ret = f.toCompletableFuture();
            // if it's not resolved, we may need to suspend
            if(!ret.isDone() && !isSuspended()) {
                suspend();
            }
            return ret;
        }

        @Override
        public CompletionStage<Void> executeBlockingIo(RunnableWithException f, boolean hasInterceptors) {
            if(!NettyUtil.isIoThread()) {
                try {
                    f.run();
                } catch (Exception e) {
                    CompletableFuture<Void> ret = new CompletableFuture<>();
                    ret.completeExceptionally(e);
                    return ret;
                }
                return CompletableFuture.completedFuture(null);
            } else if(!hasInterceptors) {
                Map<Class<?>, Object> context = ResteasyContext.getContextDataMap();
                // turn any sync request into async
                if(!isSuspended()) {
                    suspend();
                }
                return CompletableFuture.runAsync(() -> {
                    try(ResteasyContext.CloseableContext newContext = ResteasyContext.addCloseableContextDataLevel(context)){
                        f.run();
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                CompletableFuture<Void> ret = new CompletableFuture<>();
                ret.completeExceptionally(new RuntimeException("Cannot use blocking IO with interceptors when we're on the IO thread"));
                return ret;
            }
        }

        /**
         * Netty implementation of {@link AsyncResponse}.
         *
         * @author Kristoffer Sjogren
         */
        class NettyHttpAsyncResponse extends AbstractAsynchronousResponse {
            private final Object responseLock = new Object();
            protected ScheduledFuture timeoutFuture;

            private ReactorNettyHttpResponse nettyResponse;
            NettyHttpAsyncResponse(
                final SynchronousDispatcher dispatcher,
                final ReactorNettyHttpRequest request,
                final ReactorNettyHttpResponse response
            ) {
                super(dispatcher, request, response);
                this.nettyResponse = response;
            }

            @Override
            public void initialRequestThreadFinished() {
                // done
            }

            @Override
            public void complete() {
                synchronized (responseLock)
                {
                    if (done) return;
                    if (cancelled) return;
                    done = true;
                    nettyFlush();
                }
            }


            @Override
            public boolean resume(Object entity) {
                synchronized (responseLock)
                {
                    if (done) return false;
                    if (cancelled) return false;
                    done = true;
                    return internalResume(entity, t -> nettyFlush());
                }
            }

            @Override
            public boolean resume(Throwable ex) {
                synchronized (responseLock)
                {
                    if (done) return false;
                    if (cancelled) return false;
                    done = true;
                    return internalResume(ex, t -> nettyFlush());
                }
            }

            @Override
            public boolean cancel() {
                log.trace("Cancellation occurred!");
                synchronized (responseLock)
                {
                    if (cancelled) {
                        return true;
                    }
                    if (done) {
                        return false;
                    }
                    done = true;
                    cancelled = true;
                    return internalResume(Response.status(Response.Status.SERVICE_UNAVAILABLE).build(), t -> nettyFlush());
                }
            }

            @Override
            public boolean cancel(int retryAfter) {
                log.trace("Cancellation occurred!");
                synchronized (responseLock)
                {
                    if (cancelled) return true;
                    if (done) return false;
                    done = true;
                    cancelled = true;
                    return internalResume(Response.status(Response.Status.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER, retryAfter).build(),
                        t -> nettyFlush());
                }
            }

            protected synchronized void nettyFlush()
            {
                try
                {
                    nettyResponse.close();
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean cancel(Date retryAfter) {
                log.trace("Cancellation occurred!");
                synchronized (responseLock)
                {
                    if (cancelled) return true;
                    if (done) return false;
                    done = true;
                    cancelled = true;
                    return internalResume(Response.status(Response.Status.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER, retryAfter).build(),
                        t -> nettyFlush());
                }
            }

            @Override
            public boolean isSuspended() {
                return !done && !cancelled;
            }

            @Override
            public boolean isCancelled() {
                return cancelled;
            }

            @Override
            public boolean isDone() {
                return done;
            }

            @Override
            public boolean setTimeout(long time, TimeUnit unit) {
                log.debug("Setting timeout");
                synchronized (responseLock)
                {
                    if (done || cancelled) return false;
                    if (timeoutFuture != null  && !timeoutFuture.cancel(false)) {
                        return false;
                    }
                    timeout = Duration.ofNanos(unit.toNanos(time));
                }
                return true;
            }

            protected void handleTimeout()
            {
                if (timeoutHandler != null)
                {
                    timeoutHandler.handleTimeout(this);
                }
                if (done) return;
                resume(new ServiceUnavailableException());
            }
        }
    }
}
