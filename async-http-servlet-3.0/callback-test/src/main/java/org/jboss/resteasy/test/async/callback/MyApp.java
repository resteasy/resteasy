package org.jboss.resteasy.test.async.callback;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class MyApp extends Application
{

   public java.util.Set<java.lang.Class<?>> getClasses() {
      Set<Class<?>> resources = new HashSet<Class<?>>();
      resources.add(CallbackResource.class);
      resources.add(StringBeanEntityProvider.class);
      return resources;
   }
}
