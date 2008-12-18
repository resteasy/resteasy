package org.jboss.resteasy.test.smoke;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestWireSmoke
{

   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      int oldSize = dispatcher.getRegistry().getSize();
      dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
      Assert.assertTrue(oldSize < dispatcher.getRegistry().getSize());

      HttpClient client = new HttpClient();

      {
         GetMethod method = createGetMethod("/basic");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("basic", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         PutMethod method = createPutMethod("/basic");
         method.setRequestEntity(new StringRequestEntity("basic", "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(204, status);
         method.releaseConnection();
      }
      {
         GetMethod method = createGetMethod("/queryParam");
         NameValuePair[] params =
                 {new NameValuePair("param", "hello world")};
         method.setQueryString(params);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("hello world", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = createGetMethod("/uriParam/1234");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("1234", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      dispatcher.getRegistry().removeRegistrations(SimpleResource.class);
      Assert.assertEquals(oldSize, dispatcher.getRegistry().getSize());
   }

   @Test
   public void testLocatingResource() throws Exception
   {
      int oldSize = dispatcher.getRegistry().getSize();
      dispatcher.getRegistry().addPerRequestResource(LocatingResource.class);
      Assert.assertTrue(oldSize < dispatcher.getRegistry().getSize());

      HttpClient client = new HttpClient();

      {
         GetMethod method = createGetMethod("/locating/basic");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("basic", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         PutMethod method = createPutMethod("/locating/basic");
         method.setRequestEntity(new StringRequestEntity("basic", "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(204, status);
         method.releaseConnection();
      }
      {
         GetMethod method = createGetMethod("/locating/queryParam");
         NameValuePair[] params =
                 {new NameValuePair("param", "hello world")};
         method.setQueryString(params);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("hello world", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = createGetMethod("/locating/uriParam/1234");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("1234", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      dispatcher.getRegistry().removeRegistrations(LocatingResource.class);
      Assert.assertEquals(oldSize, dispatcher.getRegistry().getSize());
   }
}