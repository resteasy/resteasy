package org.jboss.resteasy.test.form;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.Serializable;


/**
 * A FormValueHolder.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class FormValueHolder implements Serializable
{


   /**
    * The serialVersionUID
    */
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

   @QueryParam("defaultValue")
   @DefaultValue("42")
   public int defaultValue;

   private int headerParam;
   private int id;
   private int queryParam;

   public int getDefaultValue()
   {
      return defaultValue;
   }

   public int getHeaderParam()
   {
      return headerParam;
   }

   @HeaderParam("custom-header")
   public void setHeaderParam(int headerParam)
   {
      this.headerParam = headerParam;
   }

   public int getId()
   {
      return id;
   }

   @PathParam("id")
   public void setId(int id)
   {
      this.id = id;
   }

   public int getQueryParam()
   {
      return queryParam;
   }

   @QueryParam("query")
   public void setQueryParam(int queryParam)
   {
      this.queryParam = queryParam;
   }

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
