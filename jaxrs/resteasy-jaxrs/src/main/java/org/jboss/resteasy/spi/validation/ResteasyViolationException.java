package org.jboss.resteasy.spi.validation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.ValidationException;

import org.jboss.resteasy.plugins.providers.validation.ViolationsContainer;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 6, 2012
 * 
 * @TODO Need to work on representation of exceptions
 * @TODO Add javadoc.
 */
public class ResteasyViolationException extends ValidationException
{  
   private static final long serialVersionUID = 2623733139912277260L;
   
   private Exception exception;
   
   private List<ResteasyConstraintViolation> fieldViolations       = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> propertyViolations    = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> classViolations       = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> parameterViolations   = new ArrayList<ResteasyConstraintViolation>();
   private List<ResteasyConstraintViolation> returnValueViolations = new ArrayList<ResteasyConstraintViolation>();
   
   private List<ResteasyConstraintViolation> allViolations; 
   private List<List<ResteasyConstraintViolation>> violationLists;
   
   public ResteasyViolationException(ViolationsContainer<?> container)
   {
      convertToStrings(container);
   }
   
   public Exception getException()
   {
      return exception;
   }

   public void setException(Exception exception)
   {
      this.exception = exception;
   }

   public List<ResteasyConstraintViolation> getViolations()
   {
      if (allViolations == null)
      {
         allViolations = new ArrayList<ResteasyConstraintViolation>();
         allViolations.addAll(fieldViolations);
         allViolations.addAll(propertyViolations);
         allViolations.addAll(classViolations);
         allViolations.addAll(parameterViolations);
         allViolations.addAll(returnValueViolations);
      }
      return allViolations;
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
      return getViolations().size();
   }
   
   public List<List<ResteasyConstraintViolation>> getViolationLists()
   {
      return violationLists;
   }
   
   public String toString()
   {
      StringBuffer sb = new StringBuffer();
      for (Iterator<List<ResteasyConstraintViolation>> it = violationLists.iterator(); it.hasNext(); )
      {
         List<ResteasyConstraintViolation> violations = it.next();
         for (Iterator<ResteasyConstraintViolation> it2 = violations.iterator(); it2.hasNext(); )
         {
            sb.append(it2.next().toString()).append('\r');
         }
      }
      return sb.toString();
   }
   
   @SuppressWarnings("rawtypes")
   protected void convertToStrings(ViolationsContainer container)
   {
      if (violationLists != null)
      {
         return;
      }
      violationLists = new ArrayList<List<ResteasyConstraintViolation>>();
      fieldViolations = container.getFieldViolations();
      propertyViolations = container.getPropertyViolations();
      classViolations = container.getClassViolations();
      parameterViolations = container.getParameterViolations();
      returnValueViolations = container.getReturnValueViolations();

      violationLists.add(fieldViolations);
      violationLists.add(propertyViolations);
      violationLists.add(classViolations);
      violationLists.add(parameterViolations);
      violationLists.add(returnValueViolations);
   }

   protected String convertArrayToString(Object o)
   {
      String result = null;
      if (o instanceof Object[])
      {
         Object[] array = Object[].class.cast(o);
         StringBuffer sb = new StringBuffer("[").append(convertArrayToString(array[0]));
         for (int i = 1; i < array.length; i++)
         {
            sb.append(", ").append(convertArrayToString(array[i]));
         }
         sb.append("]");
         result = sb.toString();
      }
      else
      {
         result = o.toString();
      }
      return result;
   }
}
