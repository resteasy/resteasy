package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Path("/products")
public class AsynchContextualDataResource {
   private AsyncResponse resumable;

   @GET
   @Produces("application/json")
   @Path("wait/{id}")
   public void getProduct(@Suspended final AsyncResponse ar, @PathParam("id") final int id) {
      resumable = ar;
   }

   @GET
   @Produces("application/json")
   @Path("res/{id}")
   public AsynchContextualDataProduct getProduct(@PathParam("id") final int id) {
      if(resumable != null) {
         resumable.resume(new AsynchContextualDataProduct(id, "Iphone"));
      }
      return new AsynchContextualDataProduct(id, "Nexus 7");
   }
}
