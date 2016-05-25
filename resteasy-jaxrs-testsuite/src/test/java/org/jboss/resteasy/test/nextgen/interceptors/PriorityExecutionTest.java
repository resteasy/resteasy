package org.jboss.resteasy.test.nextgen.interceptors;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

/**
 * JBREM-1294
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author <a href="mailto:rsigal@redhat.com">Ron Sigal</a>
 * @version $Revision: 1 $
 */
public class PriorityExecutionTest extends BaseResourceTest
{
   private static List<String> interceptors = new ArrayList<String>();
   
   @Path("test")
   public static class TestResource
   {
      @GET
      public String get()
      {
         return "test";
      }
   }

   @Priority(Integer.MIN_VALUE)
   public static class ClientResponseFilterMin implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ClientResponseFilterMin");
      }
   }
   
   @Priority(-100)
   public static class ClientResponseFilter1 implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ClientResponseFilter1");
      }
   }

   @Priority(0)
   public static class ClientResponseFilter2 implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ClientResponseFilter2");
      }
   }

   @Priority(100)
   public static class ClientResponseFilter3 implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ClientResponseFilter3");
      }
   }

   @Priority(Integer.MAX_VALUE)
   public static class ClientResponseFilterMax implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ClientResponseFilterMax");
      }
   }

   @Priority(Integer.MIN_VALUE)
   public static class ContainerResponseFilterMin implements ContainerResponseFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ContainerResponseFilterMin");
      }
   }
   
   @Priority(-100)
   public static class ContainerResponseFilter1 implements ContainerResponseFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ContainerResponseFilter1");
      }
   }

   @Priority(0)
   public static class ContainerResponseFilter2 implements ContainerResponseFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ContainerResponseFilter2");
      }
   }

   @Priority(100)
   public static class ContainerResponseFilter3 implements ContainerResponseFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ContainerResponseFilter3");
      }
   }

   @Priority(Integer.MAX_VALUE)
   public static class ContainerResponseFilterMax implements ContainerResponseFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ContainerResponseFilterMax");
      }
   }

   @Priority(Integer.MIN_VALUE)
   public static class ClientRequestFilterMin implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ClientRequestFilterMin");
      }
   }
   
   @Priority(-100)
   public static class ClientRequestFilter1 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ClientRequestFilter1");
      }
   }

   @Priority(0)
   public static class ClientRequestFilter2 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ClientRequestFilter2");
      }
   }

   @Priority(100)
   public static class ClientRequestFilter3 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ClientRequestFilter3");
      }
   }
   
   @Priority(Integer.MAX_VALUE)
   public static class ClientRequestFilterMax implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ClientRequestFilterMax");
      }
   }

   @Priority(Integer.MIN_VALUE)
   public static class ContainerRequestFilterMin implements ContainerRequestFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ContainerRequestFilterMin");
      }
   }
   
   @Priority(-100)
   public static class ContainerRequestFilter1 implements ContainerRequestFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ContainerRequestFilter1");
      }
   }
 
   @Priority(0)
   public static class ContainerRequestFilter2 implements ContainerRequestFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ContainerRequestFilter2");
      }
   }
   
   @Priority(100)
   public static class ContainerRequestFilter3 implements ContainerRequestFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ContainerRequestFilter3");
      }
   }
   
   @Priority(Integer.MAX_VALUE)
   public static class ContainerRequestFilterMax implements ContainerRequestFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         System.out.println(this);
         interceptors.add("ContainerRequestFilterMax");
      }
   }
   
   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(TestResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }

   @Test
   public void testPriority() throws Exception
   {
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      factory.register(ContainerResponseFilter2.class);
      factory.register(ContainerResponseFilter1.class);
      factory.register(ContainerResponseFilter3.class);
      factory.register(ContainerResponseFilterMin.class);
      factory.register(ContainerResponseFilterMax.class);
      factory.register(ContainerRequestFilter2.class);
      factory.register(ContainerRequestFilter1.class);
      factory.register(ContainerRequestFilter3.class);
      factory.register(ContainerRequestFilterMin.class);
      factory.register(ContainerRequestFilterMax.class);
      client.register(ClientResponseFilter3.class);
      client.register(ClientResponseFilter1.class);
      client.register(ClientResponseFilter2.class);
      client.register(ClientResponseFilterMin.class);
      client.register(ClientResponseFilterMax.class);
      client.register(ClientRequestFilter3.class);
      client.register(ClientRequestFilter1.class);
      client.register(ClientRequestFilter2.class);
      client.register(ClientRequestFilterMin.class);
      client.register(ClientRequestFilterMax.class);

      Response response = client.target(generateURL("/test")).request().get();
      response.bufferEntity();
      System.out.println(response.readEntity(String.class));
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("test", response.getEntity());
      
      Assert.assertEquals("ClientRequestFilterMin",     interceptors.get(0));
      Assert.assertEquals("ClientRequestFilter1",       interceptors.get(1));
      Assert.assertEquals("ClientRequestFilter2",       interceptors.get(2));
      Assert.assertEquals("ClientRequestFilter3",       interceptors.get(3));
      Assert.assertEquals("ClientRequestFilterMax",     interceptors.get(4));
      Assert.assertEquals("ContainerRequestFilterMin",  interceptors.get(5));
      Assert.assertEquals("ContainerRequestFilter1",    interceptors.get(6));
      Assert.assertEquals("ContainerRequestFilter2",    interceptors.get(7));
      Assert.assertEquals("ContainerRequestFilter3",    interceptors.get(8));
      Assert.assertEquals("ContainerRequestFilterMax",  interceptors.get(9));
      Assert.assertEquals("ContainerResponseFilterMax", interceptors.get(10));
      Assert.assertEquals("ContainerResponseFilter3",   interceptors.get(11));
      Assert.assertEquals("ContainerResponseFilter2",   interceptors.get(12));
      Assert.assertEquals("ContainerResponseFilter1",   interceptors.get(13));
      Assert.assertEquals("ContainerResponseFilterMin", interceptors.get(14));
      Assert.assertEquals("ClientResponseFilterMax",    interceptors.get(15));
      Assert.assertEquals("ClientResponseFilter3",      interceptors.get(16));
      Assert.assertEquals("ClientResponseFilter2",      interceptors.get(17));
      Assert.assertEquals("ClientResponseFilter1",      interceptors.get(18));
      Assert.assertEquals("ClientResponseFilterMin",    interceptors.get(19));
   }
}
