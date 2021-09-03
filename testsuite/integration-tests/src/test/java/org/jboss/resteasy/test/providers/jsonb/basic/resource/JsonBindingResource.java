package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/test/jsonBinding")
public class JsonBindingResource {

   public static final Integer RETURNED_TRANSIENT_VALUE = 12345;

   public static final Integer CLIENT_TRANSIENT_VALUE = 54321;

   @Path("cat/transient")
   @POST
   @Produces("application/json")
   @Consumes("application/json")
   public Cat getCatTransient(Cat cat) throws Exception {
      // check received message for transient variable
      if (cat.getTransientVar() != Cat.DEFAULT_TRANSIENT_VAR_VALUE) {
         throw new Exception("JsonbTransient annotation doesn't work");
      }
      // update response
      cat.setName("Alfred");
      cat.setColor("ginger");
      cat.setTransientVar(RETURNED_TRANSIENT_VALUE);
      return cat;
   }


   @Path("cat/not/transient")
   @POST
   @Produces("application/json")
   @Consumes("application/json")
   public Cat getCatNotTransient(Cat cat) throws Exception {
      // check received message for transient variable
      if (cat.getTransientVar() == Cat.DEFAULT_TRANSIENT_VAR_VALUE) {
         throw new Exception("JsonbTransient annotation works, but it shouldn't work");
      }
      // update response
      cat.setName("Alfred");
      cat.setColor("ginger");
      cat.setTransientVar(RETURNED_TRANSIENT_VALUE);
      return cat;
   }

   @Path("client/test/transient")
   @POST
   @Produces("application/json")
   @Consumes("application/json")
   public String repeaterTransient(String data) throws Exception {
      if (data.contains(CLIENT_TRANSIENT_VALUE.toString())) {
         throw new Exception("JsonbTransient annotation doesn't work");
      }
      return "{\"color\":\"tabby\",\"sort\":\"semi-british\",\"name\":\"Rosa\",\"domesticated\":true,\"transientVar\":\""
         + RETURNED_TRANSIENT_VALUE + "\"}";
   }

   @Path("get/cat")
   @GET
   @Produces("application/json")
   public Cat getCat() {
      return new Cat("a", "b", "c", true, 0);
   }


   @Path("repeater")
   @POST
   @Produces("application/json")
   @Consumes("application/json")
   public Cat repeater(Cat cat) {
      return cat;
   }
}
