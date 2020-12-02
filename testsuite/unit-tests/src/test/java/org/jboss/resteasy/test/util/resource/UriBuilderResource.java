package org.jboss.resteasy.test.util.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("resource")
public class UriBuilderResource {
   @Path("method")
   @GET
   public String get() {
      return "";
   }

   @Path("locator")
   public Object locator() {
      return null;
   }
}
