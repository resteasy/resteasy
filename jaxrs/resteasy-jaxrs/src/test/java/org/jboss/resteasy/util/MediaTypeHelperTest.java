package org.jboss.resteasy.util;


import org.junit.Test;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;

public class MediaTypeHelperTest
{

	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML + ";charset=utf8"})
	interface TestInterfaceWithProduces
   {
		void aMethod();
	}

   interface TestInterfaceWithoutProduces
   {
      void aMethod();
   }

   @Test
   public void testGetProducesWithDefaultProduces() throws Exception
   {
      MediaType result =
            MediaTypeHelper.getProduces(TestInterfaceWithoutProduces.class, TestInterfaceWithoutProduces.class.getMethod("aMethod"), MediaType.APPLICATION_XML_TYPE);
      assertEquals(MediaType.APPLICATION_XML_TYPE, result);
   }

	@Test
	public void testGetProducesWithPreferredProduces() throws Exception
   {
		MediaType result =
				MediaTypeHelper.getProduces(TestInterfaceWithProduces.class, TestInterfaceWithProduces.class.getMethod("aMethod"), null, MediaType.APPLICATION_XML_TYPE);
		assertEquals(MediaType.APPLICATION_XML_TYPE, result);
	}
}
