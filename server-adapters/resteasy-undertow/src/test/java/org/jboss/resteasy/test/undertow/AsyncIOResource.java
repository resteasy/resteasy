package org.jboss.resteasy.test.undertow;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.io.input.NullInputStream;
import org.jboss.resteasy.plugins.providers.FileRange;

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

    @GET
    @Path("io/{size}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response io(@PathParam("size") long size) {
        return Response.ok(new NullInputStream(size)).build();
    }

    @GET
    @Path("io/file-range/{size}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response fileRange(@PathParam("size") long size) throws IOException {
        File file = File.createTempFile("empty-file", "");
        file.deleteOnExit();
        RandomAccessFile rFile = new RandomAccessFile(file, "rw");
        rFile.setLength(size);
        rFile.close();
        return Response.ok(new FileRange(file, 0, size-1)).build();
    }
}
