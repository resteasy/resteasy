package org.jboss.resteasy.client.core;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.MappedBy;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.IsHttpMethod;

public class UriFor
{
   public static URI uriForObject(Object o)
   {
      try
      {
         return uriForObject(o, getTemplate(o));
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException("Could not generate a uri for object of type " + o.getClass());
      }
   }

   public static URI uriForObject(Object o, String template)
   {
      try
      {
         return uriForObject(o, WebRequestIntializer.createBuilder(template));
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException("Could not generate a uri for object of type " + o.getClass());
      }
   }

   public static URI uriForObject(Object o, UriBuilder template)
   {
      return template.build(getValues(o, template));
   }
   
   public static Object[] getValues(Object o, UriBuilder template)
   {
      List<String> paramNames = ((UriBuilderImpl)template).getPathParamNamesInDeclarationOrder();
      return getValues(o, paramNames);
   }

   public static Object[] getValues(Object o, List<String> paramNames)
   {
      Object[] values = new Object[paramNames.size()];
      int i = 0;
      for (String paramName : paramNames)
      {
         String getterName = "get" + paramName.substring(0,1).toUpperCase() + paramName.substring(1);
         try
         {
            values[i] = o.getClass().getMethod(getterName).invoke(o);
         }
         catch (Exception e)
         {
            throw new RuntimeException("Could not invoke getter: " + getterName + " in UriFor.uriFor", e);
         }
         i++;
      }
      return values;
   }

   private static UriBuilder getTemplate(Object o) throws URISyntaxException
   {
      if (o == null)
         return null;
      MappedBy mappedBy = getMappedBy(o.getClass());
      if (mappedBy == null)
         throw new RuntimeException("uriFor requires a MappedBy on the object");
      String template = mappedBy.template();
      if(template != null && !template.isEmpty())
         return WebRequestIntializer.createBuilder(template);
      if( mappedBy.resourceClass() != null )
      {
         UriBuilder builder = RuntimeDelegate.getInstance().createUriBuilder();
         String methodName = mappedBy.resourceMethodName();
         builder.path(mappedBy.resourceClass());
         if(methodName != null && !methodName.isEmpty())
            builder.path(mappedBy.resourceClass(), methodName);
         return builder;
      }
      throw new RuntimeException("uriFor requires @MappedBy to have either pathTemplate or resourceClsss set.");
   }

   @SuppressWarnings("unchecked")
   private static MappedBy getMappedBy(Class<? extends Object> c)
   {
      MappedBy mappedBy = c.getAnnotation(MappedBy.class);
      if( mappedBy != null )
         return mappedBy;
      for( Class parent : c.getDeclaredClasses() ){
         MappedBy mb = getMappedBy(parent);
         if( mb != null )
            return mb;
      }
      
      for( Class interfaces : c.getInterfaces() ){
         MappedBy mb = getMappedBy(interfaces);
         if( mb != null )
            return mb;
      }
      return null;
   }

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