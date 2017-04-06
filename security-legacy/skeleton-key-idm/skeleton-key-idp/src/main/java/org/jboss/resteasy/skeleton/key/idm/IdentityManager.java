package org.jboss.resteasy.skeleton.key.idm;

import org.jboss.resteasy.skeleton.key.idm.model.data.Realm;
import org.jboss.resteasy.skeleton.key.idm.model.data.RequiredCredential;
import org.jboss.resteasy.skeleton.key.idm.model.data.Resource;
import org.jboss.resteasy.skeleton.key.idm.model.data.Role;
import org.jboss.resteasy.skeleton.key.idm.model.data.RoleMapping;
import org.jboss.resteasy.skeleton.key.idm.model.data.ScopeMapping;
import org.jboss.resteasy.skeleton.key.idm.model.data.User;
import org.jboss.resteasy.skeleton.key.idm.model.data.UserAttribute;
import org.jboss.resteasy.skeleton.key.idm.model.data.UserCredential;

import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface IdentityManager
{
   Realm getRealm(String id);
   Realm create(Realm realm);
   void update(Realm realm);
   void delete(Realm realm);
   List<RequiredCredential> getRequiredCredentials(Realm realm);
   RequiredCredential getRealmCredential(String id);
   RequiredCredential create(Realm realm, RequiredCredential cred);
   void update(RequiredCredential cred);
   void delete(RequiredCredential cred);

   User getUser(Realm realm, String username);
   User create(Realm realm, User user);
   void update(User user);
   void delete(User user);

   List<UserCredential> getCredentials(User user);
   UserCredential getCredential(String id);
   UserCredential create(User user, UserCredential cred);
   void update(UserCredential cred);
   void delete(UserCredential cred);

   UserAttribute getUserAttribute(String id);
   UserAttribute create(User user, UserAttribute attribute);
   void update(UserAttribute attribute);
   void delete(UserAttribute attribute);

   Resource getResource(String id);
   Resource getResource(Realm realm, String name);
   List<Resource> getResources(Realm realm);
   Resource create(Realm realm, Resource resource);
   void update(Resource resource);
   void delete(Resource resource);


   Role getRoleByName(Realm realm, String roleName);
   Role getRoleByName(Resource resource, String roleName);
   List<Role> getRoles(Realm realm, Resource resource);
   List<Role> getRoles(Realm realm);
   Role getRole(String id);
   Role create(Realm realm, Resource resource, String role);
   Role create(Realm realm, String role);
   void delete(Role role);

   List<RoleMapping> getRoleMappings(Realm realm);
   List<RoleMapping> getRoleMappings(Realm realm, Resource resource);
   RoleMapping getRoleMapping(Realm realm, User user);
   RoleMapping getRoleMapping(Realm realm, Resource resource, User user);
   RoleMapping getRoleMapping(String id);
   RoleMapping create(Realm realm, User user, RoleMapping mapping);
   RoleMapping create(Realm realm, Resource resource, User user, RoleMapping mapping);
   void delete(RoleMapping role);

   List<ScopeMapping> getScopeMappings(Realm realm);
   List<ScopeMapping> getScopeMappings(Realm realm, Resource resource);
   ScopeMapping getScopeMapping(Realm realm, User user);
   ScopeMapping getScopeMapping(Realm realm, Resource resource, User user);
   ScopeMapping getScopeMapping(String id);
   ScopeMapping create(Realm realm, User user, ScopeMapping mapping);
   ScopeMapping create(Realm realm, Resource resource, User user, ScopeMapping mapping);
   void delete(ScopeMapping scope);


   List<Realm> getRealmsByName(String name);
}
