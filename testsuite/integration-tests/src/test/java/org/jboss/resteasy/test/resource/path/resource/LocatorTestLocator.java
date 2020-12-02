package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.Path;

@Path("locator")
public class LocatorTestLocator {
   @Path("responseok")
   public LocatorResource responseOk() {
      return new LocatorResource();
   }
}
