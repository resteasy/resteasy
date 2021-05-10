package org.jboss.resteasy.microprofile.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class FilterConfigSource extends BaseServletConfigSource implements ConfigSource {

   public static final int BUILT_IN_DEFAULT_ORDINAL = 50;

   private static Class<?> clazz = null;
   static {
      try {
         clazz = Class.forName("jakarta.servlet.FilterConfig");
         clazz = Class.forName("org.jboss.resteasy.microprofile.config.FilterConfigSourceImpl");
      }
      catch (Throwable e)
      {
         //RESTEASY-2228: allow loading and running this ConfigSource even when Servlet API is not available
      }
   }

   public FilterConfigSource() {
      super(clazz, BUILT_IN_DEFAULT_ORDINAL);
   }

}
