package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Stateless
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

