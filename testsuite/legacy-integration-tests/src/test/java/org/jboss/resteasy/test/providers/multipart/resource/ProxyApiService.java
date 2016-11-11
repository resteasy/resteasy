package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("Api")
public interface ProxyApiService {

    @Path("test/{key}")
    @Consumes("multipart/form-data")
    @POST
    void postAttachment(@MultipartForm ProxyAttachment attachment, @PathParam("key") String key);
}
