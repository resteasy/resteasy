package org.jboss.resteasy.plugins.server.resourcefactory;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ResourceFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Used for component jndi-based resources like EJBs.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JndiComponentResourceFactory implements ResourceFactory
{
   private String jndiName;
   private InitialContext ctx;
   private Object reference;
   private Class scannable;


   public JndiComponentResourceFactory(String jndiName, Class scannable, boolean cacheReference)
   {
      this.jndiName = jndiName;
      this.scannable = scannable;
      try
      {
         this.ctx = new InitialContext();
         if (cacheReference)
         {
            reference = ctx.lookup(jndiName);
         }
      }
      catch (NamingException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void registered(InjectorFactory factory)
   {
   }

   public Object createResource(HttpRequest request, HttpResponse response, InjectorFactory factory)
   {
      if (reference != null) return reference;
      try
      {
         return ctx.lookup(jndiName);
      }
      catch (NamingException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void unregistered()
   {
   }

   public Class<?> getScannableClass()
   {
      return scannable;
   }

   public void requestFinished(HttpRequest request, HttpResponse response, Object resource)
   {
   }
}