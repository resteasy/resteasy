package org.jboss.resteasy.test.spring.unit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.propertyeditor.mediatype.editor.MediaTypeEditor;
import java.beans.PropertyEditor;
import java.nio.charset.StandardCharsets;

/**
 * @tpSubChapter Spring
 * @tpChapter Unit test
 * @tpTestCaseDetails  Tests MediaTypeEditor
 * @tpSince RESTEasy 3.0.16
 */
public class MediaTypePropertyEditorTest {

   private PropertyEditor propertyEditor;
   private static final String ERROR_MESSAGE_TYPE = "Didn't get correct mediatype";
   private static final String ERROR_MESSAGE_ENCODING = "Didn't get correct mediatype";

   @Before
   public void setupEditor() {
      propertyEditor = new MediaTypeEditor();
   }

   /**
    * @tpTestDetails Tests that mediatype is set correctly to MediaTypeEditor
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testSetAsText() {
      propertyEditor.setAsText("application/xml");
      MediaType type = (MediaType) propertyEditor.getValue();
      Assert.assertEquals(ERROR_MESSAGE_TYPE, "application", type.getType());
      Assert.assertEquals(ERROR_MESSAGE_TYPE, "xml", type.getSubtype());
   }

   /**
    * @tpTestDetails Tests that mediatype and charset is set correctly to MediaTypeEditor
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testSetAsTextWithCharset() {
      propertyEditor.setAsText("application/xml;charset=UTF-8");
      MediaType type = (MediaType) propertyEditor.getValue();
      Assert.assertEquals(ERROR_MESSAGE_TYPE, "application", type.getType());
      Assert.assertEquals(ERROR_MESSAGE_TYPE, "xml", type.getSubtype());
      Assert.assertEquals(ERROR_MESSAGE_ENCODING, StandardCharsets.UTF_8.name(), type.getParameters().get("charset"));
   }

   /**
    * @tpTestDetails Tests that custom mediatype is set correctly to MediaTypeEditor
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testSetAsTextCustom() {
      propertyEditor.setAsText("application/custom");
      MediaType type = (MediaType) propertyEditor.getValue();
      Assert.assertEquals(ERROR_MESSAGE_TYPE, "application", type.getType());
      Assert.assertEquals(ERROR_MESSAGE_TYPE, "custom", type.getSubtype());
   }

   /**
    * @tpTestDetails Tests that getAsText() of MediaTypeEditor returns correct value
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testGetAsText() {
      MediaType type = MediaType.valueOf("application/xml");
      propertyEditor.setValue(type);
      String text = propertyEditor.getAsText();
      Assert.assertEquals(ERROR_MESSAGE_TYPE, "application/xml", text);
   }

   /**
    * @tpTestDetails Tests that getAsText() of MediaTypeEditor with custom MediaType returns correct value
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testGetAsTextCustom() {
      MediaType type = MediaType.valueOf("application/custom");
      propertyEditor.setValue(type);
      String text = propertyEditor.getAsText();
      Assert.assertEquals(ERROR_MESSAGE_TYPE, "application/custom", text);
   }

}
