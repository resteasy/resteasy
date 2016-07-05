package org.jboss.resteasy.test.form.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;

@Path("test")
public class FormUrlEncodedCharsetResource {
    @POST
    public Response form(MultivaluedMap<String, String> form) throws UnsupportedEncodingException {
        String s = form.getFirst("name");
        return Response.ok().entity(s).build();
    }
}
