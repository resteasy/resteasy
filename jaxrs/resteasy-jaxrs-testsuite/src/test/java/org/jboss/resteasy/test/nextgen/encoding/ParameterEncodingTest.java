package org.jboss.resteasy.test.nextgen.encoding;

import junit.framework.Assert;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.util.Iterator;

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
   protected static ResteasyClient client;
   
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
      client = new ResteasyClientBuilder().build();
   }

   @AfterClass
   public static void shutdown() throws Exception
   {
      client.close();
      EmbeddedContainer.stop();
      deployment = null;
   }

   @Test
   public void testResteasy734() throws Exception
   {
      ResteasyWebTarget target = null;
      Response response = null;
            
      target = client.target("http://localhost:8081/encoded/pathparam/bee bop");
      response = target.request().get();
      String entity = response.readEntity(String.class);
      System.out.println("Received encoded path param: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee%20bop", entity);
      response.close();

      target = client.target("http://localhost:8081/decoded/pathparam/bee bop");
      response = target.request().get();
      entity = response.readEntity(String.class);
      System.out.println("Received decoded path param: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee bop", entity);
      response.close();

      target = client.target("http://localhost:8081/encoded/matrix;m=bee bop");
      response = target.request().get();
      entity = response.readEntity(String.class);
      System.out.println("Received encoded matrix param: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee%20bop", entity);
      response.close();

      target = client.target("http://localhost:8081/decoded/matrix;m=bee bop");
      response = target.request().get();
      entity = response.readEntity(String.class);
      System.out.println("Received decoded matrix param: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee bop", entity);
      response.close();

      target = client.target("http://localhost:8081/encoded/query?m=bee bop");
      response = target.request().get();
      entity = response.readEntity(String.class);
      System.out.println("Received encoded query param: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee%20bop", entity);
      response.close();

      target = client.target("http://localhost:8081/decoded/query?m=bee bop");
      response = target.request().get();
      entity = response.readEntity(String.class);
      System.out.println("Received decoded query param: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee bop", entity);
      response.close();

      target = client.target("http://localhost:8081/encoded/form");
      Form form = new Form();
      form.param("f", "bee bop");
      response = target.request().post(Entity.form(form));
      entity = response.readEntity(String.class);
      System.out.println("Received encoded form param: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee+bop", entity);
      response.close();

      target = client.target("http://localhost:8081/decoded/form");
      form = new Form();
      form.param("f", "bee bop");
      response = target.request().post(Entity.form(form));
      entity = response.readEntity(String.class);
      System.out.println("Received decoded form param: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee bop", entity);
      response.close();

      target = client.target("http://localhost:8081/encoded/segment/bee bop");
      response = target.request().get();
      entity = response.readEntity(String.class);
      System.out.println("Received encoded path param from segment: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee%20bop", entity);
      response.close();

      target = client.target("http://localhost:8081/decoded/segment/bee bop");
      response = target.request().get();
      entity = response.readEntity(String.class);
      System.out.println("Received decoded path param from segment: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee bop", entity);
      response.close();

      target = client.target("http://localhost:8081/encoded/segment/matrix/params;m=bee bop");
      response = target.request().get();
      entity = response.readEntity(String.class);
      System.out.println("Received encoded matrix param from segment: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee%20bop", entity);
      response.close();

      target = client.target("http://localhost:8081/decoded/segment/matrix/params;m=bee bop");
      response = target.request().get();
      entity = response.readEntity(String.class);
      System.out.println("Received decoded matrix param from segment: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("bee bop", entity);
      response.close();
   }
}
