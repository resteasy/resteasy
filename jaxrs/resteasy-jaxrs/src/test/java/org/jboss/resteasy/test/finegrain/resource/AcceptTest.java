package org.jboss.resteasy.test.finegrain.resource;

import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AcceptTest
{
   @BeforeClass
   public static void start()
   {
   }

   @Path("/")
   public static class WebResource
   {
      @Produces("application/foo")
      @GET
      public String doGetFoo()
      {
         return "foo";
      }

      @Produces("application/bar")
      @GET
      public String doGetBar()
      {
         return "bar";
      }

      @Produces("application/baz")
      @GET
      public String doGetBaz()
      {
         return "baz";
      }

      @Produces("*/*")
      @GET
      public Response doGetWildCard()
      {
         return Response.ok("wildcard", "application/wildcard").build();
      }
   }

   private HttpRequest createRequest(String httpMethod, String path, MediaType contentType, List<MediaType> accepts)
   {
      MockHttpRequest request = null;
      try
      {
         request = MockHttpRequest.create(httpMethod, path).contentType(contentType);
      }
      catch (URISyntaxException e)
      {
         throw new RuntimeException(e);
      }
      request.accept(accepts);

      // finally strip out matrix parameters

      StringBuffer preprocessedPath = new StringBuffer();
      for (PathSegment pathSegment : request.getUri().getPathSegments())
      {
         preprocessedPath.append("/").append(pathSegment.getPath());
      }
      request.setPreprocessedPath(preprocessedPath.toString());
      return request;
   }

   @Test
   public void testAcceptGet() throws Exception
   {
      Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
      registry.addPerRequestResource(WebResource.class);

      MediaType contentType = new MediaType("text", "plain");

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.valueOf("application/foo"));
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(WebResource.class.getMethod("doGetFoo"), method.getMethod());
      }

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.valueOf("application/foo;q=0.1"));
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(WebResource.class.getMethod("doGetFoo"), method.getMethod());
      }

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.valueOf("application/foo"));
         accepts.add(MediaType.valueOf("application/bar;q=0.4"));
         accepts.add(MediaType.valueOf("application/baz;q=0.2"));
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(WebResource.class.getMethod("doGetFoo"), method.getMethod());
      }

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.valueOf("application/foo;q=0.4"));
         accepts.add(MediaType.valueOf("application/bar"));
         accepts.add(MediaType.valueOf("application/baz;q=0.2"));
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(WebResource.class.getMethod("doGetBar"), method.getMethod());
      }

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.valueOf("application/foo;q=0.4"));
         accepts.add(MediaType.valueOf("application/bar;q=0.2"));
         accepts.add(MediaType.valueOf("application/baz"));
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(WebResource.class.getMethod("doGetBaz"), method.getMethod());
      }
   }

   @Path("/xml")
   public static class XmlResource
   {
      @Consumes("application/xml;schema=foo")
      @PUT
      public void putFoo(String foo)
      {
      }

      @Consumes("application/xml")
      @PUT
      public void put(String foo)
      {
      }

      @Consumes("application/xml;schema=bar")
      @PUT
      public void putBar(String foo)
      {
      }


   }

   @Test
   public void testConsume() throws Exception
   {
      Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
      registry.addPerRequestResource(XmlResource.class);

      MediaType contentType = MediaType.valueOf("application/xml;schema=bar");
      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments("/xml");

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("PUT", "/xml", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(XmlResource.class.getMethod("putBar", String.class), method.getMethod());
      }
   }

   @Path("/xml")
   public static class XmlResource2
   {
      @Consumes("application/xml;schema=foo")
      @Produces("application/xml;schema=junk")
      @PUT
      public String putFoo(String foo)
      {
         return "hello";
      }

      @Consumes("application/xml;schema=bar")
      @Produces("application/xml;schema=stuff")
      @PUT
      public String putBar(String foo)
      {
         return "hello";
      }

      @Consumes("application/xml")
      @Produces("application/xml;schema=stuff")
      @PUT
      public String put(String foo)
      {
         return "hello";
      }

   }

   @Test
   public void testConsume2() throws Exception
   {
      Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
      registry.addPerRequestResource(XmlResource2.class);

      MediaType contentType = MediaType.valueOf("application/xml;schema=bar");
      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments("/xml");

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.valueOf("application/xml;schema=junk;q=1.0"));
         accepts.add(MediaType.valueOf("application/xml;schema=stuff;q=0.5"));
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("PUT", "/xml", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(XmlResource2.class.getMethod("putBar", String.class), method.getMethod());
      }
   }

   @Test
   public void testConsume3() throws Exception
   {
      Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
      registry.addPerRequestResource(XmlResource2.class);

      MediaType contentType = MediaType.valueOf("application/xml;schema=blah");
      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments("/xml");

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.valueOf("application/xml;schema=junk;q=1.0"));
         accepts.add(MediaType.valueOf("application/xml;schema=stuff;q=0.5"));
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("PUT", "/xml", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(XmlResource2.class.getMethod("put", String.class), method.getMethod());
      }
   }

   @Test
   public void testAcceptGetWildCard() throws Exception
   {
      Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
      registry.addPerRequestResource(WebResource.class);

      MediaType contentType = new MediaType("text", "plain");
      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments("/");

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.valueOf("application/wildcard"));
         accepts.add(MediaType.valueOf("application/foo;q=0.6"));
         accepts.add(MediaType.valueOf("application/bar;q=0.4"));
         accepts.add(MediaType.valueOf("application/baz;q=0.2"));
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(WebResource.class.getMethod("doGetWildCard"), method.getMethod());
      }
   }

   @Path("/")
   public static class MultipleResource
   {
      @Produces({"application/foo", "application/bar"})
      @GET
      public String get()
      {
         return "GET";
      }
   }

   @Test
   public void testAcceptMultiple() throws Exception
   {
      Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
      registry.addPerRequestResource(MultipleResource.class);

      MediaType contentType = new MediaType("text", "plain");
      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments("/");

      MediaType foo = MediaType.valueOf("application/foo");
      MediaType bar = MediaType.valueOf("application/bar");

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(foo);
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(MultipleResource.class.getMethod("get"), method.getMethod());
      }
      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(bar);
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(MultipleResource.class.getMethod("get"), method.getMethod());
      }
      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.valueOf("*/*"));
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(MultipleResource.class.getMethod("get"), method.getMethod());
      }
      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.valueOf("application/*"));
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(MultipleResource.class.getMethod("get"), method.getMethod());
      }
   }

   @Path("/")
   public static class ConsumeResource
   {
      @Consumes("application/foo")
      @GET
      public String doGetFoo()
      {
         return "foo";
      }

      @Consumes("application/bar")
      @GET
      public String doGetBar()
      {
         return "bar";
      }

      @Consumes("application/baz")
      @GET
      public String doGetBaz()
      {
         return "baz";
      }

      @Consumes("*/*")
      @GET
      public Response doGetWildCard()
      {
         return Response.ok("wildcard", "application/wildcard").build();
      }
   }


   @Test
   public void testContentTypeMatching() throws Exception
   {
      Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
      registry.addPerRequestResource(ConsumeResource.class);

      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments("/");
      ArrayList<MediaType> accepts = new ArrayList<MediaType>();

      {
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", MediaType.valueOf("text/plain"), accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(ConsumeResource.class.getMethod("doGetWildCard"), method.getMethod());
      }
      {
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", MediaType.valueOf("application/foo"), accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(ConsumeResource.class.getMethod("doGetFoo"), method.getMethod());
      }
      {
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", MediaType.valueOf("application/bar"), accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(ConsumeResource.class.getMethod("doGetBar"), method.getMethod());
      }
      {
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", MediaType.valueOf("application/baz"), accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(ConsumeResource.class.getMethod("doGetBaz"), method.getMethod());
      }
   }

   @Path("/")
   public static class ComplexResource
   {
      @Consumes("text/*")
      @Produces("text/html")
      @GET
      public String method1()
      {
         return null;
      }

      @Consumes("text/xml")
      @Produces("text/json")
      @GET
      public String method2()
      {
         return null;
      }
   }

   @Test
   public void testComplex() throws Exception
   {
      Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
      registry.addPerRequestResource(ComplexResource.class);

      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments("/");
      MediaType contentType = new MediaType("text", "xml");

      ArrayList<MediaType> accepts = new ArrayList<MediaType>();
      accepts.add(new MediaType("*", "*"));
      accepts.add(new MediaType("text", "html"));

      {
         ResourceMethod method = (ResourceMethod) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
         Assert.assertNotNull(method);
         Assert.assertEquals(ComplexResource.class.getMethod("method2"), method.getMethod());
      }
   }


}
