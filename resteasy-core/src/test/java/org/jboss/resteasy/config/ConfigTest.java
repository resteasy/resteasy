package org.jboss.resteasy.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class ConfigTest
{

   @Before
   public void setup()
   {
      TestConfigImpl.config.clear();
      TestConfigImpl.requestedProperties.clear();
   }

   @Test
   public void testCanAccessConfigProperties()
   {
      TestConfigImpl.config.put("abc", new Integer(123));
      Optional<Integer> opt1 = ResteasyConfig.instance.getOptionalValue("abc", Integer.class);
      assertTrue(opt1.isPresent());
      assertEquals(123, opt1.get().intValue());

      opt1 = ResteasyConfig.instance.getOptionalValue("xyz", Integer.class);
      assertFalse(opt1.isPresent());

      assertTrue(TestConfigImpl.requestedProperties.containsKey("abc"));
      assertTrue(TestConfigImpl.requestedProperties.containsKey("xyz"));
   }
}
