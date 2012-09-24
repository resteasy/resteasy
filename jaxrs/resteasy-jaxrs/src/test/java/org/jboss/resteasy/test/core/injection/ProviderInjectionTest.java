package org.jboss.resteasy.test.core.injection;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * This test verifies that Providers instance can be injected into a Provider
 * using constructor or field injection.
 *
 * @author Jozef Hartinger
 */
public class ProviderInjectionTest extends BaseResourceTest
{

   @Before
   public void setUp() throws Exception
   {
      getProviderFactory().registerProvider(SimpleMessageBodyWriter.class);
      addPerRequestResource(SimpleResourceImpl.class);

      // do a request (force provider instantiation if providers were created
      // lazily)
      SimpleResource proxy = ProxyFactory.create(SimpleResource.class, generateBaseUrl());
      assertEquals(proxy.foo(), "bar");
   }

   @Test
   public void testConstructorInjection()
   {
      for (SimpleMessageBodyWriter writer : SimpleMessageBodyWriter.getInstances())
      {
         assertTrue(writer.getConstructorProviders() != null);
      }
   }

   @Test
   public void testFieldInjection()
   {
      for (SimpleMessageBodyWriter writer : SimpleMessageBodyWriter.getInstances())
      {
         assertTrue(writer.getFieldProviders() != null);
      }
   }

   @Path("/test")
   public static interface SimpleResource
   {
      @GET
      @Produces("text/plain")
      String foo();
   }

   public static class SimpleResourceImpl implements SimpleResource
   {
      public String foo()
      {
         return "foo";
      }
   }
}
