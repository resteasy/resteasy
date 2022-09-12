package org.jboss.resteasy.test.client.exception.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/")
public class ClientErrorBadMediaTypeResource {
   @Consumes("application/bar")
   @Produces("application/foo")
   @POST
   public String doPost(String entity) {
      return "content";
   }

   @Produces("text/plain")
   @GET
   @Path("complex/match")
   public String get() {
      return "content";
   }

   @Produces("text/xml")
   @GET
   @Path("complex/{uriparam: [^/]+}")
   public String getXml(@PathParam("uriparam") String param) {
      return "<" + param + "/>";
   }

   @DELETE
   public void delete() {
   }

   @Path("/nocontent")
   @POST
   public void noreturn(String entity) {
   }
}
