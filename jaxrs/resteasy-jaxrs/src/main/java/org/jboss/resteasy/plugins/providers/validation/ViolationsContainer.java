package org.jboss.resteasy.plugins.providers.validation;

import org.hibernate.validator.method.MethodConstraintViolation;

import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 6, 2012
 */
public class ViolationsContainer<T> implements Serializable
{
   private static final long serialVersionUID = -5048958457582876197L;
   
   private Set<ConstraintViolation<T>> fieldViolations       = new HashSet<ConstraintViolation<T>>();
   private Set<ConstraintViolation<T>> propertyViolations    = new HashSet<ConstraintViolation<T>>();
   private Set<ConstraintViolation<T>> classViolations       = new HashSet<ConstraintViolation<T>>();
   private Set<ConstraintViolation<T>> parameterViolations   = new HashSet<ConstraintViolation<T>>();
   private Set<ConstraintViolation<T>> returnValueViolations = new HashSet<ConstraintViolation<T>>();
   
   private enum ConstraintType {CLASS, FIELD, PROPERTY, PARAMETER, RETURN_VALUE};
   
   public ViolationsContainer()
   {   
   }
   
   public ViolationsContainer(Set<ConstraintViolation<T>> set)
   {
      addViolations(set);
   }
   
   public ViolationsContainer(String s)
   {
      
   }
   
   public void addViolations(Set<? extends ConstraintViolation<T>> set)
   {
      Iterator<? extends ConstraintViolation<T>> it = set.iterator();
      while (it.hasNext())
      {
         ConstraintViolation<T> violation = it.next();
         switch (getConstraintType(violation))
         {
            case FIELD:
               fieldViolations.add(violation);
               break;

            case PROPERTY:
               propertyViolations.add(violation);
               break;

            case CLASS:
               classViolations.add(violation);
               break;

            case PARAMETER:
               parameterViolations.add(violation);
               break;

            case RETURN_VALUE:
               returnValueViolations.add(violation);
               break;
         }
      }
   }
   
   public void addFieldViolation(ConstraintViolation<T> v)
   {
      fieldViolations.add(v);
   }
   
   public void addPropertyViolation(ConstraintViolation<T> v)
   {
      propertyViolations.add(v);
   }
   
   public void addClassViolation(ConstraintViolation<T> v)
   {
      classViolations.add(v);
   }
   
   public void addParameterViolation(ConstraintViolation<T> v)
   {
      parameterViolations.add(v);
   }
   
   public void addReturnValueViolation(ConstraintViolation<T> v)
   {
      returnValueViolations.add(v);
   }
   
   public Set<ConstraintViolation<T>> getFieldViolations()
   {
      return fieldViolations;
   }
   
   public Set<ConstraintViolation<T>> getPropertyViolations()
   {
      return propertyViolations;
   }
   
   public Set<ConstraintViolation<T>> getClassViolations()
   {
      return classViolations;
   }
   
   public Set<ConstraintViolation<T>> getParameterViolations()
   {
      return parameterViolations;
   }
   
   public Set<ConstraintViolation<T>> getReturnValueViolations()
   {
      return returnValueViolations;
   }
   
   public int size()
   {
      return fieldViolations.size() +
             propertyViolations.size() +
             classViolations.size() + 
             parameterViolations.size() +
             returnValueViolations.size();
   }
   
   public String toString()
   {
      StringBuffer sb = setToStringBuffer(fieldViolations);
      sb.append(setToStringBuffer(propertyViolations));
      sb.append(setToStringBuffer(classViolations));
      sb.append(setToStringBuffer(parameterViolations));
      sb.append(setToStringBuffer(returnValueViolations));
      return sb.toString();
   }
   
   private StringBuffer setToStringBuffer(Set<ConstraintViolation<T>> set)
   {
      StringBuffer sb = new StringBuffer();
      Iterator<ConstraintViolation<T>> it = set.iterator();
      while (it.hasNext())
      {
         sb.append(it.next().toString()).append('\r');
      }
      return sb;
   }
   
   private ConstraintType getConstraintType(ConstraintViolation<T> v)
   {
      if (v instanceof MethodConstraintViolation)
      {
         MethodConstraintViolation<?> mv = MethodConstraintViolation.class.cast(v);
         return mv.getKind() == MethodConstraintViolation.Kind.PARAMETER ? ConstraintType.PARAMETER : ConstraintType.RETURN_VALUE;
      }
      
      Object o = v.getRootBean();
      Class<?> containingClass = v.getRootBeanClass();
      String fieldName = null;
      Field field = null;
      Iterator<Node>it = v.getPropertyPath().iterator();
      while (it.hasNext())
      {
         Node node = it.next();
         fieldName = node.getName();
         if (fieldName == null)
         {
            return ConstraintType.CLASS;
         }
         try
         {
            o = unwrapCompoundObject(o, node);
            containingClass = o.getClass();
            field = getField(containingClass, fieldName);
            field.setAccessible(true);
            o = field.get(o);
         }
         catch (NoSuchFieldException e)
         {
            throw new RuntimeException("Missing field", e);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException("Unable to access " + fieldName, e);
         }
      }
      String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
      try
      {
         getMethod(containingClass, getterName);
         return ConstraintType.PROPERTY;
      }
      catch (NoSuchMethodException e)
      {
         return ConstraintType.FIELD;
      }
   }
   
   private Object unwrapCompoundObject(Object o, Node node)
   {
      Class<?> clazz = o.getClass();
      if (Map.class.isAssignableFrom(clazz))
      {
         o = Map.class.cast(o).get(node.getKey());
      }
      else if (Iterable.class.isAssignableFrom(clazz))
      {
         o = List.class.cast(o).get(node.getIndex());
      }
      else if (clazz.isArray())
      {
         o = Array.get(o, node.getIndex());
      }
      return o;
   }
   
   private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException
   {

      Field field = null;
      try
      {
         field = clazz.getDeclaredField(fieldName);
      }
      catch (NoSuchFieldException e)
      {
         // Ignore.
      }
      while (field == null)
      {
         clazz = clazz.getSuperclass();
         if (clazz == null)
         {
            break;
         }
         try
         {
            field = clazz.getDeclaredField(fieldName);
         }
         catch (NoSuchFieldException e)
         {
            // Ignore.
         }
      }
      if (field == null)
      {
         throw new NoSuchFieldException(fieldName);
      }
      return field;
   }
   
   private Method getMethod(Class<?> clazz, String methodName) throws NoSuchMethodException
   {
      Method method = null;
      try
      {
         method = clazz.getDeclaredMethod(methodName);
      }
      catch (NoSuchMethodException e)
      {
         // Ignore.
      }
      while (method == null)
      {
         clazz = clazz.getSuperclass();
         if (clazz == null)
         {
            break;
         }
         try
         {
            method = clazz.getDeclaredMethod(methodName);
         }
         catch (NoSuchMethodException e)
         {
            // Ignore.
         }
      }
      if (method == null)
      {
         throw new NoSuchMethodException(methodName);
      }
      return method;
   }
}
