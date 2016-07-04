package org.jboss.resteasy.keystone.as7;

import org.apache.catalina.connector.Request;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.keystone.as7.i18n.Messages;
import org.jboss.resteasy.keystone.client.SkeletonKeyAdminClient;
import org.jboss.resteasy.keystone.client.SkeletonKeyClientBuilder;
import org.jboss.resteasy.keystone.core.UserPrincipal;
import org.jboss.resteasy.keystone.model.Access;
import org.jboss.resteasy.keystone.model.Role;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.WebTarget;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Map;

/**
 * Keystone Access token protocol
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SkeletonKeyStoneLoginModule extends JBossWebAuthLoginModule
{
   static ResteasyClient client;
   static volatile SkeletonKeyAdminClient admin;

   static
   {
      ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
      ClassLoader old = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(SkeletonKeyStoneLoginModule.class.getClassLoader());
      try
      {
         RegisterBuiltin.register(providerFactory);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(old);
      }
      client = new ResteasyClientBuilder().providerFactory(providerFactory)
              .connectionPoolSize(100)
              .maxPooledPerRoute(100).build();
   }

   static void initAdmin(Map<String, ?> options)
   {
      SkeletonKeyAdminClient tmp = admin;
      if (tmp == null)
      {
         synchronized (client)
         {
            tmp = admin;
            if (tmp == null)
            {
               String adminUrl = (String) options.get("skeleton.key.url");
               String username = (String) options.get("admin.username");
               String password = (String) options.get("admin.password");
               WebTarget adminTarget = client.target(adminUrl);
               tmp = admin = new SkeletonKeyClientBuilder().username(username).password(password).idp(adminTarget).admin();
            }
         }
      }

   }

   protected String projectId;
   protected Access access;

   @Override
   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options)
   {
      super.initialize(subject, callbackHandler, sharedState, options);

      initAdmin(options);
      projectId = (String) options.get("projectId");
   }

   @Override
   protected boolean login(Request request, HttpServletResponse response) throws LoginException
   {
      String tokenHeader = request.getHeader("X-Auth-Token");
      if (tokenHeader == null) return false; //throw new LoginException("No X-Auth-Token");

      access = admin.tokens().get(tokenHeader);
      if (access.getToken().expired())
      {
         throw new LoginException(Messages.MESSAGES.tokenExpired());
      }
      if (!projectId.equals(access.getToken().getProject().getId()))
      {
         throw new LoginException(Messages.MESSAGES.tokenProjectIdDoesntMatch());
      }

      this.loginOk = true;
      return true;
   }

   @Override
   protected Principal getIdentity()
   {
      Principal principal = new UserPrincipal(access.getUser());
      return principal;
   }

   @Override
   protected Group[] getRoleSets() throws LoginException
   {
      SimpleGroup roles = new SimpleGroup("Roles");
      Group[] roleSets = {roles};
      for (Role role : access.getUser().getRoles())
      {
         roles.addMember(new SimplePrincipal(role.getName()));
      }
      return roleSets;
   }
}
