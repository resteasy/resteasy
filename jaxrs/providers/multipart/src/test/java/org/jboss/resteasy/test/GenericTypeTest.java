package org.jboss.resteasy.test;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

/**
 * Unit tests for RESTEASY-1260.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date January 18, 2016
 */
public class GenericTypeTest
{
	protected static ResteasyDeployment deployment;
	protected static Dispatcher dispatcher;
	protected static final GenericType<List<String>> stringListType = new GenericType<List<String>>() {};


	@BeforeClass
	public static void before() throws Exception
	{
		deployment = EmbeddedContainer.start();
		dispatcher = deployment.getDispatcher();
		deployment.getRegistry().addPerRequestResource(TestResource.class);
		dispatcher.getProviderFactory().registerProvider(StringListReaderWriter.class);
		System.out.println("ResteasyProviderFactory: " + dispatcher.getProviderFactory());
	}

	@AfterClass
	public static void after() throws Exception
	{
		EmbeddedContainer.stop();
		dispatcher = null;
		deployment = null;
	}

	@Path("")
	public static class TestResource
	{
		@POST
		@Path("test")
		@Consumes(MediaType.MULTIPART_FORM_DATA)
		@Produces(MediaType.TEXT_PLAIN)
		public Response testInputPartSetMediaType(MultipartInput input) throws IOException
		{
			List<InputPart> parts = input.getParts();
			InputPart part = parts.get(0);
			List<String> body = part.getBody(stringListType);
			String reply = "";
			for (Iterator<String> it = body.iterator(); it.hasNext(); )
			{
				reply += it.next() + " ";
			}
			System.out.println("server response: " + reply);
			return Response.ok(reply).build();
		}
	}

	@Test
	public void testGenericType() throws Exception
	{   
		System.out.println("ResteasyProviderFactory: " + ResteasyProviderFactory.getInstance());
		ResteasyClient client = new ResteasyClientBuilder().providerFactory(ResteasyProviderFactory.getInstance()).build();
		ResteasyWebTarget target = client.target(generateURL("/test"));
		MultipartFormDataOutput output = new MultipartFormDataOutput();
		List<String> list = new ArrayList<String>();
		list.add("darth");
		list.add("sidious");
		output.addFormData("key", list, stringListType, MediaType.APPLICATION_XML_TYPE);
		Entity<MultipartFormDataOutput> entity = Entity.entity(output, MediaType.MULTIPART_FORM_DATA_TYPE);
		String response = target.request().post(entity, String.class);
		System.out.println("response: " + response);
		Assert.assertEquals("darth sidious ", response);
	}

	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public static class StringListReaderWriter implements MessageBodyReader<List<String>>, MessageBodyWriter<List<String>>
	{
		@Override
		public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
		{
			return stringListType.getType().equals(genericType);
		}

		@Override
		public long getSize(List<String> t, Class<?> type, Type genericType, Annotation[] annotations,
				MediaType mediaType) {
			return -1;
		}

		@Override
		public void writeTo(List<String> t, Class<?> type, Type genericType, Annotation[] annotations,
				MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
						throws IOException, WebApplicationException {
			List<String> list = (List<String>) t;
			for (Iterator<String> it = list.iterator(); it.hasNext(); )
			{
				entityStream.write(it.next().getBytes());
				entityStream.write("\r".getBytes());
			}
		}

		@Override
		public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
			return stringListType.getType().equals(genericType);
		}

		@Override
		public List<String> readFrom(Class<List<String>> type, Type genericType, Annotation[] annotations,
				MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
						throws IOException, WebApplicationException {
			List<String> list = new ArrayList<String>();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte b = (byte) entityStream.read();
			while (b > -1)
			{
				while (b != '\r')
				{
					baos.write(b);
					b = (byte) entityStream.read();
				}
				list.add(new String(baos.toByteArray()));
				baos.reset();
				b = (byte) entityStream.read();
			}
			return list;
		} 
	}
}
