package org.jboss.resteasy.test.finegrain.application;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.test.EmbeddedContainer;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApplicationConfigWithInterceptorTest
{
   @Path("/my")
   public static class MyResource
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         return "hello";
      }
   }

   @Provider
   public static class AddHeader implements PostProcessInterceptor
   {
      public void postProcess(ServerResponse response)
      {
         System.out.println("HERE!!!!!");
         response.getMetadata().add("custom-header", "hello");
      }
   }

   public static class MyApplicationConfig extends Application
   {
      private Set<Class<?>> classes = new HashSet<Class<?>>();

      public MyApplicationConfig()
      {
         classes.add(MyResource.class);
         classes.add(AddHeader.class);
      }

      @Override
      public Set<Class<?>> getClasses()
      {
         return classes;
      }

   }

   @BeforeClass
   public static void before() throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setApplication(new MyApplicationConfig());
      EmbeddedContainer.start(deployment);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testIt() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/my"));
      ClientResponse response = request.get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertNotNull(response.getHeaders().getFirst("custom-header"));

   }
}