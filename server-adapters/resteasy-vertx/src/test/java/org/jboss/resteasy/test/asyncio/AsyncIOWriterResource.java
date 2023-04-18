package org.jboss.resteasy.test.asyncio;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("async-io-writer")
public class AsyncIOWriterResource {
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    public CompletionStage<String> get() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Hello";
        });
    }
}
