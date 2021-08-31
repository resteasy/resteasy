package org.jboss.resteasy.test.providers.jackson2.multipart;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

@Path("/")
public interface JsonForm {

   @PUT
   @Path("form/class")
   @Consumes("multipart/form-data")
   String putMultipartForm(@MultipartForm JsonFormResource.Form form);
}
