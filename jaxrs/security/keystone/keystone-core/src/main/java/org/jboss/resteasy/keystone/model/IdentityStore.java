package org.jboss.resteasy.keystone.model;

import java.io.Serializable;
import java.util.List;

/**
* @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
* @version $Revision: 1 $
*/
public class IdentityStore implements Serializable
{
   private List<StoredUser> users;
   private List<Role> roles;
   private List<StoredProject> projects;

   public List<StoredUser> getUsers()
   {
      return users;
   }

   public void setUsers(List<StoredUser> users)
   {
      this.users = users;
   }

   public List<Role> getRoles()
   {
      return roles;
   }

   public void setRoles(List<Role> roles)
   {
      this.roles = roles;
   }

   public List<StoredProject> getProjects()
   {
      return projects;
   }

   public void setProjects(List<StoredProject> projects)
   {
      this.projects = projects;
   }
}
