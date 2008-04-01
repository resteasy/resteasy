package org.resteasy.plugins.server.resourcefactory;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.ResourceFactory;

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

   public void registered(InjectorFactory factory)
   {
   }

   public Object createResource(HttpRequest request, HttpResponse response, InjectorFactory factory)
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
}
