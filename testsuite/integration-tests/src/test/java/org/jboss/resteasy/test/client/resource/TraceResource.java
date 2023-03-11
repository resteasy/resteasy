package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;
import org.jboss.resteasy.test.client.TraceTest;

@Path("resource")
public class TraceResource {

    private static Logger logger = Logger.getLogger(TraceResource.class);

    @Context
    UriInfo uriInfo;

    @TraceTest.TRACE
    @Path("trace")
    public String trace() {
        logger.info("uriInfo.request: " + uriInfo.getRequestUri().toString());
        return "trace";
    }

    @TraceTest.TRACE
    @Path("tracenotok")
    public Response traceNotOk() {
        return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

}
