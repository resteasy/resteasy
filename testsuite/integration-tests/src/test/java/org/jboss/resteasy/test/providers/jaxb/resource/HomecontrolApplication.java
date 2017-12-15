package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/service")
public class HomecontrolApplication extends Application {


   @Override
   public Set<Class<?>> getClasses() {

      Set<Class<?>> result = new HashSet<Class<?>>();
      result.add(HomecontrolService.class);
      return result;
   }

   @Override
   public Set<Object> getSingletons() {
      Set<Object> result = new HashSet<>();
      result.add(new HomecontrolJaxbProvider());
      return result;
   }
}