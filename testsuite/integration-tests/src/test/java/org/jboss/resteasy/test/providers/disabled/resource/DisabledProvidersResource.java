package org.jboss.resteasy.test.providers.disabled.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("")
public class DisabledProvidersResource {

   @Path("foo")
   @Produces("application/foo")
   @GET
   public Foo getFoo() {
      return new Foo("bar");
   }

   @Path("string")
   @GET
   public String getString() {
      return "bar";
   }
}
