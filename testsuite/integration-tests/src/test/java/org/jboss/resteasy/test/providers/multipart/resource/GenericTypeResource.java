package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.test.providers.multipart.GenericTypeMultipartTest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Path("")
public class GenericTypeResource {
   @POST
   @Path("test")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.TEXT_PLAIN)
   public Response testInputPartSetMediaType(MultipartInput input) throws IOException {
      List<InputPart> parts = input.getParts();
      InputPart part = parts.get(0);
      List<String> body = part.getBody(GenericTypeMultipartTest.stringListType);
      String reply = "";
      for (Iterator<String> it = body.iterator(); it.hasNext(); ) {
         reply += it.next() + " ";
      }
      return Response.ok(reply).build();
   }
}
