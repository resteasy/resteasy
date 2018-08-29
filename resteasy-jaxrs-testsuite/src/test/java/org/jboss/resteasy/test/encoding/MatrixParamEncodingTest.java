package org.jboss.resteasy.test.encoding;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
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
   private static final Logger LOG = Logger.getLogger(MatrixParamEncodingTest.class);
   protected static ResteasyDeployment deployment;
   
   @Path("/")
   static public class TestResource
   {
      @GET
      @Path("decoded")
      @Produces("text/plain")
      public String matrixParamDecoded(@MatrixParam("param") String param)
      {
         LOG.info("matrixParamDecoded() received: " + param);
         return param;
      }
      
      @GET
      @Path("encoded")
      @Produces("text/plain")
      public String returnMatrixParamEncoded(@Encoded @MatrixParam("param") String param)
      {
         LOG.info("matrixParamEncoded() received: " + param);
         return param;
      }
   }
   
   @BeforeClass
   public static void setup() throws Exception
   {
      deployment = EmbeddedContainer.start();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }
   

   @AfterClass
   public static void shutdown() throws Exception
   {
      EmbeddedContainer.stop();
      deployment = null;
   }

   @Test
   public void testMatrixParamRequestDecoded() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8081/decoded");
      request.matrixParameter("param", "ac/dc");
      LOG.info("Sending request: " + request.getUri());
      ClientResponse<String> response = request.get(String.class);
      LOG.info("Received response: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ac/dc", response.getEntity());
      response.releaseConnection();
   }
   
   @Test
   public void testMatrixParamRequestEncoded() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:8081/encoded");
      request.matrixParameter("param", "ac/dc");
      LOG.info("Sending request: " + request.getUri());
      ClientResponse<String> response = request.get(String.class);
      LOG.info("Received response: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ac%2Fdc", response.getEntity());
      response.releaseConnection();
   }
   
   @Test
   public void testMatrixParamUriBuilderDecoded() throws Exception
   {
      UriBuilder uriBuilder = ResteasyUriBuilder.fromUri("http://localhost:8081/decoded");
      uriBuilder.matrixParam("param", "ac/dc");
      ClientRequest request = new ClientRequest(uriBuilder.build().toString());
      LOG.info("Sending request to " + uriBuilder.build().toString());
      ClientResponse<String> response = request.get(String.class);
      LOG.info("Received response: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ac/dc", response.getEntity());
      response.releaseConnection();
   }
   
   @Test
   public void testMatrixParamUriBuilderEncoded() throws Exception
   {
      UriBuilder uriBuilder = ResteasyUriBuilder.fromUri("http://localhost:8081/encoded");
      uriBuilder.matrixParam("param", "ac/dc");
      ClientRequest request = new ClientRequest(uriBuilder.build().toString());
      LOG.info("Sending request to " + uriBuilder.build().toString());
      ClientResponse<String> response = request.get(String.class);
      LOG.info("Received response: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ac%2Fdc", response.getEntity());
      response.releaseConnection();
   }
}
