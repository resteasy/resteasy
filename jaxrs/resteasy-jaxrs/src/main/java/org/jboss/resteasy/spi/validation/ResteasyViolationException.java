package org.jboss.resteasy.spi.validation;

import org.jboss.resteasy.plugins.providers.validation.ViolationsContainer;

import javax.validation.ConstraintViolation;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 6, 2012
 * 
 * @TODO Need to work on representation of exceptions
 * @TODO Add javadoc.
 */
public class ResteasyViolationException extends WebApplicationException
{
   private static final long serialVersionUID = 2623733139912277260L;
   
   @SuppressWarnings("rawtypes")
   private ViolationsContainer container;
   
   private List<String> allExceptions;
   
   private List<String> fieldViolations       = new ArrayList<String>();
   private List<String> propertyViolations    = new ArrayList<String>();
   private List<String> classViolations       = new ArrayList<String>();
   private List<String> parameterViolations   = new ArrayList<String>();
   private List<String> returnValueViolations = new ArrayList<String>();
   private List<List<String>> allViolations;
   
   public ResteasyViolationException()
   {
      super(500);
   }

   public ResteasyViolationException(ViolationsContainer<?> container)
   {
      super(500);
      this.container = container;
   }
   
   public void setExceptions(List<String> exceptions)
   {
      this.allExceptions = exceptions;
   }
   
   public List<String> getExceptions()
   {
      if (allExceptions == null)
      {
         allExceptions = new ArrayList<String>();
         allExceptions.addAll(fieldViolations);
         allExceptions.addAll(propertyViolations);
         allExceptions.addAll(classViolations);
         allExceptions.addAll(parameterViolations);
         allExceptions.addAll(returnValueViolations);
      }
      return allExceptions;
   }
   
   public List<String> getFieldViolations()
   {
      if (size() == 0)
      {
         convertToStrings();
      }
      return fieldViolations;
   }
   
   public List<String> getPropertyViolations()
   {
      if (size() == 0)
      {
         convertToStrings();
      }
      return propertyViolations;
   }
   
   public List<String> getClassViolations()
   {
      if (size() == 0)
      {
         convertToStrings();
      }
      return classViolations;
   }
   
   public List<String> getParameterViolations()
   {
      if (size() == 0)
      {
         convertToStrings();
      }
      return parameterViolations;
   }
   
   public List<String> getReturnValueViolations()
   {
      if (size() == 0)
      {
         convertToStrings();
      }
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
   
   public List<List<String>> getStrings()
   {
      if (size() == 0)
      {
         convertToStrings();
      }
      return allViolations;
   }
   
   @SuppressWarnings("rawtypes")
   protected ViolationsContainer getViolationsContainer()
   {
      return container;
   }
   
   @SuppressWarnings("rawtypes")
   protected void setViolationsContainer(ViolationsContainer container)
   {
      this.container = container;
   }
   
   @SuppressWarnings("rawtypes")
   protected void convertToStrings()
   {
      if (allViolations != null)
      {
         return;
      }
      allViolations = new ArrayList<List<String>>();
      Iterator it = container.getFieldViolations().iterator();
      while (it.hasNext())
      {
         ConstraintViolation cv = (ConstraintViolation) it.next();
         fieldViolations.add("field " + cv.getPropertyPath() + ": " + cv.getMessage() + ": " + cv.getInvalidValue().toString());
      }
      
      it = container.getPropertyViolations().iterator();
      while (it.hasNext())
      {
         ConstraintViolation cv = (ConstraintViolation) it.next();
         propertyViolations.add("property " + cv.getPropertyPath() + ": " + cv.getMessage() + ": " + cv.getInvalidValue().toString());
      }
      
      it = container.getClassViolations().iterator();
      while (it.hasNext())
      {
         ConstraintViolation cv = (ConstraintViolation) it.next();
         classViolations.add(cv.getMessage() + ": " + cv.getInvalidValue().toString());
      }
      
      it = container.getParameterViolations().iterator();
      while (it.hasNext())
      {
         ConstraintViolation cv = (ConstraintViolation) it.next();
         parameterViolations.add("parameter " + cv.getPropertyPath() + ": " + cv.getMessage() + ": " + cv.getInvalidValue().toString());
      }
      
      it = container.getReturnValueViolations().iterator();
      while (it.hasNext())
      {
         ConstraintViolation cv = (ConstraintViolation) it.next();
         returnValueViolations.add("return value: " + cv.getMessage() + ": " + cv.getInvalidValue().toString());
      }
      allViolations.add(fieldViolations);
      allViolations.add(propertyViolations);
      allViolations.add(classViolations);
      allViolations.add(parameterViolations);
      allViolations.add(returnValueViolations);
   }
   
   protected String expandDelimiter(String s)
   {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < s.length(); i++)
      {
         sb.append(s.charAt(i));
         if (s.charAt(i) == ':')
         {
            sb.append(':');
         }
      }
      return sb.toString();
   }
   
   protected String contractDelimiter(String s)
   {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < s.length(); i++)
      {
         sb.append(s.charAt(i));
         if (s.charAt(i) == ':' && s.charAt(i + 1) == ':')
         {
            i++;
         }
      }
      return sb.toString();
   }
}
