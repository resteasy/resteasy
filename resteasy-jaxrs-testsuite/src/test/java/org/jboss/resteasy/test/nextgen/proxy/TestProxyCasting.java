package org.jboss.resteasy.test.nextgen.proxy;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ResteasyClientProxy;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * RESTEASY-1332 / RESTEASY-1250
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date
 */
public class TestProxyCasting
{
   private static ResteasyDeployment deployment;
   private static Dispatcher dispatcher;
   private static ResteasyWebTarget target;
   
   public interface Nothing
   {
   }
   
   public interface InterfaceA
   {
      @GET
      @Path("foo")
      @Produces("text/plain")
      String getFoo();
   }

   public interface InterfaceB
   {
      @GET
      @Path("bar")
      @Produces("text/plain")
      String getBar();
   }

   @Path("/foobar")
   public static class FooBarImpl implements InterfaceA, InterfaceB, Nothing
   {
      @Override
      public String getFoo()
      {
         return "FOO";
      }

      @Override
      public String getBar()
      {
         return "BAR";
      }
   }
  
   @BeforeClass
   public static void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      dispatcher.getRegistry().addPerRequestResource(FooBarImpl.class);
      Client client = ClientBuilder.newClient();
      target = (ResteasyWebTarget) client.target("http://localhost:8081/foobar");
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
      Thread.sleep(100);
   }
   
   @Test
   public void testResourceProxy() throws Exception
   {
      InterfaceA a = ProxyBuilder.builder(InterfaceA.class, target).build();
      assertEquals("FOO", a.getFoo());
      InterfaceB b = ((ResteasyClientProxy) a).as(InterfaceB.class);
      assertEquals("BAR", b.getBar());
   }
}
