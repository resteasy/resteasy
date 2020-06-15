package org.jboss.resteasy.test.asynch.resource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;


@Path("/")
public class AsyncTimeoutResource {
    private static boolean timeout = false;
    private static CountDownLatch latch = new CountDownLatch(1);

    @GET
    @Path("async")
    public void get(@Suspended AsyncResponse response) {
        response.setTimeoutHandler(reponse -> {
            timeout = true;
            latch.countDown();
        });
        response.setTimeout(2, TimeUnit.SECONDS);
        response.resume("Async hello");
    }

    @GET
    @Path("timeout")
    public String getTimeout() {
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //ignore
        }
        return Boolean.toString(timeout);
    }
}
