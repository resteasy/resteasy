package org.jboss.resteasy.skeleton.key;

import org.jboss.resteasy.skeleton.key.model.data.Realm;
import org.jboss.resteasy.skeleton.key.model.data.Resource;
import org.jboss.resteasy.skeleton.key.model.data.Role;
import org.jboss.resteasy.skeleton.key.model.data.RoleMapping;
import org.jboss.resteasy.skeleton.key.model.data.User;
import org.jboss.resteasy.skeleton.key.model.data.UserAttribute;
import org.jboss.resteasy.skeleton.key.model.data.UserCredential;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface IdentityManager
{
   Realm getDomain(String id);
   Realm create(Realm realm);
   void update(Realm realm);
   void delete(Realm realm);

   User getUser(Realm realm, String username);
   User create(Realm realm, User user);
   void update(User user);
   void delete(User user);

   UserCredential getCredential(String id);
   UserCredential create(User user, UserCredential cred);
   void update(UserCredential cred);
   void delete(UserCredential cred);

   UserAttribute getUserAttribute(String id);
   UserAttribute create(User user, UserAttribute attribute);
   void update(UserAttribute attribute);
   void delete(UserAttribute attribute);

   Resource create(Realm realm, Resource resource);
   void update(Resource resource);
   void delete(Resource resource);


   Role getRole(String id);
   Role create(Resource resource, String role);
   void delete(Role role);

   RoleMapping getRoleMapping(String id);
   RoleMapping create(Resource resource, RoleMapping mapping);
   void delete(RoleMapping role);





}
