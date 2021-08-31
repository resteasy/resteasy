package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/test")
public class BadContenTypeTestResource {

   @GET
   public Response get() {
      BadContentTypeTestBean bean = new BadContentTypeTestBean();
      bean.setName("myname");
      return Response.ok(bean).build();
   }

   @GET
   @Produces("text/html")
   @Path("foo")
   public Response getMissingMBW() {
      BadContentTypeTestBean bean = new BadContentTypeTestBean();
      bean.setName("myname");
      return Response.ok(bean).build();
   }

   @POST
   public void post(BadContentTypeTestBean bean) {

   }

}
