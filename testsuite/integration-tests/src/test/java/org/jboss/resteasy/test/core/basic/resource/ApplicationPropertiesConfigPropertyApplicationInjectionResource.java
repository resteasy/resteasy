package org.jboss.resteasy.test.core.basic.resource;

import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("/")
public class ApplicationPropertiesConfigPropertyApplicationInjectionResource {

   @Context
   private Application application;

   @GET
   @Path("/getconfigproperty")
   public Response getProperty(@QueryParam("prop") String prop) {
      String response = "false";
      boolean containskey = application.getProperties().containsKey(prop);
      if (containskey) {
         response = "true";
      }
      Assert.assertEquals("The injected application doesn't contain property \"Prop1\"", "true", response);
      String value = (String) application.getProperties().get("Prop1");
      return Response.ok(value).build();
   }
}
