package org.jboss.resteasy.test.providers.jaxb.inheritance;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/zoo")
public class ZooWS
{

   @GET
   @Produces("application/xml")
   public Zoo getZoo()
   {
      Zoo aZoo = new Zoo();
      aZoo.add(new Dog("Foo"));
      aZoo.add(new Cat("Bar"));
      return aZoo;
   }
}
