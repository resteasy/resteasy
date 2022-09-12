package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayInputStream;

@Path("/mime")
public class HeaderFlushedOutputStreamService {

   @POST
   public Response createMyBean(@Context HttpHeaders headers, String str) {
      return Response.ok(str, headers.getMediaType()).build();
   }

   @GET
   @Produces(MediaType.MULTIPART_FORM_DATA)
   @MultipartForm
   public HeaderFlushedOutputStreamBean createMyBean() {
      HeaderFlushedOutputStreamBean myBean = new HeaderFlushedOutputStreamBean();
      myBean.setSomeBinary(new ByteArrayInputStream("bla".getBytes()));
      return myBean;
   }
}
