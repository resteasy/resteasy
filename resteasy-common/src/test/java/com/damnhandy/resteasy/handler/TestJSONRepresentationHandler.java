/**
 * 
 */
package com.damnhandy.resteasy.handler;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.damnhandy.resteasy.data.Contact;

/**
 * @author ryan
 *
 */
public class TestJSONRepresentationHandler {

	protected static final Logger logger = Logger.getLogger(TestJSONRepresentationHandler.class);
	
	
	private Contact contact;
	private File file = new File("target/Contact.json");
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		URL log4j = TestJSONRepresentationHandler.class.getClassLoader().getResource("log4j.xml");
        DOMConfigurator.configure(log4j);
		RepresentationHandlerFactory.instance();
		Contact contact = new Contact();
		contact.setCreated(new Date());
		contact.setLastModified(new Date());
		contact.setFirstName("John");
		contact.setLastName("Brown");
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getHandlerByMimeType() throws Exception {
		RepresentationHandler handler = RepresentationHandlerFactory.instance().getHandlerByMimeType("application/json");
		assertTrue(handler != null);
		assertTrue(handler instanceof JSONRepresentationHandler);
	}
	/**
	 * Test method for {@link com.damnhandy.resteasy.handler.JSONRepresentationHandler#handleResponse(java.io.OutputStream, java.lang.Object)}.
	 */
	@Test
	public void testHandlerResponseByExtention() throws Exception {
		contact = new Contact();
		contact.setCreated(new Date());
		contact.setLastModified(new Date());
		contact.setFirstName("John");
		contact.setLastName("Brown");
		RepresentationHandler handler = RepresentationHandlerFactory.instance().getHandlerByExtention("json");
 		assertTrue(handler instanceof JSONRepresentationHandler);
 		OutputStream out = new FileOutputStream(file);
 		handler.handleResponse(out, contact);
 		out.flush();
 		out.close();
 		assertTrue(file.exists());
	}
	
	/**
	 * Test method for {@link com.damnhandy.resteasy.handler.JSONRepresentationHandler#handleRequest(java.io.InputStream, java.lang.Class)}.
	 */
	@Test
	public void testHandlerRequestByExtention() throws Exception {
		contact = new Contact();
		contact.setCreated(new Date());
		contact.setLastModified(new Date());
		contact.setFirstName("John");
		contact.setLastName("Brown");
		assertTrue(file.exists());
		RepresentationHandler handler = RepresentationHandlerFactory.instance().getHandlerByExtention("json");
 		assertTrue(handler instanceof JSONRepresentationHandler);
 		Contact myContact = (Contact) handler.handleRequest(new FileInputStream(file), Contact.class);
 		assertTrue(myContact != null && contact != null);
 		assertTrue(contact.getFirstName().equals(myContact.getFirstName()));
 		assertTrue(contact.getLastName().equals(myContact.getLastName()));
	}

}
