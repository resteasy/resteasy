package org.jboss.resteasy.test.microprofile.config.resource;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;

@Path("/")
public class MicroProfileConfigResource {

   static {
     System.setProperty("system", "system-system");
   }

   @Inject Config config;

   @GET
   @Produces("text/plain")
   @Path("system/prog")
   public String systemProg() {
      return ConfigProvider.getConfig().getOptionalValue("system", String.class).orElse("d'oh");
   }

   @GET
   @Produces("text/plain")
   @Path("system/inject")
   public String systemInject() {
      return config.getOptionalValue("system", String.class).orElse("d'oh");
   }

   @GET
   @Produces("text/plain")
   @Path("init/prog")
   public String initProg() {
      return ConfigProvider.getConfig().getOptionalValue("init", String.class).orElse("d'oh");
   }

   @GET
   @Produces("text/plain")
   @Path("init/inject")
   public String initInject() {
      return config.getOptionalValue("init", String.class).orElse("d'oh");
   }

   @GET
   @Produces("text/plain")
   @Path("filter/prog")
   public String filterProg() {
      return ConfigProvider.getConfig().getOptionalValue("filter", String.class).orElse("d'oh");
   }

   @GET
   @Produces("text/plain")
   @Path("filter/inject")
   public String filterInject() {
      return config.getOptionalValue("filter", String.class).orElse("d'oh");
   }

   @GET
   @Produces("text/plain")
   @Path("context/prog")
   public String contextProg() {
      return ConfigProvider.getConfig().getOptionalValue("context", String.class).orElse("d'oh");
   }

   @GET
   @Produces("text/plain")
   @Path("context/inject")
   public String contextInject() {
      return config.getOptionalValue("context", String.class).orElse("d'oh");
   }

   @GET
   @Produces("text/plain")
   @Path("actual")
   public String testActualContextParameter() {
      return "actual";
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("configSources/ordinal")
   public Map<String, Integer> getConfigSources(@QueryParam("inject") boolean inject)
   {
      Iterable<ConfigSource> configSources;
      if (inject)
      {
         configSources = config.getConfigSources();
      }
      else
      {
         configSources = ConfigProvider.getConfig().getConfigSources();
      }
      Map<String, Integer> ordinalByConfigSource = new HashMap<>();
      for (ConfigSource configSource : configSources)
      {

         ordinalByConfigSource.put(configSource.getClass().getCanonicalName(), configSource.getOrdinal());
      }
      return ordinalByConfigSource;
   }

}
