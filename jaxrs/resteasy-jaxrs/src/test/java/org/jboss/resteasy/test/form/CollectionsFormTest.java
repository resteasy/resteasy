package org.jboss.resteasy.test.form;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class CollectionsFormTest extends BaseResourceTest
{

	public static class Person {
		@Form(prefix="telephoneNumbers") List<TelephoneNumber> telephoneNumbers;
		@Form(prefix="address") Map<String, Address> adresses;
	}

	public static class TelephoneNumber {
		@FormParam("countryCode") private String countryCode;
		@FormParam("number") private String number;
	}

	public static class Address {
		@FormParam("street") private String street;
		@FormParam("houseNumber") private String houseNumber;
	}

	@Path("person")
	public static class MyResource {

		@POST
		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		public void post (@Form Person p) {
			assertEquals(2, p.telephoneNumbers.size());
			assertEquals(2, p.adresses.size());
			assertEquals("31", p.telephoneNumbers.get(0).countryCode);
			assertEquals("91", p.telephoneNumbers.get(1).countryCode);
			assertEquals("Main Street", p.adresses.get("INVOICE").street);
			assertEquals("Square One", p.adresses.get("SHIPPING").street);
		}
	}

	@Before
	public void register () {
		addPerRequestResource(MyResource.class);
	}

	@Test
	public void shouldSupportCollectionsInForm() throws Exception {
		MockHttpResponse response = new MockHttpResponse();
		MockHttpRequest request = MockHttpRequest.post("person").accept(MediaType.TEXT_PLAIN).contentType(MediaType.APPLICATION_FORM_URLENCODED);
		request.addFormHeader("telephoneNumbers[0].countryCode", "31");
		request.addFormHeader("telephoneNumbers[0].number", "0612345678");
		request.addFormHeader("telephoneNumbers[1].countryCode", "91");
		request.addFormHeader("telephoneNumbers[1].number", "9717738723");
		request.addFormHeader("address[INVOICE].street", "Main Street");
		request.addFormHeader("address[INVOICE].houseNumber", "2");
		request.addFormHeader("address[SHIPPING].street", "Square One");
		request.addFormHeader("address[SHIPPING].houseNumber", "13");
		dispatcher.invoke(request, response);
	}
}