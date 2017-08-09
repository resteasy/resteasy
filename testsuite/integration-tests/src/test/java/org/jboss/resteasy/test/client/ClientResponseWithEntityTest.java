package org.jboss.resteasy.test.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class ClientResponseWithEntityTest {

	@XmlRootElement
	public static class Message {
		private String message;

		public Message() {
		}

		public String getMessage() {
			return this.message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	@Path("echo")
	@Produces(MediaType.APPLICATION_XML)
	public static class EchoResource {

		@GET
		public Response echo(@QueryParam("msg") String msg) {
			Message message = new Message();
			message.setMessage(String.valueOf(msg));
			return Response.ok(message).build();
		}

	}

	private static Client client;
	private static final String DEP = "ClientResponseWithEntityTest";

	@Deployment
	public static Archive<?> deploy() {
		WebArchive war = TestUtil.prepareArchive(DEP);
		war.addClass(Message.class);
		war.addClass(EchoResource.class);
		return TestUtil.finishContainerPrepare(war, null, EchoResource.class);
	}

	@BeforeClass
	public static void setup() {
		client = ClientBuilder.newClient();
	}

	@AfterClass
	public static void cleanup() {
		client.close();
	}

	private static String generateURL() {
		return PortProviderUtil.generateBaseUrl(DEP);
	}

	@Test
	public void Should_ReturnEntity_When_NoNull() throws Exception {
		Invocation.Builder request = client.target(generateURL()).path("echo").queryParam("msg", "Hello world")
				.request(MediaType.APPLICATION_XML_TYPE);
		try (ClientResponse response = (ClientResponse) request.get()) {
			Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
			Assert.assertTrue(response.hasEntity());
			Assert.assertNotNull(response.getEntity());
			Assert.assertNotNull(response.getEntityClass());
		}
	}

	@Test(expected = IllegalStateException.class)
	public void Should_ThrowIllegalStateException_When_EntityIsConsumed() throws Exception {
		Invocation.Builder request = client.target(generateURL()).path("echo").queryParam("msg", "Hello world")
				.request(MediaType.APPLICATION_XML_TYPE);
		try (ClientResponse response = (ClientResponse) request.get()) {
			Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
			Assert.assertTrue(response.hasEntity());
			InputStream entityStream = (InputStream) response.getEntity();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int wasRead = 0;
			do {
				wasRead = entityStream.read(buffer);
				if (wasRead > 0) {
					baos.write(buffer, 0, wasRead);
				}
			} while (wasRead > -1);
			response.getEntity();
		}
	}

}
