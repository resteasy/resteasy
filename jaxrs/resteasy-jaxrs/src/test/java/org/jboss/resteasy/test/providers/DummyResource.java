package org.jboss.resteasy.test.providers;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public class DummyResource
{

   @POST
   @Consumes("application/octet-stream")
   public void testConsume(DummyObject foo)
   {
   }


   @GET
   @Produces("application/octet-stream")
   public DummyObject testProduce()
   {
      return new DummyObject();
   }

}
