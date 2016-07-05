package org.jboss.resteasy.test.client.resource;

import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

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
    public Response getPositive()  {
        return Response.ok().build();
    }
}
