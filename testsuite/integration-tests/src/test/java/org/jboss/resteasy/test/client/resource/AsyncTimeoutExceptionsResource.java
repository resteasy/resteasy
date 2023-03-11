package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("/")
public class AsyncTimeoutExceptionsResource {

    private static Logger logger = Logger.getLogger(AsyncTimeoutExceptionsResource.class);

    @GET
    @Path("/sticker")
    @Produces("application/xml")
    public AsyncTimeoutExceptionsSticker sticker() throws InterruptedException {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            logger.error("Sleep was interrupted", e);
        }
        AsyncTimeoutExceptionsSticker sticker = new AsyncTimeoutExceptionsSticker();
        sticker.setName("turtle");
        return sticker;
    }

    @GET
    @Path("/sticker2")
    @Produces("application/xml")
    public AsyncTimeoutExceptionsSticker sticker2(@Suspended final AsyncResponse response) throws InterruptedException {

        // Should never run
        AsyncTimeoutExceptionsSticker sticker = new AsyncTimeoutExceptionsSticker();
        sticker.setName("turtle");
        return sticker;
    }

    @GET
    @Path("/sticker3")
    @Produces("application/xml")
    public AsyncTimeoutExceptionsSticker sticker3(@Suspended final AsyncResponse response) throws InterruptedException {

        AsyncTimeoutExceptionsSticker sticker = new AsyncTimeoutExceptionsSticker();
        new Thread() {
            public void run() {
                try {
                    sleep(10000);
                    AsyncTimeoutExceptionsSticker sticker = new AsyncTimeoutExceptionsSticker();
                    sticker.setName("turtle");
                } catch (InterruptedException e) {
                    logger.error("Sleep was interrupted", e);
                }
            }
        }.start();

        return sticker;
    }

    @GET
    @Path("/get")
    public Response get() throws InterruptedException {
        Thread.sleep(10000);
        return Response.ok().build();
    }

    @GET
    @Path("/getPositive")
    public Response getPositive() {
        return Response.ok().build();
    }
}
