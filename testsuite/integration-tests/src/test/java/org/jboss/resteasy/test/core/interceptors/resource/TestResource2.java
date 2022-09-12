package org.jboss.resteasy.test.core.interceptors.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

/**
 * Created by rsearls on 9/5/17.
 */
@Path("test")
public class TestResource2 {

   @Path("x")
   @Produces("text/plain")
   public TestSubResource locator() {
      return new TestSubResource();
   }
}
