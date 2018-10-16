package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;

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
