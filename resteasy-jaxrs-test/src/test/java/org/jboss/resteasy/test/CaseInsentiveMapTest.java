package org.jboss.resteasy.test;

import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CaseInsentiveMapTest
{
   @Test
   public void testMap()
   {
      CaseInsensitiveMap<String> map = new CaseInsensitiveMap<String>();
      map.add("Cache-Control", "nocache");
      Assert.assertEquals("nocache", map.getFirst("caChe-CONTROL"));
   }
}
