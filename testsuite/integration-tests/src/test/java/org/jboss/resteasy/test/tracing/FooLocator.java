package org.jboss.resteasy.test.tracing;

import jakarta.ws.rs.Path;

@Path("/locator")
public class FooLocator {
   @Path("foo")
   public Foo getFoo() {
      return new Foo();
   }
}
