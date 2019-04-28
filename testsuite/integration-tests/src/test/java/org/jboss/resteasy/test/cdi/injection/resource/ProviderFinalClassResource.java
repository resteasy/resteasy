package org.jboss.resteasy.test.cdi.injection.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/new")
public class ProviderFinalClassResource {
   @GET
   @Produces("text/plain")
   @Path("a")
   public ProviderFinalClassStringHandler a() throws Exception {
      ProviderFinalClassStringHandler a = new ProviderFinalClassStringHandler();
      a.setA("example");
      return a;
   }
}
