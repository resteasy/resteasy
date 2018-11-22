package org.jboss.resteasy.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.ws.rs.JAXRS.Configuration.Builder;

public class BootstrapConfigurationBuilder implements Builder {
   private Map<String, Object> properties = new HashMap<String, Object>();
   @SuppressWarnings("rawtypes")
   private BiFunction propertiesProvider;
   @Override
   public javax.ws.rs.JAXRS.Configuration build()
   {
       return new ServerConfiguration();
   }

   @Override
   public <T> Builder from(BiFunction<String, Class<T>, Optional<T>> propertiesProvider)
   {
      Objects.requireNonNull(propertiesProvider);
      this.propertiesProvider = propertiesProvider;
      return this;
   }

   @Override
   public Builder property(String name, Object value)
   {
      properties.put(name, value);
      return this;
   }

   private class ServerConfiguration implements javax.ws.rs.JAXRS.Configuration {
      @Override
      @SuppressWarnings({"rawtypes", "unchecked"})
      public Object property(String name)
      {
         Object result = properties.get(name);
         if (result == null && propertiesProvider != null)
         {
            result = propertiesProvider.apply(name, Object.class);
            return ((Optional)result).isPresent() ? ((Optional)result).get() : null;
         }
         return result;
      }
   }
}