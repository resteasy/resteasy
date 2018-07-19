package org.jboss.resteasy.test.cdi.interceptors.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("")
public class NameBoundCDIProxiesResource {

   @Path("test")
   @GET
   public String test() {
      return "test";
   }
}
