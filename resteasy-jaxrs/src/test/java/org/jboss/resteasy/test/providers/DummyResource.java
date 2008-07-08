package org.jboss.resteasy.test.providers;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;

@Path("/test")
public class DummyResource
{

   @POST
   @ConsumeMime("application/octet-stream")
   public void testConsume(DummyObject foo)
   {
   }


   @GET
   @ProduceMime("application/octet-stream")
   public DummyObject testProduce()
   {
      return new DummyObject();
   }
   
}
