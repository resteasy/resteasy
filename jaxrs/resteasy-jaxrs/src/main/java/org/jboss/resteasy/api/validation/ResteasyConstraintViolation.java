package org.jboss.resteasy.api.validation;

import java.io.Serializable;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 4, 2013
 */
public class ResteasyConstraintViolation implements Serializable
{
   private static final long serialVersionUID = -5441628046215135260L;
   
   private ConstraintType.Type constraintType;
   private String path;
   private String message;
   private String value;
   
   public ResteasyConstraintViolation(ConstraintType.Type constraintType, String path, String message, String value)
   {
      this.constraintType = constraintType;
      this.path = path;
      this.message = message;
      this.value = value;
   }
   
   /**
    * @return type of constraint
    */
   public ConstraintType.Type getConstraintType()
   {
      return constraintType;
   }
   
   /**
    * @return description of element violating constraint
    */
   public String getPath()
   {
      return path;
   }
   
   /**
    * @return description of constraint violation
    */
   public String getMessage()
   {
      return message;
   }
   
   /**
    * @return object in violation of constraint
    */
   public String getValue()
   {
      return value;
   }
   
   /**
    * @return String representation of violation
    */
   public String toString()
   {
      return type() + "| " + path + "| " + message + "| " + value;
   }
   
   /**
    * @return String form of violation type 
    */
   public String type()
   {
      return constraintType.toString();
   }
}