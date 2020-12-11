package org.jboss.resteasy.microprofile.config;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * I know this implementation is weird, but we do this for two reasons
 * 1. So that resteasy doesn't require servlet
 * 2. Because Graal VM will barf with an unhandled reference if we reference servlet classes directly
 *
 */
public class BaseServletConfigSource implements Serializable {
   private static final long serialVersionUID = -6039524571117729233L;
   protected ConfigSource source;
    protected final boolean available;

    public BaseServletConfigSource(final boolean available, final Class<?> sourceClass) {
        this.available = available;
        if (available) {
            try {
                source = (ConfigSource)sourceClass.newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Map<String, String> getProperties() {
       if (!available) {
          return Collections.<String, String>emptyMap();
       }
       return source.getProperties();
    }

    public String getValue(String propertyName) {
       if (!available) {
          return null;
       }
       return source.getValue(propertyName);
    }

    public String getName() {
       return source.getName();
    }

    public Set<String> getPropertyNames() {
        return source.getPropertyNames();
    }
}
