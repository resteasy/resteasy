package org.jboss.resteasy.test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.plugins.server.netty.NettyUtil;
import org.junit.jupiter.api.Assertions;

@Path("async-io")
public class AsyncIOResource {

    @GET
    @Path("blocking-writer-on-io-thread")
    public BlockingWriterData blockingWriterOnIoThread() {
        Assertions.assertTrue(NettyUtil.isIoThread());
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
