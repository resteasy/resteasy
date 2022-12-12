package org.jboss.resteasy.test.cdi.interceptors.resource;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("")
@NameBoundProxiesAnnotation
public class NameBoundCDIProxiesApplication extends Application {

   @Override
   public Set<Class<?>> getClasses() {
      return Set.of(NameBoundCDIProxiesResource.class, NameBoundCDIProxiesInterceptor.class);
   }
}
