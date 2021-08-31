package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public class SetMethodWithMoreArgumentsResource {

   @Path("/dog")
   @POST
   @Produces("application/json")
   @Consumes("application/json")
   public Dog getDog(Dog dog) throws Exception {
      dog.setNameAndSort("Jethro", "stafford");
      return dog;
   }

}
