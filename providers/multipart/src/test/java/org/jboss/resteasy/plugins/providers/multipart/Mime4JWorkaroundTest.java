package org.jboss.resteasy.plugins.providers.multipart;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class Mime4JWorkaroundTest {

   @Before
   public void unsetMemThresholdProperty() {
      System.clearProperty(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY);
   }
   @Test
   public void testMemThresholdDefault()
   {
      assertEquals(Mime4JWorkaround.DEFAULT_MEM_THRESHOLD, Mime4JWorkaround.getMemThreshold());
   }

   @Test
   public void testMemThresholdConfigProperty()
   {
      System.setProperty(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY, "2048");
      assertEquals(2048, Mime4JWorkaround.getMemThreshold());
   }

   @Test
   public void testInvalidMemThresholdConfigPropertyReturnsDefault_NegativeNumber()
   {
      System.setProperty(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY, "-2048");
      assertEquals(Mime4JWorkaround.DEFAULT_MEM_THRESHOLD, Mime4JWorkaround.getMemThreshold());
   }

   @Test
   public void testInvalidMemThresholdConfigPropertyReturnsDefault_DecimalNumber()
   {
      System.setProperty(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY, "2048.2");
      assertEquals(Mime4JWorkaround.DEFAULT_MEM_THRESHOLD, Mime4JWorkaround.getMemThreshold());
   }

   @Test
   public void testInvalidMemThresholdConfigPropertyReturnsDefault_NotANumber()
   {
      System.setProperty(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY, "Infinity");
      assertEquals(Mime4JWorkaround.DEFAULT_MEM_THRESHOLD, Mime4JWorkaround.getMemThreshold());
   }
}
