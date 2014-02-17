package org.jboss.resteasy.test.core.request;

import org.jboss.resteasy.core.request.AcceptHeaders;
import org.jboss.resteasy.core.request.QualityValue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * @author Pascal S. de Kloe
 */
public class LocaleQualityValueTest
{

   @Test
   public void simple()
   {
      String header = "da, en-gb;q=0.8, en;q=0.7";
      Locale[] locales = {
              new Locale("da"),
              Locale.UK,
              Locale.ENGLISH
      };
      QualityValue[] fields = {
              QualityValue.DEFAULT,
              QualityValue.valueOf("0.8"),
              QualityValue.valueOf("0.7"),
      };
      assertList(header, locales, fields);
   }


   @Test
   public void wildcard()
   {
      String header = "zh, *";
      Locale[] fields = {Locale.CHINESE, null};
      QualityValue[] qualities = {
              QualityValue.DEFAULT,
              QualityValue.DEFAULT
      };
      assertList(header, fields, qualities);
   }


   @Test
   public void undefined()
   {
      String header = "en, en-US, en-cockney, i-cherokee, x-pig-latin";
      Locale[] fields = {Locale.ENGLISH, Locale.US};
      QualityValue[] qualities = {
              QualityValue.DEFAULT,
              QualityValue.DEFAULT
      };
      assertList(header, fields, qualities);
   }


   @Test
   public void empty()
   {
      assertNull(AcceptHeaders.getLocaleQualityValues(null));
      assertNull(AcceptHeaders.getLocaleQualityValues(""));
      assertNull(AcceptHeaders.getLocaleQualityValues(" "));
   }


   private static void assertList(String header, Locale[] fields, QualityValue[] qualities)
   {
      Map<Locale, QualityValue> map = AcceptHeaders.getLocaleQualityValues(header);
      List<Locale> expectedKeys = Arrays.asList(fields);
      List<QualityValue> expectedValues = Arrays.asList(qualities);
      assertEquals(expectedKeys, new ArrayList<Locale>(map.keySet()));
      assertEquals(expectedValues, new ArrayList<QualityValue>(map.values()));
   }

}
