package org.jboss.resteasy.test.providers.jackson2.multipart;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("/")
public interface JsonForm {

    @PUT
    @Path("form/class")
    @Consumes("multipart/form-data")
    String putMultipartForm(@MultipartForm JsonFormResource.Form form);
}
