package org.jboss.resteasy.plugins.cache.server;

import java.io.Serializable;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 24, 2015
 */
public class HeaderHolder implements Serializable
{
   private static final long serialVersionUID = -8595237214355692799L;

   public enum Type
   {
      CACHE_CONTROL,
      COOKIE,
      ENTITY_TAG,
      NEW_COOKIE,
      OTHER
   }

   private Type type;
   private String value;

   public HeaderHolder(Type type, String value)
   {
      this.type = type;
      this.value = value;
   }

   public Type getType()
   {
      return type;
   }

   public void setType(Type type)
   {
      this.type = type;
   }

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }
}
