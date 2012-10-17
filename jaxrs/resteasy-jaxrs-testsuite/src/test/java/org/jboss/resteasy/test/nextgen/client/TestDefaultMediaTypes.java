package org.jboss.resteasy.test.nextgen.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;

public class TestDefaultMediaTypes extends BaseResourceTest
{
	public static interface Foo
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

   static ResteasyClient client;

	@BeforeClass
	public static void setUp() throws Exception
	{
		addPerRequestResource(FooImpl.class);
      client = new ResteasyClient();
	}

   @AfterClass
   public static void shutdown() throws Exception
   {
      client.close();
   }

	@Test(expected = RuntimeException.class)
	public void testOldBehaviorContinues() throws Exception
	{
      ResteasyWebTarget target = client.target(TestPortProvider.generateURL("/foo"));
		target.proxy(Foo.class);
	}
	
	@Test
	public void testDefaultValues() throws Exception
	{
		ResteasyWebTarget target = client.target(TestPortProvider.generateURL("/foo"));
      Foo foo = target.proxyBuilder(Foo.class).defaultProduces(MediaType.TEXT_PLAIN_TYPE).defaultConsumes(MediaType.TEXT_PLAIN_TYPE).build();

		assertEquals("[text/plain]", foo.getFoo());
		assertEquals("text/plain", foo.setFoo("SOMETHING"));
	}

	@Test
	public void testMismatch() throws Exception
	{
		// NOTE: this doesn't fail on the server because the default */* provider matches the
		// requested media type.
		ResteasyWebTarget target = client.target(TestPortProvider.generateURL("/foo"));
      Foo foo = target.proxyBuilder(Foo.class).defaultProduces(MediaType.APPLICATION_JSON_TYPE).defaultConsumes(MediaType.APPLICATION_JSON_TYPE).build();

		assertEquals("[application/json]", foo.getFoo());
		assertEquals("application/json", foo.setFoo("SOMETHING"));
	}
}
