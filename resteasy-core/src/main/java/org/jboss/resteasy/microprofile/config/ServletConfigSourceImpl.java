package org.jboss.resteasy.microprofile.config;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.resteasy.core.ResteasyContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServletConfigSourceImpl implements ConfigSource {
   private volatile String name;

   @Override
   public Map<String, String> getProperties() {
      ServletConfig config = ResteasyContext.getContextData(ServletConfig.class);
      if (config == null) {
         return Collections.<String, String>emptyMap();
      }
      Map<String, String> map = new HashMap<String, String>();
      Enumeration<String> keys = config.getInitParameterNames();
      if (keys != null) {
         while (keys.hasMoreElements())
         {
            String key = keys.nextElement();
            map.put(key, config.getInitParameter(key));
         }
      }
      return map;
   }

   @Override
   public Set<String> getPropertyNames() {
      ServletConfig config = ResteasyContext.getContextData(ServletConfig.class);
      if (config == null) {
         return Collections.<String>emptySet();
      }

      return new HashSet<String>(Collections.list(config.getInitParameterNames()));
   }

   @Override
   public String getValue(String propertyName) {
      ServletConfig config = ResteasyContext.getContextData(ServletConfig.class);
      if (config == null) {
         return null;
      }
      return config.getInitParameter(propertyName);
   }

   @Override
   public String getName() {
      if (name == null) {
         synchronized(this) {
            if (name == null) {
               ServletContext servletContext = ResteasyContext.getContextData(ServletContext.class);
               ServletConfig servletConfig = ResteasyContext.getContextData(ServletConfig.class);
               StringBuilder sb = new StringBuilder();
               name = sb.append(servletContext != null ? servletContext.getServletContextName() : null).append(":")
                     .append(servletConfig != null ? servletConfig.getServletName() : null)
                     .append(":ServletConfigSource").toString();
            }
         }
      }
      return name;
   }

   @Override
   public int getOrdinal() {
      return 60;
   }
}
