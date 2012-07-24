/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.spring;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MediaTypeEditor;
import java.beans.PropertyEditor;

import static org.junit.Assert.*;

public class TestMediaTypePropertyEditor
{

   private PropertyEditor propertyEditor;

   @BeforeClass
   public static void setup()
   {
   }

   @Before
   public void setupEditor()
   {
      propertyEditor = new MediaTypeEditor();
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
