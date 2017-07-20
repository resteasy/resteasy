package org.jboss.resteasy.test.resource.basic;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyWriter;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
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
public class MediaTypeNegotiationServerQualityTest {

	@Produces({ "application/*;qs=0.7", "text/*;qs=0.9" })
	public static class CustomMessageBodyWritter implements MessageBodyWriter<Object> {

		@Override
		public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
			return true;
		}

		@Override
		public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
			return -1;
		}

		@Override
		public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
				MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
				throws IOException, WebApplicationException {
		}

	}

	public static class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
		@Override
		public Response toResponse(NotFoundException notFoundException) {
			return Response.status(Status.NOT_FOUND).entity(new Object()).build();
		}
	}

	private static Client client;
	private static final String DEP = "MediaTypeNegotiationServerQualityTest";

	@Deployment
	public static Archive<?> deploy() {
		WebArchive war = TestUtil.prepareArchive(DEP);
		return TestUtil.finishContainerPrepare(war, null, CustomMessageBodyWritter.class,
				NotFoundExceptionMapper.class);
	}

	@BeforeClass
	public static void setup() {
		client = ClientBuilder.newClient();
	}

	@AfterClass
	public static void cleanup() {
		client.close();
	}

	private String generateURL() {
		return PortProviderUtil.generateBaseUrl(DEP);
	}

	@Test
	public void testServerQuality() throws Exception {
		Invocation.Builder request = client.target(generateURL()).path("echo").request("application/x;",
				"text/y");
		Response response = request.get();
		try {
			Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
			MediaType mediaType = response.getMediaType();
			Assert.assertEquals("text", mediaType.getType());
			Assert.assertEquals("y", mediaType.getSubtype());
		} finally {
			response.close();
		}
	}

}
