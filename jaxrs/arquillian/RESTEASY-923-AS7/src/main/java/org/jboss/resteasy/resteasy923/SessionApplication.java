package org.jboss.resteasy.resteasy923;

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
 * Copyright Mar 5, 2013
 */
//@Provider
@ApplicationPath("/")
public class SessionApplication extends Application
{
//   public Set<Class<?>> getClasses()
//   {
//      HashSet<Class<?>> classes = new HashSet<Class<?>>();
//      classes.add(SessionResourceImpl.class);
//      return classes;
//   }
}
