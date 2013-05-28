package org.jboss.resteasy.test.nextgen.interceptors;

import junit.framework.Assert;
import org.jboss.resteasy.core.interception.ClientResponseFilterRegistry;
import org.jboss.resteasy.core.interception.ContainerResponseFilterRegistry;
import org.jboss.resteasy.core.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.core.interception.LegacyPrecedence;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;

import javax.annotation.Priority;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PriorityTest
{
   @Priority(100)
   public static class ClientResponseFilter1 implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {

      }
   }

   @Priority(200)
   public static class ClientResponseFilter2 implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {

      }
   }

   @Priority(300)
   public static class ClientResponseFilter3 implements ClientResponseFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {

      }
   }

   @Priority(100)
   public static class ContainerResponseFilter1 implements ContainerResponseFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {

      }
   }

   @Priority(200)
   public static class ContainerResponseFilter2 implements ContainerResponseFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {

      }
   }

   @Priority(300)
   public static class ContainerResponseFilter3 implements ContainerResponseFilter
   {
      @Override
      public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException
      {

      }
   }

   @Priority(100)
   public static class ClientRequestFilter1 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {

      }
   }

   @Priority(200)
   public static class ClientRequestFilter2 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {

      }
   }

   @Priority(300)
   public static class ClientRequestFilter3 implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {

      }
   }

   @Test
   public void testPriority() throws Exception
   {
      ContainerResponseFilterRegistry containerResponseFilterRegistry = new ContainerResponseFilterRegistry(new ResteasyProviderFactory(), new LegacyPrecedence());
      ClientResponseFilterRegistry clientResponseFilterRegistry = new ClientResponseFilterRegistry(new ResteasyProviderFactory());
      JaxrsInterceptorRegistry<ClientRequestFilter> clientRequestFilterRegistry = new JaxrsInterceptorRegistry<ClientRequestFilter>(new ResteasyProviderFactory(), ClientRequestFilter.class);

      containerResponseFilterRegistry.registerClass(ContainerResponseFilter2.class);
      containerResponseFilterRegistry.registerClass(ContainerResponseFilter1.class);
      containerResponseFilterRegistry.registerClass(ContainerResponseFilter3.class);

      ContainerResponseFilter[] containerResponseFilters = containerResponseFilterRegistry.postMatch(null, null);
      Assert.assertTrue(containerResponseFilters[0] instanceof ContainerResponseFilter3);
      Assert.assertTrue(containerResponseFilters[1] instanceof ContainerResponseFilter2);
      Assert.assertTrue(containerResponseFilters[2] instanceof ContainerResponseFilter1);

      clientResponseFilterRegistry.registerClass(ClientResponseFilter3.class);
      clientResponseFilterRegistry.registerClass(ClientResponseFilter1.class);
      clientResponseFilterRegistry.registerClass(ClientResponseFilter2.class);

      ClientResponseFilter[] clientResponseFilters = clientResponseFilterRegistry.postMatch(null, null);
      Assert.assertTrue(clientResponseFilters[0] instanceof ClientResponseFilter3);
      Assert.assertTrue(clientResponseFilters[1] instanceof ClientResponseFilter2);
      Assert.assertTrue(clientResponseFilters[2] instanceof ClientResponseFilter1);

      clientRequestFilterRegistry.registerClass(ClientRequestFilter3.class);
      clientRequestFilterRegistry.registerClass(ClientRequestFilter1.class);
      clientRequestFilterRegistry.registerClass(ClientRequestFilter2.class);

      ClientRequestFilter[] clientRequestFilters = clientRequestFilterRegistry.postMatch(null, null);
      Assert.assertTrue(clientRequestFilters[0] instanceof ClientRequestFilter1);
      Assert.assertTrue(clientRequestFilters[1] instanceof ClientRequestFilter2);
      Assert.assertTrue(clientRequestFilters[2] instanceof ClientRequestFilter3);

   }

   @Test
   public void testPriorityOverride()
   {
      JaxrsInterceptorRegistry<ClientRequestFilter> clientRequestFilterRegistry = new JaxrsInterceptorRegistry<ClientRequestFilter>(new ResteasyProviderFactory(), ClientRequestFilter.class);

      clientRequestFilterRegistry.registerClass(ClientRequestFilter3.class, 100);
      clientRequestFilterRegistry.registerClass(ClientRequestFilter1.class, 200);
      clientRequestFilterRegistry.registerClass(ClientRequestFilter2.class, 300);

      ClientRequestFilter[] clientRequestFilters = clientRequestFilterRegistry.postMatch(null, null);
      Assert.assertTrue(clientRequestFilters[0] instanceof ClientRequestFilter3);
      Assert.assertTrue(clientRequestFilters[1] instanceof ClientRequestFilter1);
      Assert.assertTrue(clientRequestFilters[2] instanceof ClientRequestFilter2);

   }




}
