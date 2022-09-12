package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;

public class ComplexPathParamSubRes {
   @GET
   public String get() {
      return "sub1";
   }
}
