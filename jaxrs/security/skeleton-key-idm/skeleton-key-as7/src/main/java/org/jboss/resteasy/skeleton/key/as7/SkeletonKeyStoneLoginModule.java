package org.jboss.resteasy.skeleton.key.as7;

import org.apache.catalina.connector.Request;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJacksonProvider;
import org.jboss.resteasy.skeleton.key.client.SkeletonKeyAdminClient;
import org.jboss.resteasy.skeleton.key.client.SkeletonKeyClientBuilder;
import org.jboss.resteasy.skeleton.key.keystone.model.Access;
import org.jboss.resteasy.skeleton.key.keystone.model.Role;
import org.jboss.resteasy.skeleton.key.server.UserPrincipal;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.ext.Providers;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.LinkedHashSet;
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

   private static final Logger log = Logger.getLogger(SkeletonKeyStoneLoginModule.class);
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
      log.error("-------------- CAN WE FIND JACKSON PROVIDER from static{} block?????");
      logServices();

      ResteasyJacksonProvider p = new ResteasyJacksonProvider();
      client = new ResteasyClient(providerFactory);
      ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
      cm.setMaxTotal(100);
      cm.setDefaultMaxPerRoute(100);
      HttpClient httpClient = new DefaultHttpClient(cm);
      client.httpEngine(new ApacheHttpClient4Engine(httpClient));
   }

   private static void logServices()
   {
      try
      {
         log.error("META-INF/services/" + Providers.class.getName());
         Enumeration<URL> en = Thread.currentThread().getContextClassLoader().getResources("META-INF/services/" + Providers.class.getName());
         LinkedHashSet<String> set = new LinkedHashSet<String>();
         while (en.hasMoreElements())
         {
            URL url = en.nextElement();
            log.error("URL: " + url);
         }
      }
      catch (IOException e)
      {

      }

      try
      {
         log.error("with .class classloader META-INF/services/" + Providers.class.getName());
         Enumeration<URL> en = SkeletonKeyStoneLoginModule.class.getClassLoader().getResources("META-INF/services/" + Providers.class.getName());
         LinkedHashSet<String> set = new LinkedHashSet<String>();
         while (en.hasMoreElements())
         {
            URL url = en.nextElement();
            log.error("URL: " + url);
         }
      }
      catch (IOException e)
      {

      }

   }

   static void initAdmin(Map<String, ?> options)
   {
      SkeletonKeyAdminClient tmp = admin;
      if (tmp == null)
      {
         synchronized(client)
         {
            tmp = admin;
            if (tmp == null)
            {
               String adminUrl = (String)options.get("skeleton.key.url");
               String username = (String)options.get("admin.username");
               String password = (String)options.get("admin.password");
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
      projectId = (String)options.get("projectId");
   }

   @Override
   protected boolean login(Request request, HttpServletResponse response) throws LoginException
   {
      String tokenHeader = request.getHeader("X-Auth-Token");
      if (tokenHeader == null) throw new LoginException("No X-Auth-Token");

      access = admin.tokens().get(tokenHeader);
      if (access.getToken().expired())
      {
         throw new LoginException("Token expired");
      }
      if (!projectId.equals(access.getToken().getProject().getId()))
      {
         throw new LoginException("Token project id doesn't match");
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
