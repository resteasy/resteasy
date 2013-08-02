package org.jboss.resteasy.test.finegrain.application;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.HashSet;
import java.util.Set;

public class MyApplicationConfig extends Application
{
   private static MyApplicationConfig instance;

   private Set<Class<?>> classes = new HashSet<Class<?>>();

   @Context
   private UriInfo field;
   private UriInfo setter;
   private UriInfo constructor;

   public MyApplicationConfig(@Context UriInfo uriInfo)
   {
      this.constructor = uriInfo;
      classes.add(ApplicationConfigTest.MyResource.class);
      classes.add(ApplicationConfigTest.MyService.class);
      classes.add(ApplicationConfigTest.QuotedTextWriter.class);
      classes.add(ApplicationConfigTest.InjectionResource.class);
      instance = this;
   }

   @Override
   public Set<Class<?>> getClasses()
   {
      return classes;
   }

   @Context
   public void setSetter(UriInfo setter)
   {
      this.setter = setter;
   }

   public boolean isFieldInjected()
   {
      return field != null;
   }

   public boolean isSetterInjected()
   {
      return setter != null;
   }

   public boolean isConstructorInjected()
   {
      return constructor != null;
   }

   public static MyApplicationConfig getInstance()
   {
      return instance;
   }
}
