package org.jboss.resteasy.test.providers.custom.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
public class ReaderWriterResource {
    private static Logger logger = Logger.getLogger(ReaderWriterResource.class);
    @Path("/simple")
    @GET
    public Response get() {
        Response.ResponseBuilder builder = Response.ok("hello world".getBytes());
        builder.header("CoNtEnT-type", "text/plain");
        return builder.build();
    }

    @Path("/string")
    @GET
    public Response getString() {
        Response.ResponseBuilder builder = Response.ok("hello world");
        builder.header("CoNtEnT-type", "text/plain");
        logger.info("getString");
        return builder.build();
    }

    @Path("/complex")
    @GET
    public Object getComplex() {
        Response.ResponseBuilder builder = Response.status(HttpResponseCodes.SC_FOUND)
                .entity("hello world".getBytes());
        builder.header("CoNtEnT-type", "text/plain");
        return builder.build();
    }

    @Path("/implicit")
    @GET
    @Produces("application/xml")
    public Object getCustomer() {
        logger.info("GET CUSTOEMR");
        ReaderWriterCustomer cust = new ReaderWriterCustomer();
        cust.setName("bill");
        return Response.ok(cust).build();
    }

    @Path("/implicit")
    @DELETE
    public Object deleteCustomer() {
        return Response.ok().build();
    }

    @Path("/complex")
    @DELETE
    public void deleteComplex() {

    }

}
