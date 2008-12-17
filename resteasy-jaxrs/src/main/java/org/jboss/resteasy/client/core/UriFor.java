package org.jboss.resteasy.client.core;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.IsHttpMethod;

public class UriFor
{
   public static String resolve(final Class<? extends Object> clazz,
         String methodName, Object... args)
   {
      if (methodName == null)
      {
         throw new RuntimeException(
               "A URL cannot be created for a null method.");
      }

      try
      {
         return resolve(new URI("/"), clazz, methodName, true, args);
      }
      catch (URISyntaxException e)
      {
         // this shouldn't happen for "/"
         return null;
      }
   }

   public static String resolve(URI baseUri,
         final Class<? extends Object> clazz, String methodName, Object... args)
   {
      return resolve(baseUri, clazz, methodName, false, args);
   }

   public static String resolve(URI baseUri,
         final Class<? extends Object> clazz, String methodName,
         boolean allowRelative, Object... args)
   {
      Method m = getMethod(clazz, methodName, args);
      return m == null ? null : resolve(baseUri, allowRelative, m, args);
   }

   public static String resolve(URI baseUri, boolean allowRelative,
         Method method, Object... args)
   {
      return resolve(baseUri, allowRelative, method, ResteasyProviderFactory
            .getInstance(), args);
   }

   public static String resolve(URI baseUri, boolean allowRelative,
         Method method, ResteasyProviderFactory providerFactory, Object... args)
   {
      Marshaller[] marshallers = ClientMarshallerFactory.createMarshallers(method,
            providerFactory);
      WebRequestIntializer urlRetriever = new WebRequestIntializer(marshallers);
      return urlRetriever.buildUrl(baseUri, allowRelative, method, args);
   }

   public static Method getMethod(final Class<? extends Object> clazz,
         String methodName, Object... args)
   {
      if (methodName == null)
      {
         return null;
      }
      Method m = null;
      for (Method method : clazz.getMethods())
      {
         final Class<?>[] parameterTypes = method.getParameterTypes();
         if (method.getName().equals(methodName) && args.length == parameterTypes.length
               && IsHttpMethod.getHttpMethods(method) != null && isMatch(parameterTypes, args) )
         {
            return method;
         }
      }
      return m;
   }

   private static boolean isMatch(final Class<?>[] parameterTypes,
         Object... args)
   {
      for(int i=0; i<args.length; i++)
      {
         Class<?> type = parameterTypes[i];
         if( args[i] == null )
         {
            if( type.isPrimitive() )
               return false;
            else
               continue;
         }
         if(!type.isInstance(args[0]))
            return false;
      }
      return true;
   }

}