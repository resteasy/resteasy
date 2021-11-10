package org.jboss.resteasy.test.providers.inputstream.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;

@Path("/")
public class InputStreamCloseResource {
   private static InputStreamCloseInputStream inputStream;

   @GET
   @Produces("text/plain")
   @Path("create")
   public InputStream create() {
      inputStream = new InputStreamCloseInputStream("hello".getBytes());
      return inputStream;
   }

   @GET
   @Path("test")
   public Response test() {
      return (inputStream.isClosed() ? Response.ok().build() : Response.serverError().build());
   }
}
