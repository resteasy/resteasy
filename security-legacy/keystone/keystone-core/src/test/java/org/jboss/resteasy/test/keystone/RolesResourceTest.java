package org.jboss.resteasy.test.keystone;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.keystone.model.Role;
import org.jboss.resteasy.keystone.server.SkeletonKeyApplication;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RolesResourceTest
{
   private static NettyJaxrsServer server;
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
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      server.setDeployment(deployment);
      server.start();
   }

   @AfterClass
   public static void after() throws Exception
   {
      server.stop();
      server = null;
      deployment = null;
   }


   @Test
   public void testRole()
   {
      String newRole = "{ \"role\" : { \"name\" : \"admin\"} }";
      ResteasyClient client = new ResteasyClientBuilder().providerFactory(deployment.getProviderFactory()).build();
      Response response = client.target(generateURL("/roles")).request().post(Entity.json(newRole));
      Assert.assertEquals(response.getStatus(), 201);
      response.close();
      ResteasyWebTarget target = client.target(response.getLocation());
//      String role = target.request().get(String.class);
//      System.out.println(role);
      Role u = target.request().get(Role.class);
//      System.out.println(u);
      Assert.assertEquals("admin", u.getName());
      Assert.assertEquals(target.request().delete().getStatus(), 204);
      response = target.request().get();
      Assert.assertEquals(404, response.getStatus());
      client.close();
   }

   @Test
   public void testRoleText()
   {
      ResteasyClient client = new ResteasyClientBuilder().providerFactory(deployment.getProviderFactory()).build();
      Response response = client.target(generateURL("/roles")).request().post(Entity.text("admin"));
      Assert.assertEquals(response.getStatus(), 201);
      response.close();
      ResteasyWebTarget target = client.target(response.getLocation());
//      String role = target.request().get(String.class);
//      System.out.println(role);
      Role u = target.request().get(Role.class);
//      System.out.println(u);
      Assert.assertEquals("admin", u.getName());
      Assert.assertEquals(target.request().delete().getStatus(), 204);
      response = target.request().get();
      Assert.assertEquals(404, response.getStatus());
      client.close();
   }

   @Test
   public void testRoleId()
   {
      String newRole = "{ \"role\" : { \"id\" : \"5\", \"name\" : \"admin\"} }";
      ResteasyClient client = new ResteasyClientBuilder().providerFactory(deployment.getProviderFactory()).build();
      Response response = client.target(generateURL("/roles")).request().post(Entity.json(newRole));
      Assert.assertEquals(response.getStatus(), 201);
      response.close();
      ResteasyWebTarget target = client.target(response.getLocation());
//      String role = target.request().get(String.class);
//      System.out.println(role);
      Role u = target.request().get(Role.class);
//      System.out.println(u);
      Assert.assertEquals("admin", u.getName());
      Assert.assertEquals("5", u.getId());
      u.setName("administrator");
      Assert.assertEquals(target.request().put(Entity.json(u)).getStatus(), 204);
      u = target.request().get(Role.class);
//      System.out.println(u);
      Assert.assertEquals("administrator", u.getName());
      Assert.assertEquals("5", u.getId());
      Assert.assertEquals(target.request().delete().getStatus(), 204);
      response = target.request().get();
      Assert.assertEquals(404, response.getStatus());
      client.close();
   }


}
