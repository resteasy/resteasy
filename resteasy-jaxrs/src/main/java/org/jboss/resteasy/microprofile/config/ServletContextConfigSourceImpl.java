package org.jboss.resteasy.microprofile.config;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.ServletContext;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ServletContextConfigSourceImpl implements ConfigSource, Serializable {

   private static final long serialVersionUID = 3280445687403079031L;
   private volatile String name;

   @Override
   public Map<String, String> getProperties() {
      ServletContext context = ResteasyProviderFactory.getContextData(ServletContext.class);
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
      ServletContext context = ResteasyProviderFactory.getContextData(ServletContext.class);
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
               ServletContext servletContext = ResteasyProviderFactory.getContextData(ServletContext.class);
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
