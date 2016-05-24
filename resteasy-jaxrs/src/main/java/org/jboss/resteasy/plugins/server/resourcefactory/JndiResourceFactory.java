package org.jboss.resteasy.plugins.server.resourcefactory;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JndiResourceFactory implements ResourceFactory
{
   private String jndiName;
   private InitialContext ctx;

   public JndiResourceFactory(String jndiName)
   {
      this.jndiName = jndiName;
      try
      {
         this.ctx = new InitialContext();
      }
      catch (NamingException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void registered(ResteasyProviderFactory factory)
   {
   }

   public Object createResource(HttpRequest request, HttpResponse response, ResteasyProviderFactory factory)
   {
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
      try
      {
         Object obj = ctx.lookup(jndiName);
         return obj.getClass();
      }
      catch (NamingException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void requestFinished(HttpRequest request, HttpResponse response, Object resource)
   {
   }
}

