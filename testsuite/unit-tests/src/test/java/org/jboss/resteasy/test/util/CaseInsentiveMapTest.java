package org.jboss.resteasy.test.util;

import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.junit.Assert;
import org.junit.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for CaseInsensitiveMap class.
 * @tpSince RESTEasy 3.0.16
 */
public class CaseInsentiveMapTest {

   /**
    * This is done to support re-use of these tests in extensions like {@link org.jboss.resteasy.util.TrackingMap}.
    */
   protected <T> CaseInsensitiveMap<T> createMap() {
      return new CaseInsensitiveMap<>();
   }

   /**
    * @tpTestDetails Test for CaseInsensitiveMap class, key of map should be case insensitive.
    * @tpSince RESTEasy 3.0.16
    */
   @Test
   public void testMap() {
      CaseInsensitiveMap<String> map = createMap();
      map.add("Cache-Control", "nocache");
      Assert.assertEquals("key of map should be case insensitive", "nocache", map.getFirst("caChe-CONTROL"));
   }
}
