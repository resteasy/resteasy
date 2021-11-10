package org.jboss.resteasy.test.cdi.interceptors.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("")
public class NameBoundCDIProxiesResource {

   @Path("test")
   @GET
   public String test() {
      return "test";
   }
}
