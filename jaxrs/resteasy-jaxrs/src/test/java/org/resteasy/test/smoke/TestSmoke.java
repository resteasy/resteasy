package org.resteasy.test.smoke;

import org.junit.Assert;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.mock.MockHttpServletRequest;
import org.resteasy.mock.MockHttpServletResponse;
import org.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.test.MockDispatcherFactory;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestSmoke
{

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      HttpServletDispatcher servlet = MockDispatcherFactory.createDispatcher();
      Dispatcher dispatcher = servlet.getDispatcher();

      POJOResourceFactory noDefaults = new POJOResourceFactory(SimpleResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", "/basic");
         request.setPathInfo("/basic");
         MockHttpServletResponse response = new MockHttpServletResponse();

         servlet.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("basic", response.getContentAsString());
      }
      {
         MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/basic");
         request.setPathInfo("/basic");
         request.setContent("basic".getBytes());
         request.setContentType("text/plain");
         MockHttpServletResponse response = new MockHttpServletResponse();

         servlet.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      }
      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", "/queryParam");
         request.setPathInfo("/queryParam");
         request.setQueryString("param=" + URLEncoder.encode("hello world", "UTF-8"));
         MockHttpServletResponse response = new MockHttpServletResponse();

         servlet.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("hello world", response.getContentAsString());
      }
      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", "/uriParam/1234");
         request.setPathInfo("/uriParam/1234");
         MockHttpServletResponse response = new MockHttpServletResponse();

         servlet.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("1234", response.getContentAsString());
      }
   }

   @Test
   public void testLocatingResource() throws Exception
   {
      HttpServletDispatcher servlet = MockDispatcherFactory.createDispatcher();
      Dispatcher dispatcher = servlet.getDispatcher();

      POJOResourceFactory noDefaults = new POJOResourceFactory(LocatingResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", "/locating/basic");
         request.setPathInfo("/locating/basic");
         MockHttpServletResponse response = new MockHttpServletResponse();

         servlet.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("basic", response.getContentAsString());
      }
      {
         MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/locating/basic");
         request.setPathInfo("/locating/basic");
         request.setContent("basic".getBytes());
         request.setContentType("text/plain");
         MockHttpServletResponse response = new MockHttpServletResponse();

         servlet.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      }
      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", "/locating/queryParam");
         request.setPathInfo("/locating/queryParam");
         request.setQueryString("param=" + URLEncoder.encode("hello world", "UTF-8"));
         MockHttpServletResponse response = new MockHttpServletResponse();

         servlet.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("hello world", response.getContentAsString());
      }
      {
         MockHttpServletRequest request = new MockHttpServletRequest("GET", "/locating/uriParam/1234");
         request.setPathInfo("/locating/uriParam/1234");
         MockHttpServletResponse response = new MockHttpServletResponse();

         servlet.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("1234", response.getContentAsString());
      }
   }
}
