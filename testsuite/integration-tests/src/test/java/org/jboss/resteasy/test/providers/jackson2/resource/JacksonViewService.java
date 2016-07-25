package org.jboss.resteasy.test.providers.jackson2.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/json_view")
public class JacksonViewService {
   
   public static Something SOMETHING = new Something("annotated value", "annotated value 2", "not annotated value");

   @GET
   @Produces("application/json")
   @Path("/something")
   public Something getSomething()
   {
      return SOMETHING;
   }

   @GET
   @Produces("application/json")
   @JsonView(TestJsonView.class)
   @Path("/something_w_view")
   public Something getSomethingWithView()
   {
      return SOMETHING;

   }

   @GET
   @Produces("application/json")
   @JsonView(TestJsonView2.class)
   @Path("/something_w_view2")
   public Something getSomethingWithView2()
   {
      return SOMETHING;

   }

}
