package org.jboss.resteasy.keystone.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StoredProject implements Serializable
{
   private Project project;
   private Map<String, Set<String>> userRoles = new HashMap<String, Set<String>>();
   private Map<String, String> userNameIds = new HashMap<String, String>();

   public StoredProject()
   {
   }

   public StoredProject(Project project)
   {
      this.project = project;
   }

   public Project getProject()
   {
      return project;
   }

   public void setProject(Project project)
   {
      this.project = project;
   }

   public Map<String, Set<String>> getUserRoles()
   {
      return userRoles;
   }

   public void setUserRoles(Map<String, Set<String>> userRoles)
   {
      this.userRoles = userRoles;
   }

   public Set<String> roleMapping(String userId)
   {
      return userRoles.get(userId);
   }

   public void addUserRoleMapping(User user, Role role)
   {
      userNameIds.put(user.getUsername(), user.getId());
      Set<String> roleMapping = userRoles.get(user.getId());
      if (roleMapping == null)
      {
         roleMapping = new HashSet<String>();
         userRoles.put(user.getId(), roleMapping);
      }
      roleMapping.add(role.getId());
   }

   public void removeUserRoleMapping(User user, Role role)
   {
      userNameIds.put(user.getUsername(), user.getId());
      Set<String> roleMapping = userRoles.get(user.getId());
      if (roleMapping == null) return;
      roleMapping.remove(role.getId());
      if (roleMapping.size() < 1)
      {
         userRoles.remove(user.getId());
         userNameIds.remove(user.getUsername());
      }
      roleMapping.add(role.getId());
   }


   public Map<String, String> getUserNameIds()
   {
      return userNameIds;
   }

   public void setUserNameIds(Map<String, String> userNameIds)
   {
      this.userNameIds = userNameIds;
   }
}
