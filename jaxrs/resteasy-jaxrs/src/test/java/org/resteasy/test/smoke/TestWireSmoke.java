package org.resteasy.test.smoke;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.test.EmbeddedServletContainer;

import javax.servlet.http.HttpServletResponse;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestWireSmoke
{

   private static HttpServletDispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedServletContainer.start();
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedServletContainer.stop();
   }

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      int oldSize = dispatcher.getRegistry().getSize();
      dispatcher.getRegistry().addResource(SimpleResource.class);
      Assert.assertTrue(oldSize < dispatcher.getRegistry().getSize());

      HttpClient client = new HttpClient();

      {
         GetMethod method = new GetMethod("http://localhost:8081/basic");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("basic", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         PutMethod method = new PutMethod("http://localhost:8081/basic");
         method.setRequestEntity(new StringRequestEntity("basic", "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081/queryParam");
         NameValuePair[] params = {new NameValuePair("param", "hello world")};
         method.setQueryString(params);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("hello world", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081/uriParam/1234");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("1234", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081/should/accept/anything");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("Wild", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081/basic/should/accept");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("Wild", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081/uriParam");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("Wild", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      dispatcher.getRegistry().removeRegistrations(SimpleResource.class);
      Assert.assertEquals(oldSize, dispatcher.getRegistry().getSize());
   }


   @Test
   public void testLocatingResource() throws Exception
   {
      int oldSize = dispatcher.getRegistry().getSize();
      dispatcher.getRegistry().addResource(LocatingResource.class);
      Assert.assertTrue(oldSize < dispatcher.getRegistry().getSize());

      HttpClient client = new HttpClient();

      {
         GetMethod method = new GetMethod("http://localhost:8081/locating/basic");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("basic", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         PutMethod method = new PutMethod("http://localhost:8081/locating/basic");
         method.setRequestEntity(new StringRequestEntity("basic", "text/plain", null));
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081/locating/queryParam");
         NameValuePair[] params = {new NameValuePair("param", "hello world")};
         method.setQueryString(params);
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("hello world", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      {
         GetMethod method = new GetMethod("http://localhost:8081/locating/uriParam/1234");
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("1234", method.getResponseBodyAsString());
         method.releaseConnection();
      }
      dispatcher.getRegistry().removeRegistrations(LocatingResource.class);
      Assert.assertEquals(oldSize, dispatcher.getRegistry().getSize());
   }
}