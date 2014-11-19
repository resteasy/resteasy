package org.jboss.resteasy.resteasy1125;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

/**
 * RESTEASY-1125
 *
 * Nov 19, 2014
 */
@Provider
public class TestApplication extends Application
{
   public Set<Class<?>> getClasses()
   {
      HashSet<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(TestResource.class);
      classes.add(TestResource2.class);
      return classes;
   }
}
