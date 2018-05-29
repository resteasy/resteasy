package org.jboss.resteasy.core;

import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.MethodHashing;

import static org.jboss.resteasy.util.FindAnnotation.findAnnotation;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.PathParam;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PropertyInjectorImpl implements PropertyInjector
{
   protected HashMap<Field, ValueInjector> fieldMap = new HashMap<Field, ValueInjector>();

   private static class SetterMethod
   {
      private SetterMethod(Method method, ValueInjector extractor)
      {
         this.method = method;
         this.extractor = extractor;
      }

      public Method method;
      public ValueInjector extractor;
   }

   protected List<SetterMethod> setters = new ArrayList<SetterMethod>();
   protected HashMap<Long, Method> setterhashes = new HashMap<Long, Method>();
   protected Class<?> clazz;

   public PropertyInjectorImpl(Class<?> clazz, ResteasyProviderFactory factory)
   {
      this.clazz = clazz;

      populateMap(clazz, factory);
   }

   protected void populateMap(Class<?> clazz, ResteasyProviderFactory factory)
   {
      for (Field field : getDeclaredFields(clazz))
      {
         Annotation[] annotations = field.getAnnotations();
         if (annotations == null || annotations.length == 0) continue;
         Class<?> type = field.getType();
         Type genericType = field.getGenericType();

         ValueInjector extractor = getParameterExtractor(clazz, factory, field, field.getName(), annotations, type, genericType);
         if (extractor != null)
         {
            if (!Modifier.isPublic(field.getModifiers()))
            {
               setAccessible(field);
            }
            fieldMap.put(field, extractor);
         }
      }
      for (Method method : getDeclaredMethods(clazz))
      {
         if (!method.getName().startsWith("set")) continue;
         if (method.getParameterTypes().length != 1) continue;

         Annotation[] annotations = method.getAnnotations();
         if (annotations == null || annotations.length == 0) continue;

         Class<?> type = method.getParameterTypes()[0];
         Type genericType = method.getGenericParameterTypes()[0];

         String propertyName = Introspector.decapitalize(method.getName().substring(3));
         
         ValueInjector extractor = getParameterExtractor(clazz, factory, method, propertyName, annotations, type, genericType);
         if (extractor != null)
         {
            long hash = 0;
            try
            {
               hash = MethodHashing.methodHash(method);
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

            if (!Modifier.isPublic(method.getModifiers()))
            {
               setAccessible(method);
            }
            setters.add(new SetterMethod(method, extractor));
            setterhashes.put(hash, method);
         }

      }
      if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class))
         populateMap(clazz.getSuperclass(), factory);


   }

   private ValueInjector getParameterExtractor(Class<?> clazz, ResteasyProviderFactory factory, AccessibleObject accessibleObject,
                                               String defaultName, Annotation[] annotations, Class<?> type, Type genericType)
   {
      boolean extractBody = (FindAnnotation.findAnnotation(annotations, Body.class) != null);
      ValueInjector injector = factory.getInjectorFactory().createParameterExtractor(clazz, accessibleObject, defaultName, type, genericType,
              annotations, extractBody, factory);
      return injector;
   }

   public void inject(HttpRequest request, HttpResponse response, Object target) throws Failure
   {
      for (Map.Entry<Field, ValueInjector> entry : fieldMap.entrySet())
      {
         try
         {
            entry.getKey().set(target, entry.getValue().inject(request, response));
         }
         catch (IllegalAccessException e)
         {
            throw new InternalServerErrorException(e);
         }
      }
      for (SetterMethod setter : setters)
      {
         try
         {
            setter.method.invoke(target, setter.extractor.inject(request, response));
         }
         catch (IllegalAccessException e)
         {
            throw new InternalServerErrorException(e);
         }
         catch (InvocationTargetException e)
         {
            throw new ApplicationException(e.getCause());
         }
      }
   }

   public void inject(Object target)
   {
      for (Map.Entry<Field, ValueInjector> entry : fieldMap.entrySet())
      {
         try
         {
            entry.getKey().set(target, entry.getValue().inject());
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
      }
      for (SetterMethod setter : setters)
      {
         try
         {
            setter.method.invoke(target, setter.extractor.inject());
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
         catch (InvocationTargetException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   private Field[] getDeclaredFields(final Class<?> clazz)
   {
       final SecurityManager sm = System.getSecurityManager();
       if (sm != null)
       {
          return AccessController.doPrivileged(new PrivilegedAction<Field[]>()
          {
             @Override
             public Field[] run()
             {
                return clazz.getDeclaredFields();
             }
          });
       }
       return clazz.getDeclaredFields();
   }

   private Method[] getDeclaredMethods(final Class<?> clazz)
   {
      final SecurityManager sm = System.getSecurityManager();
      if (sm != null)
      {
         return AccessController.doPrivileged(new PrivilegedAction<Method[]>()
         {
            @Override
            public Method[] run()
            {
               return clazz.getDeclaredMethods();
            }
         });
      }
      return clazz.getDeclaredMethods();
   }

   private void setAccessible(final AccessibleObject member)
   {
      final SecurityManager sm = System.getSecurityManager();
      if (sm != null)
      {
         AccessController.doPrivileged(new PrivilegedAction<Void>()
         {
            @Override
            public Void run()
            {
               member.setAccessible(true);
               return null;
            }
         });
      }
      else
      {
         member.setAccessible(true);
      }
   }
}
