package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.core.LoggerCategories;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;

import javax.ws.rs.ext.Providers;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegisterBuiltin
{

   private final static Logger logger = LoggerCategories.getProviderLogger();


   public static void register(ResteasyProviderFactory factory)
   {
      synchronized (factory)
      {
         if (factory.isBuiltinsRegistered()) return;
         try
         {
            registerProviders(factory);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         factory.setBuiltinsRegistered(true);
      }
   }

   public static void registerProviders(ResteasyProviderFactory factory) throws Exception
   {
      Enumeration<URL> en = Thread.currentThread().getContextClassLoader().getResources("META-INF/services/" + Providers.class.getName());
      LinkedHashSet<String> set = new LinkedHashSet<String>();
      while (en.hasMoreElements())
      {
         URL url = en.nextElement();
         BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
         String line;
         while ((line = reader.readLine()) != null)
         {
            line = line.trim();
            if (line.equals("")) continue;
            set.add(line);
         }
      }
      for (String line : set)
      {
         Class clazz = null;
         try
         {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(line);
         }
         catch (ClassNotFoundException e)
         {
            logger.warn("ClassNotFoundException: Unable to load builtin provider: " + line);
         }
         factory.registerProvider(clazz, true);
      }
   }

}
