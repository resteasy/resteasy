package org.jboss.resteasy.test.microprofile.config.resource;

import javax.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

@Path("/")
public class MicroProfileConfigUseGlobalResource {

   @Inject Config config;

   @GET
   @Produces("text/plain")
   @Path("prefix")
   public String prefix() {
      String p = ConfigProvider.getConfig().getOptionalValue("resteasy.servlet.mapping.prefix", String.class).orElse("d'oh");
      return p;
   }
}