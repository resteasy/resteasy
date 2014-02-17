package org.jboss.resteasy.test.finegrain.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CovariantReturnSubresourceLocatorsTest
{
	private static Dispatcher dispatcher;

	public static interface Root
	{
		@Path("sub/{path}")
		public Sub getSub(@PathParam("path") String path);
	}

	public static interface Sub
	{
		@GET
		@Produces("text/plain")
		public String get();
	}

	@Path("/path")
	public static class RootImpl implements Root
	{
		@Override
		public SubImpl getSub(String path)
		{
			return new SubImpl(path);
		}
	}

	public static class SubImpl implements Sub
	{
		private final String path;

		public SubImpl(String path)
		{
			this.path = path;
		}

		public String get()
		{
			return "Boo! - " + path;
		}
	}

	@BeforeClass
	public static void before() throws Exception
	{
	}

	@AfterClass
	public static void after() throws Exception
	{
	}

	private void _test(String path, String body)
	{
	   ClientRequest request = new ClientRequest(generateURL(path));
	   ClientResponse<String> response = null;
	   try
	   {
	      response = request.get(String.class);
	      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
	      Assert.assertEquals(body, response.getEntity());
	   }
	   catch (Exception e)
       {
          throw new RuntimeException(e);
       } 

	}

	@Test
	public void testIt() throws Exception
	{
		dispatcher = EmbeddedContainer.start().getDispatcher();
		try
		{
			dispatcher.getRegistry().addPerRequestResource(RootImpl.class);
			_test("/path/sub/xyz", "Boo! - xyz");
		}
		finally
		{
			EmbeddedContainer.stop();
		}
	}
}