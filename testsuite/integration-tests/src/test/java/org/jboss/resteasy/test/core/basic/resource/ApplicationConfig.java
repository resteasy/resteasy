package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class ApplicationConfig extends Application {
   private static ApplicationConfig instance;

   private Set<Class<?>> classes = new HashSet<Class<?>>();

   @Context
   private UriInfo field;
   private UriInfo setter;
   private UriInfo constructor;

   public ApplicationConfig(@Context final UriInfo uriInfo) {
      this.constructor = uriInfo;
      classes.add(ApplicationConfigResource.class);
      classes.add(ApplicationConfigService.class);
      classes.add(ApplicationConfigQuotedTextWriter.class);
      classes.add(ApplicationConfigInjectionResource.class);
      instance = this;
   }

   public static ApplicationConfig getInstance() {
      return instance;
   }

   @Override
   public Set<Class<?>> getClasses() {
      return classes;
   }

   @Context
   public void setSetter(UriInfo setter) {
      this.setter = setter;
   }

   public boolean isFieldInjected() {
      return field != null;
   }

   public boolean isSetterInjected() {
      return setter != null;
   }

   public boolean isConstructorInjected() {
      return constructor != null;
   }
}
