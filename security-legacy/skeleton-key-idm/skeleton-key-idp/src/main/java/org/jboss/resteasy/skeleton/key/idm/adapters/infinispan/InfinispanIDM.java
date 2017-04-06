package org.jboss.resteasy.skeleton.key.idm.adapters.infinispan;

import org.infinispan.Cache;
import org.jboss.resteasy.skeleton.key.idm.IdentityManager;
import org.jboss.resteasy.skeleton.key.idm.model.data.Realm;
import org.jboss.resteasy.skeleton.key.idm.model.data.RequiredCredential;
import org.jboss.resteasy.skeleton.key.idm.model.data.Resource;
import org.jboss.resteasy.skeleton.key.idm.model.data.Role;
import org.jboss.resteasy.skeleton.key.idm.model.data.RoleMapping;
import org.jboss.resteasy.skeleton.key.idm.model.data.ScopeMapping;
import org.jboss.resteasy.skeleton.key.idm.model.data.User;
import org.jboss.resteasy.skeleton.key.idm.model.data.UserAttribute;
import org.jboss.resteasy.skeleton.key.idm.model.data.UserCredential;
import org.jboss.resteasy.spi.NotImplementedYetException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class InfinispanIDM implements IdentityManager
{
   private static AtomicLong counter = new AtomicLong(1);
   private static String generateId()
   {
      return counter.getAndIncrement() + "-" + System.currentTimeMillis();
   }

   protected Cache cache;

   public InfinispanIDM(Cache cache)
   {
      this.cache = cache;
   }

   @Override
   public Realm getRealm(String id)
   {
      return (Realm)cache.get("/realms/" +id);
   }

   @Override
   public List<Realm> getRealmsByName(String name)
   {
      Set<String> realms = (Set<String>)cache.get("/realms/names/" + name);
      List<Realm> list = new ArrayList<Realm>();
      if (realms == null) return list;
      for (String id : realms)
      {
         Realm realm = getRealm(id);
         if (realm != null) list.add(realm);
      }
      return list;
   }

   @Override
   public Realm create(Realm realm)
   {
      realm.setId(generateId());
      cache.put(realmKey(realm), realm);
      Set<String> realms = (Set<String>)cache.get("/realms/names/" + realm.getName());
      if (realms == null)
      {
         realms = new HashSet<String>();
      }
      realms.add(realm.getId());
      cache.put("/realms/names/" + realm.getName(), realms);

      return realm;
   }

   @Override
   public void update(Realm realm)
   {
      cache.put(realmKey(realm), realm);
   }

   protected String realmKey(Realm realm)
   {
      return "/realms/"+realm.getId();
   }

   @Override
   public void delete(Realm realm)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public List<RequiredCredential> getRequiredCredentials(Realm realm)
   {
      Set<String> creds = (Set<String>)cache.get(realmKey(realm) + "/RequiredCredentials");
      List<RequiredCredential> list = new ArrayList<RequiredCredential>();
      if (creds != null)
      {
         for (String id : creds)
         {
            RequiredCredential cred = getRealmCredential(id);
            list.add(cred);
         }
      }
      return list;
   }

   @Override
   public RequiredCredential create(Realm realm, RequiredCredential cred)
   {
      cred.setId(generateId());
      cache.put("/RequiredCredentials/"+cred.getId(), cred);
      Set<String> creds = (Set<String>)cache.get(realmKey(realm) + "/RequiredCredentials");
      if (creds == null)
      {
         creds = new HashSet<String>();
      }
      creds.add(cred.getId());
      cache.put(realmKey(realm) + "/RequiredCredentials", creds);
      return cred;
   }

   @Override
   public RequiredCredential getRealmCredential(String id)
   {
      return (RequiredCredential)cache.get("/RequiredCredentials/"+id);
   }



   @Override
   public void update(RequiredCredential cred)
   {
      cache.put("/RequiredCredentials/"+cred.getId(), cred);
   }

   @Override
   public void delete(RequiredCredential cred)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public User create(Realm realm, User user)
   {
      user.setId(generateId());
      cache.put("/users/"+user.getId(), user);
      cache.put(realmKey(realm)+"/users/"+user.getUsername(), user.getId());
      return user;
   }
   @Override
   public User getUser(Realm realm, String username)
   {
      String id = (String)cache.get(realmKey(realm)+"/users/"+username);
      if (id == null) return null;
      return (User)cache.get("/users/"+id);
   }


   @Override
   public void update(User user)
   {
      cache.put("/users/"+user.getId(), user);
   }

   @Override
   public void delete(User user)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public UserCredential create(User user, UserCredential cred)
   {
      cred.setId(generateId());
      cache.put("/UserCredentials/"+cred.getId(), cred);
      Set<String> creds = (Set<String>)cache.get("/users/" + user.getId() + "/UserCredentials");
      if (creds == null)
      {
         creds = new HashSet<String>();
      }
      creds.add(cred.getId());
      cache.put("/users/" + user.getId() + "/UserCredentials", creds);
      return cred;
   }

   @Override
   public List<UserCredential> getCredentials(User user)
   {
      Set<String> creds = (Set<String>)cache.get("/users/" + user.getId() + "/UserCredentials");
      List<UserCredential> list = new ArrayList<UserCredential>();
      if (creds != null)
      {
         for (String id : creds)
         {
            UserCredential cred = getCredential(id);
            list.add(cred);
         }
      }
      return list;
   }

   @Override
   public UserCredential getCredential(String id)
   {
      return (UserCredential)cache.get("/UserCredentials/"+id);
   }


   @Override
   public void update(UserCredential cred)
   {
      cache.put("/UserCredentials/"+cred.getId(), cred);
   }

   @Override
   public void delete(UserCredential cred)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public UserAttribute getUserAttribute(String id)
   {
      return (UserAttribute)cache.get("/UserAtributes/"+id);
   }

   @Override
   public UserAttribute create(User user, UserAttribute attribute)
   {
      attribute.setId(generateId());
      cache.put("/UserAttributes/"+attribute.getId(), attribute);
      Set<String> creds = (Set<String>)cache.get("/users/" + user.getId() + "/UserAttributes");
      if (creds == null)
      {
         creds = new HashSet<String>();
      }
      creds.add(attribute.getId());
      cache.put("/users/" + user.getId() + "/UserAttributes", creds);
      return attribute;
   }

   @Override
   public void update(UserAttribute attribute)
   {
      cache.put("/UserAttributes/"+attribute.getId(), attribute);
   }

   @Override
   public void delete(UserAttribute attribute)
   {
      throw new NotImplementedYetException();

   }

   @Override
   public Resource getResource(String id)
   {
      return (Resource)cache.get("/resources/"+id);
   }

   @Override
   public Resource getResource(Realm realm, String name)
   {
      String id = (String)cache.get(realmKey(realm)+"/resources/"+name);
      if (id == null) return null;
      return getResource(id);
   }

   @Override
   public List<Resource> getResources(Realm realm)
   {
      Set<String> resources = (Set<String>)cache.get(realmKey(realm)+"/resources");
      List<Resource> list = new ArrayList<Resource>();
      if (resources != null)
      {
         for (String id : resources)
         {
            list.add(getResource(id));
         }
      }
      return list;
   }

   @Override
   public Resource create(Realm realm, Resource resource)
   {
      resource.setId(generateId());
      cache.put("/resources/"+resource.getId(), resource);
      cache.put(realmKey(realm)+"/resources/"+resource.getName(), resource.getId());
      Set<String> resources = (Set<String>)cache.get(realmKey(realm)+"/resources");
      if (resources == null)
      {
         resources = new HashSet<String>();
      }
      resources.add(resource.getId());
      cache.put(realmKey(realm)+"/resources", resources);
      return resource;
   }

   @Override
   public void update(Resource resource)
   {
      cache.put("/resources/"+resource.getId(), resource);

   }

   @Override
   public void delete(Resource resource)
   {
      throw new NotImplementedYetException();

   }

   @Override
   public Role getRoleByName(Realm realm, String roleName)
   {
      String id = (String)cache.get(realmKey(realm) + "/roles/" + roleName);
      if (id == null) return null;
      return getRole(id);
   }

   @Override
   public Role getRoleByName(Resource resource, String roleName)
   {
      String id = (String)cache.get("/resources/" + resource.getId() + "/roles/" + roleName);
      if (id == null) return null;
      return getRole(id);
   }

   @Override
   public List<Role> getRoles(Realm realm, Resource resource)
   {
      Set<String> roles = (Set<String>)cache.get("/resources/" + resource.getId() + "/roles");
      List<Role> list = new ArrayList<Role>();
      if (roles != null)
      {
         for (String id : roles)
         {
            list.add(getRole(id));
         }
      }
      return list;
   }

   @Override
   public List<Role> getRoles(Realm realm)
   {
      Set<String> roles = (Set<String>)cache.get(realmKey(realm) + "/roles");
      List<Role> list = new ArrayList<Role>();
      if (roles != null)
      {
         for (String id : roles)
         {
            list.add(getRole(id));
         }
      }
      return list;
   }

   @Override
   public Role getRole(String id)
   {
      return (Role)cache.get("/roles/" + id);
   }

   @Override
   public Role create(Realm realm, Resource resource, String roleName)
   {
      Role role = new Role();
      role.setName(roleName);
      role.setId(generateId());
      cache.put("/roles/"+role.getId(), role);
      Set<String> roles = (Set<String>)cache.get("/resources/" + resource.getId() + "/roles");
      if (roles == null)
      {
         roles = new HashSet<String>();
      }
      roles.add(role.getId());
      cache.put("/resources/" + resource.getId() + "/roles", roles);
      cache.put("/resources/" + resource.getId() + "/roles/" + role.getName(), role.getId());
      return role;
   }

   @Override
   public Role create(Realm realm, String roleName)
   {
      Role role = new Role();
      role.setName(roleName);
      role.setId(generateId());
      cache.put("/roles/"+role.getId(), role);
      Set<String> roles = (Set<String>)cache.get(realmKey(realm) + "/roles");
      if (roles == null)
      {
         roles = new HashSet<String>();
      }
      roles.add(role.getId());
      cache.put(realmKey(realm) + "/roles", roles);
      cache.put(realmKey(realm) + "/roles/" + role.getName(), role.getId());
      return role;
   }

   @Override
   public void delete(Role role)
   {
      throw new NotImplementedYetException();

   }

   @Override
   public List<RoleMapping> getRoleMappings(Realm realm)
   {
      Set<String> mappings = (Set<String>)cache.get("/realms/" + realm.getId() + "/RoleMappings");
      List<RoleMapping> list = new ArrayList<RoleMapping>();
      if (mappings != null)
      {
         for (String id : mappings)
         {
            list.add(getRoleMapping(id));
         }
      }
      return list;
   }

   @Override
   public List<RoleMapping> getRoleMappings(Realm realm, Resource resource)
   {
      Set<String> mappings = (Set<String>)cache.get("/resources/" + resource.getId() + "/RoleMappings");
      List<RoleMapping> list = new ArrayList<RoleMapping>();
      if (mappings != null)
      {
         for (String id : mappings)
         {
            list.add(getRoleMapping(id));
         }
      }
      return list;
   }

   @Override
   public RoleMapping getRoleMapping(Realm realm, User user)
   {
      String id = (String)cache.get("/realms/" + realm.getId() + "/RoleMappings/users/" + user.getId());
      return getRoleMapping(id);
   }

   @Override
   public RoleMapping getRoleMapping(Realm realm, Resource resource, User user)
   {
      String id = (String)cache.get("/resources/" + resource.getId() + "/RoleMappings/users/" + user.getId());
      return getRoleMapping(id);
   }

   @Override
   public RoleMapping getRoleMapping(String id)
   {
      return (RoleMapping)cache.get("/RoleMappings/" + id);
   }

   @Override
   public RoleMapping create(Realm realm, User user, RoleMapping mapping)
   {
      mapping.setId(generateId());
      mapping.setUserid(user.getId());
      cache.put("/RoleMappings/" + mapping.getId(), mapping);
      cache.put(realmKey(realm) + "/RoleMappings/users/" + user.getId(), mapping.getId());
      Set<String> mappings = (Set<String>)cache.get(realmKey(realm) + "/RoleMappings");
      if (mappings == null)
      {
         mappings = new HashSet<String>();
      }
      mappings.add(mapping.getId());
      cache.put(realmKey(realm) + "/RoleMappings", mappings);
      return mapping;
   }

   @Override
   public RoleMapping create(Realm realm, Resource resource, User user, RoleMapping mapping)
   {
      mapping.setId(generateId());
      mapping.setUserid(user.getId());
      cache.put("/RoleMappings/"+mapping.getId(), mapping);
      cache.put("/resources/" + resource.getId() + "/RoleMappings/users/" + user.getId(), mapping.getId());
      Set<String> mappings = (Set<String>)cache.get("/resources/" + resource.getId() + "/RoleMappings");
      if (mappings == null)
      {
         mappings = new HashSet<String>();
      }
      mappings.add(mapping.getId());
      cache.put("/resources/" + resource.getId() + "/RoleMappings", mappings);
      return mapping;
   }

   @Override
   public void delete(RoleMapping role)
   {
      throw new NotImplementedYetException();

   }

   @Override
   public List<ScopeMapping> getScopeMappings(Realm realm)
   {
      Set<String> mappings = (Set<String>)cache.get("/realms/" + realm.getId() + "/ScopeMappings");
      List<ScopeMapping> list = new ArrayList<ScopeMapping>();
      if (mappings != null)
      {
         for (String id : mappings)
         {
            list.add(getScopeMapping(id));
         }
      }
      return list;
   }

   @Override
   public List<ScopeMapping> getScopeMappings(Realm realm, Resource resource)
   {
      Set<String> mappings = (Set<String>)cache.get("/resources/" + resource.getId() + "/ScopeMappings");
      List<ScopeMapping> list = new ArrayList<ScopeMapping>();
      if (mappings != null)
      {
         for (String id : mappings)
         {
            list.add(getScopeMapping(id));
         }
      }
      return list;
   }

   @Override
   public ScopeMapping getScopeMapping(Realm realm, User user)
   {
      String id = (String)cache.get("/realms/" + realm.getId() + "/ScopeMappings/users/" + user.getId());
      return getScopeMapping(id);
   }

   @Override
   public ScopeMapping getScopeMapping(Realm realm, Resource resource, User user)
   {
      String id = (String)cache.get("/resources/" + resource.getId() + "/ScopeMappings/users/" + user.getId());
      return getScopeMapping(id);
   }

   @Override
   public ScopeMapping getScopeMapping(String id)
   {
      return (ScopeMapping)cache.get("/ScopeMappings/" + id);
   }

   @Override
   public ScopeMapping create(Realm realm, User user, ScopeMapping mapping)
   {
      mapping.setId(generateId());
      mapping.setUserid(user.getId());
      cache.put("/ScopeMappings/" + mapping.getId(), mapping);
      cache.put(realmKey(realm) + "/ScopeMappings/users/" + user.getId(), mapping.getId());
      Set<String> mappings = (Set<String>)cache.get(realmKey(realm) + "/ScopeMappings");
      if (mappings == null)
      {
         mappings = new HashSet<String>();
      }
      mappings.add(mapping.getId());
      cache.put(realmKey(realm) + "/ScopeMappings", mappings);
      return mapping;
   }

   @Override
   public ScopeMapping create(Realm realm, Resource resource, User user, ScopeMapping mapping)
   {
      mapping.setId(generateId());
      mapping.setUserid(user.getId());
      cache.put("/ScopeMappings/"+mapping.getId(), mapping);
      cache.put("/resources/" + resource.getId() + "/ScopeMappings/users/" + user.getId(), mapping.getId());
      Set<String> mappings = (Set<String>)cache.get("/resources/" + resource.getId() + "/ScopeMappings");
      if (mappings == null)
      {
         mappings = new HashSet<String>();
      }
      mappings.add(mapping.getId());
      cache.put("/resources/" + resource.getId() + "/ScopeMappings", mappings);
      return mapping;
   }

   @Override
   public void delete(ScopeMapping scope)
   {
      throw new NotImplementedYetException();

   }
}
