package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Check that the timeout handler is not called after resume executes.
 */
@Path("/")
public class TimeoutRunsResource {
    private static boolean timeout = false;
    private static CountDownLatch latch = new CountDownLatch(1);

    @GET
    @Path("async")
    public void get(@Suspended AsyncResponse response) {
        response.setTimeoutHandler(reponse -> {
            timeout = true;
            latch.countDown();
        });
        response.setTimeout(1, TimeUnit.SECONDS);
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
