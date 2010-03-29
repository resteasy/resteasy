package org.jboss.resteasy.test.encoding;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;

public class EncodingTest
{
   private static TJWSEmbeddedJaxrsServer tjws;
   private static TestClient testClient;

   @BeforeClass
   public static void setupContainer()
   {
      tjws = new TJWSEmbeddedJaxrsServer();
      tjws.setPort(TestPortProvider.getPort());
      tjws.setRootResourcePath("/");
      tjws.start();
      tjws.getDeployment().getDispatcher().getRegistry().addSingletonResource(new MyTestResource());
      String url = "http://localhost:" + TestPortProvider.getPort();
      testClient = ProxyFactory.create(TestClient.class, url);

   }

   @AfterClass
   public static void teardownContainer()
   {
      tjws.stop();
   }

   Character[] RESERVED_CHARACTERS = {
           '?', ':', '@', '&', '=', '+', '$', ','
   };

   //also includes a-zA-Z0-9
   Character[] UNRESERVED_CHARACTERS = {
           '-', '_', '.', '!', '~', '*', '\'', '(', ')'
   };

   //also includes 0x00-0x1F and 0x7F
   Character[] EXCLUDED_CHARACTERS = {
           ' ', '<', '>', '#', '%', '\"'
   };

   Character[] UNWISE_CHARACTERS = {
           '{', '}', '|', '\\', '^', '[', ']', '`'
   };

   /**
    * Tests requesting special characters via a ClientProxy.
    */
   @Test
   public void testEncodingCharacters() throws Exception
   {
      for (Character ch : RESERVED_CHARACTERS)
      {
         encodingCharacter(ch);
      }
      for (Character ch : UNRESERVED_CHARACTERS)
      {
         encodingCharacter(ch);
      }
      for (Character ch : EXCLUDED_CHARACTERS)
      {
         encodingCharacter(ch);
      }
      for (Character ch : UNWISE_CHARACTERS)
      {
         encodingCharacter(ch);
      }
   }

   public void encodingCharacter(Character toTest)
   {
      String paramWithChar = "start" + toTest + "end";
      System.out.println("*** " + paramWithChar);
      ClientResponse<String> returned = testClient.getPathParam(paramWithChar);
      Assert.assertNotNull(returned);
      Assert.assertEquals(returned.getStatus(), HttpURLConnection.HTTP_OK);
      Assert.assertEquals(returned.getEntity(), paramWithChar);
   }

   @Test
   public void testPercent()
   {
      encodingCharacter('\\');
   }

   /**
    * Tests requesting special characters via manual URL construction.
    */
   @Test
   public void testViaDirectURI() throws Exception
   {
      for (Character ch : RESERVED_CHARACTERS)
      {
         viaDirectURI(ch);
      }
      for (Character ch : UNRESERVED_CHARACTERS)
      {
         viaDirectURI(ch);
      }
      for (Character ch : EXCLUDED_CHARACTERS)
      {
         viaDirectURI(ch);
      }
      for (Character ch : UNWISE_CHARACTERS)
      {
         viaDirectURI(ch);
      }

   }

   public void viaDirectURI(Character toTest) throws Exception
   {
      System.out.println("*** testing character '" + toTest + "'");
      String uriBase = "http://localhost:" + TestPortProvider.getPort() + "/test/path-param/";
      String expected = "start" + toTest + "end";
      String encoded = "start%" + Integer.toHexString(toTest).toUpperCase() + "end";
      URI uri = URI.create(uriBase + encoded);

      HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("accept", "text/plain");
      InputStream is = connection.getInputStream();
      Reader r = new InputStreamReader(is, "UTF-8");
      StringBuffer buf = new StringBuffer();
      char[] chars = new char[1024];
      int charsRead;
      while ((charsRead = r.read(chars)) != -1)
      {
         buf.append(chars, 0, charsRead);
      }
      r.close();
      is.close();

      Assert.assertEquals(buf.toString(), expected);
   }
}
