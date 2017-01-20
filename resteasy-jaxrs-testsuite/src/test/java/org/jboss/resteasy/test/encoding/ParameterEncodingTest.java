package org.jboss.resteasy.test.encoding;

import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;

import org.junit.Assert;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * RESTEASY-737
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 8, 2012
 */
public class ParameterEncodingTest
{  
   protected static ResteasyDeployment deployment;
   
   @Path("/")
   static public class TestResource
   {
      @GET
      @Produces("text/plain")
      @Path("encoded/pathparam/{pathParam}")
      public String getEncodedPathParam(@Encoded @PathParam("pathParam") String pathParam)
      {
         System.out.println("getEncodedPathParam(): encoded: " + pathParam);
         return pathParam;
      }
      
      @GET
      @Produces("text/plain")
      @Path("decoded/pathparam/{pathParam}")
      public String getDecodedPathParam(@PathParam("pathParam") String pathParam)
      {
         System.out.println("getDecodedPathParam(): decoded: " + pathParam);
         return pathParam;
      }
      
      @GET
      @Produces("text/plain")
      @Path("encoded/matrix")
      public String getEncodedMatrixParam(@Encoded @MatrixParam("m") String matrixParam)
      {
         System.out.println("getEncodedMatrixParam(): encoded: " + matrixParam);
         return matrixParam;
      }
      
      @GET
      @Produces("text/plain")
      @Path("decoded/matrix")
      public String getDecodedMatrixParam(@MatrixParam("m") String matrixParam)
      {
         System.out.println("getDecodedMatrixParam(): decoded: " + matrixParam);
         return matrixParam;
      }
      
      @GET
      @Produces("text/plain")
      @Path("encoded/query")
      public String getEncodedQueryParam(@Encoded @QueryParam("m") String queryParam)
      {
         System.out.println("getEncodedQueryParam(): encoded: " + queryParam);
         return queryParam;
      }
      
      @GET
      @Produces("text/plain")
      @Path("decoded/query")
      public String getDecodedQueryParam(@QueryParam("m") String queryParam)
      {
         System.out.println("getDecodedQueryParam(): decoded: " + queryParam);
         return queryParam;
      }
      
      @POST
      @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
      @Produces("text/plain")
      @Path("encoded/form")
      public String getEncodedFormParam(@Encoded @FormParam("f") String formParam)
      {
         System.out.println("getEncodedFormParamPost(): encoded: " + formParam);
         return formParam;
      }
      
      @POST
      @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
      @Produces("text/plain")
      @Path("decoded/form")
      public String getDecodedFormParam(@FormParam("f") String formParam)
      {
         System.out.println("getDecodedFormParamPost(): decoded: " + formParam);
         return formParam;
      }
      
      @GET
      @Produces("text/plain")
      @Path("encoded/segment/{pathParam}")
      public String getEncodedSegmentPathParam(@Encoded @PathParam("pathParam") PathSegment segment)
      {
         System.out.println("getEncodedSegmentPathParam(): encoded segment: " + segment.getPath());
         return segment.getPath();
      }
      
      @GET
      @Produces("text/plain")
      @Path("decoded/segment/{pathParam}")
      public String getDecodedSegmentPathParam(@PathParam("pathParam") PathSegment segment)
      {
         System.out.println("getDecodedSegmentPathParam(): decoded segment: " + segment.getPath());
         return segment.getPath();
      }
      
      @GET
      @Produces("text/plain")
      @Path("encoded/segment/matrix/{params}")
      public String getEncodedSegmentMatrixParam(@Encoded @PathParam("params") PathSegment segment)
      {
         MultivaluedMap<String, String> map = segment.getMatrixParameters();
         Iterator<String> it = map.keySet().iterator();
         System.out.println("getEncodedSegmentMatrixParam(): encoded matrix params: ");
         StringBuilder builder = new StringBuilder();
         while(it.hasNext())
         {
            String key = it.next();
            System.out.println("  " + key + "->" + map.getFirst(key));
            builder.append(map.getFirst(key));
         }
         return builder.toString();
      }
      
      @GET
      @Produces("text/plain")
      @Path("decoded/segment/matrix/{params}")
      public String getDecodedSegmentMatrixParam(@PathParam("params") PathSegment segment)
      {
         MultivaluedMap<String, String> map = segment.getMatrixParameters();
         Iterator<String> it = map.keySet().iterator();
         System.out.println("getDecodedSegmentMatrixParam(): decoded matrix params: ");
         StringBuilder builder = new StringBuilder();
         while(it.hasNext())
         {
            String key = it.next();
            System.out.println("  " + key + "->" + map.getFirst(key));
            builder.append(map.getFirst(key));
         }
         return builder.toString();
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
   public void testResteasy734() throws Exception
   {
      ClientRequest request = null;
      ClientResponse<String> response = null;
            
      request = new ClientRequest("http://localhost:8081/encoded/pathparam/bee bop");
      response = request.get(String.class);
      System.out.println("Received encoded path param: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee%20bop", response.getEntity());
      response.releaseConnection();

      request = new ClientRequest("http://localhost:8081/decoded/pathparam/bee bop");
      response = request.get(String.class);
      System.out.println("Received decoded path param: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee bop", response.getEntity());
      response.releaseConnection();

      request = new ClientRequest("http://localhost:8081/encoded/matrix;m=bee bop");
      response = request.get(String.class);
      System.out.println("Received encoded matrix param: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee%20bop", response.getEntity());
      response.releaseConnection();

      request = new ClientRequest("http://localhost:8081/decoded/matrix;m=bee bop");
      response = request.get(String.class);
      System.out.println("Received decoded matrix param: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee bop", response.getEntity());
      response.releaseConnection();

      request = new ClientRequest("http://localhost:8081/encoded/query?m=bee bop");
      response = request.get(String.class);
      System.out.println("Received encoded query param: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee%20bop", response.getEntity());
      response.releaseConnection();

      request = new ClientRequest("http://localhost:8081/decoded/query?m=bee bop");
      response = request.get(String.class);
      System.out.println("Received decoded query param: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee bop", response.getEntity());
      response.releaseConnection();

      request = new ClientRequest("http://localhost:8081/encoded/form");
      request.formParameter("f", "bee bop");
      response = request.post(String.class);
      System.out.println("Received encoded form param: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee+bop", response.getEntity());
      response.releaseConnection();

      request = new ClientRequest("http://localhost:8081/decoded/form");
      request.formParameter("f", "bee bop");
      response = request.post(String.class);
      System.out.println("Received decoded form param: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee bop", response.getEntity());
      response.releaseConnection();

      request = new ClientRequest("http://localhost:8081/encoded/segment/bee bop");
      response = request.get(String.class);
      System.out.println("Received encoded path param from segment: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee%20bop", response.getEntity());
      response.releaseConnection();

      request = new ClientRequest("http://localhost:8081/decoded/segment/bee bop");
      response = request.get(String.class);
      System.out.println("Received decoded path param from segment: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee bop", response.getEntity());
      response.releaseConnection();

      request = new ClientRequest("http://localhost:8081/encoded/segment/matrix/params;m=bee bop");
      response = request.get(String.class);
      System.out.println("Received encoded matrix param from segment: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee%20bop", response.getEntity());
      response.releaseConnection();

      request = new ClientRequest("http://localhost:8081/decoded/segment/matrix/params;m=bee bop");
      response = request.get(String.class);
      System.out.println("Received decoded matrix param from segment: " + response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee bop", response.getEntity());
      response.releaseConnection();
   }
}
