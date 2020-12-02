package org.jboss.resteasy.microprofile.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/{id}")
public interface RESTEASY_2335_Resource {
   @GET
   String get(@PathParam("id") String id);

   @POST
   String post(@PathParam("id") String id);
}
