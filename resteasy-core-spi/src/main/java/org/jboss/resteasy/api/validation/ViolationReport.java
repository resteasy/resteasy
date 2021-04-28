package org.jboss.resteasy.api.validation;

import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.validation.ConstraintTypeUtil;

/**
*
* @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
* @version $Revision: 1.1 $
*
* Copyright July 27, 2013
*/
@XmlRootElement(name="violationReport")
@XmlAccessorType(XmlAccessType.FIELD)
public class ViolationReport
{
   private String exception;

   private ArrayList<ResteasyConstraintViolation> propertyViolations = new ArrayList<ResteasyConstraintViolation>();
   private ArrayList<ResteasyConstraintViolation> classViolations = new ArrayList<ResteasyConstraintViolation>();
   private ArrayList<ResteasyConstraintViolation> parameterViolations = new ArrayList<ResteasyConstraintViolation>();
   private ArrayList<ResteasyConstraintViolation> returnValueViolations = new ArrayList<ResteasyConstraintViolation>();

   public ViolationReport(final ResteasyViolationException exception)
   {
      Exception e = exception.getException();
      if (e != null)
      {
         this.exception = e.toString();
      }
      this.propertyViolations = (ArrayList<ResteasyConstraintViolation>) exception.getPropertyViolations();
      this.classViolations = (ArrayList<ResteasyConstraintViolation>) exception.getClassViolations();
      this.parameterViolations = (ArrayList<ResteasyConstraintViolation>) exception.getParameterViolations();
      this.returnValueViolations = (ArrayList<ResteasyConstraintViolation>) exception.getReturnValueViolations();
   }

   public ViolationReport(final String s)
   {
      this(new ResteasyViolationException(s)
      {
         private static final long serialVersionUID = 1L;

         @Override
         public ConstraintTypeUtil getConstraintTypeUtil()
         {
            return null;
         }

         @Override
         protected ResteasyConfiguration getResteasyConfiguration()
         {
            return null;
         }
      });
   }

   public ViolationReport()
   {
   }

   public String getException()
   {
      return exception;
   }

   public ArrayList<ResteasyConstraintViolation> getPropertyViolations()
   {
      return propertyViolations;
   }

   public ArrayList<ResteasyConstraintViolation> getClassViolations()
   {
      return classViolations;
   }

   public ArrayList<ResteasyConstraintViolation> getParameterViolations()
   {
      return parameterViolations;
   }

   public ArrayList<ResteasyConstraintViolation> getReturnValueViolations()
   {
      return returnValueViolations;
   }

   public void setException(String exception)
   {
      this.exception = exception;
   }

   public void setPropertyViolations(ArrayList<ResteasyConstraintViolation> propertyViolations)
   {
      this.propertyViolations = propertyViolations;
   }

   public void setClassViolations(ArrayList<ResteasyConstraintViolation> classViolations)
   {
      this.classViolations = classViolations;
   }

   public void setParameterViolations(ArrayList<ResteasyConstraintViolation> parameterViolations)
   {
      this.parameterViolations = parameterViolations;
   }

   public void setReturnValueViolations(ArrayList<ResteasyConstraintViolation> returnValueViolations)
   {
      this.returnValueViolations = returnValueViolations;
   }
}
