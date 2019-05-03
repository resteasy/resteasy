package org.jboss.resteasy.test.providers.noproduces.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("")
public class ProviderWithNoProducesResource {

   @GET
   @Path("foo")
   public Foo getFoo() throws Exception {
       return new Foo("foo");
   }
}
