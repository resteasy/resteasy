package org.jboss.resteasy.test.client.old;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 */
public class ContextTest extends BaseResourceTest
{
   public static class Resource implements ResourceInterface
   {

      public String echo(UriInfo info)
      {
         Assert.assertNotNull(info);
         return "content";
      }
   }

   @Path(value = "/test")
   public interface ResourceInterface
   {

      @GET
      @Produces("text/plain")
      String echo(@Context UriInfo info);
   }

   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(Resource.class);
   }


   @Test
   public void testEcho()
   {
      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      ResourceInterface client = ProxyFactory.create(ResourceInterface.class, generateBaseUrl());
      Assert.assertEquals("content", client.echo(null));
   }


}
