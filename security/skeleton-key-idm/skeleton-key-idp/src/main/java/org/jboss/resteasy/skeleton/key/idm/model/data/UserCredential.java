package org.jboss.resteasy.skeleton.key.idm.model.data;

import java.io.Serializable;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UserCredential implements Serializable
{
   private static final long serialVersionUID = 1L;
   public static final String PASSWORD = "PASSWORD";
   public static final String CALLER_PRINCIPAL = "CALLER_PRINCIPAL";

   protected String id;
   protected String type;
   protected String value;
   protected boolean hashed;

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
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

   public boolean isHashed()
   {
      return hashed;
   }

   public void setHashed(boolean hashed)
   {
      this.hashed = hashed;
   }
}
