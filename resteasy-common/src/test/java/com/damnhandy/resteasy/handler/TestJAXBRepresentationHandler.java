/**
 * 
 */
package com.damnhandy.resteasy.handler;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author ryan
 *
 */
public class TestJAXBRepresentationHandler {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void getHandlerByMimeTypeTextXml() throws Exception {
		RepresentationHandler handler = RepresentationHandlerFactory.instance().getHandlerByMimeType("text/xml");
		assertTrue(handler != null);
		assertTrue(handler instanceof JAXBRepresentationHandler);
	}
	
	@Test
	public void getHandlerByMimeTypeApplicationXml() throws Exception {
		RepresentationHandler handler = RepresentationHandlerFactory.instance().getHandlerByMimeType("application/xml");
		assertTrue(handler != null);
		assertTrue(handler instanceof JAXBRepresentationHandler);
	}

	/**
	 * Test method for {@link com.damnhandy.resteasy.handler.JAXBRepresentationHandler#handleResponse(java.io.OutputStream, java.lang.Object)}.
	 */
	@Test
	public void testHandleResponse() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.damnhandy.resteasy.handler.JAXBRepresentationHandler#handleRequest(java.io.InputStream, java.lang.Class)}.
	 */
	@Test
	public void testHandleRequest() {
		//fail("Not yet implemented");
	}

}
