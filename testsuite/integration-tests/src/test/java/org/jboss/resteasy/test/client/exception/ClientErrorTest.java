package org.jboss.resteasy.test.client.exception;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.jboss.resteasy.test.client.exception.resource.ClientErrorResource;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.20
 * @tpTestCaseDetails Test client error caused by bad media type
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientErrorTest
{
   private static Client client;

   @Deployment
   public static Archive<?> deploy() {
       WebArchive war = TestUtil.prepareArchive(ClientErrorTest.class.getSimpleName());
       war.addClass(PortProviderUtil.class);
       war.addClass(TestUtil.class);
       return TestUtil.finishContainerPrepare(war, null, ClientErrorResource.class);
   }

   private String generateURL(String path) {
       return PortProviderUtil.generateURL(path, ClientErrorTest.class.getSimpleName());
   }

   @BeforeClass
   public static void before() throws Exception
   {
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception
   {
      client.close();
   }

   /**
    * @tpTestDetails There are two methods that match path, but only one matches Accept.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testComplex()
   {
      Builder builder = client.target(generateURL("/complex/match")).request();
      builder.header(HttpHeaderNames.ACCEPT, "text/xml");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }

   /**
    * @tpTestDetails No method matches path.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testNotFound()
   {
      Builder builder = client.target(generateURL("/foo/notthere")).request();
      builder.header(HttpHeaderNames.ACCEPT, "application/foo");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }

   /**
    * @tpTestDetails Two methods match path, but neither matches HTTP method.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testMethodNotAllowed()
   {
      Builder builder = client.target(generateURL("")).request();
      builder.header(HttpHeaderNames.ACCEPT, "application/foo");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, response.getStatus());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }

   /**
    * @tpTestDetails There is a method that matches path but does not match Accept.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testNotAcceptable()
   {
      Builder builder = client.target(generateURL("")).request();
      builder.header(HttpHeaderNames.ACCEPT, "application/bar");
      Response response = null;
      try
      {
         response = builder.post(Entity.entity("content", "application/bar"));
         Assert.assertEquals(HttpServletResponse.SC_NOT_ACCEPTABLE, response.getStatus());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }

   /**
    * @tpTestDetails Matching POST method returns no entity.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testNoContentPost()
   {
      Builder builder = client.target(generateURL("/nocontent")).request();
      Response response = null;
      try
      {
         response = builder.post(Entity.entity("content", "text/plain"));
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }

   /**
    * @tpTestDetails Matching DELETE returns no entity.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testNoContent()
   {
      Builder builder = client.target(generateURL("")).request();
      Response response = null;
      try
      {
         response = builder.delete();
         Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }  
      finally
      {
         response.close();
      }
   }

   /**
    * @tpTestDetails Method matches path but does not match content type.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testUnsupportedMediaType()
   {
      Builder builder = client.target(generateURL("")).request();
      builder.header(HttpHeaderNames.ACCEPT, "application/foo");
      Response response = null;
      try
      {
         response = builder.post(Entity.entity("content", "text/plain"));
         Assert.assertEquals(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, response.getStatus());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }

   /**
    * @tpTestDetails Method matches path but not bad Accept media type with no subtype.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testBadAcceptMediaTypeNoSubType()
   {
      Builder builder = client.target(generateURL("/complex/match")).request();
      builder.header(HttpHeaderNames.ACCEPT, "text");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }

   /**
    * @tpTestDetails Method matches path but not bad Accept media type with nonnumerical q value.
    * @tpSince RESTEasy 3.0.20
    */
   @Test
   public void testBadAcceptMediaTypeNonNumericQualityValue()
   {
      Builder builder = client.target(generateURL("/complex/match")).request();
      builder.header(HttpHeaderNames.ACCEPT, "text/plain; q=bad");
      Response response = null;
      try
      {
         response = builder.get();
         Assert.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         response.close();
      }
   }
   
   static class TestMediaTypeHeaderDelegate extends MediaTypeHeaderDelegate
   {
      public static MediaType parse(String type)
      {
         if ("text".equals(type))
         {
            return new MediaType("text", "");
         }
         return MediaTypeHeaderDelegate.parse(type);
      }
   }
}
