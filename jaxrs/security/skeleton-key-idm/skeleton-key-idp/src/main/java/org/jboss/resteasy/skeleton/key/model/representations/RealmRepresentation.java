package org.jboss.resteasy.skeleton.key.model.representations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RealmRepresentation
{
   protected String self; // link
   protected String realm;
   protected boolean enabled;
   protected List<UserRepresentation> users;
   protected List<ResourceRepresentation> resources;
   protected Set<String> admins;

   public String getSelf()
   {
      return self;
   }

   public void setSelf(String self)
   {
      this.self = self;
   }

   public String getRealm()
   {
      return realm;
   }

   public void setRealm(String realm)
   {
      this.realm = realm;
   }

   public List<UserRepresentation> getUsers()
   {
      return users;
   }

   public List<ResourceRepresentation> getResources()
   {
      return resources;
   }

   public ResourceRepresentation resource(String name)
   {
      ResourceRepresentation resource = new ResourceRepresentation();
      if (resources == null) resources = new ArrayList<ResourceRepresentation>();
      resources.add(resource);
      resource.setName(name);
      return resource;
   }

   public void setUsers(List<UserRepresentation> users)
   {
      this.users = users;
   }

   public UserRepresentation user(String username)
   {
      UserRepresentation user = new UserRepresentation();
      user.setUsername(username);
      if (users == null) users = new ArrayList<UserRepresentation>();
      users.add(user);
      return user;
   }

   public Set<String> getAdmins()
   {
      return admins;
   }

   public void setAdmins(Set<String> admins)
   {
      this.admins = admins;
   }

   public RealmRepresentation admin(String username)
   {
      if (admins == null) admins = new HashSet<String>();
      admins.add(username);
      return this;
   }

   public void setResources(List<ResourceRepresentation> resources)
   {
      this.resources = resources;
   }

   public boolean isEnabled()
   {
      return enabled;
   }

   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }
}
