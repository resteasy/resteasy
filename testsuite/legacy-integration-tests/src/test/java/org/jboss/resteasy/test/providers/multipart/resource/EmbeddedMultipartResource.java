package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("")
public class EmbeddedMultipartResource {
    @Path("embedded")
    @POST
    @Consumes("multipart/mixed")
    @Produces(MediaType.TEXT_PLAIN)
    public Response post(MultipartInput input) throws Exception {
        InputPart inputPart = input.getParts().iterator().next();
        MultipartInput multipart = inputPart.getBody(MultipartInput.class, null);
        inputPart = multipart.getParts().iterator().next();
        EmbeddedMultipartCustomer customer = inputPart.getBody(EmbeddedMultipartCustomer.class, null);
        return Response.ok(customer.getName()).build();
    }

    @Path("customer")
    @POST
    @Consumes("multipart/mixed")
    @Produces(MediaType.TEXT_PLAIN)
    public Response postCustomer(MultipartInput input) throws IOException {
        InputPart part = input.getParts().iterator().next();
        EmbeddedMultipartCustomer customer = part.getBody(EmbeddedMultipartCustomer.class, null);
        return Response.ok(customer.getName()).build();
    }

    @Path("invalid")
    @POST
    @Consumes("multipart/mixed")
    @Produces(MediaType.TEXT_PLAIN)
    public Response postInvalid(MultipartInput input) throws IOException {
        InputPart part = input.getParts().iterator().next();
        Object o = part.getBody(EmbeddedMultipartResource.class, null);
        return Response.ok(o).build();
    }
}
