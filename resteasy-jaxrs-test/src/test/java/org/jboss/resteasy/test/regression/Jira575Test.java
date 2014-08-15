package org.jboss.resteasy.test.regression;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Jira575Test extends BaseResourceTest
{
   public static class Foo
   {

   }

   @Path("/regression")
   public static class RegressionResource
   {
      @GET
      public Response get()
      {
         return Response.status(401).entity("hello").type("application/error").build();
      }
   }

   @Path("/regression")
   public static interface RegressionProxy
   {
      @GET
      @Produces("application/foo")
      public Foo getFoo();
   }

   @Override
   @Before
   public void before() throws Exception {
      super.before();
      addPerRequestResource(RegressionResource.class);
   }

   @Test
   public void testProxy() throws Exception
   {
      RegressionProxy proxy = ProxyFactory.create(RegressionProxy.class, TestPortProvider.generateURL("/"));
      try
      {
         proxy.getFoo();
      }
      catch (ClientResponseFailure e)
      {
         Assert.assertEquals(e.getResponse().getStatus(), 401);
         String val = (String)e.getResponse().getEntity(String.class);
         Assert.assertEquals("hello", val);

      }
   }

}
