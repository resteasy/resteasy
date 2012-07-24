package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExtensionTest
{

   @Path("/extension")
   public static class ExtensionResource
   {
      @GET
      @Produces("*/*")
      public String getDefault()
      {
         return "default";
      }

      @GET
      @Produces("application/xml")
      public String getXml(@Context HttpHeaders headers)
      {
         @SuppressWarnings("unused")
         List<Locale> languages = headers.getAcceptableLanguages();
         @SuppressWarnings("unused")
         List<MediaType> mediaTypes = headers.getAcceptableMediaTypes();
         return "xml";
      }

      @GET
      @Produces("text/html")
      public String getXmlTwo(@Context HttpHeaders headers)
      {
         List<Locale> languages = headers.getAcceptableLanguages();
         Assert.assertEquals(1, languages.size());
         Assert.assertEquals(new Locale("en", "us"), languages.get(0));
         Assert.assertEquals(MediaType.valueOf("text/html"), headers.getAcceptableMediaTypes().get(0));
         return "html";
      }

      @GET
      @Path("/stuff.old")
      @Produces("text/plain")
      public String getJson(@Context HttpHeaders headers)
      {
         List<Locale> languages = headers.getAcceptableLanguages();
         Assert.assertEquals(1, languages.size());
         Assert.assertEquals(new Locale("en", "us"), languages.get(0));
         Assert.assertEquals(MediaType.valueOf("text/plain"), headers.getAcceptableMediaTypes().get(0));
         return "plain";
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      Map<String, String> mimeMap = new HashMap<String, String>();
      mimeMap.put("xml", "application/xml");
      mimeMap.put("html", "text/html");
      mimeMap.put("txt", "text/plain");
      Map<String, String> languageMap = new HashMap<String, String>();
      languageMap.put("en", "en-us");
      deployment.setMediaTypeMappings(mimeMap);
      deployment.setLanguageExtensions(languageMap);
      deployment.getActualResourceClasses().add(ExtensionResource.class);
      EmbeddedContainer.start(deployment);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   private void _test(String path, String body)
   {
      ClientRequest request = new ClientRequest(generateURL(path));
      try
      {
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
         Assert.assertEquals(body, response.getEntity());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Test
   public void testIt()
   {
      _test("/extension.xml", "xml");
      _test("/extension.html.en", "html");
      _test("/extension.en.html", "html");
      _test("/extension/stuff.old.en.txt", "plain");
      _test("/extension/stuff.en.old.txt", "plain");
      _test("/extension/stuff.en.txt.old", "plain");
   }

   @Test
   public void testError()
   {
      ClientRequest request = new ClientRequest(generateURL("/extension.junk"));
      try
      {
         ClientResponse<String> response = request.get(String.class);
         Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, response.getStatus());
         response.releaseConnection();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}