package org.jboss.resteasy.test.providers.custom.resource;

import java.lang.annotation.Annotation;
import java.util.Date;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
