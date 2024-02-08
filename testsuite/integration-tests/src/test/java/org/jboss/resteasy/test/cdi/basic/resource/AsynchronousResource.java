package org.jboss.resteasy.test.cdi.basic.resource;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Response;

@Stateless
@Dependent
@Path("/")
public class AsynchronousResource {
    @Inject
    private Logger log;

    @Inject
    private AsynchronousStatelessLocal stateless;

    public static final Long DELAY = 5000L;

    @GET
    @Path("asynch/simple")
    public Response asynchSimple() throws ExecutionException, InterruptedException {
        log.info("entering asynch()");
        Future<Boolean> asyncResult = stateless.asynch();
        return asyncResult.get() ? Response.ok().build() : Response.serverError().build();
    }

    @Asynchronous
    @GET
    @Path("asynch/ejb")
    public void asynchEJB(@Suspended final AsyncResponse asyncResponse) throws ExecutionException, InterruptedException {
        log.info("entering asynchEJB()");
        Thread.sleep(DELAY);
        asyncResponse.resume(Response.ok().build());
    }
}
