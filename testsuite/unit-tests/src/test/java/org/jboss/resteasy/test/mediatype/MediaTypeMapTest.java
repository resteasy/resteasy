package org.jboss.resteasy.test.mediatype;

import java.util.List;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.core.MediaTypeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Media type
 * @tpChapter Unit tests
 * @tpSince RESTEasy 3.0.16
 */
public class MediaTypeMapTest {

    /**
     * @tpTestDetails Test to add media types into MediaTypeMap
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatching() {
        MediaTypeMap<String> map = new MediaTypeMap<String>();
        test(map);
        MediaTypeMap<String> locked = new MediaTypeMap<String>();
        locked.lockSnapshots();
        test(locked);
    }

    @Test
    public void testMatchingString() {
        MediaTypeMap<String> map = new MediaTypeMap<String>();
        testString(map);
        MediaTypeMap<String> locked = new MediaTypeMap<String>();
        locked.lockSnapshots();
        testString(locked);
    }

    protected void test(MediaTypeMap<String> map) {
        String defaultPlainText = "defaultPlainText";
        map.add(new MediaType("text", "plain"), defaultPlainText);
        String jaxb = "jaxb";
        map.add(new MediaType("text", "xml"), jaxb);
        String wildcard = "wildcard";
        map.add(new MediaType("*", "*"), wildcard);
        String allText = "allText";
        map.add(new MediaType("text", "*"), allText);
        String allXML = "allXML";
        map.add(new MediaType("text", "*+xml"), allXML);
        String app = "app";
        map.add(new MediaType("application", "*"), app);

        evalMapping(map, defaultPlainText, jaxb, wildcard, allText, allXML, app);
    }

    protected void testString(MediaTypeMap<String> map) {
        String defaultPlainText = "defaultPlainText";
        map.add(MediaType.TEXT_PLAIN, defaultPlainText);
        String jaxb = "jaxb";
        map.add(MediaType.TEXT_XML, jaxb);
        String wildcard = "wildcard";
        map.add(MediaType.WILDCARD, wildcard);
        String allText = "allText";
        map.add("text/*", allText);
        String allXML = "allXML";
        map.add("text/*+xml", allXML);
        String app = "app";
        map.add("application/*", app);

        evalMapping(map, defaultPlainText, jaxb, wildcard, allText, allXML, app);
    }

    private void evalMapping(MediaTypeMap<String> map, String defaultPlainText, String jaxb, String wildcard, String allText,
            String allXML, String app) {
        List<String> list = map.getPossible(new MediaType("text", "plain"));
        Assertions.assertNotNull(list, "Media types for \"text, plain\" is empty");
        Assertions.assertEquals(3, list.size(),
                "The list of media types doesn't contain expected number of elements");
        Assertions.assertTrue(list.get(0) == defaultPlainText, "Unexpected item in the list");
        Assertions.assertTrue(list.get(1) == allText, "Unexpected item in the list");
        Assertions.assertTrue(list.get(2) == wildcard, "Unexpected item in the list");

        list = map.getPossible(new MediaType("*", "*"));
        Assertions.assertNotNull(list, "Media types for \"*, *\" is empty");
        Assertions.assertEquals(6, list.size(),
                "The list of media types doesn't contain expected number of elements");
        Assertions.assertTrue(list.get(0) == defaultPlainText || list.get(0) == jaxb, list.get(0));
        Assertions.assertTrue(list.get(1) == defaultPlainText || list.get(1) == jaxb, list.get(1));
        Assertions.assertTrue(list.get(2) == allXML, list.get(2));
        Assertions.assertTrue(list.get(3) == allText || list.get(3) == app, list.get(3));
        Assertions.assertTrue(list.get(4) == allText || list.get(4) == app, list.get(4));
        Assertions.assertTrue(list.get(5) == wildcard, list.get(5));

        list = map.getPossible(new MediaType("text", "*"));
        Assertions.assertNotNull(list, "Media types for \"text, *\" is empty");
        Assertions.assertEquals(5, list.size(),
                "The list of media types doesn't contain expected number of elements");
        Assertions.assertTrue(list.get(0) == defaultPlainText || list.get(0) == jaxb, list.get(0));
        Assertions.assertTrue(list.get(1) == defaultPlainText || list.get(1) == jaxb, list.get(1));
        Assertions.assertTrue(list.get(2) == allXML, list.get(2));
        Assertions.assertTrue(list.get(3) == allText, list.get(3));
        Assertions.assertTrue(list.get(4) == wildcard, list.get(4));

        list = map.getPossible(new MediaType("text", "xml"));
        Assertions.assertNotNull(list, "Media types for \"text, xml\" is empty");
        Assertions.assertEquals(4, list.size(),
                "The list of media types doesn't contain expected number of elements");
        Assertions.assertTrue(list.get(0) == jaxb);
        Assertions.assertTrue(list.get(1) == allXML);
        Assertions.assertTrue(list.get(2) == allText);
        Assertions.assertTrue(list.get(3) == wildcard);
    }

}
