package org.jboss.resteasy.test.client.exception.resource;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("app")
public class ClientWebApplicationExceptionApplication extends Application {

   @Override
   public Set<Class<?>> getClasses() {
      HashSet<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(ClientWebApplicationExceptionResource.class);
      return classes;
   }
}