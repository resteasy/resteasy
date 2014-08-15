package org.jboss.resteasy.test.smoke;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Simple smoke test
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestResourceWithMultipleInterfaces
{
	public static interface IntA
	{
		@GET
		@Path("foo")
		@Produces("text/plain")
		public String getFoo();
	}
	
	public static interface Empty
	{
	}
	
	@Path("/")
	public static class RootResource implements IntA, Empty
	{
		@Override
		public String getFoo()
		{
			return "FOO";
		}
	}
	
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testNoDefaultsResource() throws Exception
   {
      POJOResourceFactory noDefaults = new POJOResourceFactory(RootResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);

      IntA client = ProxyFactory.create(IntA.class, generateBaseUrl());

      Assert.assertEquals("FOO", client.getFoo());
   }
}