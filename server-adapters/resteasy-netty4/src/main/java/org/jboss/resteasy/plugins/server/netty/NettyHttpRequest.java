package org.jboss.resteasy.plugins.server.netty;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.AbstractExecutionContext;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.ResteasyContext.CloseableContext;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.BaseHttpRequest;
import org.jboss.resteasy.plugins.server.netty.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.specimpl.ResteasyUriInfo;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;
import org.jboss.resteasy.spi.RunnableWithException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;

/**
 * Abstraction for an inbound http request on the server, or a response from a server to a client
 * <p>
 * We have this abstraction so that we can reuse marshalling objects in a client framework and serverside framework
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author Norman Maurer
 * @author Kristoffer Sjogren
 * @version $Revision: 1 $
 */
public class NettyHttpRequest extends BaseHttpRequest {
    protected ResteasyHttpHeaders httpHeaders;
    protected SynchronousDispatcher dispatcher;
    protected String httpMethod;
    protected InputStream inputStream;
    protected Map<String, Object> attributes = new HashMap<String, Object>();
    protected NettyHttpResponse response;
    private final boolean is100ContinueExpected;
    private final NettyExecutionContext executionContext;
    private final ChannelHandlerContext ctx;
    private volatile boolean flushed;
    private ByteBuf content;

    public NettyHttpRequest(final ChannelHandlerContext ctx, final ResteasyHttpHeaders httpHeaders, final ResteasyUriInfo uri,
            final String httpMethod, final SynchronousDispatcher dispatcher, final NettyHttpResponse response,
            final boolean is100ContinueExpected) {
        super(uri);
        this.is100ContinueExpected = is100ContinueExpected;
        this.response = response;
        this.dispatcher = dispatcher;
        this.httpHeaders = httpHeaders;
        this.httpMethod = httpMethod;
        this.ctx = ctx;
        this.executionContext = new NettyExecutionContext(this, response, dispatcher);
    }

    @Override
    public MultivaluedMap<String, String> getMutableHeaders() {
        return httpHeaders.getMutableHeaders();
    }

    @Override
    public void setHttpMethod(String method) {
        this.httpMethod = method;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(new HashSet<>(attributes.keySet()));
    }

    @Override
    public ResteasyAsynchronousContext getAsyncContext() {
        return executionContext;
    }

    public boolean isFlushed() {
        return flushed;
    }

    @Override
    public Object getAttribute(String attribute) {
        return attributes.get(attribute);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void setInputStream(InputStream stream) {
        this.inputStream = stream;
    }

    @Override
    public String getHttpMethod() {
        return httpMethod;
    }

    public NettyHttpResponse getResponse() {
        return response;
    }

    public boolean isKeepAlive() {
        return response.isKeepAlive();
    }

    public boolean is100ContinueExpected() {
        return is100ContinueExpected;
    }

    @Override
    public void forward(String path) {
        throw new NotImplementedYetException();
    }

    @Override
    public boolean wasForwarded() {
        return false;
    }

    public void setContentBuffer(ByteBuf content) {
        this.content = content;
        this.inputStream = new ByteBufInputStream(content);
    }

    public void releaseContentBuffer() {
        if (content != null) {
            this.content.release();
        }
    }

    class NettyExecutionContext extends AbstractExecutionContext {
        protected final NettyHttpRequest request;
        protected final NettyHttpResponse response;
        protected volatile boolean done;
        protected volatile boolean cancelled;
        protected volatile boolean wasSuspended;
        protected final NettyHttpAsyncResponse asyncResponse;

        NettyExecutionContext(final NettyHttpRequest request, final NettyHttpResponse response,
                final SynchronousDispatcher dispatcher) {
            super(dispatcher, request, response);
            this.request = request;
            this.response = response;
            this.asyncResponse = new NettyHttpAsyncResponse(dispatcher, request, response);
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
            if (wasSuspended) {
                throw new IllegalStateException(Messages.MESSAGES.alreadySuspended());
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
            if (!ret.isDone() && !isSuspended()) {
                suspend();
            }
            return ret;
        }

        @Override
        public CompletionStage<Void> executeBlockingIo(RunnableWithException f, boolean hasInterceptors) {
            if (!NettyUtil.isIoThread()) {
                // we're blocking
                try {
                    f.run();
                } catch (Exception e) {
                    CompletableFuture<Void> ret = new CompletableFuture<>();
                    ret.completeExceptionally(e);
                    return ret;
                }
                return CompletableFuture.completedFuture(null);
            } else if (!hasInterceptors) {
                Map<Class<?>, Object> context = ResteasyContext.getContextDataMap();
                // turn any sync request into async
                if (!isSuspended()) {
                    suspend();
                }
                return CompletableFuture.runAsync(() -> {
                    try (CloseableContext newContext = ResteasyContext.addCloseableContextDataLevel(context)) {
                        f.run();
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                CompletableFuture<Void> ret = new CompletableFuture<>();
                ret.completeExceptionally(
                        new RuntimeException("Cannot use blocking IO with interceptors when we're on the IO thread"));
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
            protected ScheduledFuture<?> timeoutFuture;
            private final NettyHttpResponse nettyResponse;

            NettyHttpAsyncResponse(final SynchronousDispatcher dispatcher, final NettyHttpRequest request,
                    final NettyHttpResponse response) {
                super(dispatcher, request, response);
                this.nettyResponse = response;
            }

            @Override
            public void initialRequestThreadFinished() {
                // done
            }

            @Override
            public void complete() {
                synchronized (responseLock) {
                    if (done)
                        return;
                    if (cancelled)
                        return;
                    done = true;
                    nettyFlush();
                }
            }

            @Override
            public boolean resume(Object entity) {
                synchronized (responseLock) {
                    if (done)
                        return false;
                    if (cancelled)
                        return false;
                    done = true;
                    return internalResume(entity, t -> nettyFlush());
                }
            }

            @Override
            public boolean resume(Throwable ex) {
                synchronized (responseLock) {
                    if (done)
                        return false;
                    if (cancelled)
                        return false;
                    done = true;
                    return internalResume(ex, t -> nettyFlush());
                }
            }

            @Override
            public boolean cancel() {
                synchronized (responseLock) {
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
                synchronized (responseLock) {
                    if (cancelled)
                        return true;
                    if (done)
                        return false;
                    done = true;
                    cancelled = true;
                    return internalResume(
                            Response.status(Response.Status.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER, retryAfter)
                                    .build(),
                            t -> nettyFlush());
                }
            }

            protected void nettyFlush() {
                flushed = true;
                try {
                    synchronized (responseLock) {
                        nettyResponse.finish();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean cancel(Date retryAfter) {
                synchronized (responseLock) {
                    if (cancelled)
                        return true;
                    if (done)
                        return false;
                    done = true;
                    cancelled = true;
                    return internalResume(
                            Response.status(Response.Status.SERVICE_UNAVAILABLE).header(HttpHeaders.RETRY_AFTER, retryAfter)
                                    .build(),
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
                synchronized (responseLock) {
                    if (done || cancelled)
                        return false;
                    if (timeoutFuture != null && !timeoutFuture.cancel(false)) {
                        return false;
                    }
                    Runnable task = new Runnable() {
                        @Override
                        public void run() {
                            handleTimeout();
                        }
                    };
                    timeoutFuture = ctx.executor().schedule(task, time, unit);
                }
                return true;
            }

            protected void handleTimeout() {
                if (timeoutHandler != null) {
                    timeoutHandler.handleTimeout(this);
                    return;
                }
                if (done)
                    return;
                resume(new ServiceUnavailableException());
            }
        }
    }

    @Override
    public String getRemoteHost() {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getHostName();
    }

    @Override
    public String getRemoteAddress() {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
    }
}
