package org.jboss.resteasy.validation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 6, 2012
 */
public class ViolationsContainer implements Serializable
{
   private static final long serialVersionUID = -5048958457582876197L;
   
   private Set<ConstraintViolation<?>> fieldViolations       = new HashSet<ConstraintViolation<?>>();
   private Set<ConstraintViolation<?>> propertyViolations    = new HashSet<ConstraintViolation<?>>();
   private Set<ConstraintViolation<?>> classViolations       = new HashSet<ConstraintViolation<?>>();
   private Set<ConstraintViolation<?>> parameterViolations   = new HashSet<ConstraintViolation<?>>();
   private Set<ConstraintViolation<?>> returnValueViolations = new HashSet<ConstraintViolation<?>>();
   
   private Exception exception;
   
   private enum ConstraintType {CLASS, FIELD, PROPERTY, PARAMETER, RETURN_VALUE};
   
   public ViolationsContainer()
   {   
   }
   
   public ViolationsContainer(Set<ConstraintViolation<Object>> set)
   {
      addViolations(set);
   }
   
   public ViolationsContainer(String s)
   {  
   }
   
   public Exception getException()
   {
      return exception;
   }

   public void setException(Exception exception)
   {
      this.exception = exception;
   }

   public void addViolations(Set<? extends ConstraintViolation<?>> set)
   {
      if (set == null)
      {
         return;
      }
      
      Iterator<? extends ConstraintViolation<?>> it = set.iterator();
      while (it.hasNext())
      {
         ConstraintViolation<?> violation = it.next();
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
   
   public void addFieldViolation(ConstraintViolation<?> v)
   {
      fieldViolations.add(v);
   }
   
   public void addPropertyViolation(ConstraintViolation<?> v)
   {
      propertyViolations.add(v);
   }
   
   public void addClassViolation(ConstraintViolation<?> v)
   {
      classViolations.add(v);
   }
   
   public void addParameterViolation(ConstraintViolation<?> v)
   {
      parameterViolations.add(v);
   }
   
   public void addReturnValueViolation(ConstraintViolation<?> v)
   {
      returnValueViolations.add(v);
   }
   
   public Set<ConstraintViolation<?>> getFieldViolations()
   {
      return fieldViolations;
   }
   
   public Set<ConstraintViolation<?>> getPropertyViolations()
   {
      return propertyViolations;
   }
   
   public Set<ConstraintViolation<?>> getClassViolations()
   {
      return classViolations;
   }
   
   public Set<ConstraintViolation<?>> getParameterViolations()
   {
      return parameterViolations;
   }
   
   public Set<ConstraintViolation<?>> getReturnValueViolations()
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
   
   private StringBuffer setToStringBuffer(Set<ConstraintViolation<?>> set)
   {
      StringBuffer sb = new StringBuffer();
      Iterator<ConstraintViolation<?>> it = set.iterator();
      while (it.hasNext())
      {
         sb.append(it.next().toString()).append('\r');
      }
      return sb;
   }
   
   private ConstraintType getConstraintType(ConstraintViolation<?> v)
   {
//      if (v instanceof MethodConstraintViolation)
//      {
//         MethodConstraintViolation<?> mv = MethodConstraintViolation.class.cast(v);
//         return mv.getKind() == MethodConstraintViolation.Kind.PARAMETER ? ConstraintType.PARAMETER : ConstraintType.RETURN_VALUE;
//      }
      if (v.getExecutableReturnValue() != null)
      {
         return ConstraintType.RETURN_VALUE;
      }
      if (v.getExecutableParameters() != null)
      {
         return ConstraintType.PARAMETER;
      }
//      Object o = v.getRootBean();
//      Class<?> containingClass = getRepresentedClass(v.getRootBeanClass(), o);
      String fieldName = null;
//      Field field = null;
      Iterator<Node>it = v.getPropertyPath().iterator();
      while (it.hasNext())
      {
         Node node = it.next();
         fieldName = node.getName();
         if (fieldName == null)
         {
            return ConstraintType.CLASS;
         }
//         try
//         {
//            o = unwrapCompoundObject(o, node);
//            containingClass = getRepresentedClass(o.getClass(), o);
//            field = getField(containingClass, fieldName);
//            field.setAccessible(true);
//            o = getTargetInstance(o);
//            if (o != null)
//            {
//               o = field.get(o);
//            }
//         }
//         catch (NoSuchFieldException e)
//         {
//            // Could be a CDI proxy.
//            if (!containingClass.equals(o.getClass()))
//            {
//               break;
//            }
//            throw new RuntimeException("Missing field", e);
//         }
//         catch (IllegalAccessException e)
//         {
//            throw new RuntimeException("Unable to access " + fieldName, e);
//         }
      }
      String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
      try
      {
         getMethod(v.getLeafBean().getClass(), getterName);
         return ConstraintType.PROPERTY;
      }
      catch (NoSuchMethodException e)
      {
         return ConstraintType.FIELD;
      }
   }
   
//   private Object unwrapCompoundObject(Object o, Node node)
//   {
//      Class<?> clazz = o.getClass();
//      if (Map.class.isAssignableFrom(clazz))
//      {
//         o = Map.class.cast(o).get(node.getKey());
//      }
//      else if (Iterable.class.isAssignableFrom(clazz))
//      {
//         o = List.class.cast(o).get(node.getIndex());
//      }
//      else if (clazz.isArray())
//      {
//         o = Array.get(o, node.getIndex());
//      }
//      return o;
//   }
//   
//   private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException
//   {
//
//      Field field = null;
//      try
//      {
//         field = clazz.getDeclaredField(fieldName);
//      }
//      catch (NoSuchFieldException e)
//      {
//         // Ignore.
//      }
//      while (field == null)
//      {
//         clazz = clazz.getSuperclass();
//         if (clazz == null)
//         {
//            break;
//         }
//         try
//         {
//            field = clazz.getDeclaredField(fieldName);
//         }
//         catch (NoSuchFieldException e)
//         {
//            // Ignore.
//         }
//      }
//      if (field == null)
//      {
//         throw new NoSuchFieldException(fieldName);
//      }
//      return field;
//   }
   
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
   
//   private Class<?> getRepresentedClass(Class<?> clazz, Object target)
//   {
//      Method method;
//      try
//      {
//         method = clazz.getDeclaredMethod("getTargetClass");
//      }
//      catch (NoSuchMethodException e)
//      {
//         return clazz;
//      }
//      try
//      {
//         return Class.class.cast(method.invoke(target));
//      }
//      catch (Exception e)
//      {
//         return clazz;
//      }
//   }
//   
//   private Object getTargetInstance(Object target)
//   {
//      Method method;
//      try
//      {
//         method = target.getClass().getDeclaredMethod("getTargetInstance");
//      }
//      catch (NoSuchMethodException e)
//      {
//         return target;
//      }
//      try
//      {
//         return method.invoke(target);
//      }
//      catch (Exception e)
//      {
//         return target;
//      }
//   }
}
