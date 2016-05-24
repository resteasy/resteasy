package org.jboss.resteasy.test.nextgen.proxy;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
public class TestProxyCastingSimple
{
   private static ResteasyDeployment deployment;
   private static Dispatcher dispatcher;
   private static ResteasyWebTarget target;
   
	public static interface InterfaceAorB
	{
		public <T> T as(Class<T> iface);
	}

	public static interface InterfaceA extends InterfaceAorB
	{
		@GET
		@Path("foo")
		@Produces("text/plain")
		String getFoo();
	}

	public static interface InterfaceB extends InterfaceAorB
	{
		@GET
		@Path("bar")
		@Produces("text/plain")
		String getBar();
	}

	public static interface FooBar
	{
		@Path("{thing}")
		InterfaceAorB getThing(@PathParam("thing") String thing);
	}

	@Path("/foobar")
	public static class FooBarImpl implements FooBar
	{
		@Override
		public InterfaceAorB getThing(String thing)
		{
			if ("a".equalsIgnoreCase(thing))
			{
				return new InterfaceA()
				{
					@Override
					public String getFoo()
					{
						return "FOO";
					}

					@Override
					public <T> T as(Class<T> iface)
					{
						return iface.cast(this);
					}
				};
			}
			else if ("b".equalsIgnoreCase(thing))
			{
				return new InterfaceB()
				{
					@Override
					public String getBar()
					{
						return "BAR";
					}

					@Override
					public <T> T as(Class<T> iface)
					{
						return iface.cast(this);
					}
				};
			}
			else
			{
				throw new IllegalArgumentException("Bad arg: " + thing);
			}
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
	public void testSubresourceProxy() throws Exception
	{
		FooBar foobar = ProxyBuilder.builder(FooBar.class, target).build();
		{
			InterfaceA a = ((ResteasyClientProxy) foobar.getThing("a")).as(InterfaceA.class);
			assertEquals("FOO", a.getFoo());
			InterfaceB b = ((ResteasyClientProxy) foobar.getThing("b")).as(InterfaceB.class);
			assertEquals("BAR", b.getBar());
		}
		{
			InterfaceA a = foobar.getThing("a").as(InterfaceA.class);
			assertEquals("FOO", a.getFoo());
			InterfaceB b = foobar.getThing("b").as(InterfaceB.class);
			assertEquals("BAR", b.getBar());
		}
	}
}
