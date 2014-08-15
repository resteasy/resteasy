package org.jboss.resteasy.test.resteasy767;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy767.TestApplication;
import org.jboss.resteasy.resteasy767.TestMessageBodyWriterInterceptor;
import org.jboss.resteasy.resteasy767.TestPostProcessInterceptor;
import org.jboss.resteasy.resteasy767.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 27, 2013
 */
@RunWith(Arquillian.class)
public class AsyncPostProcessingTest
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-767.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(TestMessageBodyWriterInterceptor.class, TestPostProcessInterceptor.class)
            .addAsWebInfResource("web.xml");
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testSync() throws Exception
   {
      reset();
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-767/sync");
      ClientResponse<?> response = request.get();
      System.out.println("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      System.out.println("TestMessageBodyWriterInterceptor.called: " + TestMessageBodyWriterInterceptor.called);
      System.out.println("TestPostProcessInterceptor.called: " + TestPostProcessInterceptor.called);
      System.out.println("returned entity: " + response.getEntity(String.class));
      Assert.assertTrue(TestMessageBodyWriterInterceptor.called);
      Assert.assertTrue(TestPostProcessInterceptor.called);
      Assert.assertEquals("sync", response.getEntity(String.class));
   }
   
   @Test
   public void testAsyncWithDelay() throws Exception
   {
      reset();
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-767/async/delay");
      ClientResponse<?> response = request.get();
      System.out.println("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      System.out.println("TestMessageBodyWriterInterceptor.called: " + TestMessageBodyWriterInterceptor.called);
      System.out.println("TestPostProcessInterceptor.called: " + TestPostProcessInterceptor.called);
      System.out.println("returned entity: " + response.getEntity(String.class));
      Assert.assertTrue(TestMessageBodyWriterInterceptor.called);
      Assert.assertTrue(TestPostProcessInterceptor.called);
      Assert.assertEquals("async/delay", response.getEntity(String.class));
   }
   
   @Test
   public void testAsyncWithNoDelay() throws Exception
   {
      reset();
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-767/async/nodelay");
      ClientResponse<?> response = request.get();
      System.out.println("Status: " + response.getStatus());
      assertEquals(200, response.getStatus());
      System.out.println("TestMessageBodyWriterInterceptor.called: " + TestMessageBodyWriterInterceptor.called);
      System.out.println("TestPostProcessInterceptor.called: " + TestPostProcessInterceptor.called);
      System.out.println("returned entity: " + response.getEntity(String.class));
      Assert.assertTrue(TestMessageBodyWriterInterceptor.called);
      Assert.assertTrue(TestPostProcessInterceptor.called);
      Assert.assertEquals("async/nodelay", response.getEntity(String.class));
   }
   
   private void reset()
   {
      TestMessageBodyWriterInterceptor.called = false;
      TestPostProcessInterceptor.called = false;
   }
}
