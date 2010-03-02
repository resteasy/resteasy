package org.jboss.resteasy.cdi;

import javax.ws.rs.WebApplicationException;

import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;

/**
 * This implementation of PropertyInjector does not do anything in order to
 * prevent double JAX-RS property injection.
 * 
 * @author Jozef Hartinger
 * @see CdiInjectorFactory#createPropertyInjector(Class)
 *
 */
public class NoopPropertyInjector implements PropertyInjector
{
   public void inject(Object target)
   {
      // noop
   }

   public void inject(HttpRequest request, HttpResponse response, Object target) throws Failure, WebApplicationException, ApplicationException
   {
      // noop
   }
}
