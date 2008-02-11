package org.resteasy.test.finegrain.resource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.Registry;
import org.resteasy.ResourceMethod;
import org.resteasy.specimpl.PathSegmentImpl;
import org.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
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
      ResteasyProviderFactory.initializeInstance();
   }

   @Path("/")
   public static class WebResource
   {
      @ProduceMime("application/foo")
      @GET
      public String doGetFoo()
      {
         return "foo";
      }

      @ProduceMime("application/bar")
      @GET
      public String doGetBar()
      {
         return "bar";
      }

      @ProduceMime("application/baz")
      @GET
      public String doGetBaz()
      {
         return "baz";
      }

      @ProduceMime("*/*")
      @GET
      public Response doGetWildCard()
      {
         return Response.ok("wildcard", "application/wildcard").build();
      }
   }

   @Test
   public void testAcceptGet() throws Exception
   {
      Registry registry = new Registry(ResteasyProviderFactory.getInstance());
      registry.addResource(WebResource.class);

      MediaType contentType = new MediaType("text", "plain");
      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments("/");

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.parse("application/foo"));
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, contentType, accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(WebResource.class.getMethod("doGetFoo"), method.getMethod());
      }

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.parse("application/foo;q=0.1"));
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, contentType, accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(WebResource.class.getMethod("doGetFoo"), method.getMethod());
      }

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.parse("application/foo"));
         accepts.add(MediaType.parse("application/bar;q=0.4"));
         accepts.add(MediaType.parse("application/baz;q=0.2"));
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, contentType, accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(WebResource.class.getMethod("doGetFoo"), method.getMethod());
      }

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.parse("application/foo;q=0.4"));
         accepts.add(MediaType.parse("application/bar"));
         accepts.add(MediaType.parse("application/baz;q=0.2"));
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, contentType, accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(WebResource.class.getMethod("doGetBar"), method.getMethod());
      }

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.parse("application/foo;q=0.4"));
         accepts.add(MediaType.parse("application/bar;q=0.2"));
         accepts.add(MediaType.parse("application/baz"));
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, contentType, accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(WebResource.class.getMethod("doGetBaz"), method.getMethod());
      }
   }

   @Test
   public void testAcceptGetWildCard() throws Exception
   {
      Registry registry = new Registry(ResteasyProviderFactory.getInstance());
      registry.addResource(WebResource.class);

      MediaType contentType = new MediaType("text", "plain");
      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments("/");

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.parse("application/wildcard"));
         accepts.add(MediaType.parse("application/foo;q=0.6"));
         accepts.add(MediaType.parse("application/bar;q=0.4"));
         accepts.add(MediaType.parse("application/baz;q=0.2"));
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, contentType, accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(WebResource.class.getMethod("doGetWildCard"), method.getMethod());
      }
   }

   @Path("/")
   public static class MultipleResource
   {
      @ProduceMime({"application/foo", "application/bar"})
      @GET
      public String get()
      {
         return "GET";
      }
   }

   @Test
   public void testAcceptMultiple() throws Exception
   {
      Registry registry = new Registry(ResteasyProviderFactory.getInstance());
      registry.addResource(MultipleResource.class);

      MediaType contentType = new MediaType("text", "plain");
      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments("/");

      MediaType foo = MediaType.parse("application/foo");
      MediaType bar = MediaType.parse("application/bar");

      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(foo);
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, contentType, accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(MultipleResource.class.getMethod("get"), method.getMethod());
      }
      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(bar);
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, contentType, accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(MultipleResource.class.getMethod("get"), method.getMethod());
      }
      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.parse("*/*"));
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, contentType, accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(MultipleResource.class.getMethod("get"), method.getMethod());
      }
      {
         ArrayList<MediaType> accepts = new ArrayList<MediaType>();
         accepts.add(MediaType.parse("application/*"));
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, contentType, accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(MultipleResource.class.getMethod("get"), method.getMethod());
      }
   }

   @Path("/")
   public static class ConsumeResource
   {
      @ConsumeMime("application/foo")
      @GET
      public String doGetFoo()
      {
         return "foo";
      }

      @ConsumeMime("application/bar")
      @GET
      public String doGetBar()
      {
         return "bar";
      }

      @ConsumeMime("application/baz")
      @GET
      public String doGetBaz()
      {
         return "baz";
      }

      @ConsumeMime("*/*")
      @GET
      public Response doGetWildCard()
      {
         return Response.ok("wildcard", "application/wildcard").build();
      }
   }


   @Test
   public void testContentTypeMatching() throws Exception
   {
      Registry registry = new Registry(ResteasyProviderFactory.getInstance());
      registry.addResource(ConsumeResource.class);

      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments("/");
      ArrayList<MediaType> accepts = new ArrayList<MediaType>();

      {
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, MediaType.parse("text/plain"), accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(ConsumeResource.class.getMethod("doGetWildCard"), method.getMethod());
      }
      {
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, MediaType.parse("application/foo"), accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(ConsumeResource.class.getMethod("doGetFoo"), method.getMethod());
      }
      {
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, MediaType.parse("application/bar"), accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(ConsumeResource.class.getMethod("doGetBar"), method.getMethod());
      }
      {
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, MediaType.parse("application/baz"), accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(ConsumeResource.class.getMethod("doGetBaz"), method.getMethod());
      }
   }

   @Path("/")
   public static class ComplexResource
   {
      @ConsumeMime("text/*")
      @ProduceMime("text/html")
      @GET
      public String method1()
      {
         return null;
      }

      @ConsumeMime("text/xml")
      @ProduceMime("text/json")
      @GET
      public String method2()
      {
         return null;
      }
   }

   @Test
   public void testComplex() throws Exception
   {
      Registry registry = new Registry(ResteasyProviderFactory.getInstance());
      registry.addResource(ComplexResource.class);

      List<PathSegment> pathSegments = PathSegmentImpl.parseSegments("/");
      MediaType contentType = new MediaType("text", "xml");

      ArrayList<MediaType> accepts = new ArrayList<MediaType>();
      accepts.add(new MediaType("*", "*"));
      accepts.add(new MediaType("text", "html"));

      {
         ResourceMethod method = registry.getResourceInvoker("GET", pathSegments, contentType, accepts);
         Assert.assertNotNull(method);
         Assert.assertEquals(ComplexResource.class.getMethod("method2"), method.getMethod());
      }
   }


}
