package org.jboss.resteasy.test.providers.jackson2.resource;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public class CustomJackson2ProviderResource {

   @GET
   @Produces("text/plain")
   @Path("/jackson2providerpath")
   public String getProviderPath()
   {
      return ResteasyJackson2Provider.class.getProtectionDomain().getCodeSource().getLocation().getPath();
   }
}
