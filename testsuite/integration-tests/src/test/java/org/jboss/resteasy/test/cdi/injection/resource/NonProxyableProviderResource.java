package org.jboss.resteasy.test.cdi.injection.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/new")
public class NonProxyableProviderResource {
   @GET
   @Produces("text/plain")
   @Path("a")
   public ProviderFinalClassStringHandler a() throws Exception {
      ProviderFinalClassStringHandler a = new ProviderFinalClassStringHandler();
      a.setA("example");
      return a;
   }

   @GET
   @Produces("text/plain")
   @Path("b")
   public ProviderFinalInheritedMethodStringHandler b() {
      ProviderFinalInheritedMethodStringHandler b = new ProviderFinalInheritedMethodStringHandler();
      b.setB("example");
      return b;
   }

   @GET
   @Produces("text/plain")
   @Path("c")
   public ProviderOneArgConstructorStringHandler c() {
      ProviderOneArgConstructorStringHandler c = new ProviderOneArgConstructorStringHandler();
      c.setC("example");
      return c;
   }
}
