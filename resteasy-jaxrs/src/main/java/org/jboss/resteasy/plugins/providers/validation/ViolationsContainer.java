package org.jboss.resteasy.plugins.providers.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 6, 2012
 */
public class ViolationsContainer<T> implements Serializable
{
   private static final long serialVersionUID = -7895854137980651539L;
   
   private List<ResteasyConstraintViolation> fieldViolations       = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> propertyViolations    = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> classViolations       = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> parameterViolations   = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> returnValueViolations = new ArrayList<ResteasyConstraintViolation>();
   private Exception exception;
   private Object target;
   
   public ViolationsContainer(Object target)
   {
      this.target = target;
   }
   
   public ViolationsContainer(Set<ResteasyConstraintViolation> set)
   {
      addViolations(set);
   }
   
   public Exception getException()
   {
      return exception;
   }

   public void setException(Exception exception)
   {
      this.exception = exception;
   }
   
   public Object getTarget()
   {
      return target;
   }

   public void setTarget(Object target)
   {
      this.target = target;
   }

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
}
