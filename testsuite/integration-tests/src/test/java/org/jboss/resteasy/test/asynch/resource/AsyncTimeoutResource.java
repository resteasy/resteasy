package org.jboss.resteasy.test.asynch.resource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;


@Path("/")
public class AsyncTimeoutResource {
    private static boolean timeout = false;
    private static CountDownLatch latch = new CountDownLatch(1);
    private static AtomicBoolean timeoutExtended = new AtomicBoolean(false);

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

    @GET
    @Path("extendedTimeout")
    public void extendedTimeout(@Suspended AsyncResponse response) {
        response.setTimeoutHandler(ar -> {
            if (timeoutExtended.getAndSet(true)) {
               ar.resume("Extended timeout hello");
            } else {
               ar.setTimeout(2, TimeUnit.SECONDS); // wait 2 more seconds
            }
        });
        response.setTimeout(2, TimeUnit.SECONDS);
    }

    @GET
    @Path("resumeAfterSettingTimeoutHandler")
    public void resumeAfterSettingTimeoutHandler(@Suspended AsyncResponse response) {
        response.setTimeoutHandler(ar -> {
           ar.resume("From TimeoutHandler");
        });
        response.setTimeout(2, TimeUnit.SECONDS);
        response.resume("From initial");
    }
}
