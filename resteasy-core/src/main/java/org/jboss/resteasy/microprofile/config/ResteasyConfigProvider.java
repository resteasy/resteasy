package org.jboss.resteasy.microprofile.config;

import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.jboss.resteasy.config.ResteasyConfig;

public final class ResteasyConfigProvider implements ResteasyConfig
{
   private static Config getConfig()
   {
      return ConfigProvider.getConfig(ResteasyConfigProvider.class.getClassLoader());
   }

   public static void registerConfig(Config config)
   {
      ConfigProviderResolver.instance().registerConfig(config, ResteasyConfigProvider.class.getClassLoader());
   }

   public static ConfigBuilder getBuilder()
   {
      return ConfigProviderResolver.instance().getBuilder()
            .forClassLoader(ResteasyConfigProvider.class.getClassLoader());
   }

   @Override
   public <T> Optional<T> getOptionalValue(String propertyKey, Class<T> propertyType)
   {
      return getConfig().getOptionalValue(propertyKey, propertyType);
   }
}
