package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.test.providers.multipart.GenericTypeMultipartTest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Path("")
public class GenericTypeResource {
    @POST
    @Path("test")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response testInputPartSetMediaType(MultipartInput input) throws IOException {
        List<InputPart> parts = input.getParts();
        InputPart part = parts.get(0);
        List<String> body = part.getBody(GenericTypeMultipartTest.stringListType);
        String reply = "";
        for (Iterator<String> it = body.iterator(); it.hasNext(); ) {
            reply += it.next() + " ";
        }
        return Response.ok(reply).build();
    }
}
