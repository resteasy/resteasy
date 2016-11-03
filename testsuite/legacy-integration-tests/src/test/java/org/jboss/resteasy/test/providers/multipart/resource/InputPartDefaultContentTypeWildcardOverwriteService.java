package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/mime")
public class InputPartDefaultContentTypeWildcardOverwriteService {
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public int echoMultipartForm(@MultipartForm InputPartDefaultContentTypeWildcardOverwriteContainerBean containerBean) {
        return containerBean.getFoo().getMyInt();
    }
}
