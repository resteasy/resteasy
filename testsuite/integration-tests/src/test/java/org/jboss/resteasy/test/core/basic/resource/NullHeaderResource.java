package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

@Path("test")
public class NullHeaderResource {

   @GET
   public Response get(@Context HttpHeaders headers) {
      String clientHeader = headers.getRequestHeader("X-Client-Header").get(0);
      if (clientHeader != null && !"".equals(clientHeader)) {
         return Response.serverError().build();
      }
      return Response.ok().header("X-Server-Header", null).build();
   }
}
