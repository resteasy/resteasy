package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/")
public class SegmentNullResource {

   @GET
   @Produces("text/plain")
   public String doNothing() {
      throw new RuntimeException("Not Implemented");
   }

   @GET
   @Produces("text/plain")
   @Path("child")
   public String childDoNothing() {
      throw new RuntimeException("Not Implemented");
   }

   @GET
   @Produces("text/plain")
   @Path("child/{name:[A-Za-z]+}")
   public String childWithName(@PathParam("name") String name) {
      throw new RuntimeException("Not Implemented");
   }

   @GET
   @Produces("text/plain")
   @Path("child/{id:[0-9]+}")
   public String childWithId(@PathParam("id") String id) {
      throw new RuntimeException("Not Implemented");
   }

   @GET
   @Produces("text/plain")
   @Path("child1/{id:\\d+}")
   public String child1WithId(@PathParam("id") String id) {
      throw new RuntimeException("Not Implemented");
   }
}
