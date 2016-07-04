package org.jboss.resteasy.resteasy1119;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 10, 2015
 */
//@Provider
@ApplicationPath("/")
public class TestApplication extends Application
{
//   public Set<Class<?>> getClasses()
//   {
//      HashSet<Class<?>> classes = new HashSet<Class<?>>();
//      classes.add(TestResource.class);
//      return classes;
//   }
}
