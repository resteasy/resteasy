package org.jboss.resteasy.test.client;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.client.MatrixPathParamTest.TestInterfaceClient;
import org.junit.Before;
import org.junit.Test;

/**
 * @author marvgilb
 * @since 09.07.2016
 *
 */
public class MultipleAcceptHeaderTest extends BaseResourceTest {
   
   protected ResteasyDeployment deployment;
   TestInterfaceClient service;
   
   protected static String APPLICATION_JSON = "Content-Type: application/json";
   protected static String APPLICATION_XML = "Content-Type: application/xml";
   
   //#####################################
   
   @Path("/test")
   public class TestResourceServer
   {
      @GET
      @Path("accept")
      @Produces("application/json")
      public String acceptJson() {
         return APPLICATION_JSON;
      }

      @GET
      @Path("accept")
      @Produces({"application/xml", "text/plain"})
      public String acceptXml() {
         return APPLICATION_XML;
      }
   }
   
   @Path("test")
   interface TestInterfaceClient
   {
      @GET
      @Path("accept")
      @Produces("application/json")
      public String getJson();
      
      @GET
      @Path("accept")
      @Produces("application/xml")
      public String getXml();
      
      @GET
      @Path("accept")
      @Produces({"application/wrong1", "application/wrong2", "application/xml"})
      public String getXmlMultiple();
      
      @GET
      @Path("accept")
      @Produces({"application/wrong1", "text/plain"})
      public String getXmlPlainMultiple();
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
      String result = service.getJson();
      assertEquals(APPLICATION_JSON, result);
      if(APPLICATION_JSON.equals(result)){
         System.out.println("Success");
      } else {
         System.out.println("Fail");
      }
   }
   
   @Test
   public void testSingleAcceptHeader2() throws Exception
   {
      String result = service.getXml();
      assertEquals(APPLICATION_XML, result);
      if(APPLICATION_XML.equals(result)){
         System.out.println("Success");
      } else {
         System.out.println("Fail");
      }
   }
   
   @Test
   public void testMultipleAcceptHeader() throws Exception
   {
      String result = service.getXmlMultiple();
      assertEquals(APPLICATION_XML, result);
      if(APPLICATION_XML.equals(result)){
         System.out.println("Success");
      } else {
         System.out.println("Fail");
      }
   }
   
   @Test
   public void testMultipleAcceptHeaderSecondHeader() throws Exception
   {
      String result = service.getXmlPlainMultiple();
      assertEquals(APPLICATION_XML, result);
      if(APPLICATION_XML.equals(result)){
         System.out.println("Success");
      } else {
         System.out.println("Fail");
      }
   }
}
