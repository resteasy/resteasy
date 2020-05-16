package org.jboss.resteasy.test.microprofile.config.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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