package org.jboss.resteasy.test.tracing;

import org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class TracingApp extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set set = new HashSet<Class<?>>();
        set.add(HttpMethodOverride.class);
        set.add(GZIPEncodingInterceptor.class);
        set.add(GZIPDecodingInterceptor.class);
        return set;
    }

    @Override
    public Set<Object> getSingletons() {
        Set set = new HashSet<Class<?>>();
        set.add(new TracingConfigResource());
        set.add(new FooLocator());
        return set;
    }
}
