package org.jboss.resteasy.test.resource.basic;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class MultipleAcceptHeaderTest
{

   protected static String APPLICATION_JSON = "Content-Type: application/json";

   protected static String APPLICATION_XML = "Content-Type: application/xml";

   private TestInterfaceClient service;

   @Deployment
   public static Archive<?> deploy() throws Exception
   {
      WebArchive war = TestUtil.prepareArchive(MultipleAcceptHeaderTest.class.getSimpleName());
      return TestUtil.finishContainerPrepare(war, null, TestResourceServer.class);
   }

   private String generateBaseUrl()
   {
      return PortProviderUtil.generateBaseUrl(MultipleAcceptHeaderTest.class.getSimpleName());
   }

   @Path("/test")
   static public class TestResourceServer
   {
      @GET
      @Path("accept")
      @Produces("application/json")
      public String acceptJson()
      {
         return APPLICATION_JSON;
      }

      @GET
      @Path("accept")
      @Produces(
      {"application/xml", "text/plain"})
      public String acceptXml()
      {
         return APPLICATION_XML;
      }
   }

   @Path("test")
   interface TestInterfaceClient
   {
      @GET
      @Path("accept")
      @Produces("application/json")
      String getJson();

      @GET
      @Path("accept")
      @Produces("application/xml")
      String getXml();

      @GET
      @Path("accept")
      @Produces(
      {"application/wrong1", "application/wrong2", "application/xml"})
      String getXmlMultiple();

      @GET
      @Path("accept")
      @Produces(
      {"application/wrong1", "text/plain"})
      String getXmlPlainMultiple();
   }

   @Before
   public void setUp() throws Exception
   {
      ResteasyWebTarget target = (ResteasyWebTarget) ClientBuilder.newClient().target(generateBaseUrl());
      service = target.proxy(TestInterfaceClient.class);
   }

   @Test
   public void testSingleAcceptHeader() throws Exception
   {
      String result = service.getJson();
      Assert.assertEquals(APPLICATION_JSON, result);
   }

   @Test
   public void testSingleAcceptHeader2() throws Exception
   {
      String result = service.getXml();
      Assert.assertEquals(APPLICATION_XML, result);
   }

   @Test
   public void testMultipleAcceptHeader() throws Exception
   {
      String result = service.getXmlMultiple();
      Assert.assertEquals(APPLICATION_XML, result);
   }

   @Test
   public void testMultipleAcceptHeaderSecondHeader() throws Exception
   {
      String result = service.getXmlPlainMultiple();
      Assert.assertEquals(APPLICATION_XML, result);
   }
}
