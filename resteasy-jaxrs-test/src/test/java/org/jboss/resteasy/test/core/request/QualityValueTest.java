package org.jboss.resteasy.test.core.request;

import org.jboss.resteasy.core.request.QualityValue;
import org.jboss.resteasy.spi.BadRequestException;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author Pascal S. de Kloe
 */
public class QualityValueTest
{

   @Test
   public void zero()
   {
      assertEquals(0, QualityValue.valueOf("0").intValue());
      assertEquals(0, QualityValue.valueOf("0.").intValue());
      assertEquals(0, QualityValue.valueOf("0.0").intValue());
      assertEquals(0, QualityValue.valueOf("0.00").intValue());
      assertEquals(0, QualityValue.valueOf("0.000").intValue());
   }


   @Test
   public void one()
   {
      assertEquals(1000, QualityValue.valueOf("1").intValue());
      assertEquals(1000, QualityValue.valueOf("1.").intValue());
      assertEquals(1000, QualityValue.valueOf("1.0").intValue());
      assertEquals(1000, QualityValue.valueOf("1.00").intValue());
      assertEquals(1000, QualityValue.valueOf("1.000").intValue());
   }


   @Test
   public void fractions()
   {
      assertEquals(1, QualityValue.valueOf("0.001").intValue());
      assertEquals(12, QualityValue.valueOf("0.012").intValue());
      assertEquals(123, QualityValue.valueOf("0.123").intValue());
      assertEquals(120, QualityValue.valueOf("0.12").intValue());
      assertEquals(100, QualityValue.valueOf("0.1").intValue());
   }


   @Test
   public void equivalent()
   {
      assertTrue(QualityValue.valueOf("0.1").equals(QualityValue.valueOf("0.10")));
      assertFalse(QualityValue.valueOf("1.").equals(QualityValue.valueOf("0.999")));
   }


   @Test
   public void comparison()
   {
      assertTrue(QualityValue.LOWEST.compareTo(QualityValue.HIGHEST) < 0);
      assertTrue(QualityValue.DEFAULT.compareTo(QualityValue.HIGHEST) == 0);
      assertTrue(QualityValue.LOWEST.compareTo(QualityValue.NOT_ACCEPTABLE) > 0);
   }


   @Test(expected = BadRequestException.class)
   public void tooLarge()
   {
      QualityValue.valueOf("1.001");
   }


   @Test(expected = BadRequestException.class)
   public void tooSmall()
   {
      QualityValue.valueOf("-0.001");
   }


   @Test(expected = BadRequestException.class)
   public void tooLong()
   {
      QualityValue.valueOf("0.1234");
   }


   @Test(expected = BadRequestException.class)
   public void tooShort()
   {
      QualityValue.valueOf("");
   }


   @Test(expected = BadRequestException.class)
   public void wrongContent()
   {
      QualityValue.valueOf("0,2F");
   }


   @Test
   public void nullContent()
   {
      assertEquals(QualityValue.DEFAULT, QualityValue.valueOf(null));
   }


   @Test
   public void numbers()
   {
      QualityValue x = QualityValue.valueOf("0.08");
      assertEquals(80, x.intValue());
      assertEquals(80L, x.longValue());
      assertEquals(0.08f, x.floatValue());
      assertEquals(0.08d, x.doubleValue());
   }

}