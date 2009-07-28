package org.jboss.fastjaxb;

import org.jboss.fastjaxb.util.Types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by IntelliJ IDEA.
 * User: monica_scalpato
 * Date: Jul 24, 2009
 * Time: 9:55:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class Property
{
   protected Method setter;
   protected Method getter;
   protected String name;
   protected Class baseType;


   public Property()
   {
   }

   public Property(String name)
   {
      this.name = name;
   }

   public Property clone()
   {
      Property p = new Property();
      p.setter = this.setter;
      p.getter = this.getter;
      p.name = this.name;
      p.baseType = this.baseType;
      return p;
   }

   public Method getGetter()
   {
      return getter;
   }

   public Method getSetter()
   {
      return setter;
   }

   public void setSetter(Method setter)
   {
      this.setter = setter;
   }

   public void setGetter(Method getter)
   {
      this.getter = getter;
   }

   public String getName()
   {
      return name;
   }

   public Class<?> getType()
   {
      if (getter != null) return getter.getReturnType();
      else if (setter != null) return setter.getParameterTypes()[0];
      throw new RuntimeException("No type information!!");
   }

   public Type getGenericType()
   {
      if (getter != null) return getter.getGenericReturnType();
      else if (setter != null) return setter.getGenericParameterTypes()[0];
      throw new RuntimeException("No type information!!");
   }

   public Class<?> getBaseType()
   {
      if (baseType != null) return baseType;
      Class ct = getType();
      Type gt = getGenericType();
      try
      {
         baseType = Types.getCollectionBaseType(ct, gt);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Failed to get base type for property: " + name);
      }
      if (baseType == null) baseType = ct;
      return baseType;
   }

   public void setBaseType(Class baseType)
   {
      this.baseType = baseType;
   }

   public <T extends Annotation> T getAnnotation(Class<T> annotation)
   {
      if (getter != null)
      {
         T ann = getter.getAnnotation(annotation);
         if (ann != null) return ann;
      }
      if (setter != null)
      {
         T ann = setter.getAnnotation(annotation);
         if (ann != null) return ann;
      }
      return null;
   }

   public boolean isAnnotationPresent(java.lang.Class<? extends Annotation> annotation)
   {
      if (getter != null)
      {
         if (getter.isAnnotationPresent(annotation)) return true;
      }
      if (setter != null)
      {
         if (setter.isAnnotationPresent(annotation)) return true;
      }
      return false;
   }
}
