package org.jboss.resteasy.test.form.resource;

import java.io.UnsupportedEncodingException;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

@Path("test")
public class FormUrlEncodedCharsetResource {
    @POST
    public Response form(MultivaluedMap<String, String> form) throws UnsupportedEncodingException {
        String s = form.getFirst("name");
        return Response.ok().entity(s).build();
    }
}
