package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test/jsonBinding")
public class JsonBindingResource {

   @Path("cat")
   @POST
   @Produces("application/json")
   @Consumes("application/json")
   public Cat getCat(Cat cat) {
      cat.setName("Alfred");
      cat.setColor("ginger");
      return cat;
   }

}