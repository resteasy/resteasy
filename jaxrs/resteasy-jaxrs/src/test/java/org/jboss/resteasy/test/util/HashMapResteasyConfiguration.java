package org.jboss.resteasy.test.util;

import org.jboss.resteasy.spi.ResteasyConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HashMapResteasyConfiguration implements ResteasyConfiguration {

    private final Map<String, String> config = new HashMap<>();

    public String put(String key, String value) {
        return config.put(key, value);
    }

    @Override
    public String getParameter(String name) {
        return config.get(name);
    }

    @Override
    public Set<String> getParameterNames() {
        return config.keySet();
    }

    @Override
    public String getInitParameter(String name) {
        return getParameter(name);
    }

    @Override
    public Set<String> getInitParameterNames() {
        return getParameterNames();
    }

}
