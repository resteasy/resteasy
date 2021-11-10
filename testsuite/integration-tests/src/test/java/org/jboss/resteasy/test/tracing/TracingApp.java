package org.jboss.resteasy.test.tracing;

import org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class TracingApp extends Application {

   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> set = new HashSet<>();
      set.add(HttpMethodOverride.class);
      set.add(GZIPEncodingInterceptor.class);
      set.add(GZIPDecodingInterceptor.class);
      return set;
   }

   @Override
   public Set<Object> getSingletons() {
      Set<Object> set = new HashSet<>();
      set.add(new TracingConfigResource());
      set.add(new FooLocator());
      return set;
   }
}
