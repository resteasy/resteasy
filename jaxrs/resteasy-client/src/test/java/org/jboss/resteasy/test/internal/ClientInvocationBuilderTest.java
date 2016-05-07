package org.jboss.resteasy.test.internal;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClientInvocationBuilderTest {

	protected static ResteasyDeployment deployment;
	protected static Dispatcher dispatcher;
	protected static ByteArrayOutputStream baos = new ByteArrayOutputStream();

	@Path("/")
	public static class TestResource {
		@POST
		@Produces("text/plain")
		public String post(String s) {
			System.out.println("server entity: " + s);
			return s;
		}

		@GET
		@Produces("text/plain")
		public String get() {
			String s = "default";
			System.out.println("server entity: " + s);
			return s;
		}
	}

	@BeforeClass
	public static void before() throws Exception {
		deployment = EmbeddedContainer.start();
		dispatcher = deployment.getDispatcher();
		deployment.getRegistry().addPerRequestResource(TestResource.class);
	}

	@AfterClass
	public static void after() throws Exception {
		EmbeddedContainer.stop();
		dispatcher = null;
		deployment = null;
	}

	@Test
	public void test_build_method_return_new_instance() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		try {
			ResteasyWebTarget webTarget = client.target("http://localhost:8081");
			Builder invocationBuilder = webTarget.request();

			// GET invocation
			ClientInvocation getInvocation = (ClientInvocation) invocationBuilder.accept(MediaType.TEXT_PLAIN_TYPE)
					.build("GET");
			Assert.assertEquals("default", getInvocation.invoke(String.class));

			// Alter invocationBuilder
			invocationBuilder.accept(MediaType.APPLICATION_XML_TYPE);
			invocationBuilder.property("property1", "property1Value");
			// Previously built getInvocation must not have been altered.(Those
			// tests are not about immutability of
			// getInvocation instance but about Builder pattern behavior).
			Assert.assertFalse(getInvocation.getHeaders().getAcceptableMediaTypes()
					.contains(MediaType.APPLICATION_XML_TYPE));
			Assert.assertFalse(getInvocation.getConfiguration().getProperties().containsKey("property1"));

			// POST invocation
			ClientInvocation postInvocation = (ClientInvocation) invocationBuilder.accept(MediaType.TEXT_PLAIN_TYPE)
					.build("POST", Entity.text("test"));
			// Previous lines must build a new postInvocation instance and not
			// modify previously built getInvocation instance.
			// (It's all about Builder pattern behavior not immutability since
			// Invocation is a mutable object.)
			Assert.assertNotSame(getInvocation, postInvocation);
			Assert.assertEquals("default", getInvocation.invoke(String.class));
			Assert.assertTrue(postInvocation.getConfiguration().getProperties().containsKey("property1"));
			Assert.assertEquals("test", postInvocation.invoke(String.class));
		} finally {
			client.close();
		}
	}

	@Test
	public void test_build_method_reset_entity() throws InterruptedException, ExecutionException {
		ResteasyClient client = new ResteasyClientBuilder().build();
		try {
			ResteasyWebTarget webTarget = client.target("http://localhost:8081");
			Builder invocationBuilder = webTarget.request().accept(MediaType.TEXT_PLAIN_TYPE);

			// POST invocation
			ClientInvocation postInvocation = (ClientInvocation) invocationBuilder.build("POST", Entity.text("test"));
			Assert.assertEquals("test", postInvocation.invoke(String.class));

			// GET invocation
			ClientInvocation getInvocation = (ClientInvocation) invocationBuilder.build("GET");
			// In order the request to be OK, invocation instance built from
			// invocationBuilder must not contain the previous entity used for
			// post request.
			Assert.assertNull(getInvocation.getEntity());
			Assert.assertEquals("default", getInvocation.invoke(String.class));
			
			//Same test for async request
			AsyncInvoker async = invocationBuilder.async();
			
			// POST invocation
			Future<String> postFuture = async.post(Entity.text("test"), String.class);
			Assert.assertEquals("test",postFuture.get());
			
			// GET invocation
			Future<String> getFuture = async.get(String.class);
			Assert.assertEquals("default",getFuture.get());
		} finally {
			client.close();
		}
	}

}
