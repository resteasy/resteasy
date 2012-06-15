package org.jboss.resteasy.client.jaxrs.internal;

import javax.ws.rs.client.Configuration;
import javax.ws.rs.client.Feature;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientConfiguration implements Configuration
{
   protected HashMap<String, Object> properties = new HashMap<String, Object>();

   public ClientConfiguration()
   {
   }

   public ClientConfiguration(ClientConfiguration parent)
   {
      properties.putAll(parent.properties);
   }

   public Map<String, Object> getMutableProperties()
   {
      return properties;
   }

   @Override
   public Map<String, Object> getProperties()
   {
      return Collections.unmodifiableMap(properties);
   }

   @Override
   public Object getProperty(String name)
   {
      return properties.get(name);
   }

   @Override
   public Set<Feature> getFeatures()
   {
      return null;
   }

   @Override
   public Set<Class<?>> getProviderClasses()
   {
      return null;
   }

   @Override
   public Set<Object> getProviderInstances()
   {
      return null;
   }

   @Override
   public Configuration update(Configuration configuration)
   {
      return null;
   }

   @Override
   public Configuration register(Class<?> providerClass)
   {
      return null;
   }

   @Override
   public Configuration register(Object provider)
   {
      return null;
   }

   @Override
   public Configuration setProperties(Map<String, ? extends Object> properties)
   {
      return null;
   }

   @Override
   public Configuration setProperty(String name, Object value)
   {
      properties.put(name, value);
      return this;
   }
}
