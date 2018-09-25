package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class FileExtensionMappingApplication extends Application {
   private Set<Class<?>> classes = new HashSet<Class<?>>();
   private Set<Object> singletons = new HashSet<Object>();

   public FileExtensionMappingApplication() {
      classes.add(FileExtensionMappingResource.class);
   }

   public Set<Class<?>> getClasses() {
      return classes;
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
}
