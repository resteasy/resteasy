package org.jboss.resteasy.test.typemapping;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.assertEquals;

public class TypeMappingTest
{

   @Test
   public void acceptXMLOnlyRequestNoProducesNoExtension() throws Exception
   {
      requestAndAssert("noproduces", null, "application/xml", "application/xml");
   }

   @Test
   public void acceptJSONOnlyRequestNoProducesNoExtension() throws Exception
   {
      requestAndAssert("noproduces", null, "application/json", "application/json");
   }

   @Test
   public void acceptNullRequestNoProducesJSONExtension() throws Exception
   {
      requestAndAssert("noproduces", "json", null, "application/json");
   }

   @Test
   public void acceptNullRequestNoProducesXMLExtension() throws Exception
   {
      requestAndAssert("noproduces", "xml", null, "application/xml");
   }

   @Test
   public void acceptJSONOnlyRequestNoProducesJSONExtension() throws Exception
   {
      requestAndAssert("noproduces", "json", "application/json", "application/json");
   }

   @Test
   public void acceptJSONOnlyRequestNoProducesXMLExtension() throws Exception
   {
      requestAndAssert("noproduces", "xml", "application/json", "application/xml");
   }

   @Test
   public void acceptJSONAndXMLRequestNoProducesJSONExtension() throws Exception
   {
      requestAndAssert("noproduces", "json", "application/json, application/xml",
              "application/json");
   }

   @Test
   public void acceptXMLAndJSONRequestNoProducesJSONExtension() throws Exception
   {
      requestAndAssert("noproduces", "json", "application/xml, application/json",
              "application/json");
   }

   @Test
   public void acceptXMLOnlyRequestNoProducesXMLExtension() throws Exception
   {
      requestAndAssert("noproduces", "xml", "application/xml", "application/xml");
   }

   @Test
   public void acceptXMLOnlyRequestNoProducesJSONExtension() throws Exception
   {
      requestAndAssert("noproduces", "json", "application/xml", "application/json");
   }

   @Test
   public void acceptJSONAndXMLRequestNoProducesXMLExtension() throws Exception
   {
      requestAndAssert("noproduces", "xml", "application/json, application/xml",
              "application/xml");
   }

   @Test
   public void acceptXMLAndJSONRequestNoProducesXMLExtension() throws Exception
   {
      requestAndAssert("noproduces", "xml", "application/xml, application/json",
              "application/xml");
   }

   @Before
   public void startServer() throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      Map<String, String> mediaTypeMappings = new HashMap<String, String>();
      mediaTypeMappings.put("xml", "application/xml");
      mediaTypeMappings.put("json", "application/json");
      deployment.setMediaTypeMappings(mediaTypeMappings);
      EmbeddedContainer.start(deployment);

      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @After
   public void stopServer() throws Exception
   {
      EmbeddedContainer.stop();
   }

   private void requestAndAssert(String path, String extension, String accept,
                                 String expectedContentType) throws Exception
   {
      String url = generateURL("/test/" + path);
      if (extension != null)
      {
         url = url + "." + extension;
      }
      ClientRequest request = new ClientRequest(url);
      if (accept != null)
      {
         request.header(HttpHeaderNames.ACCEPT, accept);
      }
      ClientResponse<?> response = request.get();
      assertEquals("Request for " + url + " returned a non-200 status", 200, response.getStatus());
      assertEquals("Request for " + url + " returned an unexpected content type",
              expectedContentType, response.getResponseHeaders().getFirst("Content-type"));
   }

   @XmlRootElement
   public static class TestBean
   {
      private String name;

      public TestBean()
      {

      }

      public TestBean(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

   }

   @Path("/test")
   public static class TestResource
   {

      @GET
      @Path("/noproduces")
      public TestBean get()
      {
         return new TestBean("name");
      }
   }
}
