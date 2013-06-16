package org.jboss.resteasy.plugins.validation.hibernate;

import java.lang.reflect.Method;
import java.util.Iterator;

import javax.validation.ConstraintViolation;
import javax.validation.Path.Node;

import org.hibernate.validator.method.MethodConstraintViolation;
import org.jboss.resteasy.api.validation.ConstraintType.Type;
import org.jboss.resteasy.plugins.providers.validation.ConstraintTypeUtil;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 6, 2012
 */
public class ConstraintTypeUtil10 implements ConstraintTypeUtil
{
   public Type getConstraintType(Object o)
   {
      if (!(o instanceof ConstraintViolation))
      {
         throw new RuntimeException("unknown object passed as constraint violation: " + o);
      }
      ConstraintViolation<?> v = ConstraintViolation.class.cast(o);
      if (v instanceof MethodConstraintViolation)
      {
         MethodConstraintViolation<?> mv = MethodConstraintViolation.class.cast(v);
         return mv.getKind() == MethodConstraintViolation.Kind.PARAMETER ? Type.PARAMETER : Type.RETURN_VALUE;
      }
      
      Object b = v.getRootBean();
      String fieldName = null;
      Iterator<Node>it = v.getPropertyPath().iterator();
      while (it.hasNext())
      {
         Node node = it.next();
         fieldName = node.getName();
         if (fieldName == null)
         {
            return Type.CLASS;
         }
      }
      String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
      try
      {
         getMethod(v.getLeafBean().getClass(), getterName);
         return Type.PROPERTY;
      }
      catch (NoSuchMethodException e)
      {
         return Type.FIELD;
      }
   }
   
   private static Method getMethod(Class<?> clazz, String methodName) throws NoSuchMethodException
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
