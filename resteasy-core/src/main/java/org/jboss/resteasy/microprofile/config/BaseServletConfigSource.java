package org.jboss.resteasy.microprofile.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * I know this implementation is weird, but we do this for two reasons
 * 1. So that resteasy doesn't require servlet
 * 2. Because Graal VM will barf with an unhandled reference if we reference servlet classes directly
 *
 */
public class BaseServletConfigSource implements ConfigSource {
    protected ConfigSource source;
    protected final boolean available;
    protected final int defaultOrdinal;
    private final String name;

    public BaseServletConfigSource(final Class<?> sourceClass, final int defaultOrdinal) {
        this.defaultOrdinal = defaultOrdinal;
        if (sourceClass != null) {
            try {
                source = (ConfigSource)sourceClass.newInstance();
                name = source.getName();
                available = true;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } else {
            name = toString();
            available = false;
        }
    }

   @Override
   public Map<String, String> getProperties() {
       if (!available) {
          return Collections.<String, String>emptyMap();
       }
       return source.getProperties();
    }

   @Override
   public Set<String> getPropertyNames() {
        return getProperties().keySet();
    }

    @Override
    public int getOrdinal() {
       if (!available) {
          return defaultOrdinal;
       }
       return source.getOrdinal();
   }

   @Override
   public String getValue(String propertyName) {
       if (!available) {
          return null;
       }
       return source.getValue(propertyName);
    }

    @Override
    public String getName() {
       return name;
    }

}
