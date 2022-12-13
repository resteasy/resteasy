package org.jboss.resteasy.test.providers.custom.resource;

import java.lang.annotation.Annotation;
import java.util.Date;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/")
public class ResponseGetAnnotationsResource {
    @POST
    @Path("entity")
    public Response entity(Date date) {
        Annotation[] annotations = ResponseGetAnnotationsAnnotatedClass.class.getAnnotations();
        Response response = Response.ok().entity(date, annotations).build();
        return response;
    }

}
