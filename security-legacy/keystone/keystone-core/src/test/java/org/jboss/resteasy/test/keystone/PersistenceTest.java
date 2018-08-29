package org.jboss.resteasy.test.keystone;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.keystone.client.SkeletonKeyAdminClient;
import org.jboss.resteasy.keystone.client.SkeletonKeyClientBuilder;
import org.jboss.resteasy.keystone.model.Project;
import org.jboss.resteasy.keystone.model.Projects;
import org.jboss.resteasy.keystone.model.Role;
import org.jboss.resteasy.keystone.model.StoredUser;
import org.jboss.resteasy.keystone.model.User;
import org.jboss.resteasy.keystone.server.Loader;
import org.jboss.resteasy.keystone.server.SkeletonKeyApplication;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PersistenceTest
{
   private static final Logger LOG = Logger.getLogger(PersistenceTest.class);
   private static NettyJaxrsServer server;
   private static ResteasyDeployment deployment;
   private static SkeletonKeyApplication app;

   public static void init() throws Exception
   {

      StoredUser admin = new StoredUser();
      admin.setName("Bill");
      admin.setUsername("wburke");
      HashMap<String, String> creds = new HashMap<String, String>();
      creds.put("password", "geheim");
      admin.setCredentials(creds);
      app.getUsers().create(admin);

      Project project = new Project();
      project.setName("Skeleton Key");
      project.setEnabled(true);
      app.getProjects().createProject(project);

      Role adminRole = new Role();
      adminRole.setName("admin");
      app.getRoles().create(adminRole);

      app.getProjects().addUserRole(project.getId(), admin.getId(), adminRole.getId());

      // Test export/import
      LOG.info(new Loader().export(app.getCache()));

      try
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         new Loader().export(app.getCache(), baos);
         ByteArrayInputStream bios = new ByteArrayInputStream(baos.toByteArray());
         app.getCache().clear();
         new Loader().importStore(bios, app.getCache());
      }
      catch (Exception e)
      {
      }
   }

   private static void stopDeployment() throws Exception
   {
      app.getCache().stop();
      deployment = null;
      app = null;
      server.stop();
      server = null;
   }

   public static class SApp extends Application
   {
      SkeletonKeyApplication app;

      public SApp(@Context Configurable confgurable)
      {
         this.app = new SkeletonKeyApplication(confgurable);
      }



      @Override
      public Set<Object> getSingletons()
      {
         return app.getSingletons();
      }
   }

   private static void startDeployment() throws Exception
   {
      deployment = new ResteasyDeployment();
      deployment.setSecurityEnabled(true);
      deployment.setApplicationClass(SApp.class.getName());
      ResteasyProviderFactory factory = new ResteasyProviderFactory();
      deployment.setProviderFactory(factory);
      factory.property(SkeletonKeyApplication.SKELETON_KEY_INFINISPAN_CONFIG_FILE, "cache.xml");
      factory.property(SkeletonKeyApplication.SKELETON_KEY_INFINISPAN_CACHE_NAME, "idp-store");

      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      server.setDeployment(deployment);
      server.start();
      app = ((SApp)deployment.getApplication()).app;
   }

   @Test
   public void testAppLoad() throws Exception
   {
      clearCache();
      startDeployment();
      init();
      stopDeployment();
      startDeployment();
      ResteasyClient client = new ResteasyClientBuilder().build();
      WebTarget target = client.target(generateBaseUrl());
      SkeletonKeyAdminClient admin = new SkeletonKeyClientBuilder().username("wburke").password("geheim").idp(target).admin();

      StoredUser newUser = new StoredUser();
      newUser.setName("John Smith");
      newUser.setUsername("jsmith");
      newUser.setEnabled(true);
      Map<String, String> creds = new HashMap<>();
      creds.put("password", "foobar");
      newUser.setCredentials(creds);
      Response response = admin.users().create(newUser);
      User user = response.readEntity(User.class);
      response = admin.roles().create("user");
      Role role = response.readEntity(Role.class);
      Projects projects = admin.projects().query("Skeleton Key");
      Project project = projects.getList().get(0);
      admin.projects().addUserRole(project.getId(), user.getId(), role.getId());

      admin = new SkeletonKeyClientBuilder().username("jsmith").password("foobar").idp(target).admin();
      response = admin.roles().create("error");
      Assert.assertEquals(403, response.getStatus());
      client.close();
      stopDeployment();
   }

   @Test
   public void testImportExport() throws Exception
   {
      clearCache();
      startDeployment();
      Assert.assertEquals(0, app.getCache().size());
      init();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      new Loader().export(app.getCache(), baos);
      app.getCache().clear();
      stopDeployment();

      startDeployment();
      Assert.assertEquals(0, app.getCache().size());
      ByteArrayInputStream bios = new ByteArrayInputStream(baos.toByteArray());
      new Loader().importStore(bios, app.getCache());
      stopDeployment();
      startDeployment();
      Assert.assertTrue(0 < app.getCache().size());


      ResteasyClient client = new ResteasyClientBuilder().build();
      WebTarget target = client.target(generateBaseUrl());
      SkeletonKeyAdminClient admin = new SkeletonKeyClientBuilder().username("wburke").password("geheim").idp(target).admin();

      StoredUser newUser = new StoredUser();
      newUser.setName("John Smith");
      newUser.setUsername("jsmith");
      newUser.setEnabled(true);
      Map<String, String> creds = new HashMap<>();
      creds.put("password", "foobar");
      newUser.setCredentials(creds);
      Response response = admin.users().create(newUser);
      User user = response.readEntity(User.class);
      response = admin.roles().create("user");
      Role role = response.readEntity(Role.class);
      String json = target.path("projects").queryParam("name", "Skeleton Key").request().get(String.class);
      Projects projects = admin.projects().query("Skeleton Key");
      Project project = projects.getList().get(0);
      admin.projects().addUserRole(project.getId(), user.getId(), role.getId());

      admin = new SkeletonKeyClientBuilder().username("jsmith").password("foobar").idp(target).admin();
      response = admin.roles().create("error");
      Assert.assertEquals(403, response.getStatus());
      client.close();
      stopDeployment();
   }


   private void clearCache() throws IOException
   {
      Cache<Object,Object> cache = new DefaultCacheManager("cache.xml").getCache("idp-store");
      cache.clear();
      cache.stop();
   }

}
