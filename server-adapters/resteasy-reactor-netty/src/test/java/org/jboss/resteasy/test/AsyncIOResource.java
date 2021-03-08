package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.reactor.netty.NettyUtil;
import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Path("async-io")
public class AsyncIOResource {

    @GET
    @Path("blocking-writer-on-io-thread")
    public BlockingWriterData blockingWriterOnIoThread() {
        Assert.assertTrue(NettyUtil.isIoThread());
        return new BlockingWriterData();
    }

    @GET
    @Path("async-writer-on-io-thread")
    public AsyncWriterData asyncWriterOnIoThread() {
        return new AsyncWriterData(true, false);
    }

    @GET
    @Path("slow-async-writer-on-io-thread")
    public AsyncWriterData slowAsyncWriterOnIoThread() {
        return new AsyncWriterData(true, true);
    }

    @GET
    @Path("blocking-writer-on-worker-thread")
    public CompletionStage<BlockingWriterData> blockingWriterOnWorkerThread() {
        return CompletableFuture.supplyAsync(() -> new BlockingWriterData());
    }

    @GET
    @Path("async-writer-on-worker-thread")
    public CompletionStage<AsyncWriterData> asyncWriterOnWorkerThread() {
        return hopefullySlowEnoughAsyncWriterData(false, false);
    }

    @GET
    @Path("slow-async-writer-on-worker-thread")
    public CompletionStage<AsyncWriterData> slowAsyncWriterOnWorkerThread() {
        return hopefullySlowEnoughAsyncWriterData(false, true);
    }

    /**
     * Get off the server thread by leveraging {@link CompletableFuture#supplyAsync(Supplier)} where
     * the tests expect that things like {@link org.jboss.resteasy.spi.AsyncMessageBodyWriter} will
     * run on the spawned thread.  The problem with that assertion is that it assumes the lambda
     * run on the forkjoin pool completes _after_ RestEasy calls methods on the Resource-returned
     * future (e.g. {@link CompletableFuture#whenComplete(BiConsumer)}).  If `supplyAsync` lambda has already
     * completed on the fork-join thread, then lambdas on `whenComplete`/`handle`/etc. will run
     * on the thread that calls them.
     */
    private static CompletableFuture<AsyncWriterData> hopefullySlowEnoughAsyncWriterData(
        final boolean isMaybeOnIoThread,
        final boolean isSimulateSlowIo
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(250);
            } catch (InterruptedException ie) {
                Thread.interrupted();
                throw new RuntimeException(ie);
            }
            return new AsyncWriterData(isMaybeOnIoThread, isSimulateSlowIo);
        });
    }
}
