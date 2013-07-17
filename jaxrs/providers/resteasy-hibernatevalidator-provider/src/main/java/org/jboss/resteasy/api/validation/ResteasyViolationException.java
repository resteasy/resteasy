package org.jboss.resteasy.api.validation;

import org.jboss.resteasy.plugins.providers.validation.ViolationsContainer;

import javax.validation.ValidationException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
      exception = container.getException();
   }
   
   public ResteasyViolationException(String stringRep)
   {
      convertFromString(stringRep);
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
   
   protected void convertFromString(String stringRep)
   {
      InputStream is = new ByteArrayInputStream(stringRep.getBytes());
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String line;
      try
      {
         int index = 0;
         line = br.readLine();
         while (line != null )
         {
//            int nextIndex = getField(index, line);
//            ConstraintType.Type type = ConstraintType.Type.valueOf(line.substring(++index, nextIndex));
//            index = nextIndex + 1;
//            nextIndex = getField(index, line);
//            String path = line.substring(++index, nextIndex);
//            index = nextIndex + 1;
//            nextIndex = getField(index, line);
//            String message = line.substring(++index, nextIndex);
//            index = nextIndex + 1;
//            nextIndex = getField(index, line);
//            String value = line.substring(++index, nextIndex);
            ConstraintType.Type type = ConstraintType.Type.valueOf(line.substring(1, line.length() - 1));
            line = br.readLine();
            String path = line.substring(1, line.length() - 1);
            line = br.readLine();
            String message = line.substring(1, line.length() - 1);
            line = br.readLine();
            String value = line.substring(1, line.length() - 1);
            ResteasyConstraintViolation rcv = new ResteasyConstraintViolation(type, path, message, value);
            
            switch (type)
            {
               case FIELD:
                  fieldViolations.add(rcv);
                  break;
                  
               case PROPERTY:
                  propertyViolations.add(rcv);
                  break;
                  
               case CLASS:
                  classViolations.add(rcv);
                  break;
                  
               case PARAMETER:
                  parameterViolations.add(rcv);
                  break;
                  
               case RETURN_VALUE:
                  returnValueViolations.add(rcv);
                  break;
                  
               default:
                  throw new RuntimeException("unexpected violation type: " + type);
            }
            index = 0;
            line = br.readLine(); // consume ending '\r'
            line = br.readLine();
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException("Unable to parse ResteasyViolationException");
      }
      
      violationLists = new ArrayList<List<ResteasyConstraintViolation>>();
      violationLists.add(fieldViolations);
      violationLists.add(propertyViolations);
      violationLists.add(classViolations);
      violationLists.add(parameterViolations);
      violationLists.add(returnValueViolations);
   }
   
   protected int getField(int start, String line)
   {
      int beginning = line.indexOf('[', start);
      if (beginning == -1)
      {
         throw new RuntimeException("ResteasyViolationException has invalid format: " + line);
      }
      int index = beginning;
      int bracketCount = 1;
      while (++index < line.length())
      {
         char c = line.charAt(index);
         if (c == '[')
         {
            bracketCount++;
         }
         else if (c == ']')
         {
            bracketCount--;
         }
         if (bracketCount == 0)
         {
            break;
         }
      }
      if (bracketCount != 0)
      {
         throw new RuntimeException("ResteasyViolationException has invalid format: " + line);
      }
      return index;
   }
}
