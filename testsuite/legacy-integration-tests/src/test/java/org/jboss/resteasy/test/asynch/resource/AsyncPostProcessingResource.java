package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;

@Path("/")
public class AsyncPostProcessingResource {

    private static Logger logger = Logger.getLogger(AsyncPostProcessingResource.class);

    @GET
    @Path("sync")
    public Response sync() {
        return Response.ok().entity("sync").build();
    }

    @GET
    @Path("async/delay")
    public void asyncDelay(@Suspend(10000) final AsynchronousResponse response) throws Exception {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    Response jaxrs = Response.ok("async/delay").build();
                    response.setResponse(jaxrs);
                } catch (Exception e) {
                    logger.info("Error: " + e.getStackTrace());
                }
            }
        };
        t.start();
    }

    @GET
    @Path("async/nodelay")
    public void asyncNoDelay(@Suspend(10000) final AsynchronousResponse response) throws Exception {
        Response jaxrs = Response.ok("async/nodelay").build();
        response.setResponse(jaxrs);
    }

}
