package org.jboss.resteasy.test.providers.multipart.resource;

import java.io.ByteArrayInputStream;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("/rest/zba")
public class EmptyInputStreamMultipartProviderResource {
    @GET
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @MultipartForm
    public EmptyInputStreamMultipartProviderMyBean get() {
        EmptyInputStreamMultipartProviderMyBean myBean = new EmptyInputStreamMultipartProviderMyBean();
        myBean.setSomeBinary(new ByteArrayInputStream(new byte[0]));
        return myBean;
    }
}
