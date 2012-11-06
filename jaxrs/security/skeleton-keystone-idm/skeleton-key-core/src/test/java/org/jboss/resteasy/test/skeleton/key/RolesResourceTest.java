package org.jboss.resteasy.test.skeleton.key;

import junit.framework.Assert;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.skeleton.key.keystone.model.Role;
import org.jboss.resteasy.skeleton.key.server.SkeletonKeyApplication;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Set;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RolesResourceTest
{
   private static ResteasyDeployment deployment;

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

   @BeforeClass
   public static void before() throws Exception
   {
      deployment = new ResteasyDeployment();
      deployment.setApplicationClass(SApp.class.getName());
      EmbeddedContainer.start(deployment);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }


   @Test
   public void testRole()
   {
      String newRole = "{ \"role\" : { \"name\" : \"admin\"} }";
      ResteasyClient client = new ResteasyClient(deployment.getProviderFactory());
      Response response = client.target(generateURL("/roles")).request().post(Entity.json(newRole));
      Assert.assertEquals(response.getStatus(), 201);
      response.close();
      ResteasyWebTarget target = client.target(response.getLocation());
      String role = target.request().get(String.class);
      System.out.println(role);
      Role u = target.request().get(Role.class);
      System.out.println(u);
      Assert.assertEquals("admin", u.getName());
      Assert.assertEquals(target.request().delete().getStatus(), 204);
      response = target.request().get();
      Assert.assertEquals(404, response.getStatus());
   }

   @Test
   public void testRoleText()
   {
      ResteasyClient client = new ResteasyClient(deployment.getProviderFactory());
      Response response = client.target(generateURL("/roles")).request().post(Entity.text("admin"));
      Assert.assertEquals(response.getStatus(), 201);
      response.close();
      ResteasyWebTarget target = client.target(response.getLocation());
      String role = target.request().get(String.class);
      System.out.println(role);
      Role u = target.request().get(Role.class);
      System.out.println(u);
      Assert.assertEquals("admin", u.getName());
      Assert.assertEquals(target.request().delete().getStatus(), 204);
      response = target.request().get();
      Assert.assertEquals(404, response.getStatus());
   }

   @Test
   public void testRoleId()
   {
      String newRole = "{ \"role\" : { \"id\" : \"5\", \"name\" : \"admin\"} }";
      ResteasyClient client = new ResteasyClient(deployment.getProviderFactory());
      Response response = client.target(generateURL("/roles")).request().post(Entity.json(newRole));
      Assert.assertEquals(response.getStatus(), 201);
      response.close();
      ResteasyWebTarget target = client.target(response.getLocation());
      String role = target.request().get(String.class);
      System.out.println(role);
      Role u = target.request().get(Role.class);
      System.out.println(u);
      Assert.assertEquals("admin", u.getName());
      Assert.assertEquals("5", u.getId());
      u.setName("administrator");
      Assert.assertEquals(target.request().put(Entity.json(u)).getStatus(), 204);
      u = target.request().get(Role.class);
      System.out.println(u);
      Assert.assertEquals("administrator", u.getName());
      Assert.assertEquals("5", u.getId());
      Assert.assertEquals(target.request().delete().getStatus(), 204);
      response = target.request().get();
      Assert.assertEquals(404, response.getStatus());
   }


}
