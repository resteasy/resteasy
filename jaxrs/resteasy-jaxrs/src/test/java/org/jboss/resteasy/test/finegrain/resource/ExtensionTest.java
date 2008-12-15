package org.jboss.resteasy.test.finegrain.resource;

import static org.jboss.resteasy.test.TestPortProvider.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ExtensionTest
{
   private static Dispatcher dispatcher;

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
      dispatcher = EmbeddedContainer.start();
      Map<String, MediaType> mimeMap = new HashMap<String, MediaType>();
      mimeMap.put("xml", MediaType.valueOf("application/xml"));
      mimeMap.put("html", MediaType.valueOf("text/html"));
      mimeMap.put("txt", MediaType.valueOf("text/plain"));
      Map<String, String> languageMap = new HashMap<String, String>();
      languageMap.put("en", "en-us");
      dispatcher.setMediaTypeMappings(mimeMap);
      dispatcher.setLanguageMappings(languageMap);
      dispatcher.getRegistry().addPerRequestResource(ExtensionResource.class);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   private void _test(HttpClient client, String path, String body)
   {
      {
         GetMethod method = createGetMethod(path);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
            Assert.assertEquals(body, method.getResponseBodyAsString());
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }

   }

   @Test
   public void testIt()
   {
      HttpClient client = new HttpClient();
      _test(client, "/extension.xml", "xml");
      _test(client, "/extension.html.en", "html");
      _test(client, "/extension.en.html", "html");
      _test(client, "/extension/stuff.old.en.txt", "plain");
      _test(client, "/extension/stuff.en.old.txt", "plain");
      _test(client, "/extension/stuff.en.txt.old", "plain");
   }

   @Test
   public void testError()
   {
      HttpClient client = new HttpClient();
      {
         GetMethod method = createGetMethod("/extension.junk");
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_NOT_FOUND);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
   }
}