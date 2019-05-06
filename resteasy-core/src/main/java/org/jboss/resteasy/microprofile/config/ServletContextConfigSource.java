package org.jboss.resteasy.microprofile.config;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.resteasy.core.ResteasyContext;

public class ServletContextConfigSource implements ConfigSource {

   private static final boolean SERVLET_AVAILABLE;
   static {
      Class<?> clazz = null;
      try {
         clazz = Class.forName(ServletContext.class.getName());
      }
      catch (Throwable e)
      {
         //RESTEASY-2228: allow loading and running this ConfigSource even when Servlet API is not available
      }
      SERVLET_AVAILABLE = clazz != null;
   }

   private volatile String name;

   @Override
   public Map<String, String> getProperties() {
      if (!SERVLET_AVAILABLE) {
         return Collections.<String, String>emptyMap();
      }
      ServletContext context = ResteasyContext.getContextData(ServletContext.class);
      if (context == null) {
         return Collections.<String, String>emptyMap();
      }
      Map<String, String> map = new HashMap<String, String>();
      Enumeration<String> keys = context.getInitParameterNames();
      if (keys != null) {
         while (keys.hasMoreElements())
         {
            String key = keys.nextElement();
            map.put(key, context.getInitParameter(key));
         }
      }
      return map;
   }

   @Override
   public String getValue(String propertyName) {
      if (!SERVLET_AVAILABLE) {
         return null;
      }
      ServletContext context = ResteasyContext.getContextData(ServletContext.class);
      if (context == null) {
         return null;
      }
      return context.getInitParameter(propertyName);
   }

   @Override
   public String getName() {
      if (name == null) {
         synchronized(this) {
            if (name == null) {
               if (!SERVLET_AVAILABLE) {
                  name = toString();
               }
               ServletContext servletContext = ResteasyContext.getContextData(ServletContext.class);
               StringBuilder sb = new StringBuilder();
               name = sb.append(servletContext != null ? servletContext.getServletContextName() : null)
                     .append(":ServletContextConfigSource").toString();
            }
         }
      }
      return name;
   }

   @Override
   public int getOrdinal() {
      return 40;
   }
}
