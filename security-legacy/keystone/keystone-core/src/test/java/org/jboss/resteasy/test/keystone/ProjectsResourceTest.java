package org.jboss.resteasy.test.keystone;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.keystone.model.Mappers;
import org.jboss.resteasy.keystone.model.Project;
import org.jboss.resteasy.keystone.model.Projects;
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
public class ProjectsResourceTest
{
   private static final Logger LOG = Logger.getLogger(ProjectsResourceTest.class);
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
   public void testProjects()
   {
      String newProject = "{ \"project\" : { \"name\" : \"Resteasy\", \"description\" : \"The Best of REST\", \"enabled\" : true } }";
      ResteasyClient client = new ResteasyClientBuilder().providerFactory(deployment.getProviderFactory()).build();
      ResteasyWebTarget projectsTarget = client.target(generateURL("/projects"));
      Response response = projectsTarget.request().post(Entity.json(newProject));
      Assert.assertEquals(response.getStatus(), 201);
      response.close();
      ResteasyWebTarget target = client.target(response.getLocation());
      String project = target.request().get(String.class);
      LOG.info(project);
      Project u = target.request().get(Project.class);
      LOG.info(u);
      Assert.assertEquals("Resteasy", u.getName());
      Assert.assertEquals("The Best of REST", u.getDescription());
      Assert.assertTrue(u.getEnabled());
      u.setName("Resteasy JAX-RS");
      Assert.assertEquals(target.request().put(Entity.json(u)).getStatus(), 204);
      u = target.request().get(Project.class);
      Assert.assertEquals("Resteasy JAX-RS", u.getName());
      Assert.assertEquals(target.request().delete().getStatus(), 204);
      response = target.request().get();
      Assert.assertEquals(404, response.getStatus());
      response.close();

      projectsTarget = client.target(generateURL("/projects"));
      Mappers.registerContextResolver(projectsTarget);
      Projects projects = projectsTarget.request().get(Projects.class);
      client.close();
   }
   @Test
   public void testProjectsId()
   {
      String newProject = "{ \"project\" : { \"id\" : \"5\", \"name\" : \"Resteasy\", \"description\" : \"The Best of REST\", \"enabled\" : true } }";
      ResteasyClient client = new ResteasyClientBuilder().providerFactory(deployment.getProviderFactory()).build();
      Response response = client.target(generateURL("/projects")).request().post(Entity.json(newProject));
      Assert.assertEquals(response.getStatus(), 201);
      response.close();
      ResteasyWebTarget target = client.target(response.getLocation());
      String project = target.request().get(String.class);
      LOG.info(project);
      Project u = target.request().get(Project.class);
      LOG.info(u);
      Assert.assertEquals("5", u.getId());
      Assert.assertEquals("Resteasy", u.getName());
      Assert.assertEquals("The Best of REST", u.getDescription());
      Assert.assertTrue(u.getEnabled());
      u.setName("Resteasy JAX-RS");
      Assert.assertEquals(target.request().put(Entity.json(u)).getStatus(), 204);
      u = target.request().get(Project.class);
      Assert.assertEquals("Resteasy JAX-RS", u.getName());
      Assert.assertEquals(target.request().delete().getStatus(), 204);
      response = target.request().get();
      Assert.assertEquals(404, response.getStatus());
      client.close();
   }
}
