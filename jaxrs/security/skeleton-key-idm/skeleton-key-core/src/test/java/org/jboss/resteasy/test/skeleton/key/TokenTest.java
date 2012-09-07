package org.jboss.resteasy.test.skeleton.key;

import junit.framework.Assert;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.skeleton.key.client.SkeletonKeyAdminClient;
import org.jboss.resteasy.skeleton.key.client.SkeletonKeyClientBuilder;
import org.jboss.resteasy.skeleton.key.keystone.model.Project;
import org.jboss.resteasy.skeleton.key.keystone.model.Projects;
import org.jboss.resteasy.skeleton.key.keystone.model.Role;
import org.jboss.resteasy.skeleton.key.keystone.model.StoredUser;
import org.jboss.resteasy.skeleton.key.keystone.model.User;
import org.jboss.resteasy.skeleton.key.server.Loader;
import org.jboss.resteasy.skeleton.key.server.SkeletonKeyApplication;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TokenTest
{
   private static ResteasyDeployment deployment;

   @BeforeClass
   public static void before() throws Exception
   {
      deployment = new ResteasyDeployment();
      deployment.setSecurityEnabled(true);
      deployment.setApplicationClass(SkeletonKeyApplication.class.getName());

      EmbeddedContainer.start(deployment);
      SkeletonKeyApplication app = (SkeletonKeyApplication)deployment.getApplication();

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
      System.out.println(new Loader().export(app.getCache()));

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
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }

   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testAuth() throws Exception
   {
      ResteasyClient client = new ResteasyClient();
      WebTarget target = client.target(generateBaseUrl());
      SkeletonKeyAdminClient admin = new SkeletonKeyClientBuilder().username("wburke").password("geheim").idp(target).admin();

      StoredUser newUser = new StoredUser();
      newUser.setName("John Smith");
      newUser.setUsername("jsmith");
      newUser.setEnabled(true);
      Map creds = new HashMap();
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
      Assert.assertEquals(401, response.getStatus());



   }

   @Test
   public void testNotAuthenticated()
   {
      {
         // assuming @RolesAllowed is on class level. Too lazy to test it all!
         String newUser = "{ \"user\" : { \"username\" : \"wburke\", \"name\" : \"Bill Burke\", \"email\" : \"bburke@redhat.com\", \"enabled\" : true, \"credentials\" : { \"password\" : \"geheim\" }} }";
         ResteasyClient client = new ResteasyClient(deployment.getProviderFactory());
         Response response = client.target(generateURL("/users")).request().post(Entity.json(newUser));
         Assert.assertEquals(response.getStatus(), 401);
         response.close();
      }
      {
         String newRole = "{ \"role\" : { \"name\" : \"admin\"} }";
         ResteasyClient client = new ResteasyClient(deployment.getProviderFactory());
         Response response = client.target(generateURL("/roles")).request().post(Entity.json(newRole));
         Assert.assertEquals(response.getStatus(), 401);
         response.close();

      }
      {
         String newProject = "{ \"project\" : { \"id\" : \"5\", \"name\" : \"Resteasy\", \"description\" : \"The Best of REST\", \"enabled\" : true } }";
         ResteasyClient client = new ResteasyClient(deployment.getProviderFactory());
         Response response = client.target(generateURL("/projects")).request().post(Entity.json(newProject));
         Assert.assertEquals(response.getStatus(), 401);
         response.close();
      }
   }

}
