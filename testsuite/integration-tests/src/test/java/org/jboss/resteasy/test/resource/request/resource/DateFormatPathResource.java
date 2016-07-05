package org.jboss.resteasy.test.resource.request.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/")
public class DateFormatPathResource {
    @Path("/widget/{date}")
    @GET
    @Produces("text/plain")
    public String get(@PathParam("date") String date) {
        return date;
    }
}
