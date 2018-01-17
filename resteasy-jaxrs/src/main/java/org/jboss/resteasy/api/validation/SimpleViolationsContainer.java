package org.jboss.resteasy.api.validation;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;

public class SimpleViolationsContainer implements Serializable
{
   private static final long serialVersionUID = -7895854137980651540L;
   
   private Set<ConstraintViolation<Object>> violations = new HashSet<ConstraintViolation<Object>>();
   private Exception exception;
   private Object target;
   private boolean fieldsValidated;
   
   public SimpleViolationsContainer(Object target)
   {
      this.target = target;
   }
   
   public SimpleViolationsContainer(Set<ConstraintViolation<Object>> cvs)
   {
      violations.addAll(cvs);
   }
   
   public void addViolations(Set<ConstraintViolation<Object>> cvs)
   {
      violations.addAll(cvs);
   }
   
   public int size()
   {
      return violations.size();
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
   
   public Set<ConstraintViolation<Object>> getViolations()
   {
      return violations;
   }

   public boolean isFieldsValidated()
   {
      return fieldsValidated;
   }

   public void setFieldsValidated(boolean fieldsValidated)
   {
      this.fieldsValidated = fieldsValidated;
   }
}
