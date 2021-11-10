package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/f")
public class DuplicitePathNoDupliciteApplication extends Application {
   @Override
   public Set<Class<?>> getClasses() {
      HashSet<Class<?>> set = new HashSet<Class<?>>();
      set.add(DuplicitePathMethodResource.class);
      set.add(DuplicitePathDupliciteResourceOne.class);
      set.add(DuplicitePathDupliciteResourceTwo.class);
      return set;
   }
}
