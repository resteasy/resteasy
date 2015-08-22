package org.jboss.resteasy.plugins.server.embedded;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

/**
 * POJO Security domain.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SimpleSecurityDomain implements SecurityDomain
{
   private ConcurrentHashMap<String, String> users = new ConcurrentHashMap<String, String>();
   private ConcurrentHashMap<String, Set<String>> roles = new ConcurrentHashMap<String, Set<String>>();


   public void addRole(String user, String role)
   {
      Set<String> users = roles.get(role);
      if (users == null)
      {
         users = new CopyOnWriteArraySet<String>();
         roles.putIfAbsent(role, users);
         users = roles.get(role);
      }
      users.add(user);
   }

   public void addUser(String username, String password, String[] roles)
   {
      users.put(username, password);
      for (String role : roles) addRole(username, role);
   }

   public void addRoles(String role, String[] users)
   {
      for (String user : users) addRole(user, role);
   }

   public Principal authenticate(String username, String password) throws SecurityException
   {
      String passwd = users.get(username);
      if (passwd == null) throw new SecurityException(Messages.MESSAGES.userIsNotRegistered(username));
      if (!passwd.equals(password)) throw new SecurityException(Messages.MESSAGES.wrongPassword(username));
      
      return new SimplePrincipal(username);
   }

   public boolean isUserInRole(Principal username, String role)
   {
      //System.out.println("Is user in role: " + username.getName() + " for role " + role);
      Set<String> users = roles.get(role);
      if (users == null)
      {
         //System.out.println("No user of that name");
         return false;
      }

      boolean result = users.contains(username.getName());
      //System.out.println("Result is: " + result);
      return result;
   }
}
