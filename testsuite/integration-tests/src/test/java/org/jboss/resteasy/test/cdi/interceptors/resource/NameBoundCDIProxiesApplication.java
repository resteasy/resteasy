package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("")
@NameBoundProxiesAnnotation
public class NameBoundCDIProxiesApplication extends Application {

   public Set<Class<?>> getClasses() {
      Set<Class<?>> set = new HashSet<Class<?>>();
      set.add(NameBoundCDIProxiesResource.class);
      return set;
   }

   public Set<Object> getSingletons() {
      Set<Object> set = new HashSet<Object>();
      set.add(new NameBoundCDIProxiesInterceptor());
      return set;
   }
}
