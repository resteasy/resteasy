package org.jboss.resteasy.test;

import junit.framework.Assert;
import org.jboss.resteasy.client.ProxyFactory;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

public class TestAnnotationInheritance extends BaseResourceTest
{
	public static interface SuperInt
	{
		@Path("foo")
		@GET
		@Produces("application/json")
		String getFoo();
	}

	public static abstract class SuperIntAbstract implements SuperInt
	{
		@Override
		public String getFoo()
		{
			return "Foo: " + getName();
		}

		protected abstract String getName();
	}

	public static interface NotAResource
	{
		// no annotation here!
		String blah();
	}

	public static interface SomeOtherInterface
	{
		@Path("superint")
		SuperInt getSuperInt();

		@Path("failure")
		NotAResource getFailure();
	}

	@Path("/somewhere")
	public static class SomeOtherResource implements SomeOtherInterface
	{
		public SuperInt getSuperInt()
		{
			return new SuperIntAbstract()
			{
				@Override
				protected String getName()
				{
					return "Fred";
				}
			};
		}

		public NotAResource getFailure()
		{
			return new NotAResource()
			{
				@Override
				public String blah()
				{
					return "Nothing";
				}
			};
		}
	}

	@Test
	public void testSuperclassInterfaceAnnotation()
	{
		addPerRequestResource(SomeOtherResource.class);
		SomeOtherInterface proxy = ProxyFactory.create(SomeOtherInterface.class, TestPortProvider.generateURL("/somewhere"));
		Assert.assertEquals("Foo: Fred", proxy.getSuperInt().getFoo());
	}

	@Test(expected = Exception.class)
	public void testDetectionOfNonResource()
	{
		addPerRequestResource(SomeOtherResource.class);
		SomeOtherResource proxy = ProxyFactory.create(SomeOtherResource.class, TestPortProvider.generateURL("/somewhere"));
		proxy.getFailure().blah();
	}

}
