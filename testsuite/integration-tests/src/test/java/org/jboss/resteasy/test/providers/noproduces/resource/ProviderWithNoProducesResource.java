package org.jboss.resteasy.test.providers.noproduces.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("")
public class ProviderWithNoProducesResource {

   @GET
   @Path("foo")
   public Foo getFoo() throws Exception {
       return new Foo("foo");
   }
}
