package org.jboss.resteasy.test.providers.html.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.html.View;

@Path("/test")
public class HeadersInViewResponseResource {
   
   @GET
   @Path("get")
   public Response get()
   {
      return Response.ok(new View("/test/view"))
             .header("abc", "123")
             .cookie(new NewCookie("name1", "value1"))
             .build();
   }

   @GET
   @Path("view")
   public Response view()
   {
      return Response.ok()
            .header("xyz", "789")
            .cookie(new NewCookie("name2", "value2"))
            .build();
   }
}
