package org.jboss.resteasy.test.finegrain;

import junit.framework.Assert;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.registry.RootSegment;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
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

   ResourceMethodRegistry registry;

   @BeforeClass
   public static void initEnv()
   {
   }

   @Before
   public void init()
   {
      registry = new ResourceMethodRegistry(ResteasyProviderFactory
              .getInstance());
      registry.addSingletonResource(new NullResource());
   }

   @Test
   public void testRoot() throws URISyntaxException
   {
      assertMatchRoot("/", "doNothing", NullResource.class);
   }

   @Test
   public void testChild() throws URISyntaxException
   {
      assertMatchRoot("/child", "childDoNothing", NullResource.class);
      assertMatchRoot("/child/foo", "childWithName", NullResource.class);
      assertMatchRoot("/child/1", "childWithId", NullResource.class);
   }

   @Test
   public void testChildWithSlashD() throws URISyntaxException
   {
      assertMatchRoot("/child1/1", "child1WithId", NullResource.class);
   }


   private void assertMatchRoot(final String url, final String methodName,
                                final Class<?> clazz) throws URISyntaxException
   {
      ResourceMethodInvoker matchRoot = getResourceMethod(url);
      Assert.assertEquals(clazz, matchRoot.getResourceClass());
      Assert.assertEquals(methodName, matchRoot.getMethod().getName());
   }

   private ResourceMethodInvoker getResourceMethod(String url)
           throws URISyntaxException
   {
      return (ResourceMethodInvoker) registry.getResourceInvoker(MockHttpRequest.get(url));
   }
}
