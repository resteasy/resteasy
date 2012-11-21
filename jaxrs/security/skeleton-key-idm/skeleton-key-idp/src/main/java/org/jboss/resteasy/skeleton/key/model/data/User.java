package org.jboss.resteasy.skeleton.key.model.data;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class User
{
   protected String id;
   protected String username;
   protected boolean enabled;
   protected Set<String> attributeIds;
   protected Set<String> credentialIds;

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   public boolean isEnabled()
   {
      return enabled;
   }

   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

   public Set<String> getAttributeIds()
   {
      return attributeIds;
   }

   public void setAttributeIds(Set<String> attributeIds)
   {
      this.attributeIds = attributeIds;
   }

   public User attribute(String id)
   {
      if (attributeIds == null) attributeIds = new HashSet<String>();
      attributeIds.add(id);
      return this;
   }

   public Set<String> getCredentialIds()
   {
      return credentialIds;
   }

   public void setCredentialIds(Set<String> credentialIds)
   {
      this.credentialIds = credentialIds;
   }

   public User credential(String id)
   {
      if (credentialIds == null) credentialIds = new HashSet<String>();
      credentialIds.add(id);
      return this;
   }

}
