package org.jboss.resteasy.microprofile.client;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/{id}")
public interface RESTEASY_2335_Resource {
   @GET
   String get(@PathParam("id") String id);

   @POST
   String post(@PathParam("id") String id);
}
