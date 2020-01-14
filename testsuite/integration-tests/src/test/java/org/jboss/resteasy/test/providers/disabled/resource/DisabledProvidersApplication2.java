package org.jboss.resteasy.test.providers.disabled.resource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("")
public class DisabledProvidersApplication2 extends Application {

   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(DisabledProvidersResource.class);
      return classes;
   }

   @Override
   public Set<Object> getSingletons() {
      Set<Object> objects = new HashSet<Object>();
      objects.add(new FooReaderWriter());
      return objects;
   }
}
