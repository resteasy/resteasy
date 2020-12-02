package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("nomedia")
public class ResourceMatchingNoMediaResource {

   @GET
   @Path("list")
   public List<String> serializable() {
      return java.util.Collections.singletonList("AA");
   }

   @GET
   @Path("responseoverride")
   public Response overrideNoProduces() {
      return Response.ok("<a>responseoverride</a>")
            .type(MediaType.APPLICATION_XML_TYPE).build();
   }

   @GET
   @Path("nothing")
   public ResourceMatchingStringBean nothing() {
      return new ResourceMatchingStringBean("nothing");
   }

   @GET
   @Path("response")
   public Response response() {
      return Response.ok(nothing()).build();
   }

}
