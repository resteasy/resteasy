package org.jboss.resteasy.test.microprofile.config.resource;

import java.lang.reflect.Field;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.config.Config;
import org.jboss.resteasy.microprofile.config.ResteasyConfig;
import org.jboss.resteasy.microprofile.config.ResteasyConfigFactory;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.microprofile.config.ResteasyConfig.SOURCE;

@Path("")
public class ResteasyConfigResource {

   private static Config savedConfig;

   static
   {
      System.setProperty("system", "system-system");
   }

   @GET
   @Path("delete")
   public void deleteResteasyConfig() throws Exception
   {
      Class<?> clazz = ResteasyConfig.class;
      Field field = clazz.getDeclaredField("config");
      field.setAccessible(true);
      ResteasyConfig resteasyConfig = ResteasyConfigFactory.getConfig();
      savedConfig = (Config) field.get(resteasyConfig);
      field.set(resteasyConfig, null);
   }

   @GET
   @Path("restore")
   public void restoreResteasyConfig() throws Exception
   {
      Class<?> clazz = ResteasyConfig.class;
      Field field = clazz.getDeclaredField("config");
      field.setAccessible(true);
      ResteasyConfig resteasyConfig = ResteasyConfigFactory.getConfig();
      field.set(resteasyConfig, savedConfig);
   }

   @GET
   @Path("noMPconfig")
   public String noMPConfig()
   {
      return ResteasyConfigFactory.getConfig().getValue("system");
   }

   @GET
   @Path("system")
   public String getSystemFromSource()
   {
      return ResteasyConfigFactory.getConfig().getValue("system", SOURCE.SYSTEM);
   }

   @GET
   @Path("servletcontext")
   public String getServletContextFromSource()
   {
      return ResteasyConfigFactory.getConfig().getValue("system", SOURCE.SERVLET_CONTEXT);
   }

   @GET
   @Path("bootstrap/init")
   public String getFromBootstrapInit()
   {
      ResteasyConfiguration config = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
      return config.getParameter("init");
   }

   @GET
   @Path("bootstrap/context")
   public String getFromBootstrapContext()
   {
      ResteasyConfiguration config = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
      return config.getParameter("context");
   }
}