package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class AppConfigApplication extends Application {
   private Set<Class<?>> classes = new HashSet<Class<?>>();
   private Set<Object> singletons = new HashSet<Object>();

   public AppConfigApplication() {
      classes.add(AppConfigResources.MyResource.class);
      singletons.add(new AppConfigResources.QuotedTextWriter());
   }

   public Set<Class<?>> getClasses() {
      return classes;
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}
