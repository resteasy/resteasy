package org.jboss.resteasy.plugins.providers.validation;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path.Node;

import org.jboss.resteasy.validation.ResteasyConstraintViolation;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 6, 2012
 */
public class ViolationsContainer<T> implements Serializable
{
   private static final long serialVersionUID = -5048958457582876197L;
   
//   private Set<ResteasyConstraintViolation> fieldViolations       = new HashSet<ResteasyConstraintViolation>();
//   private Set<ResteasyConstraintViolation> propertyViolations    = new HashSet<ResteasyConstraintViolation>();
//   private Set<ResteasyConstraintViolation> classViolations       = new HashSet<ResteasyConstraintViolation>();
//   private Set<ResteasyConstraintViolation> parameterViolations   = new HashSet<ResteasyConstraintViolation>();
//   private Set<ResteasyConstraintViolation> returnValueViolations = new HashSet<ResteasyConstraintViolation>();

   private List<ResteasyConstraintViolation> fieldViolations       = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> propertyViolations    = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> classViolations       = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> parameterViolations   = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> returnValueViolations = new ArrayList<ResteasyConstraintViolation>();

   private enum ConstraintType {CLASS, FIELD, PROPERTY, PARAMETER, RETURN_VALUE};
   
   public ViolationsContainer()
   {   
   }
   
//   public ViolationsContainer(Set<ConstraintViolation<T>> set)
//   {
//      addViolations(set);
//   }
   
   public ViolationsContainer(String s)
   {
      
   }
   
   public ViolationsContainer(Set<ResteasyConstraintViolation> set)
   {
      addViolations(set);
   } 
   
//   public void addViolations(Set<? extends ConstraintViolation<T>> set)
//   {
//      Iterator<? extends ConstraintViolation<T>> it = set.iterator();
//      while (it.hasNext())
//      {
//         ConstraintViolation<T> violation = it.next();
//         switch (getConstraintType(violation))
//         {
//            case FIELD:
//               fieldViolations.add(violation);
//               break;
//
//            case PROPERTY:
//               propertyViolations.add(violation);
//               break;
//
//            case CLASS:
//               classViolations.add(violation);
//               break;
//
//            case PARAMETER:
//               parameterViolations.add(violation);
//               break;
//
//            case RETURN_VALUE:
//               returnValueViolations.add(violation);
//               break;
//         }
//      }
//   }
   
   public void addViolations(Set<? extends ResteasyConstraintViolation> set)
   {
    Iterator<? extends ResteasyConstraintViolation> it = set.iterator();
    while (it.hasNext())
    {
       ResteasyConstraintViolation violation = it.next();
       switch (violation.getConstraintType())
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
   
   public void addFieldViolation(ResteasyConstraintViolation v)
   {
      fieldViolations.add(v);
   }
   
   public void addPropertyViolation(ResteasyConstraintViolation v)
   {
      propertyViolations.add(v);
   }
   
   public void addClassViolation(ResteasyConstraintViolation v)
   {
      classViolations.add(v);
   }
   
   public void addParameterViolation(ResteasyConstraintViolation v)
   {
      parameterViolations.add(v);
   }
   
   public void addReturnValueViolation(ResteasyConstraintViolation v)
   {
      returnValueViolations.add(v);
   }
   
   public List<ResteasyConstraintViolation> getFieldViolations()
   {
      return fieldViolations;
   }
   
   public List<ResteasyConstraintViolation> getPropertyViolations()
   {
      return propertyViolations;
   }
   
   public List<ResteasyConstraintViolation> getClassViolations()
   {
      return classViolations;
   }
   
   public List<ResteasyConstraintViolation> getParameterViolations()
   {
      return parameterViolations;
   }
   
   public List<ResteasyConstraintViolation> getReturnValueViolations()
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
   
   private StringBuffer setToStringBuffer(List<ResteasyConstraintViolation> set)
   {
      StringBuffer sb = new StringBuffer();
      Iterator<ResteasyConstraintViolation> it = set.iterator();
      while (it.hasNext())
      {
         sb.append(it.next().toString()).append('\r');
      }
      return sb;
   }

   private ConstraintType getConstraintType(ConstraintViolation<T> v)
   {
      Node leafNode = getLeafNode(v);

      if (leafNode.getKind() == ElementKind.PARAMETER ||
         leafNode.getKind() == ElementKind.CROSS_PARAMETER)
      {
         return ConstraintType.PARAMETER;
      }
      else if (leafNode.getKind() == ElementKind.RETURN_VALUE)
      {
         return ConstraintType.RETURN_VALUE;
      }

      Object o = v.getRootBean();
      Class<?> containingClass = getRepresentedClass(v.getRootBeanClass(), o);
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
         getMethod(containingClass, getterName);
         return ConstraintType.PROPERTY;
      }
      catch (NoSuchMethodException e)
      {
         return ConstraintType.FIELD;
      }
   }
   
   private Node getLeafNode(ConstraintViolation<T> violation) {
      Iterator<Node> nodes = violation.getPropertyPath().iterator();
      Node leafNode = null;

      while(nodes.hasNext()) {
         leafNode = nodes.next();
      }

      return leafNode;
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
   
   private Class<?> getRepresentedClass(Class<?> clazz, Object target)
   {
      Method method;
      try
      {
         method = clazz.getDeclaredMethod("getTargetClass");
      }
      catch (NoSuchMethodException e)
      {
         return clazz;
      }
      try
      {
         return Class.class.cast(method.invoke(target));
      }
      catch (Exception e)
      {
         return clazz;
      }
   }
   
   private Object getTargetInstance(Object target)
   {
      Method method;
      try
      {
         method = target.getClass().getDeclaredMethod("getTargetInstance");
      }
      catch (NoSuchMethodException e)
      {
         return target;
      }
      try
      {
         return method.invoke(target);
      }
      catch (Exception e)
      {
         return target;
      }
   }
}
