package org.jboss.resteasy.test.cdi.basic.resource.resteasy1082;

import java.util.HashSet;
import java.util.Set;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TestApplication extends Application {
   public TestApplication() {
   }

   public Set<Class<?>> getClasses() {
      HashSet<Class<?>> classes = new HashSet<>();
      classes.add(FooResource.class);
      return classes;
   }
}
