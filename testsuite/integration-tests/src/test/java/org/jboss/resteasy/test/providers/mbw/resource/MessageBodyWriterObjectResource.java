package org.jboss.resteasy.test.providers.mbw.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("test")
public class MessageBodyWriterObjectResource {
   
   @GET
   @Path("")
   public Response test() {
      return Response.ok(new MessageBodyWriterObjectMessage("ok")).build();
   }
   
   @GET
   @Path("used")
   @Produces("text/plain")
   public Response used() {
      return Response.ok(Boolean.toString(MessageBodyWriterObjectMessageBodyWriter.used)).build();
   }
   @GET
   @Path("/getbool")
   public boolean testBoolean() {
      return true;
   }
}
