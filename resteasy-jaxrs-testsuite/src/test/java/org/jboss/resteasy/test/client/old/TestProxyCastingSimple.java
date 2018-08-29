package org.jboss.resteasy.test.client.old;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.marshallers.ResteasyClientProxy;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static org.junit.Assert.assertEquals;

public class TestProxyCastingSimple extends BaseResourceTest
{
	public interface InterfaceAorB
	{
		<T> T as(Class<T> iface);
	}

	public interface InterfaceA extends InterfaceAorB
	{
		@GET
		@Path("foo")
		@Produces("text/plain")
		String getFoo();
	}

	public interface InterfaceB extends InterfaceAorB
	{
		@GET
		@Path("bar")
		@Produces("text/plain")
		String getBar();
	}

	public interface FooBar
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

	@Before
	public void setUp() throws Exception
	{
		addPerRequestResource(FooBarImpl.class);
	}

	@Test
	public void testSubresourceProxy() throws Exception
	{
		String url = TestPortProvider.generateURL("/foobar");
		FooBar foobar = ProxyFactory.create(FooBar.class, url);
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
