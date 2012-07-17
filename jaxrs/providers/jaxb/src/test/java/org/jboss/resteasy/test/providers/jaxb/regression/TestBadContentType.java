package org.jboss.resteasy.test.providers.jaxb.regression;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import static org.jboss.resteasy.test.TestPortProvider.*;
import static org.junit.Assert.*;

/**
 * Test case for RESTEASY-169
 *
 * @author edelsonj
 */

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

   @XmlRootElement
   public static class TestBean
   {
      private String name;

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

   }


   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(TestResource.class);
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
      ClientResponse res = request.post();
      Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), res.getStatus());
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
      String entity = response.getEntity();
      System.out.println("response: " + entity);
      assertEquals(500, response.getStatus());
      assertTrue(entity.contains("media type: text/html"));
   }

}
