package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
