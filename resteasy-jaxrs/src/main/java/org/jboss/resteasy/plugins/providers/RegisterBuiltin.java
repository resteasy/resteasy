package org.jboss.resteasy.plugins.providers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.plugins.interceptors.encoding.AcceptEncodingGZIPFilter;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPEncodingInterceptor;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RegisterBuiltin
{

   public static void register(ResteasyProviderFactory factory)
   {
      final ResteasyProviderFactory monitor = (factory instanceof ThreadLocalResteasyProviderFactory)
            ? ((ThreadLocalResteasyProviderFactory) factory).getDelegate()
            : factory;
      synchronized (monitor)
      {
         if (factory.isBuiltinsRegistered() || !factory.isRegisterBuiltins())
            return;
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
      Enumeration<URL> en;
      if (System.getSecurityManager() == null)
      {
         en = Thread.currentThread().getContextClassLoader().getResources("META-INF/services/" + Providers.class.getName());
      }
      else
      {
         en = AccessController.doPrivileged(new PrivilegedExceptionAction<Enumeration<URL>>()
         {
            @Override
            public Enumeration<URL> run() throws IOException
            {
               return Thread.currentThread().getContextClassLoader().getResources("META-INF/services/" + Providers.class.getName());
            }
         });
      }

      Map<String, URL> origins = new HashMap<String, URL>();
      while (en.hasMoreElements())
      {
         final URL url = en.nextElement();
         InputStream is;
         if (System.getSecurityManager() == null)
         {
            is = url.openStream();
         }
         else
         {
            is = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>()
            {
               @Override
               public InputStream run() throws IOException
               {
                  return url.openStream();
               }
            });
         }

         try
         {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null)
            {
               line = line.trim();
               if (line.equals("")) continue;
               origins.put(line, url);
            }
         }
         finally
         {
            is.close();
         }
      }
      for (final Entry<String, URL> entry : origins.entrySet())
      {
         final String line = entry.getKey();
         try
         {
            Class clazz;
            if (System.getSecurityManager() == null)
            {
               clazz = Thread.currentThread().getContextClassLoader().loadClass(line);
            }
            else
            {
               clazz = AccessController.doPrivileged(new PrivilegedExceptionAction<Class>()
               {
                  @Override
                  public Class run() throws ClassNotFoundException
                  {
                     return Thread.currentThread().getContextClassLoader().loadClass(line);
                  }
               });
            }

            factory.registerProvider(clazz, true);
         }
         catch (NoClassDefFoundError e)
         {
            LogMessages.LOGGER.noClassDefFoundErrorError(line, entry.getValue(), e);
         }
         catch (ClassNotFoundException | PrivilegedActionException ex)
         {
            LogMessages.LOGGER.classNotFoundException(line, entry.getValue(), ex);
         }
      }
      if (AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         @Override
         public Boolean run() {
            final String value = System.getProperty("resteasy.allowGzip");
            if ("".equals(value)) return Boolean.FALSE;
            return Boolean.parseBoolean(value);
         }
      })) {
         factory.registerProvider(AcceptEncodingGZIPFilter.class, true);
         factory.registerProvider(GZIPDecodingInterceptor.class, true);
         factory.registerProvider(GZIPEncodingInterceptor.class, true);
      }
   }

}
