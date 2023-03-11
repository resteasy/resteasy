package org.jboss.resteasy.test.providers.multipart.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("Api")
public interface ProxyApiService {

    @Path("test/{key}")
    @Consumes("multipart/form-data")
    @POST
    void postAttachment(@MultipartForm ProxyAttachment attachment, @PathParam("key") String key);
}
