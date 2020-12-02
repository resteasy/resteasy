package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/test")
public class SingletonCustomProviderResource {

   @POST
   @Consumes("application/octet-stream")
   public void testConsume(SingletonCustomProviderObject foo) {
   }


   @GET
   @Produces("application/octet-stream")
   public SingletonCustomProviderObject testProduce() {
      return new SingletonCustomProviderObject();
   }

}
