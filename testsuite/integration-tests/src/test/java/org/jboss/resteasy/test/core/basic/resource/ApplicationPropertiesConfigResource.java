package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;

@Path("/")
public class ApplicationPropertiesConfigResource {

   @Context
   private Configuration configuration;

   @GET
   @Path("/getconfigproperty")
   public String getProperty(@QueryParam("prop") String prop) {
      String value = (String) configuration.getProperty(prop);
      return value;
   }
}
