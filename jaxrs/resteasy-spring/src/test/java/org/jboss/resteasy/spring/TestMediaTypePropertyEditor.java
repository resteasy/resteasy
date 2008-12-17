/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.spring;

import static org.junit.Assert.*;

import java.beans.PropertyEditor;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.spring.MediaTypePropertyEditor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestMediaTypePropertyEditor
{

   private PropertyEditor propertyEditor;

   @BeforeClass
   public static void setup()
   {
      ResteasyProviderFactory.initializeInstance();
   }

   @Before
   public void setupEditor()
   {
      propertyEditor = new MediaTypePropertyEditor();
   }

   @Test
   public void validateTypeMappingsExistInSpring()
   {
      ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-typemapping-test-server.xml");
      SynchronousDispatcher dispatcher = (SynchronousDispatcher) ctx.getBean("resteasy.dispatcher");
      Map<String, MediaType> mappings = dispatcher.getMediaTypeMappings();
      assertEquals(2, mappings.size());
      assertEquals("application/xml", mappings.get("xml").toString());
      assertEquals("application/json", mappings.get("json").toString());
   }

   @Test
   public void testSetAsText()
   {
      propertyEditor.setAsText("application/xml");
      MediaType type = (MediaType) propertyEditor.getValue();
      assertEquals("application", type.getType());
      assertEquals("xml", type.getSubtype());
   }

   @Test
   public void testSetAsTextWithCharset()
   {
      propertyEditor.setAsText("application/xml;charset=UTF-8");
      MediaType type = (MediaType) propertyEditor.getValue();
      assertEquals("application", type.getType());
      assertEquals("xml", type.getSubtype());
      assertEquals("UTF-8", type.getParameters().get("charset"));
   }

   @Test
   public void testSetAsTextCustom()
   {
      propertyEditor.setAsText("application/custom");
      MediaType type = (MediaType) propertyEditor.getValue();
      assertEquals("application", type.getType());
      assertEquals("custom", type.getSubtype());
   }

   @Test
   public void testGetAsText()
   {
      MediaType type = MediaType.valueOf("application/xml");
      propertyEditor.setValue(type);
      String text = propertyEditor.getAsText();
      assertEquals("application/xml", text);
   }

   @Test
   public void testGetAsTextCustom()
   {
      MediaType type = MediaType.valueOf("application/custom");
      propertyEditor.setValue(type);
      String text = propertyEditor.getAsText();
      assertEquals("application/custom", text);
   }

}
