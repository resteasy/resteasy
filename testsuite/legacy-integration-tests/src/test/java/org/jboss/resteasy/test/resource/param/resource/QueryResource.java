package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Query;

@Path("search")
public class QueryResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String get(@Query QuerySearchQuery searchQuery) {
        return searchQuery.toString();
    }
}
