package org.jboss.resteasy.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestConfigImpl implements ResteasyConfig
{
   static final Map<String, Object> config = new HashMap<>();
   static final Map<String, Integer> requestedProperties = new HashMap<>();

   @Override
   public <T> Optional<T> getOptionalValue(String propertyKey, Class<T> propertyType)
   {
      requestedProperties.merge(propertyKey, 1, Integer::sum);
      return Optional.ofNullable(propertyType.cast(config.get(propertyKey)));
   }
}
