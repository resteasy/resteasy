package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import java.util.Date;

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
