package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormMarshaller implements Marshaller
{
   protected HashMap<Field, Marshaller> fieldMap = new HashMap<Field, Marshaller>();

   private static class GetterMethod
   {
      private GetterMethod(Method method, Marshaller marshaller)
      {
         this.method = method;
         this.marshaller = marshaller;
      }

      public Method method;
      public Marshaller marshaller;
   }

   protected List<GetterMethod> setters = new ArrayList<GetterMethod>();
   protected HashMap<Long, Method> setterhashes = new HashMap<Long, Method>();
   protected Class clazz;

   public FormMarshaller(Class clazz, ResteasyProviderFactory factory)
   {
      this.clazz = clazz;

      populateMap(clazz, factory);
   }

   public static long methodHash(Method method)
           throws Exception
   {
      Class[] parameterTypes = method.getParameterTypes();
      StringBuffer methodDesc = new StringBuffer(method.getName() + "(");
      for (int j = 0; j < parameterTypes.length; j++)
      {
         methodDesc.append(getTypeString(parameterTypes[j]));
      }
      methodDesc.append(")" + getTypeString(method.getReturnType()));
      return createHash(methodDesc.toString());
   }

   public static long createHash(String methodDesc)
           throws Exception
   {
      long hash = 0;
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(512);
      MessageDigest messagedigest = MessageDigest.getInstance("SHA");
      DataOutputStream dataoutputstream = new DataOutputStream(new DigestOutputStream(bytearrayoutputstream, messagedigest));
      dataoutputstream.writeUTF(methodDesc);
      dataoutputstream.flush();
      byte abyte0[] = messagedigest.digest();
      for (int j = 0; j < Math.min(8, abyte0.length); j++)
         hash += (long) (abyte0[j] & 0xff) << j * 8;
      return hash;

   }

   static String getTypeString(Class cl)
   {
      if (cl == Byte.TYPE)
      {
         return "B";
      }
      else if (cl == Character.TYPE)
      {
         return "C";
      }
      else if (cl == Double.TYPE)
      {
         return "D";
      }
      else if (cl == Float.TYPE)
      {
         return "F";
      }
      else if (cl == Integer.TYPE)
      {
         return "I";
      }
      else if (cl == Long.TYPE)
      {
         return "J";
      }
      else if (cl == Short.TYPE)
      {
         return "S";
      }
      else if (cl == Boolean.TYPE)
      {
         return "Z";
      }
      else if (cl == Void.TYPE)
      {
         return "V";
      }
      else if (cl.isArray())
      {
         return "[" + getTypeString(cl.getComponentType());
      }
      else
      {
         return "L" + cl.getName().replace('.', '/') + ";";
      }
   }

   protected void populateMap(Class clazz, ResteasyProviderFactory factory)
   {
      for (Field field : clazz.getDeclaredFields())
      {
         Annotation[] annotations = field.getAnnotations();
         if (annotations == null || annotations.length == 0) continue;
         Class type = field.getType();
         Type genericType = field.getGenericType();

         Marshaller marshaller = ClientInvoker.createMarshaller(clazz, factory, type, annotations, genericType, field, true);
         if (marshaller != null)
         {
            if (!Modifier.isPublic(field.getModifiers())) field.setAccessible(true);
            fieldMap.put(field, marshaller);
         }
      }
      for (Method method : clazz.getDeclaredMethods())
      {
         if (!method.getName().startsWith("get")) continue;

         if (method.getParameterTypes().length > 0) continue;

         Annotation[] annotations = method.getAnnotations();
         if (annotations == null || annotations.length == 0) continue;

         Class type = method.getReturnType();
         Type genericType = method.getGenericReturnType();

         Marshaller marshaller = ClientInvoker.createMarshaller(clazz, factory, type, annotations, genericType, method, true);
         if (marshaller != null)
         {
            long hash = 0;
            try
            {
               hash = methodHash(method);
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
            if (!Modifier.isPrivate(method.getModifiers()))
            {
               Method older = setterhashes.get(hash);
               if (older != null) continue;
            }

            if (!Modifier.isPublic(method.getModifiers())) method.setAccessible(true);
            setters.add(new GetterMethod(method, marshaller));
            setterhashes.put(hash, method);
         }

      }
      if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class))
         populateMap(clazz.getSuperclass(), factory);


   }

   public void buildUri(Object object, UriBuilderImpl uri)
   {
      for (Map.Entry<Field, Marshaller> entry : fieldMap.entrySet())
      {

         try
         {
            Object val = entry.getKey().get(object);
            entry.getValue().buildUri(val, uri);
         }
         catch (IllegalAccessException e)
         {
            throw new LoggableFailure(e);
         }
      }
      for (GetterMethod setter : setters)
      {
         Object val = null;
         try
         {
            val = setter.method.invoke(object);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
         catch (InvocationTargetException e)
         {
            throw new RuntimeException(e);
         }
         setter.marshaller.buildUri(val, uri);
      }
   }

   public void setHeaders(Object object, HttpMethodBase httpMethod)
   {
      for (Map.Entry<Field, Marshaller> entry : fieldMap.entrySet())
      {

         try
         {
            Object val = entry.getKey().get(object);
            entry.getValue().setHeaders(val, httpMethod);
         }
         catch (IllegalAccessException e)
         {
            throw new LoggableFailure(e);
         }
      }
      for (GetterMethod setter : setters)
      {
         Object val = null;
         try
         {
            val = setter.method.invoke(object);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
         catch (InvocationTargetException e)
         {
            throw new RuntimeException(e);
         }
         setter.marshaller.setHeaders(val, httpMethod);
      }
   }

   public void buildRequest(Object object, HttpMethodBase httpMethod)
   {
      for (Map.Entry<Field, Marshaller> entry : fieldMap.entrySet())
      {

         try
         {
            Object val = entry.getKey().get(object);
            entry.getValue().buildRequest(val, httpMethod);
         }
         catch (IllegalAccessException e)
         {
            throw new LoggableFailure(e);
         }
      }
      for (GetterMethod setter : setters)
      {
         Object val = null;
         try
         {
            val = setter.method.invoke(object);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
         catch (InvocationTargetException e)
         {
            throw new RuntimeException(e);
         }
         setter.marshaller.buildRequest(val, httpMethod);
      }
   }
}