package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/mime")
public class InputPartDefaultContentTypeEncodingOverwriteService {

   @POST
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.TEXT_PLAIN)
   public String sendDefaultContentType(MultipartInput input) {
      return input.getParts().get(0).getMediaType().toString();
   }
}
