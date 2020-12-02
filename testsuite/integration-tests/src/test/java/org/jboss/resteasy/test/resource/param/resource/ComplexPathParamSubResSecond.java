package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;

public class ComplexPathParamSubResSecond {
   @GET
   public String get() {
      return "sub2";
   }
}
