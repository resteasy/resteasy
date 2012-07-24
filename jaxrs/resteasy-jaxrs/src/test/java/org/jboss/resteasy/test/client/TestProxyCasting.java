package org.jboss.resteasy.test.client;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.marshallers.ResteasyClientProxy;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static org.junit.Assert.*;

public class TestProxyCasting extends BaseResourceTest
{
	public static interface Nothing
	{
	}
	
	public static interface InterfaceA
	{
		@GET
		@Path("foo")
		@Produces("text/plain")
		String getFoo();
	}

	public static interface InterfaceB
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

	@Before
	public void setUp() throws Exception
	{
		addPerRequestResource(FooBarImpl.class);
	}

	@Test
	public void testSubresourceProxy() throws Exception
	{
		String url = TestPortProvider.generateURL("/foobar");
		InterfaceA a = ProxyFactory.create(InterfaceA.class, url);
		assertEquals("FOO", a.getFoo());
		InterfaceB b = ((ResteasyClientProxy) a).as(InterfaceB.class);
		assertEquals("BAR", b.getBar());
	}
}
