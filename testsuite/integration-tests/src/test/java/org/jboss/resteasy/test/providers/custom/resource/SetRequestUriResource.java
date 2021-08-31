package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

@Path("base/resource")
public class SetRequestUriResource {

   @Context
   protected UriInfo uriInfo;

   @GET
   @Path("setrequesturi1/uri")
   public String setRequestUri() {
      return "OK";
   }

   @GET
   @Path("setrequesturi1")
   public String setRequestUriDidNotChangeUri() {
      return "Filter did not change the uri to go to";
   }

   @GET
   @Path("change")
   public String changeProtocol() {
      return uriInfo.getAbsolutePath().toString();
   }
}
