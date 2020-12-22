package org.jboss.resteasy.microprofile.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

public interface ResteasyConfigSource extends ConfigSource
{

   @Override
   default int getOrdinal() {
      String configOrdinal = getValue(CONFIG_ORDINAL);
      if(configOrdinal != null) {
          try {
              return Integer.parseInt(configOrdinal);
          }
          catch (NumberFormatException ignored) {

          }
      }
      return getDefaultOrdinal();
  }

   default int getDefaultOrdinal() {
      return DEFAULT_ORDINAL;
   }

}
