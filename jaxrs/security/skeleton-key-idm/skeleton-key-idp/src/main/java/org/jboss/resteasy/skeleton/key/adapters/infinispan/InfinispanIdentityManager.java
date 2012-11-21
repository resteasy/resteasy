package org.jboss.resteasy.skeleton.key.adapters.infinispan;

import org.jboss.resteasy.skeleton.key.IdentityManager;
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
public class InfinispanIdentityManager implements IdentityManager
{
   @Override
   public Realm getDomain(String id)
   {
      return null;
   }

   @Override
   public Realm create(Realm domain)
   {
      return null;
   }

   @Override
   public void update(Realm domain)
   {

   }

   @Override
   public void delete(Realm domain)
   {

   }

   @Override
   public User getUser(Realm domain, String username)
   {
      return null;
   }

   @Override
   public User create(Realm domain, User user)
   {
      return null;
   }

   @Override
   public void update(User user)
   {

   }

   @Override
   public void delete(User user)
   {

   }

   @Override
   public UserCredential getCredential(String id)
   {
      return null;
   }

   @Override
   public UserCredential create(User user, UserCredential cred)
   {
      return null;
   }

   @Override
   public void update(UserCredential cred)
   {

   }

   @Override
   public void delete(UserCredential cred)
   {

   }

   @Override
   public UserAttribute getUserAttribute(String id)
   {
      return null;
   }

   @Override
   public UserAttribute create(User user, UserAttribute attribute)
   {
      return null;
   }

   @Override
   public void update(UserAttribute attribute)
   {

   }

   @Override
   public void delete(UserAttribute attribute)
   {

   }

   @Override
   public Resource create(Realm domain, Resource resource)
   {
      return null;
   }

   @Override
   public void update(Resource resource)
   {

   }

   @Override
   public void delete(Resource resource)
   {

   }

   @Override
   public Role getRole(String id)
   {
      return null;
   }

   @Override
   public Role create(Resource resource, String role)
   {
      return null;
   }

   @Override
   public void delete(Role role)
   {

   }

   @Override
   public RoleMapping getRoleMapping(String id)
   {
      return null;
   }

   @Override
   public RoleMapping create(Resource resource, RoleMapping mapping)
   {
      return null;
   }

   @Override
   public void delete(RoleMapping role)
   {

   }
}
