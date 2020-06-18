package org.jboss.resteasy.microprofile.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class FilterConfigSource extends BaseServletConfigSource implements ConfigSource {

   public static final int BUILT_IN_DEFAULT_ORDINAL = 50;

   private static final boolean SERVLET_AVAILABLE;
   private static Class<?> clazz = null;
   static {
      try {
         clazz = Class.forName("javax.servlet.FilterConfig");
         clazz = Class.forName("org.jboss.resteasy.microprofile.config.FilterConfigSourceImpl");
      }
      catch (Throwable e)
      {
         //RESTEASY-2228: allow loading and running this ConfigSource even when Servlet API is not available
      }
      SERVLET_AVAILABLE = clazz != null;
   }

   public FilterConfigSource() {
      super(SERVLET_AVAILABLE, clazz, BUILT_IN_DEFAULT_ORDINAL);
   }

}
