package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/mime")
public interface MyServiceProxy {

   @GET
   @Produces(MediaType.MULTIPART_FORM_DATA)
   @MultipartForm
   NullPartBean createMyBean();
}
