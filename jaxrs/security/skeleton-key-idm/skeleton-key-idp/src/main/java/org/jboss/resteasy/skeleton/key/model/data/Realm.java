package org.jboss.resteasy.skeleton.key.model.data;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Realm
{
   protected String id;
   protected String name;
   protected boolean enabled;
   protected Set<String> adminIds;
   protected Set<String> resourceIds;
   protected Set<String> userIds;

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public boolean isEnabled()
   {
      return enabled;
   }

   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }

   public Set<String> getAdminIds()
   {
      return adminIds;
   }

   public void setAdminIds(Set<String> adminIds)
   {
      this.adminIds = adminIds;
   }

   public Set<String> getResourceIds()
   {
      return resourceIds;
   }

   public void setResourceIds(Set<String> resourceIds)
   {
      this.resourceIds = resourceIds;
   }

   public Set<String> getUserIds()
   {
      return userIds;
   }

   public void setUserIds(Set<String> userIds)
   {
      this.userIds = userIds;
   }
}
