package org.jboss.resteasy.client.jaxrs.internal.proxy.processors;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.spi.LoggableFailure;

import javax.ws.rs.client.WebTarget;
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
public class FormProcessor implements InvocationProcessor, WebTargetProcessor
{
   protected HashMap<Field, Object> fieldMap = new HashMap<Field, Object>();

   private static class GetterMethod
   {
      private GetterMethod(Method method, Object processor)
      {
         this.method = method;
         this.processor = processor;
      }

      public Method method;
      public Object processor;
   }

   protected List<GetterMethod> getters = new ArrayList<GetterMethod>();
   protected HashMap<Long, Method> getterHashes = new HashMap<Long, Method>();
   protected Class clazz;

   public FormProcessor(Class clazz, ClientConfiguration configuration)
   {
      this.clazz = clazz;

      populateMap(clazz, configuration);
   }

   public static long methodHash(Method method)
           throws Exception
   {
      Class[] parameterTypes = method.getParameterTypes();
      StringBuilder methodDesc = new StringBuilder(method.getName()).append("(");
      for (int j = 0; j < parameterTypes.length; j++)
      {
         methodDesc.append(getTypeString(parameterTypes[j]));
      }
      methodDesc.append(")").append(getTypeString(method.getReturnType()));
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

   protected void populateMap(Class clazz, ClientConfiguration configuration)
   {
      for (Field field : clazz.getDeclaredFields())
      {
         Annotation[] annotations = field.getAnnotations();
         if (annotations == null || annotations.length == 0) continue;
         Class type = field.getType();
         Type genericType = field.getGenericType();

         Object processor = ProcessorFactory.createProcessor(
                 clazz, configuration, type, annotations, genericType, field, true);
         if (processor != null)
         {
            if (!Modifier.isPublic(field.getModifiers())) field.setAccessible(true);
            fieldMap.put(field, processor);
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

         Object processor = ProcessorFactory
                 .createProcessor(clazz, configuration, type, annotations,
                         genericType, method, true);
         if (processor != null)
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
               Method older = getterHashes.get(hash);
               if (older != null) continue;
            }

            if (!Modifier.isPublic(method.getModifiers())) method.setAccessible(true);
            getters.add(new GetterMethod(method, processor));
            getterHashes.put(hash, method);
         }

      }
      if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class))
         populateMap(clazz.getSuperclass(), configuration);


   }


   interface Process
   {
      Object process(Object target, Object value, Object processor);
   }

   @Override
   public WebTarget build(WebTarget target, Object param)
   {
      if (param == null) return target;
      return (WebTarget) process(new Process()  {
         @Override
         public Object process(Object target, Object value, Object processor)
         {
            return build((WebTarget)target, value, processor);
         }
      }, target, param);
   }

   @Override
   public void process(ClientInvocationBuilder invocation, Object param)
   {
      process(new Process()  {
         @Override
         public Object process(Object target, Object value, Object processor)
         {
            processParam((ClientInvocationBuilder)target, value, processor);
            return target;
         }
      }, invocation, param);
   }


   protected Object process(Process process, Object target, Object param)
   {
      if (param == null) return target;

      for (Map.Entry<Field, Object> entry : fieldMap.entrySet())
      {

         try
         {
            Object val = entry.getKey().get(param);
            Object proc = entry.getValue();
            target = process.process(target, val, proc);
         }
         catch (IllegalAccessException e)
         {
            throw new LoggableFailure(e);
         }
      }
      for (GetterMethod getter : getters)
      {
         Object val = null;
         try
         {
            val = getter.method.invoke(param);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
         catch (InvocationTargetException e)
         {
            throw new RuntimeException(e);
         }
         Object proc = getter.processor;
         target = process.process(target, val, proc);
      }
      return target;
   }

   private WebTarget build(WebTarget target, Object val, Object proc)
   {
      if (proc instanceof WebTargetProcessor)
      {
         WebTargetProcessor processor = (WebTargetProcessor) proc;
         target = processor.build(target, val);
      }
      return target;
   }

   private void processParam(ClientInvocationBuilder invocation, Object val, Object proc)
   {
      if (proc instanceof InvocationProcessor)
      {
         InvocationProcessor processor = (InvocationProcessor) proc;
         processor.process(invocation, val);
      }
   }
}