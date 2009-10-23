package org.jboss.resteasy.test.providers.jaxb;

import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;


/**
 @author Pascal S. de Kloe
 */
@Path("/")
public class CharacterSetTest {

	private final Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
	
	private final String[] characterSets = {"US-ASCII", "UTF-8", "ISO-8859-1"};
	
	@XmlRootElement(name="test-data")
	public static class TestData {
		private String text = "Text \u0100.";

		public String getText() {
			return text;
		}

		public void setText(String value) {
			text = value;
		}
	}

	
	public CharacterSetTest() {
		dispatcher.getRegistry().addSingletonResource(this);
	}


	@GET
	@Path("out-of-the-box")
	@Produces("application/xml")
	public Response getOutOfTheBox() {
		return Response.ok(new TestData()).build();
	}


	@GET
	@Path("variant-selection")
	@Produces("application/xml")
	public Response getVariantSelection(@Context Request request) {
		int i = characterSets.length;
		MediaType[] mediaTypes = new MediaType[i];
		while (--i >= 0)
			mediaTypes[i] = MediaType.valueOf("application/xml;charset=" + characterSets[i]);
		List<Variant> variants = Variant.mediaTypes(mediaTypes).build();
		Variant variant = request.selectVariant(variants);
		if (variant == null)
			return Response.notAcceptable(variants).build();
		return Response.ok(new TestData(), variant).build();
	}


	@Ignore("Not implemented yet.")
	@Test
	public void outOfTheBox() throws URISyntaxException {
		assertCharset("/out-of-the-box");
	}
	

	@Test
	public void variantSelection() throws URISyntaxException {
		assertCharset("/variant-selection");
	}
	

	private void assertCharset(String path) throws URISyntaxException {
		for (String characterSet : characterSets) {
			MockHttpRequest request = MockHttpRequest.get(path);
			request.accept("application/xml");
			request.header("Accept-Charset", characterSet);
		
			MockHttpResponse response = new MockHttpResponse();
			dispatcher.invoke(request, response);
			assertEquals("Status code.", 200, response.getStatus());

			String contentType = response.getOutputHeaders().getFirst("Content-Type").toString();
			String charsetPattern = "application/xml\\s*;\\s*charset\\s*=\\s*\"?" + characterSet + "\"?";
			String charsetErrorMessage = contentType + " does not match " + charsetPattern;
			assertTrue(charsetErrorMessage, contentType.matches(charsetPattern));

			String xml = response.getContentAsString();
			String encodingPattern = "<\\?xml[^>]*encoding\\s*=\\s*['\"]" + characterSet + "['\"].*";
			String encodingErrorMessage = xml + " does not match " + encodingPattern;
			assertTrue(encodingErrorMessage, xml.matches(encodingPattern));
		}
	}

}
