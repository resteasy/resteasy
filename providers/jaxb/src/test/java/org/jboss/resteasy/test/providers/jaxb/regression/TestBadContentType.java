package org.jboss.resteasy.test.providers.jaxb.regression;

import static org.jboss.resteasy.test.TestPortProvider.*;
import static org.junit.Assert.*;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for RESTEASY-169
 *
 * @author edelsonj
 */

@Ignore
public class TestBadContentType extends BaseResourceTest
{

   @Path("/test")
   public static class TestResource
   {

      @GET
      public TestBean get()
      {
         TestBean bean = new TestBean();
         bean.setName("myname");
         return bean;
      }

      @POST
      public void post(TestBean bean)
      {

      }

   }

   @Before
   public void setUp() throws Exception
   {
      stopContainer();
      createContainer(initParams, contextParams);
      addPerRequestResource(TestResource.class, TestBean.class);
      startContainer();
   }

   /**
    * RESTEASY-519
    *
    * @throws Exception
    */
   @Test
   public void testBadRequest() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.body("application/xml", "<junk");
      ClientResponse<?> res = request.post();
      int status = res.getStatus();
      res.releaseConnection();
      Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), status);
   }

   /**
    *  * Test case for RESTEASY-169
    *
    * @throws Exception
    */
   @Test
   public void testHtmlError() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.header("Accept", "text/html");
      ClientResponse<String> response = request.get(String.class);
      int status = response.getStatus();
      String entity = response.getEntity();
      response.releaseConnection();
      System.out.println("response: " + entity);
      assertEquals(500, status);
      assertTrue(entity.contains("media type: text/html"));
   }

   @Test
   public void testBadRequestAfterHtmlError() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/test"));
      request.body("application/xml", "<junk");
      ClientResponse<?> res = request.post();
      int status = res.getStatus();
      res.releaseConnection();
      Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), status);

      request = new ClientRequest(generateURL("/test"));
      request.header("Accept", "text/html");
      ClientResponse<String> response = request.get(String.class);
      status = response.getStatus();
      String entity = response.getEntity();
      response.releaseConnection();
      System.out.println("response: " + entity);
      assertEquals(500, status);
      assertTrue(entity.contains("media type: text/html"));
   }

}
