package org.jboss.resteasy.test.core.encoding.resource;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.UriInfo;

import org.junit.Assert;

@Resource(name = "test")
@Path(value = "/sayhello")
public class SpecialCharactersResource {
    private static final String QUERY = "select p from VirtualMachineEntity p where guest.guestId = :id";

    @Inject
    UriInfo info;

    @GET
    @Path("/en/{in}")
    @Produces("text/plain")
    public String echo(@PathParam(value = "in") String in) {
        Assert.assertEquals("something something", in);
        return in;
    }

    @POST
    @Path("/compile")
    public String compile(@QueryParam("query") String queryText) {
        Assert.assertEquals(queryText, QUERY);
        return queryText;
    }

    @Path("/widget/{date}")
    @GET
    @Produces("text/plain")
    public String get(@PathParam("date") String date) {
        return date;
    }

    @Path("/plus/{plus}")
    @GET
    @Produces("text/plain")
    public String getPlus(@PathParam("plus") String p) {
        Assert.assertEquals("foo+bar", p);
        return p;
    }
}
