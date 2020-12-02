package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.PathSegment;

@Path(value = "/PathParamTest")
public class LocatorWithClassHierarchyPathParamResource {

   @Produces(MediaType.TEXT_HTML)
   @GET
   @Path("/{id}/{id1}")
   public String two(@PathParam("id") String id,
                      @PathParam("id1") PathSegment id1) {
      return "double=" + id + id1.getPath();
   }

   @Produces(MediaType.TEXT_PLAIN)
   @GET
   @Path("/ParamEntityWithConstructor/{id}")
   public String paramEntityWithConstructorTest(
         @DefaultValue("PathParamTest") @PathParam("id") LocatorWithClassHierarchyParamEntityWithConstructor paramEntityWithConstructor) {
      return paramEntityWithConstructor.getValue();
   }
}
