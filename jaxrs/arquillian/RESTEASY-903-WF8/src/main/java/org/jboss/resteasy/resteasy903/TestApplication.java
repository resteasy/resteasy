package org.jboss.resteasy.resteasy903;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 5, 2013
 */
@Provider
public class TestApplication extends Application
{
   public Set<Class<?>> getClasses()
   {
      HashSet<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(TestResource.class);
      return classes;
   }
}
