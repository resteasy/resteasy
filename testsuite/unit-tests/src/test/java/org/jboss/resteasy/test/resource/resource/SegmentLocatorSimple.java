package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.GET;

public class SegmentLocatorSimple {
   @GET
   public String ok() {
      return "ok";
   }
}
