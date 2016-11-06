package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/mime")
public interface MyServiceProxy {

    @GET
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @MultipartForm
    NullPartBean createMyBean();
}
