package org.jboss.resteasy.test.keystone;

import junit.framework.Assert;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.keystone.model.Mappers;
import org.jboss.resteasy.keystone.model.Project;
import org.jboss.resteasy.keystone.model.Projects;
import org.jboss.resteasy.keystone.server.SkeletonKeyApplication;
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
public class ProjectsResourceTest
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
      System.out.println(project);
      Project u = target.request().get(Project.class);
      System.out.println(u);
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
      System.out.println(project);
      Project u = target.request().get(Project.class);
      System.out.println(u);
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
   }
}
