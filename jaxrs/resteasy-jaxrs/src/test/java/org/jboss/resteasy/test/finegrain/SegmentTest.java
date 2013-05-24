package org.jboss.resteasy.test.finegrain;

import junit.framework.Assert;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceLocatorInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.registry.RootSegment;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.spi.DefaultOptionsMethodException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.net.URISyntaxException;


/**
 * Tests to make sure that standard segment mapping work correctly, especially
 * regexes that contain "\"
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */
public class SegmentTest
{
   @Path("/")
   public static class NullResource
   {

      @GET
      @Produces("text/plain")
      public String doNothing()
      {
         throw new RuntimeException("Not Implemented");
      }

      @GET
      @Produces("text/plain")
      @Path("child")
      public String childDoNothing()
      {
         throw new RuntimeException("Not Implemented");
      }

      @GET
      @Produces("text/plain")
      @Path("child/{name:[A-Za-z]+}")
      public String childWithName(@PathParam("name") String name)
      {
         throw new RuntimeException("Not Implemented");
      }

      @GET
      @Produces("text/plain")
      @Path("child/{id:[0-9]+}")
      public String childWithId(@PathParam("id") String id)
      {
         throw new RuntimeException("Not Implemented");
      }

      @GET
      @Produces("text/plain")
      @Path("child1/{id:\\d+}")
      public String child1WithId(@PathParam("id") String id)
      {
         throw new RuntimeException("Not Implemented");
      }
   }

   public static class Locator
   {
      @OPTIONS
      public void options() {}
   }

   @Path("/resource")
   public static class Resource
   {
      @GET
      @Path("sub")
      public String get() {return null;}

      @Path("{id}")
      public Locator locator()
      {
         return new Locator();}

   }

   @Path("/resource")
   public static class Resource2
   {
      @GET
      @Path("{id}")
      public String get() {return null;}

      @Path("sub")
      public Locator locator()
      {
         return new Locator();}

   }



   @Test
   public void testBasic() throws URISyntaxException
   {
      ResourceMethodRegistry registry = new ResourceMethodRegistry(ResteasyProviderFactory
              .getInstance());
      registry.addSingletonResource(new NullResource());
      assertMatchRoot(registry, "/", "doNothing", NullResource.class);
      assertMatchRoot(registry, "/child", "childDoNothing", NullResource.class);
      assertMatchRoot(registry, "/child/foo", "childWithName", NullResource.class);
      assertMatchRoot(registry, "/child/1", "childWithId", NullResource.class);
      assertMatchRoot(registry, "/child1/1", "child1WithId", NullResource.class);
   }

   @Test
   public void testDefaultOptions() throws URISyntaxException
   {
      ResourceMethodRegistry registry = new ResourceMethodRegistry(ResteasyProviderFactory
              .getInstance());
      registry.addPerRequestResource(Resource.class);
      try
      {
         ResourceInvoker invoker = registry.getResourceInvoker(MockHttpRequest.options("/resource/sub"));
      }
      catch (DefaultOptionsMethodException e)
      {
      }
      try
      {
         ResourceInvoker invoker = registry.getResourceInvoker(MockHttpRequest.put("/resource/sub"));
      }
      catch (NotAllowedException e)
      {
      }
   }

   @Test
   public void testLocatorOptions() throws URISyntaxException
   {
      ResourceMethodRegistry registry = new ResourceMethodRegistry(ResteasyProviderFactory
              .getInstance());
      registry.addPerRequestResource(Resource2.class);
      ResourceLocatorInvoker invoker = (ResourceLocatorInvoker)registry.getResourceInvoker(MockHttpRequest.options("/resource/sub"));
      Assert.assertNotNull(invoker);
      Assert.assertEquals(invoker.getMethod().getName(), "locator");
   }

   @Path("resource")
   public static class Resource3 {
      @GET
      @Path("responseok")
      public String responseOk() {
         return "ok";
      }

      @Path("{id}")
      public Object locate(@PathParam("id") int id)
      {
         return new Locator2();
      }
   }

   public static class Locator2 {
      @GET
      public String ok() {
         return "ok";
      }
   }


   @Path("locator")
   public static class Locator3 {
      @Path("responseok")
      public Resource3 responseOk() {
         return new Resource3();
      }
   }



   @Test
   public void testLocator3() throws URISyntaxException
   {
      ResourceMethodRegistry registry = new ResourceMethodRegistry(ResteasyProviderFactory
              .getInstance());
      registry.addPerRequestResource(Locator3.class);
      ResourceLocatorInvoker invoker = (ResourceLocatorInvoker)registry.getResourceInvoker(MockHttpRequest.get("/locator/responseok/responseok"));
      Assert.assertNotNull(invoker);
      Assert.assertEquals(invoker.getMethod().getName(), "responseOk");
   }






   private void assertMatchRoot(ResourceMethodRegistry registry, final String url, final String methodName,
                                final Class<?> clazz) throws URISyntaxException
   {
      ResourceMethodInvoker matchRoot = getResourceMethod(url, registry);
      Assert.assertEquals(clazz, matchRoot.getResourceClass());
      Assert.assertEquals(methodName, matchRoot.getMethod().getName());
   }

   private ResourceMethodInvoker getResourceMethod(String url, ResourceMethodRegistry registry)
           throws URISyntaxException
   {
      return (ResourceMethodInvoker) registry.getResourceInvoker(MockHttpRequest.get(url));
   }
}
