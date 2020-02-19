package org.jboss.resteasy.test.undertow;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("async-io")
public class AsyncIOResource {

    @GET
    @Path("blocking-writer-on-worker-thread")
    public BlockingWriterData blockingWriterOnWorkerThread() {
        return new BlockingWriterData();
    }

    @GET
    @Path("async-writer-on-worker-thread")
    public AsyncWriterData asyncWriterOnWorkerThread() {
        return new AsyncWriterData(false, false);
    }

    @GET
    @Path("slow-async-writer-on-worker-thread")
    public AsyncWriterData slowAsyncWriterOnWorkerThread() {
        return new AsyncWriterData(false, true);
    }
}
