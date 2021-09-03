package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/mime")
public class InputPartDefaultContentTypeWildcardOverwriteService {
   @POST
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces(MediaType.TEXT_PLAIN)
   public int echoMultipartForm(@MultipartForm InputPartDefaultContentTypeWildcardOverwriteContainerBean containerBean) {
      return containerBean.getFoo().getMyInt();
   }
}
