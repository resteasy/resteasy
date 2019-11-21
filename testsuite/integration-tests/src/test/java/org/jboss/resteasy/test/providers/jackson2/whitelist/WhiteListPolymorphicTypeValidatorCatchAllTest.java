package org.jboss.resteasy.test.providers.jackson2.whitelist;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.jackson.WhiteListPolymorphicTypeValidatorBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

/**
 * @tpSubChapter Jackson2 provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.10.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class WhiteListPolymorphicTypeValidatorCatchAllTest
{
   protected static final Logger logger = Logger
         .getLogger(WhiteListPolymorphicTypeValidatorCatchAllTest.class.getName());

   public static class TestBuilder extends WhiteListPolymorphicTypeValidatorBuilder {

      public static List<String> baseTypes = new ArrayList<>();
      public static List<String> subTypes = new ArrayList<>();

      @Override
      public BasicPolymorphicTypeValidator.Builder allowIfBaseType(String s) {
         baseTypes.add(s);
         return this;
      }

      @Override
      public BasicPolymorphicTypeValidator.Builder allowIfSubType(String s) {
         subTypes.add(s);
         return this;
      }
   }

   @Test
   public void testCatchAll() throws Exception {
      String btp = System.getProperty("resteasy.jackson.deserialization.whitelist.allowIfBaseType.prefix");
      System.setProperty("resteasy.jackson.deserialization.whitelist.allowIfBaseType.prefix", "*");
      String stp = System.getProperty("resteasy.jackson.deserialization.whitelist.allowIfSubType.prefix");
      System.setProperty("resteasy.jackson.deserialization.whitelist.allowIfSubType.prefix", "*");
      try {
         new TestBuilder();
         Assert.assertEquals(1, TestBuilder.baseTypes.size());
         Assert.assertTrue(TestBuilder.baseTypes.contains(""));
         Assert.assertEquals(1, TestBuilder.subTypes.size());
         Assert.assertTrue(TestBuilder.subTypes.contains(""));
      } finally {
         if (btp != null) {
            System.setProperty("resteasy.jackson.deserialization.whitelist.allowIfBaseType.prefix", btp);
         } else {
            System.clearProperty("resteasy.jackson.deserialization.whitelist.allowIfBaseType.prefix");
         }
         if (stp != null) {
            System.setProperty("resteasy.jackson.deserialization.whitelist.allowIfSubType.prefix", stp);
         } else {
            System.clearProperty("resteasy.jackson.deserialization.whitelist.allowIfSubType.prefix");
         }
      }
   }
}
