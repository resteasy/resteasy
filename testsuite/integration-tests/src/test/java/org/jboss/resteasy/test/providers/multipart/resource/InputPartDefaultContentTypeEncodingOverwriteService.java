package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/mime")
public class InputPartDefaultContentTypeEncodingOverwriteService {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public String sendDefaultContentType(MultipartInput input) {
        return input.getParts().get(0).getMediaType().toString();
    }
}
