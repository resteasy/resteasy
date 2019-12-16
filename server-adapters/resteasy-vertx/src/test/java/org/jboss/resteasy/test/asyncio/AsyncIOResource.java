package org.jboss.resteasy.test.asyncio;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.junit.Assert;

import io.vertx.core.Context;

@Path("async-io")
public class AsyncIOResource {

    @GET
    @Path("blocking-writer-on-io-thread")
    public BlockingWriterData blockingWriterOnIoThread() {
        Assert.assertTrue(Context.isOnEventLoopThread());
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
        return CompletableFuture.supplyAsync(() -> new AsyncWriterData(false, true));
    }

    @GET
    @Path("slow-async-writer-on-worker-thread")
    public CompletionStage<AsyncWriterData> slowAsyncWriterOnWorkerThread() {
        return CompletableFuture.supplyAsync(() -> new AsyncWriterData(false, true));
    }
}
