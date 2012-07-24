package org.jboss.resteasy.test.providers.multipart;

import junit.framework.Assert;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Attila Kiraly
 */
public class InputPartDefaultContentTypeWildcardOverwriteTest extends
		BaseResourceTest {
	protected static final String WILDCARD_WITH_CHARSET_UTF_8 = "*/*; charset=UTF-8";

	@Path("/mime")
	public static class FormService {

		@POST
		@Consumes(MediaType.MULTIPART_FORM_DATA)
		@Produces(MediaType.TEXT_PLAIN)
		public int echoMultipartForm(@MultipartForm ContainerBean containerBean) {
			return containerBean.getFoo().getMyInt();
		}
	}

	public static class ContainerBean {
		@FormParam("foo")
		@PartType(MediaType.APPLICATION_XML)
		private XmlBean foo;

		public XmlBean getFoo() {
			return foo;
		}

		public void setFoo(XmlBean foo) {
			this.foo = foo;
		}
	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class XmlBean {
		private int myInt;
		private String myString;

		public int getMyInt() {
			return myInt;
		}

		public void setMyInt(int myInt) {
			this.myInt = myInt;
		}

		public String getMyString() {
			return myString;
		}

		public void setMyString(String myString) {
			this.myString = myString;
		}
	}

	@Provider
	@ServerInterceptor
	public static class ContentTypeSetterPreProcessorInterceptor implements
			PreProcessInterceptor {

		public ServerResponse preProcess(HttpRequest request,
				ResourceMethod method) throws Failure, WebApplicationException {
			request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY,
					WILDCARD_WITH_CHARSET_UTF_8);
			return null;
		}

	}

	@Before
	public void setUp() throws Exception {
		dispatcher.getRegistry().addPerRequestResource(FormService.class);
		dispatcher.getProviderFactory().registerProvider(
				ContentTypeSetterPreProcessorInterceptor.class, false);
	}

	private static final String TEST_URI = TestPortProvider.generateURL("");

	@Test
	public void testContentType() throws Exception {
		String message = "--boo\r\n"
				+ "Content-Disposition: form-data; name=\"foo\"\r\n"
				+ "Content-Transfer-Encoding: 8bit\r\n\r\n"
				+ "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<xmlBean><myInt>27</myInt><myString>Lorem Ipsum</myString></xmlBean>\r\n"
				+ "--boo--\r\n";
		
		ClientRequest request = new ClientRequest(TEST_URI + "/mime");
		request.body("multipart/form-data; boundary=boo", message.getBytes("utf-8"));
		ClientResponse<String> response = request.post(String.class);
        Assert.assertEquals("Status code is wrong.", 20, response.getStatus() / 10);
        Assert.assertEquals("Response text is wrong", "27", response.getEntity());
	}
}
