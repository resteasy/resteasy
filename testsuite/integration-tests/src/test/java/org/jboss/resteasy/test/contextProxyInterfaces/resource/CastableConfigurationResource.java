package org.jboss.resteasy.test.contextProxyInterfaces.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.spi.HeaderValueProcessor;

@Path("/config")
public class CastableConfigurationResource {
   @Context
   Configuration config;

   @GET
   public Response getConfigurationClassName() {
      Response.ResponseBuilder builder = Response.ok(config.toString());
      if (config instanceof HeaderValueProcessor) {
         builder.header("Instanceof-HeaderValueProcessor", "true");
      } else {
         builder.header("Instanceof-HeaderValueProcessor", "false");
      }

      return builder.build();
   }
}
