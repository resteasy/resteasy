package org.jboss.resteasy.microprofile.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class ServletConfigSource extends BaseServletConfigSource implements ConfigSource {

   public static final int BUILT_IN_DEFAULT_ORDINAL = 60;

   private static Class<?> clazz = null;
   static {
      try {
         clazz = Class.forName("jakarta.servlet.ServletConfig");
         clazz = Class.forName("org.jboss.resteasy.microprofile.config.ServletConfigSourceImpl");
      }
      catch (Throwable e)
      {
         //RESTEASY-2228: allow loading and running this ConfigSource even when Servlet API is not available
      }
   }

   public ServletConfigSource() {
      super(clazz, BUILT_IN_DEFAULT_ORDINAL);
   }

}
