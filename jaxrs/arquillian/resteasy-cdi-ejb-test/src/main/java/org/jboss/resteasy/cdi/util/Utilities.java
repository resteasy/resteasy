package org.jboss.resteasy.cdi.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.jboss.resteasy.cdi.extension.bean.Boston;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 30, 2012
 */
@Singleton
public class Utilities
{
   @Inject private BeanManager beanManager;
   
   public boolean isApplicationScoped(Class<?> c)
   {
      return testScope(c, ApplicationScoped.class);
   }

   public boolean isDependentScoped(Class<?> c)
   {
      return testScope(c, Dependent.class);
   }
   
   public boolean isRequestScoped(Class<?> c)
   {
      return testScope(c, RequestScoped.class);
   }
   
   public boolean isSessionScoped(Class<?> c)
   {
      return testScope(c, SessionScoped.class);
   }
   
   public boolean testScope(Class<?> c, Class<?> scopeClass)
   {
      Class<? extends Annotation> annotation = getScope(c);
      if (annotation == null)
      {
         return false;
      }
      return annotation.isAssignableFrom(scopeClass);
   }

   public Class<? extends Annotation> getScope(Class<?> c)
   {
//      Set<Bean<?>> allBeans = beanManager.getBeans(Object.class);
////      System.out.println(allBeans);
////      Iterator<Bean<?>> it2 = allBeans.iterator();
////      while (it2.hasNext())
////      {
////         System.out.println(it2.next());
////      }
//      Iterator<Bean<?>> it = allBeans.iterator();
//      while (it.hasNext())
//      {
//         Bean<?> b = it.next();
//         if (c.equals(b.getBeanClass()))
//         {
//            return b.getScope();
//         }
//      }
//      return null;
      
      Set<Bean<?>> beans = beanManager.getBeans(c);
      if (beans != null && !beans.isEmpty())
      {
         Iterator<Bean<?>> it = beans.iterator();
         if (it.hasNext())
         {
            Bean<?> bean = beans.iterator().next();
            return bean.getScope();
         }
      }
//      for (Annotation annotation: c.getAnnotations())
//      {
//         if (beanManager.isScope(annotation.annotationType()))
//         {
//            return annotation.annotationType();
//         }
//      }
      return null;
   }
   
   static public Class<? extends Annotation> getScopeAnnotation(Class<?> c)
   {

      return getScopeAnnotation(c.getAnnotations());
   }
   
   static public Class<? extends Annotation> getScopeAnnotation(Annotation[] annotations)
   {
      for (int i = 0; i < annotations.length; i++)
      {
         Class<? extends Annotation> annotationType = annotations[i].annotationType();
         Annotation[] typeAnnotations = annotationType.getAnnotations();
         for (int j = 0; j < typeAnnotations.length; j++)
         {
            if (NormalScope.class.equals(typeAnnotations[j].annotationType()))
            {
               return annotationType;
            }
         }
      }
      return null;
   }
   
   static public Class<? extends Annotation> getScopeAnnotation(Set<Annotation> annotations)
   {
      for (Annotation annotation : annotations)
      {
         Class<? extends Annotation> annotationType = annotation.annotationType();
         Annotation[] typeAnnotations = annotationType.getAnnotations();
         for (int j = 0; j < typeAnnotations.length; j++)
         {
            if (NormalScope.class.equals(typeAnnotations[j].annotationType()))
            {
               return annotationType;
            }
         }
      }
      return null;
   }
   
   static public Set<Annotation> getQualifiers(Class<?> clazz)
   {
      return getQualifiers(clazz.getAnnotations());
   }
   
   static public Set<Annotation> getQualifiers(Annotation[] annotations)
   {
      HashSet<Annotation> result = new HashSet<Annotation>();
      for (int i = 0; i < annotations.length; i++)
      {
         Class<?> annotationType = annotations[i].annotationType();
         Annotation[] typeAnnotations = annotationType.getAnnotations();
         for (int j = 0; j < typeAnnotations.length; j++)
         {
            if (Qualifier.class.equals(typeAnnotations[j].annotationType()))
            {
               result.add(annotations[i]);
               break;
            }
         }
      }
      return result;
   }
   
   static public boolean hasQualifier(Class<?> clazz, Class<?> qualifier)
   {
      Annotation[] annotations = clazz.getAnnotations();
      for (int i = 0; i < annotations.length; i++)
      {
         if (qualifier.equals(annotations[i].annotationType()))
         {
            return true;
         }
      }
      return false;
   }
   
   static public boolean hasQualifier(Set<Annotation> annotations, Class<?> qualifier)
   {
      for (Annotation annotation: annotations)
      {
         if (qualifier.equals(annotation.annotationType()))
         {
            return true;
         }
      }
      return false;
   }
   
   static public boolean isBoston(Class<?> clazz)
   {
      return hasQualifier(clazz, Boston.class);
   }
   
   static public Set<Type> getTypeClosure(Class<?> clazz)
   {
      HashSet<Type> set = new HashSet<Type>();
      accumulateTypes(set, clazz);
      return set;
   }
   
   static void accumulateTypes(Set<Type> set, Class<?> clazz)
   {
      set.add(clazz);
      if (clazz.getSuperclass() != null)
      {
         accumulateTypes(set, clazz.getSuperclass());
      }
      for (Class<?> c : clazz.getInterfaces())
      {
         accumulateTypes(set, c);
      }
   }
   
   static public Set<Annotation> getAnnotationSet(Class<?> clazz)
   {
      return new HashSet<Annotation>(Arrays.asList(clazz.getAnnotations()));
   }
   
   static public boolean isAnnotationPresent(Class<?> clazz, Class<? extends Annotation> annotationType)
   {
      for (Annotation annotation : clazz.getAnnotations())
      {
         if (annotation.annotationType().equals(annotationType))
         {
            return true;
         }
      }
      return false;
   }

   @SuppressWarnings("unchecked")
   static public <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType)
   {
      for (Annotation annotation : clazz.getAnnotations())
      {
         if (annotation.annotationType().equals(annotationType))
         {
            return (T) annotation;
         }
      }
      return null;
   }
}
