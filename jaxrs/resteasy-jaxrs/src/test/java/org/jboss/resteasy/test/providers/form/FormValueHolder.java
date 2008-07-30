/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.providers.form;

import java.io.Serializable;

import org.jboss.resteasy.annotations.FormParam;
import org.jboss.resteasy.annotations.FormValues;

/**
 * A FormValueHolder.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@FormValues
public class FormValueHolder implements Serializable
{


   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;

   @FormParam("booleanValue")
   private Boolean booleanValue;

   @FormParam("name")
   private String name;

   @FormParam("doubleValue")
   private Double doubleValue;

   @FormParam("longValue")
   private Long longValue;

   @FormParam("integerValue")
   private Integer integerValue;
   
   @FormParam("shortValue")
   private Short shortValue;

   /**
    * Get the booleanValue.
    * 
    * @return the booleanValue.
    */
   public Boolean getBooleanValue()
   {
      return booleanValue;
   }

   /**
    * Set the booleanValue.
    * 
    * @param booleanValue The booleanValue to set.
    */
   public void setBooleanValue(Boolean booleanValue)
   {
      this.booleanValue = booleanValue;
   }

   /**
    * Get the name.
    * 
    * @return the name.
    */
   public String getName()
   {
      return name;
   }

   /**
    * Set the name.
    * 
    * @param name The name to set.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * Get the doubleValue.
    * 
    * @return the doubleValue.
    */
   public Double getDoubleValue()
   {
      return doubleValue;
   }

   /**
    * Set the doubleValue.
    * 
    * @param doubleValue The doubleValue to set.
    */
   public void setDoubleValue(Double doubleValue)
   {
      this.doubleValue = doubleValue;
   }

   /**
    * Get the longValue.
    * 
    * @return the longValue.
    */
   public Long getLongValue()
   {
      return longValue;
   }

   /**
    * Set the longValue.
    * 
    * @param longValue The longValue to set.
    */
   public void setLongValue(Long longValue)
   {
      this.longValue = longValue;
   }

   /**
    * Get the integerValue.
    * 
    * @return the integerValue.
    */
   public Integer getIntegerValue()
   {
      return integerValue;
   }

   /**
    * Set the integerValue.
    * 
    * @param integerValue The integerValue to set.
    */
   public void setIntegerValue(Integer integerValue)
   {
      this.integerValue = integerValue;
   }

   /**
    * Get the shortValue.
    * 
    * @return the shortValue.
    */
   public Short getShortValue()
   {
      return shortValue;
   }

   /**
    * Set the shortValue.
    * 
    * @param shortValue The shortValue to set.
    */
   public void setShortValue(Short shortValue)
   {
      this.shortValue = shortValue;
   }
}
