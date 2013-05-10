package org.jboss.resteasy.test.nextgen.encoding;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

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
   protected static ResteasyDeployment deployment;
   protected static ResteasyClient client;
   
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
   public void testMatrixParamRequestDecoded() throws Exception
   {
      ResteasyWebTarget target = client.target("http://localhost:8081/decoded").matrixParam("param", "ac/dc");
      Response response = target.request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ac/dc", response.readEntity(String.class));
      response.close();
   }
   
   @Test
   public void testMatrixParamRequestEncoded() throws Exception
   {
      ResteasyWebTarget target = client.target("http://localhost:8081/encoded").matrixParam("param", "ac/dc");
      Response response = target.request().get();
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ac%2Fdc", response.readEntity(String.class));
      response.close();
   }
   
   @Test
   public void testMatrixParamUriBuilderDecoded() throws Exception
   {
      UriBuilder uriBuilder = ResteasyUriBuilder.fromUri("http://localhost:8081/decoded");
      uriBuilder.matrixParam("param", "ac/dc");
      ResteasyWebTarget target = client.target(uriBuilder.build().toString());
      System.out.println("Sending request to " + uriBuilder.build().toString());
      Response response = target.request().get();
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ac/dc", entity);
      response.close();
   }
   
   @Test
   public void testMatrixParamUriBuilderEncoded() throws Exception
   {
      UriBuilder uriBuilder = ResteasyUriBuilder.fromUri("http://localhost:8081/encoded");
      uriBuilder.matrixParam("param", "ac/dc");
      ResteasyWebTarget target = client.target(uriBuilder.build().toString());
      System.out.println("Sending request to " + uriBuilder.build().toString());
      Response response = target.request().get();
      String entity = response.readEntity(String.class);
      System.out.println("Received response: " + entity);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("ac%2Fdc", entity);
      response.close();
   }
}
