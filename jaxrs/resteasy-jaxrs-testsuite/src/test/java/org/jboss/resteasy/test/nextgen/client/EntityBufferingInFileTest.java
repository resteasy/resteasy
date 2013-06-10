package org.jboss.resteasy.test.nextgen.client;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *          <p/>
 *          Copyright Sep 29, 2012
 */
public class EntityBufferingInFileTest
{
   private static final Logger log = Logger.getLogger(EntityBufferingInFileTest.class);

   protected ResteasyDeployment deployment;

   @Path("/")
   static public class TestResource
   {
      @POST
      @Produces("text/plain")
      @Path("hello")
      public String resourceMethod(String body)
      {
         System.out.println("entered resourceMethod()");
         return body;
      }
   }

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      deployment = null;
   }

   @Test
   public void testInMemoryBytes() throws Exception
   {
      doTest(ApacheHttpClient4Engine.MemoryUnit.BY, 16, 10, true);
   }

   @Test
   public void testOnDiskBytes() throws Exception
   {
      doTest(ApacheHttpClient4Engine.MemoryUnit.BY, 16, 20, false);
   }

   @Test
   public void testInMemoryKilobytes() throws Exception
   {
      doTest(ApacheHttpClient4Engine.MemoryUnit.KB, 1, 500, true);
   }

   @Test
   public void testOnDiskKilobytes() throws Exception
   {
      doTest(ApacheHttpClient4Engine.MemoryUnit.KB, 1, 2000, false);
   }

   @Test
   public void testInMemoryMegabytes() throws Exception
   {
      doTest(ApacheHttpClient4Engine.MemoryUnit.MB, 1, 500000, true);
   }

   @Test
   public void testOnDiskMegabytes() throws Exception
   {
      doTest(ApacheHttpClient4Engine.MemoryUnit.MB, 1, 2000000, false);
   }

   @Test
   public void testInMemoryGigabytes() throws Exception
   {
      doTest(ApacheHttpClient4Engine.MemoryUnit.GB, 1, 500000000, true);
   }

   @Test
   public void testOnDiskGigabytes() throws Exception
   {
      doTest(ApacheHttpClient4Engine.MemoryUnit.GB, 1, 2000000000, false);
   }

   protected void doTest(ApacheHttpClient4Engine.MemoryUnit memoryUnit, int threshold, int length, boolean inMemory) throws Exception
   {
      try
      {
         TestClientExecutor executor = new TestClientExecutor();
         executor.setFileUploadMemoryUnit(memoryUnit);
         executor.setFileUploadInMemoryThresholdLimit(threshold);
         StringBuffer sb = new StringBuffer();
         for (int i = 0; i < length; i++)
         {
            sb.append("0");
         }
         String body = sb.toString();

         ResteasyClient client = new ResteasyClientBuilder().httpEngine(executor).build();
         Response response = client.target(generateURL("/hello")).request().post(Entity.text(body));
         System.out.println("Received response");
         Assert.assertEquals(200, response.getStatus());
         Assert.assertEquals(body, response.readEntity(String.class));
         if (inMemory)
         {
            Assert.assertTrue(executor.getBuildEntity() instanceof ByteArrayEntity);
         }
         else
         {
            Assert.assertTrue(executor.getBuildEntity() instanceof FileEntity);
         }
      }
      catch (OutOfMemoryError e)
      {
         // Ok, skip it.
         log.info("OutOfMemoryError on " + memoryUnit + " test.");

      }
   }

   static class TestClientExecutor extends ApacheHttpClient4Engine
   {
      private HttpEntity entityToBuild;

      protected HttpEntity buildEntity(final ClientInvocation request) throws IOException
      {
         entityToBuild = super.buildEntity(request);
         return entityToBuild;
      }

      public HttpEntity getBuildEntity()
      {
         return entityToBuild;
      }
   }
}
