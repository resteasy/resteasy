package org.jboss.resteasy.test.nextgen.finegrain;

import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.providers.SerializableProvider;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for RESTEASY-839.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date August 30, 2013
 */
public class SerializableWithParametersTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;

   public static class Foo implements Serializable
   {
      private static final long serialVersionUID = -1068336400309384949L;
      private String s;

      public Foo(String s)
      {
         this.s = s;
      }
      public String toString()
      {
         return "Foo[" + s + "]";
      }
      public boolean equals(Object o)
      {
         if (o == null || !(o instanceof Foo))
         {
            return false;
         }
         return this.s.equals(Foo.class.cast(o).s);
      }
   }
   
   @Path("test")
   static public class TestResource
   {
      @GET
      public Response test()
      {
         return Response.ok().entity(new Foo("abc")).type(SerializableProvider.APPLICATION_SERIALIZABLE + ";q=0.5").build();
      }
   }

   public static void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testInvalidMediaTypes() throws Exception
   {
      before();
      ResteasyClient client = new ResteasyClientBuilder().build();
      Invocation.Builder request = client.target(TestPortProvider.generateURL("/test")).request();
      Foo foo = request.get(Foo.class);
      System.out.println("foo: " + foo);
      Assert.assertEquals(new Foo("abc"), foo);
      after();
   }
}
