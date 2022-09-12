package org.jboss.resteasy.embedded.test.core.basic.resource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("a/explicit")
public class ApplicationTestAExplicitApplication extends Application {

   @Override
   public Set<Class<?>> getClasses() {
      HashSet<Class<?>> set = new HashSet<Class<?>>();
      set.add(ApplicationTestResourceA.class);
      return set;
   }

   @Override
   public Set<Object> getSingletons() {
      HashSet<Object> set = new HashSet<>();
      set.add(new ApplicationTestSingletonA());
      return set;
   }
}
