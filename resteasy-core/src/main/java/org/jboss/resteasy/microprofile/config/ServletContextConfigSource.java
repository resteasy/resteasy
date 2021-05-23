package org.jboss.resteasy.microprofile.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class ServletContextConfigSource extends BaseServletConfigSource implements ConfigSource {

   public static final int BUILT_IN_DEFAULT_ORDINAL = 40;

   private static Class<?> clazz = null;
   static {
      try {
         clazz = Class.forName("jakarta.servlet.ServletContext");
         clazz = Class.forName("org.jboss.resteasy.microprofile.config.ServletContextConfigSourceImpl");
      }
      catch (Throwable e)
      {
         //RESTEASY-2228: allow loading and running this ConfigSource even when Servlet API is not available
      }
   }

   public ServletContextConfigSource() {
      super(clazz, BUILT_IN_DEFAULT_ORDINAL);
   }

}
