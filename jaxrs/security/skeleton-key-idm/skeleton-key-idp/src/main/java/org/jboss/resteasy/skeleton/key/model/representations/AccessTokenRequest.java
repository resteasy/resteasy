package org.jboss.resteasy.skeleton.key.model.representations;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AccessTokenRequest
{
   public static class Credential
   {
      protected String type;
      protected String value;

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
   }

   protected String username;
   protected List<Credential> credentials = new ArrayList<Credential>();

   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   public List<Credential> getCredentials()
   {
      return credentials;
   }

}
