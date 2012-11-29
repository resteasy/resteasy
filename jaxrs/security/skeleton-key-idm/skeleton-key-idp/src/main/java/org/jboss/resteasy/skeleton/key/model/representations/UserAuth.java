package org.jboss.resteasy.skeleton.key.model.representations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UserAuth
{
   protected String username;
   protected Map<String, String> credentials = new HashMap<String, String>();

   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   public Map<String, String> getCredentials()
   {
      return credentials;
   }

}
