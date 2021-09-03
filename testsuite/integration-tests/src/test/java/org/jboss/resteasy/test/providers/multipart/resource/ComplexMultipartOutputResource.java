package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.Iterator;

@Path("mpart")
public class ComplexMultipartOutputResource {

    @GET
    @Path("test")
    @Produces("multipart/mixed")
    public Response test() {
        final MultipartOutput multipartOutput = new MultipartOutput();

        MultipartRelatedOutput outputBill = new MultipartRelatedOutput();
        outputBill.setStartInfo("text/html");
        outputBill.addPart("Bill", new MediaType("image", "png"),
                "bill", "binary");
        multipartOutput.addPart(outputBill, MediaType.valueOf("multipart/related"));

        MultipartRelatedOutput outputBob = new MultipartRelatedOutput();
        outputBob.setStartInfo("text/html");
        outputBob.addPart("Bob", new MediaType("image", "png"),
                "bob", "binary");
        multipartOutput.addPart(outputBob, MediaType.valueOf("multipart/related"));

        return Response.ok(multipartOutput, MediaType.valueOf("multipart/mixed")).build();
    }

    @POST
    @Consumes("multipart/related")
    @Produces("multipart/related")
    @Path("post/related")
    public MultipartRelatedOutput postRelated(MultipartRelatedInput customers)
            throws IOException {

        MultipartRelatedOutput rtnMRelatedOutput = new MultipartRelatedOutput();
        rtnMRelatedOutput.setStartInfo("text/html");

        for (Iterator<InputPart> it = customers.getParts().iterator(); it.hasNext(); ) {
            InputPart part = it.next();
            String name = part.getBody(String.class, null);
            rtnMRelatedOutput.addPart("Hello " + name,
                    new MediaType("image", "png"), name, null);
        }
        return rtnMRelatedOutput;
    }

}
