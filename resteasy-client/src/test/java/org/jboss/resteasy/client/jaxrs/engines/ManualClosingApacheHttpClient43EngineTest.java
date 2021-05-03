package org.jboss.resteasy.client.jaxrs.engines;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ManualClosingApacheHttpClient43EngineTest {

   @Before
   public void unsetMemThresholdProperty() {
      System.clearProperty(ManualClosingApacheHttpClient43Engine.FILE_UPLOAD_IN_MEMORY_THRESHOLD_PROPERTY);
   }
   @Test
   public void testMemThresholdDefault()
   {
      assertEquals(1, new ManualClosingApacheHttpClient43Engine().getFileUploadInMemoryThresholdLimit());
   }

   @Test
   public void testMemThresholdConfigProperty()
   {
      System.setProperty(ManualClosingApacheHttpClient43Engine.FILE_UPLOAD_IN_MEMORY_THRESHOLD_PROPERTY, "8");
      assertEquals(8, new ManualClosingApacheHttpClient43Engine().getFileUploadInMemoryThresholdLimit());
   }

   @Test
   public void testInvalidMemThresholdConfigPropertyReturnsDefault_NegativeNumber()
   {
      System.setProperty(ManualClosingApacheHttpClient43Engine.FILE_UPLOAD_IN_MEMORY_THRESHOLD_PROPERTY, "-8");
      assertEquals(1, new ManualClosingApacheHttpClient43Engine().getFileUploadInMemoryThresholdLimit());
   }

   @Test
   public void testInvalidMemThresholdConfigPropertyReturnsDefault_DecimalNumber()
   {
      System.setProperty(ManualClosingApacheHttpClient43Engine.FILE_UPLOAD_IN_MEMORY_THRESHOLD_PROPERTY, "2048.2");
      assertEquals(1, new ManualClosingApacheHttpClient43Engine().getFileUploadInMemoryThresholdLimit());
   }

   @Test
   public void testInvalidMemThresholdConfigPropertyReturnsDefault_NotANumber()
   {
      System.setProperty(ManualClosingApacheHttpClient43Engine.FILE_UPLOAD_IN_MEMORY_THRESHOLD_PROPERTY, "Infinity");
      assertEquals(1, new ManualClosingApacheHttpClient43Engine().getFileUploadInMemoryThresholdLimit());
   }
}
