package org.jboss.resteasy.test.client;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author marvgilb
 * @since 09.07.2016
 *
 */
public class MatrixPathParamTest extends BaseResourceTest {
   
   protected ResteasyDeployment deployment;
   TestInterfaceClient service;
   
   //#####################################
   
   @Path("/")
   static public class TestResourceServer
   {
      @Path("matrix1")
      public TestSubResourceServer getM1(@MatrixParam("m1") String m1) {
         return new TestSubResourceServer(m1);
      }
   }
   
   static public class TestSubResourceServer
   {
      protected String m1;
      
      TestSubResourceServer(String m1) {
         this.m1 = m1;
      }
      
      @GET
      @Path("matrix2")
      public String getM2(@MatrixParam("m2") String m2) {
         return m1 + m2;
      }
   }
   
   @Path("/")
   public interface TestInterfaceClient
   {
      @Path("matrix1")
      public TestSubInterfaceClient getM1(@MatrixParam("m1") String m1);
   }
   
   public interface TestSubInterfaceClient
   {
      @GET
      @Path("matrix2")
      public String getM2(@MatrixParam("m2") String m2);
   }
   
   //#############Methods###############
   
   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(TestResourceServer.class);
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/"));
      service = target.proxy(TestInterfaceClient.class);
   }
   
   @Test
   public void testSingleAcceptHeader() throws Exception
   {
      String result = service.getM1("a").getM2("b");
      assertEquals("ab", result);
   }

}
