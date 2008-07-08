package org.jboss.resteasy.test.providers;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;

@Path("/test")
public class TestResource
{

   @POST
   @ConsumeMime("application/octet-stream")
   public void testConsume(TestDummy foo)
   {
   }


   @GET
   @ProduceMime("application/octet-stream")
   public TestDummy testProduce()
   {
      return new TestDummy();
   }
   
}
