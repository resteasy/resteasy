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
public class BaseServletConfigSource {
    protected ConfigSource source;
    protected final boolean available;
    private final String name;

    public BaseServletConfigSource(final boolean available, final Class<?> sourceClass) {
        this.available = available;
        if (available) {
            try {
                source = (ConfigSource)sourceClass.newInstance();
                name = source.getName();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        } else {
            name = toString();
        }
    }

    public Map<String, String> getProperties() {
       if (!available) {
          return Collections.<String, String>emptyMap();
       }
       return source.getProperties();
    }

    public Set<String> getPropertyNames() {
        return getProperties().keySet();
    }

    public String getValue(String propertyName) {
       if (!available) {
          return null;
       }
       return source.getValue(propertyName);
    }

    public String getName() {
       return name;
    }
}
