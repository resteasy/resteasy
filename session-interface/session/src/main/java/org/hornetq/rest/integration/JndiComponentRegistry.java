package org.hornetq.rest.integration;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JndiComponentRegistry implements BindingRegistry
{
   private Context context;

   public JndiComponentRegistry(Context context)
   {
      this.context = context;
   }

   public JndiComponentRegistry() throws Exception
   {
      this.context = new InitialContext();
   }

   @Override
   public Object lookup(String name)
   {
      try
      {
         return context.lookup(name);
      }
      catch (NamingException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public boolean bind(String name, Object obj)
   {
      try
      {
         return bindToJndi(name, obj);
      }
      catch (NamingException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void unbind(String name)
   {
      try
      {
         context.unbind(name);
      }
      catch (NamingException e)
      {
      }
   }

   @Override
   public void close()
   {
      try
      {
         context.close();
      }
      catch (NamingException e)
      {
      }
   }


   private boolean bindToJndi(final String jndiName, final Object objectToBind) throws NamingException
   {
      if (context != null)
      {
         String parentContext;
         String jndiNameInContext;
         int sepIndex = jndiName.lastIndexOf('/');
         if (sepIndex == -1)
         {
            parentContext = "";
         }
         else
         {
            parentContext = jndiName.substring(0, sepIndex);
         }
         jndiNameInContext = jndiName.substring(sepIndex + 1);
         try
         {
            context.lookup(jndiName);

            //JMSServerManagerImpl.log.warn("Binding for " + jndiName + " already exists");
            return false;
         }
         catch (Throwable e)
         {
            // OK
         }

         Context c = org.hornetq.utils.JNDIUtil.createContext(context, parentContext);

         c.rebind(jndiNameInContext, objectToBind);
      }
      return true;
   }

}
