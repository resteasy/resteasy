package org.jboss.resteasy.util;


import org.junit.Test;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;

public class MediaTypeHelperTest {

	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML + ";charset=utf8"})
	static interface TestInterface {

		void aMethod();

	}

	@Test
	public void testGetProduces() throws Exception {
		MediaType result =
				MediaTypeHelper.getProduces(TestInterface.class, TestInterface.class.getMethod("aMethod"), MediaType.APPLICATION_XML_TYPE);
		assertEquals(MediaType.APPLICATION_XML_TYPE, result);


	}
}
