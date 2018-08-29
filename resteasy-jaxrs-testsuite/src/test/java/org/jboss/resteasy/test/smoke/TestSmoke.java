package org.jboss.resteasy.test.smoke;

import org.jboss.logging.Logger;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.junit.Assert;
import org.junit.Test;

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

   private static final Logger LOG = Logger.getLogger(TestSmoke.class);

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

      POJOResourceFactory noDefaults = new POJOResourceFactory(SimpleResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      {
         MockHttpRequest request = MockHttpRequest.get("/basic/");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("basic", response.getContentAsString());
      }
      {
         MockHttpRequest request = MockHttpRequest.put("/basic");
         request.content("basic".getBytes());
         request.contentType("text/plain");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(204, response.getStatus());
      }
      {
         MockHttpRequest request = MockHttpRequest.get("/queryParam?" + "param=" + URLEncoder.encode("hello world", "UTF-8"));
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("hello world", response.getContentAsString());
      }
      {
         MockHttpRequest request = MockHttpRequest.get("/uriParam/1234");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("1234", response.getContentAsString());
      }
   }

   @Test
   public void testLocatingResource() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

      POJOResourceFactory noDefaults = new POJOResourceFactory(LocatingResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      {
         MockHttpRequest request = MockHttpRequest.get("/locating/basic");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("basic", response.getContentAsString());
      }
      {
         MockHttpRequest request = MockHttpRequest.put("/locating/basic");
         request.content("basic".getBytes());
         request.contentType("text/plain");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(204, response.getStatus());
      }
      {
         MockHttpRequest request = MockHttpRequest.get("/locating/queryParam?" + "param=" + URLEncoder.encode("hello world", "UTF-8"));
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("hello world", response.getContentAsString());
      }
      {
         MockHttpRequest request = MockHttpRequest.get("/locating/uriParam/1234");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);


         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("1234", response.getContentAsString());
      }
   }


   @Test
   public void testBadNumericMatch() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

      POJOResourceFactory noDefaults = new POJOResourceFactory(LocatingResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      {
         MockHttpRequest request = MockHttpRequest.get("/locating/uriParam/x123");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);

         Assert.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
      }

   }


   @Test
   public void testNotLocating() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

      POJOResourceFactory noDefaults = new POJOResourceFactory(LocatingResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      {
         MockHttpRequest request = MockHttpRequest.get("/notlocating");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);

         Assert.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
      }

   }


   @Test
   public void testNotMappedInSubresource() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

      POJOResourceFactory noDefaults = new POJOResourceFactory(LocatingResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      {
         MockHttpRequest request = MockHttpRequest.get("/locating/notmatching");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);

         Assert.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
      }

   }


   @Test
   public void testSubSubresource() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

      POJOResourceFactory noDefaults = new POJOResourceFactory(LocatingResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      {
         MockHttpRequest request = MockHttpRequest.get("/subresource/subresource/subresource/basic");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);

         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
         Assert.assertEquals("basic", response.getContentAsString());
      }

   }


   @Test
   public void testContextParam() throws Exception
   {
      Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();

      POJOResourceFactory noDefaults = new POJOResourceFactory(LocatingResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      {

         LOG.info("Expect to see WARN about not injecting in subresources");

         MockHttpRequest request = MockHttpRequest.get("/subresource/testContextParam");
         MockHttpResponse response = new MockHttpResponse();

         dispatcher.invoke(request, response);

         Assert.assertEquals(204, response.getStatus());
      }

   }


}
