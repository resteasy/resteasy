package org.jboss.resteasy.api.validation;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
   
   private ArrayList<ResteasyConstraintViolation> fieldViolations = new ArrayList<ResteasyConstraintViolation>();
   private ArrayList<ResteasyConstraintViolation> propertyViolations = new ArrayList<ResteasyConstraintViolation>();
   private ArrayList<ResteasyConstraintViolation> classViolations = new ArrayList<ResteasyConstraintViolation>();
   private ArrayList<ResteasyConstraintViolation> parameterViolations = new ArrayList<ResteasyConstraintViolation>();
   private ArrayList<ResteasyConstraintViolation> returnValueViolations = new ArrayList<ResteasyConstraintViolation>();

   public ViolationReport(ResteasyViolationException exception)
   {
      Exception e = exception.getException();
      if (e != null)
      {
         this.exception = e.toString();
      }
      this.fieldViolations = (ArrayList<ResteasyConstraintViolation>) exception.getFieldViolations();
      this.propertyViolations = (ArrayList<ResteasyConstraintViolation>) exception.getPropertyViolations();
      this.classViolations = (ArrayList<ResteasyConstraintViolation>) exception.getClassViolations();
      this.parameterViolations = (ArrayList<ResteasyConstraintViolation>) exception.getParameterViolations();
      this.returnValueViolations = (ArrayList<ResteasyConstraintViolation>) exception.getReturnValueViolations();
   }
   
   public ViolationReport(String s)
   {
      this(new ResteasyViolationException(s));
   }
   
   public ViolationReport()
   {
   }

   public String getException()
   {
      return exception;
   }

   public ArrayList<ResteasyConstraintViolation> getFieldViolations()
   {
      return fieldViolations;
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

   public void setFieldViolations(ArrayList<ResteasyConstraintViolation> fieldViolations)
   {
      this.fieldViolations = fieldViolations;
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