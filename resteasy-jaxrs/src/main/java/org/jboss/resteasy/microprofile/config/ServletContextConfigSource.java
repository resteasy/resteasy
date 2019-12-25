package org.jboss.resteasy.microprofile.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class ServletContextConfigSource extends BaseServletConfigSource implements ConfigSource {
   private static final boolean SERVLET_AVAILABLE;
   private static Class<?> clazz = null;
   static {
      try {
         clazz = Class.forName("javax.servlet.ServletContext");
         clazz = Class.forName("org.jboss.resteasy.microprofile.config.ServletContextConfigSourceImpl");
      }
      catch (Throwable e)
      {
         //RESTEASY-2228: allow loading and running this ConfigSource even when Servlet API is not available
      }
      SERVLET_AVAILABLE = clazz != null;
   }

   public ServletContextConfigSource() {
      super(SERVLET_AVAILABLE, clazz);
   }

   @Override
   public int getOrdinal() {
      return 40;
   }
}
