package org.jboss.resteasy.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

public final class ResteasyConfigProvider
{
   public static Config getConfig() {
      return ConfigProvider.getConfig(ResteasyConfigProvider.class.getClassLoader());
   }

   public static void registerConfig(Config config) {
      ConfigProviderResolver.instance().registerConfig(config, ResteasyConfigProvider.class.getClassLoader());
   }

   public static ConfigBuilder getBuilder() {
      return ConfigProviderResolver.instance().getBuilder().forClassLoader(ResteasyConfigProvider.class.getClassLoader());
   }

}
