package org.jboss.resteasy.plugins.providers.validation;

import java.lang.reflect.Method;
import java.util.Iterator;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path.Node;

import org.jboss.resteasy.api.validation.ConstraintType;
import org.jboss.resteasy.plugins.providers.validation.ConstraintTypeUtil;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 6, 2012
 */
public class ConstraintTypeUtil11 implements ConstraintTypeUtil
{
   public ConstraintType.Type getConstraintType(Object o)
   {
      if (!(o instanceof ConstraintViolation))
      {
         throw new RuntimeException(Messages.MESSAGES.unknownObjectPassedAsConstraintViolation(o));
      }
      ConstraintViolation<?> v = ConstraintViolation.class.cast(o);
      
      Iterator<Node> nodes = v.getPropertyPath().iterator();
      Node firstNode = nodes.next();
      if (firstNode.getKind() == ElementKind.METHOD)
      {
         Node secondNode = nodes.next();
         
         if (secondNode.getKind() == ElementKind.PARAMETER ||
             secondNode.getKind() == ElementKind.CROSS_PARAMETER)
         {
            return ConstraintType.Type.PARAMETER;
         }
         else if (secondNode.getKind() == ElementKind.RETURN_VALUE)
         {
            return ConstraintType.Type.RETURN_VALUE;
         }
         else
         {
            throw new RuntimeException(Messages.MESSAGES.unexpectedPathNodeViolation(secondNode.getKind()));
         }
      }

      if (firstNode.getKind() == ElementKind.BEAN)
      {
         return ConstraintType.Type.CLASS;
      }
      
      if (firstNode.getKind() == ElementKind.PROPERTY)
      {
         String fieldName = firstNode.getName();
         try
         {
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            getMethod(v.getRootBeanClass(), getterName);
            return ConstraintType.Type.PROPERTY;
         }
         catch (NoSuchMethodException e)
         {
            try
            {
               String getterName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
               Method m = getMethod(v.getLeafBean().getClass(), getterName);
               if (m.getReturnType().equals(boolean.class))
                {
                  return ConstraintType.Type.PROPERTY;
                }
               else
               {
                  return ConstraintType.Type.FIELD;
               }
            }
            catch (NoSuchMethodException e1)
            {
               return ConstraintType.Type.FIELD;
            }
         }
      }
      
      throw new RuntimeException(Messages.MESSAGES.unexpectedPathNode(firstNode.getKind()));
   }
   
   private static Method getMethod(Class<?> clazz, String methodName) throws NoSuchMethodException
   {
      Method method = null;
      method = checkMethodAccess(clazz, methodName);

      while (method == null)
      {
         clazz = clazz.getSuperclass();
         if (clazz == null)
         {
            break;
         }

         method = checkMethodAccess(clazz, methodName);
      }
      if (method == null)
      {
         throw new NoSuchMethodException(methodName);
      }
      return method;
   }

   private static Method checkMethodAccess(final Class<?> clazz, final String methodName) {
      Method method = null;
      try
      {
         if (System.getSecurityManager() == null)
         {
            method = clazz.getDeclaredMethod(methodName);
         }
         else
         {
            method = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
               @Override
               public Method run() throws Exception {
                  return clazz.getDeclaredMethod(methodName);
               }
            });
         }
      }
      catch(PrivilegedActionException pae) {
      }
      catch (NoSuchMethodException e)
      {
         // Ignore.
      }

      return method;
   }
}
