package org.jboss.resteasy.test.finegrain;

import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import junit.framework.Assert;

import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.registry.RootSegment;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



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
   RootSegment root = null;

   @BeforeClass
   public static void initEnv()
   {
      ResteasyProviderFactory.initializeInstance();
   }

   @Before
   public void init()
   {
      registry = new ResourceMethodRegistry(ResteasyProviderFactory
            .getInstance());
      root = registry.getRoot();
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
      ResourceMethod matchRoot = getResourceMethod(url);
      Assert.assertEquals(clazz, matchRoot.getResourceClass());
      Assert.assertEquals(methodName, matchRoot.getMethod().getName());
   }

   private ResourceMethod getResourceMethod(String url)
         throws URISyntaxException
   {
      return (ResourceMethod) root.matchRoot(MockHttpRequest.get(url));
   }
}
