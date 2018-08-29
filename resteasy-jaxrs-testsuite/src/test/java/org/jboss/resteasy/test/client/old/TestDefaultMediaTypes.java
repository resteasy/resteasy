package org.jboss.resteasy.test.client.old;

import org.jboss.resteasy.client.ProxyBuilder;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;

public class TestDefaultMediaTypes extends BaseResourceTest
{
	public interface Foo
	{
		@GET
		@Path("foo")
		String getFoo();

		@PUT
		@Path("foo")
		String setFoo(String value);
	}

	@Path("foo")
	public static class FooImpl implements Foo
	{
		@Context
		HttpRequest request;
		
		@Override
		public String getFoo()
		{
			return request.getHttpHeaders().getAcceptableMediaTypes().toString();
		}

		@Override
		public String setFoo(String value)
		{
			return request.getHttpHeaders().getMediaType().toString();
		}
	}

	@Before
	public void setUp() throws Exception
	{
		addPerRequestResource(FooImpl.class);
	}

	@Test(expected = RuntimeException.class)
	public void testOldBehaviorContinues() throws Exception
	{
		String url = TestPortProvider.generateURL("/foo");
		ProxyFactory.create(Foo.class, url);
	}
	
	@Test
	public void testDefaultValues() throws Exception
	{
		String url = TestPortProvider.generateURL("/foo");
		Foo foo = ProxyBuilder.build(Foo.class, url).serverMediaType(MediaType.TEXT_PLAIN_TYPE).now();
		
		assertEquals("[text/plain]", foo.getFoo());
		assertEquals("text/plain", foo.setFoo("SOMETHING"));
	}

	@Test
	public void testMismatch() throws Exception
	{
		// NOTE: this doesn't fail on the server because the default */* provider matches the
		// requested media type.
		String url = TestPortProvider.generateURL("/foo");
		Foo foo = ProxyBuilder.build(Foo.class, url).serverMediaType(MediaType.APPLICATION_JSON_TYPE).now();
		
		assertEquals("[application/json]", foo.getFoo());
		assertEquals("application/json", foo.setFoo("SOMETHING"));
	}
}
