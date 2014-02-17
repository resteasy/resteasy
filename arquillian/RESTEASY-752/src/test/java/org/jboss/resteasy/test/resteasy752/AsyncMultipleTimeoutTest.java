package org.jboss.resteasy.test.resteasy752;

import static org.junit.Assert.*;

import java.util.Date;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.resteasy752.TestApplication;
import org.jboss.resteasy.resteasy752.TestResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 3, 2012
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AsyncMultipleTimeoutTest
{
   private static final Logger log = LoggerFactory.getLogger(AsyncMultipleTimeoutTest.class);

   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-752.war")
            .addClasses(TestApplication.class, TestResource.class)
            .addClass(TestResource.class)
            .addClasses(AsyncMultipleTimeoutTest.class)
            .addAsWebInfResource("web.xml")
            .addAsWebInfResource("log4j.properties");
      System.out.println(war.toString(true));
      return war;
   }

   @Test
   public void testAsynchTimeout() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8080/RESTEASY-752/timeoutStacks/");
      ClientResponse<String> response = null;
      long start = System.currentTimeMillis();

      log.info("start 1: " + new Date());
      try
      {
         response = request.get(String.class);
      }
      catch (Exception e)
      {
         log.info("Exception " + e.getMessage());
      }
      finally
      {
        long elapsed = System.currentTimeMillis() - start;
        int responseStatus = response.getStatus();
        String responseContent = response.getEntity();
         log.info("finish 1:  " + new Date());
         log.info("elapsed: " + elapsed + " ms");;
         log.info("status: " + responseStatus);
         log.info("response: " + responseContent);
         assertEquals(503, responseStatus);
         //FIXME - response is empty for EAP assertTrue("Expected response contains timeout, no found. Response is " + responseContent, responseContent.indexOf("timeout") >= 0);
         assertTrue(elapsed < TestResource.SERVLET_TIMEOUT + 5000);
         Thread.sleep(TestResource.SERVLET_TIMEOUT + TestResource.SERVLET_EXTRA_TIME - elapsed + 5000);
      }

      start = System.currentTimeMillis();
      log.info("start 2: " + new Date());
      try
      {
         response = request.get(String.class);
      }
      catch (Exception e)
      {
         log.info(e.getMessage());
      }
      finally
      {
         log.info("finish 2:  " + new Date());
         long elapsed = System.currentTimeMillis() - start;
         log.info("elapsed: " + elapsed + " ms");;
         log.info("status: " + response.getStatus());
         log.info("response: " + response.getEntity());
         assertEquals(200, response.getStatus());
         assertTrue(response.getEntity().indexOf("string returned at") >= 0);
         assertTrue(elapsed < 5000);
         Thread.sleep(2000);
      }

      start = System.currentTimeMillis();
      log.info("start 3: " + new Date());
      try
      {
         response = request.get(String.class);
      }
      catch (Exception e)
      {
         log.info(e.getMessage());
      }
      finally
      {
         log.info("finish 3:  " + new Date());
         long elapsed = System.currentTimeMillis() - start;
         log.info("elapsed: " + elapsed + " ms");;
         log.info("status: " + response.getStatus());
         log.info("response: " + response.getEntity());
         assertEquals(503, response.getStatus());
         //FIXME - response is empty for EAP assertTrue(response.getEntity().indexOf("timeout") >= 0);
         assertTrue(elapsed < TestResource.SERVLET_TIMEOUT + 5000);
         Thread.sleep(TestResource.SERVLET_TIMEOUT + TestResource.SERVLET_EXTRA_TIME - elapsed + 5000);
      }

      start = System.currentTimeMillis();
      log.info("start 4: " + new Date());
      try
      {
         response = request.get(String.class);
      }
      catch (Exception e)
      {
         log.info(e.getMessage());
      }
      finally
      {
         log.info("finish 4:  " + new Date());
         long elapsed = System.currentTimeMillis() - start;
         log.info("elapsed: " + elapsed + " ms");;
         log.info("status: " + response.getStatus());
         log.info("response: " + response.getEntity());
         assertEquals(200, response.getStatus());
         assertTrue(response.getEntity().indexOf("string returned at") >= 0);
         assertTrue(elapsed < 5000);
      }
   }
}
