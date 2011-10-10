package org.jboss.resteasy.test.finegrain.resource;

import static org.jboss.resteasy.test.TestPortProvider.createGetMethod;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CovariantReturnSubresourceLocators
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

	private void _test(HttpClient client, String path, String body)
	{
		{
			GetMethod method = createGetMethod(path);
			try
			{
				int status = client.executeMethod(method);
				Assert.assertEquals(status, HttpResponseCodes.SC_OK);
				Assert.assertEquals(body, method.getResponseBodyAsString());
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}

	}

	@Test
	public void testIt() throws Exception
	{
		dispatcher = EmbeddedContainer.start().getDispatcher();
		try
		{
			dispatcher.getRegistry().addPerRequestResource(RootImpl.class);
			HttpClient client = new HttpClient();
			_test(client, "/path/sub/xyz", "Boo! - xyz");
		}
		finally
		{
			EmbeddedContainer.stop();
		}
	}
}