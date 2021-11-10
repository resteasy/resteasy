package org.jboss.resteasy.test.cdi.generic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

public interface ConcreteResourceIntf {
   @GET
   @Path("injection")
   Response testGenerics();

   @GET
   @Path("decorators/clear")
   Response clear();

   @GET
   @Path("decorators/execute")
   Response execute();

   @GET
   @Path("decorators/test")
   Response testDecorators();
}
