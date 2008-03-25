package org.resteasy.plugins.server.resourcefactory;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResourceReference;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JndiResourceFactory implements ResourceReference
{
   private String jndiName;

   public JndiResourceFactory(String jndiName)
   {
      this.jndiName = jndiName;
   }

   public ResourceFactory getFactory(InjectorFactory factory)
   {
      try
      {
         final InitialContext ic = new InitialContext();
         return new ResourceFactory()
         {
            public Object createResource(HttpRequest input, HttpResponse response)
            {
               try
               {
                  Object obj = ic.lookup(jndiName);
                  return obj;
               }
               catch (NamingException e)
               {
                  throw new RuntimeException(e);
               }
            }

         };
      }
      catch (NamingException e)
      {
         throw new RuntimeException(e);
      }
   }


   public Class<?> getScannableClass()
   {
      try
      {
         InitialContext ic = new InitialContext();
         Object obj = ic.lookup(jndiName);
         return obj.getClass();
      }
      catch (NamingException e)
      {
         throw new RuntimeException(e);
      }
   }
}
