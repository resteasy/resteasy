package org.jboss.resteasy.test.encoding;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-729
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 16, 2012
 */
public class MatrixParamEncodingTest
{
   protected ResteasyDeployment deployment;

   @Path("/")
   static public class TestResource
   {
      @GET
      @Path("decoded")
      @Produces("text/plain")
      public String matrixParamDecoded(@MatrixParam("param") String param)
      {
         System.out.println("matrixParamDecoded() received: " + param);
         return param;
      }

      @GET
      @Path("encoded")
      @Produces("text/plain")
      public String returnMatrixParamEncoded(@Encoded @MatrixParam("param") String param)
      {
         System.out.println("matrixParamEncoded() received: " + param);
         return param;
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
   public void testMatrixParamRequestDecoded() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/decoded"));
      request.matrixParameter("param", "ac/dc");
      System.out.println("Sending request: " + request.getUri());
      ClientResponse<String> response = request.get(String.class);
      System.out.println("Received response: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ac/dc", response.getEntity());
   }

   @Test
   public void testMatrixParamRequestEncoded() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/encoded"));
      request.matrixParameter("param", "ac/dc");
      System.out.println("Sending request: " + request.getUri());
      ClientResponse<String> response = request.get(String.class);
      System.out.println("Received response: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ac%2Fdc", response.getEntity());
   }

   @Test
   public void testMatrixParamUriBuilderDecoded() throws Exception
   {
      UriBuilder uriBuilder = UriBuilderImpl.fromUri(TestPortProvider.generateURL("/decoded"));
      uriBuilder.matrixParam("param", "ac/dc");
      ClientRequest request = new ClientRequest(uriBuilder.build().toString());
      System.out.println("Sending request to " + uriBuilder.build().toString());
      ClientResponse<String> response = request.get(String.class);
      System.out.println("Received response: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ac/dc", response.getEntity());
   }

   @Test
   public void testMatrixParamUriBuilderEncoded() throws Exception
   {
      UriBuilder uriBuilder = UriBuilderImpl.fromUri(TestPortProvider.generateURL("/encoded"));
      uriBuilder.matrixParam("param", "ac/dc");
      ClientRequest request = new ClientRequest(uriBuilder.build().toString());
      System.out.println("Sending request to " + uriBuilder.build().toString());
      ClientResponse<String> response = request.get(String.class);
      System.out.println("Received response: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ac%2Fdc", response.getEntity());
   }
}
