package org.jboss.resteasy.test.resteasy736;

import static org.junit.Assert.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy736.TestApplication;
import org.jboss.resteasy.resteasy736.TestResource;
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
 * Copyright Aug 3, 2012
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AsyncTimeoutTest
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-736.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClasses(AsyncTimeoutTest.class)
            .addAsWebInfResource("web.xml");
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testAsynchTimeout() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-736/test/");
      long start = System.currentTimeMillis();
      System.out.println("start:   " + start);
      ClientResponse<String> response = null;
      try
      {
         response = request.get(String.class);
      }
      catch (Exception e)
      {
         System.out.println(e);
      }
      finally
      {
         System.out.println("finish:  " + System.currentTimeMillis());
         long elapsed = System.currentTimeMillis() - start;
         System.out.println("elapsed: " + elapsed + " ms");;
         System.out.println("status: " + response.getStatus());
         assertTrue(response != null);
         System.out.println("response: " + response.getEntity());
         assertTrue(response.getStatus() == 503);
         assertTrue(elapsed < 10000);
      }
   }

   @Test
   public void testDefaultAsynchTimeout() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-736/default/");
      long start = System.currentTimeMillis();
      System.out.println("start:   " + start);
      ClientResponse<String> response = null;
      try
      {
         response = request.get(String.class);
      }
      catch (Exception e)
      {
         System.out.println(e);
      }
      finally
      {
         System.out.println("finish:  " + System.currentTimeMillis());
         long elapsed = System.currentTimeMillis() - start;
         System.out.println("elapsed: " + elapsed + " ms");;
         System.out.println("status: " + response.getStatus());
         assertTrue(response != null);
         System.out.println("response: " + response.getEntity());
         assertTrue(response.getStatus() == 503);
         assertTrue(elapsed < 35000); // Jetty async timeout defaults to 30000.
      }
   }
}
