package org.jboss.resteasy.test.util;

import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for CaseInsensitiveMap class.
 * @tpSince RESTEasy 3.0.16
 */
public class CaseInsentiveMapTest {

    /**
     * @tpTestDetails Test for CaseInsensitiveMap class, key of map should be case insensitive.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMap() {
        CaseInsensitiveMap<String> map = new CaseInsensitiveMap<>();
        map.add("Cache-Control", "nocache");
        Assertions.assertEquals("nocache", map.getFirst("caChe-CONTROL"),
                "key of map should be case insensitive");
    }
}
