package org.resteasy.plugins.server.resourcefactory;

import org.resteasy.spi.HttpInput;
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

   public JndiResourceFactory(String jndiName)
   {
      this.jndiName = jndiName;
   }

   public Object createResource(HttpInput input)
   {
      try
      {
         InitialContext ic = new InitialContext();
         Object obj = ic.lookup(jndiName);
         return obj;
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
