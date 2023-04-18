package org.jboss.resteasy.plugins.server.netty;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class MultiPromise {

    private final ChannelHandlerContext ctx;
    private final AtomicInteger created = new AtomicInteger();
    private final AtomicReference<Throwable> cause = new AtomicReference<>();
    private final ChannelPromise promise;
    private boolean started;

    public MultiPromise(final ChannelHandlerContext ctx, final ChannelPromise promise) {
        this.ctx = ctx;
        this.promise = promise;
    }

    public ChannelPromise newPromise() {
        created.getAndIncrement();
        return ctx.newPromise().addListener(f -> {
            if (!f.isSuccess())
                cause.compareAndSet(null, f.cause());
            if (created.decrementAndGet() == 0)
                forward();
        });
    }

    private synchronized void forward() {
        if (!started)
            return;
        if (!promise.isDone()) {
            Throwable throwable = cause.get();
            if (throwable != null)
                promise.setFailure(throwable);
            else
                promise.setSuccess();
        }
    }

    public synchronized void readyToForward() {
        started = true;
        if (created.get() == 0)
            forward();
    }

}
