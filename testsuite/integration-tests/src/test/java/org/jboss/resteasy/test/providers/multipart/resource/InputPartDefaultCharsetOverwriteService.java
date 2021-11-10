package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("")
public class InputPartDefaultCharsetOverwriteService {
   private static Logger logger = Logger.getLogger(InputPartDefaultCharsetOverwriteService.class);

   @POST
   @Path("test")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.TEXT_PLAIN)
   public Response testDefaultContentType(MultipartInput input) throws IOException {
      List<InputPart> parts = input.getParts();
      InputPart part = parts.get(0);
      String s1 = part.getBody(String.class, null);
      String s2 = part.getBodyAsString();
      String result = part.getMediaType() + ":" + s1 + ":" + s2;
      logger.info("server response: " + result);
      return Response.ok(result, part.getMediaType()).build();
   }

   @POST
   @Path("query")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.TEXT_PLAIN)
   public Response testInputPartSetMediaType(@QueryParam("contentType") String mediaType, MultipartInput input) throws IOException {
      List<InputPart> parts = input.getParts();
      InputPart part = parts.get(0);
      part.setMediaType(MediaType.valueOf(mediaType));
      String s1 = part.getBody(String.class, null);
      String s2 = part.getBodyAsString();
      String result = part.getMediaType() + ":" + s1 + ":" + s2;
      logger.info("server response: " + result);
      return Response.ok(result, part.getMediaType()).build();
   }

   @POST
   @Path("bytes")
   @Consumes(MediaType.WILDCARD)
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   public Response testByteArray(MultipartInput input) throws IOException {
      List<InputPart> parts = input.getParts();
      InputPart part = parts.get(0);
      byte[] b = part.getBody(byte[].class, null);
      return Response.ok(b).build();
   }
}
