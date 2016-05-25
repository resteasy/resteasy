package org.jboss.resteasy.cdi.ejb;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Application;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 3, 2012
 */
@Singleton
@ApplicationScoped
public class EJBApplication extends Application
{
   @Override
   public Set<Class<?>> getClasses()
   {
      HashSet<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(EJBBookReaderImpl.class);
      classes.add(EJBBookWriterImpl.class);
      classes.add(EJBBookResource.class);
      return classes;
   }
}

