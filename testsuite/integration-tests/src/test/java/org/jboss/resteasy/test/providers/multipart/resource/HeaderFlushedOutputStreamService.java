package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
