package org.jboss.resteasy.test.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.WeightedMediaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Requests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for jakarta.ws.rs.core.MediaType class.
 * @tpSince RESTEasy 3.0.16
 */
public class MediaTypeTest {

    /**
     * @tpTestDetails Test for MediaType parsing.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testParsing() {
        String errorMsg = "MediaType parse error";
        MediaType mediaType;

        mediaType = MediaType.valueOf("application/xml");
        Assertions.assertEquals("application", mediaType.getType(), errorMsg);
        Assertions.assertEquals("xml", mediaType.getSubtype(), errorMsg);

        mediaType = MediaType.valueOf("text/*;q=0.3");
        Assertions.assertEquals("text", mediaType.getType(), errorMsg);
        Assertions.assertEquals("*", mediaType.getSubtype(), errorMsg);
        Assertions.assertTrue(mediaType.isWildcardSubtype(), errorMsg);
        Assertions.assertEquals(1, mediaType.getParameters().size(), errorMsg);
        Assertions.assertEquals("0.3", mediaType.getParameters().get("q"), errorMsg);

        mediaType = MediaType.valueOf("text/html;level=2;q=0.4");
        Assertions.assertEquals("text", mediaType.getType(), errorMsg);
        Assertions.assertEquals("html", mediaType.getSubtype(), errorMsg);
        Assertions.assertEquals(2, mediaType.getParameters().size(), errorMsg);
        Assertions.assertEquals("0.4", mediaType.getParameters().get("q"), errorMsg);
        Assertions.assertEquals("2", mediaType.getParameters().get("level"), errorMsg);

        MediaTypeHeaderDelegate delegate = new MediaTypeHeaderDelegate();
        String str = delegate.toString(mediaType);
        mediaType = MediaType.valueOf(str);
        Assertions.assertEquals("text", mediaType.getType(), errorMsg);
        Assertions.assertEquals("html", mediaType.getSubtype(), errorMsg);
        Assertions.assertEquals(2, mediaType.getParameters().size(), errorMsg);
        Assertions.assertEquals("0.4", mediaType.getParameters().get("q"), errorMsg);
        Assertions.assertEquals("2", mediaType.getParameters().get("level"), errorMsg);

        mediaType = MediaType.valueOf("text/html;level=  \"2\";q=0.4");
        Assertions.assertEquals("text", mediaType.getType(), errorMsg);
        Assertions.assertEquals("html", mediaType.getSubtype(), errorMsg);
        Assertions.assertEquals(2, mediaType.getParameters().size(), errorMsg);
        Assertions.assertEquals("0.4", mediaType.getParameters().get("q"), errorMsg);
        Assertions.assertEquals("2", mediaType.getParameters().get("level"), errorMsg);

        mediaType = MediaType.valueOf("text/html;level=  \"2\";q=  \"0.4\"   ");
        Assertions.assertEquals("text", mediaType.getType(), errorMsg);
        Assertions.assertEquals("html", mediaType.getSubtype(), errorMsg);
        Assertions.assertEquals(2, mediaType.getParameters().size(), errorMsg);
        Assertions.assertEquals("0.4", mediaType.getParameters().get("q"), errorMsg);
        Assertions.assertEquals("2", mediaType.getParameters().get("level"), errorMsg);

        mediaType = MediaType.valueOf("text/html;level=  \"2\";q=  \"0.4;\"   ");
        Assertions.assertEquals("text", mediaType.getType(), errorMsg);
        Assertions.assertEquals("html", mediaType.getSubtype(), errorMsg);
        Assertions.assertEquals(2, mediaType.getParameters().size(), errorMsg);
        Assertions.assertEquals("0.4;", mediaType.getParameters().get("q"), errorMsg);
        Assertions.assertEquals("2", mediaType.getParameters().get("level"), errorMsg);

    }

    /**
     * @tpTestDetails Test for MediaType sorting.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSort() {
        String errorMsg = "MediaType sort error";
        {
            MediaType[] array = {
                    MediaType.valueOf("text/*"),
                    MediaType.valueOf("text/html"),
                    MediaType.valueOf("text/html;level=1"),
                    MediaType.valueOf("*/*")
            };
            List<MediaType> list = new ArrayList<MediaType>();
            list.add(array[0]);
            list.add(array[1]);
            list.add(array[2]);
            list.add(array[3]);

            MediaTypeHelper.sortByWeight(list);

            Assertions.assertTrue(array[2] == list.get(0), errorMsg + list.get(0).toString());
            Assertions.assertTrue(array[1] == list.get(1), errorMsg);
            Assertions.assertTrue(array[0] == list.get(2), errorMsg);
            Assertions.assertTrue(array[3] == list.get(3), errorMsg);
        }
        {
            MediaType[] array = {
                    MediaType.valueOf("text/*;q=0.3"),
                    MediaType.valueOf("text/html;q=0.7"),
                    MediaType.valueOf("text/html;level=1"),
                    MediaType.valueOf("text/html;level=2;q=0.4"),
                    MediaType.valueOf("*/*;q=0.5")
            };
            List<MediaType> list = new ArrayList<MediaType>();
            list.add(array[0]);
            list.add(array[1]);
            list.add(array[2]);
            list.add(array[3]);
            list.add(array[4]);

            MediaTypeHelper.sortByWeight(list);

            Assertions.assertTrue(array[2] == list.get(0), errorMsg);
            Assertions.assertTrue(array[1] == list.get(1), errorMsg);
            Assertions.assertTrue(array[4] == list.get(2), errorMsg);
            Assertions.assertTrue(array[3] == list.get(3), errorMsg);
            Assertions.assertTrue(array[0] == list.get(4), errorMsg);

        }
    }

    /**
     * @tpTestDetails Test for MediaType sorting with weight
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWeightedSort() {
        String errorMsg = "MediaType sort error";
        {
            WeightedMediaType[] array = {
                    WeightedMediaType.valueOf("text/*"),
                    WeightedMediaType.valueOf("text/html"),
                    WeightedMediaType.valueOf("text/html;level=1"),
                    WeightedMediaType.valueOf("*/*")
            };
            List<WeightedMediaType> list = new ArrayList<WeightedMediaType>();
            list.add(array[0]);
            list.add(array[1]);
            list.add(array[2]);
            list.add(array[3]);

            Collections.sort(list);

            Assertions.assertTrue(array[2] == list.get(0), errorMsg + list.get(0).toString());
            Assertions.assertTrue(array[1] == list.get(1), errorMsg);
            Assertions.assertTrue(array[0] == list.get(2), errorMsg);
            Assertions.assertTrue(array[3] == list.get(3), errorMsg);

        }
        {
            WeightedMediaType[] array = {
                    WeightedMediaType.valueOf("text/*;q=0.3"),
                    WeightedMediaType.valueOf("text/html;q=0.7"),
                    WeightedMediaType.valueOf("text/html;level=1"),
                    WeightedMediaType.valueOf("text/html;level=2;q=0.4"),
                    WeightedMediaType.valueOf("*/*;q=0.5")
            };
            List<WeightedMediaType> list = new ArrayList<WeightedMediaType>();
            list.add(array[0]);
            list.add(array[1]);
            list.add(array[2]);
            list.add(array[3]);
            list.add(array[4]);

            Collections.sort(list);

            Assertions.assertTrue(array[2] == list.get(0), errorMsg);
            Assertions.assertTrue(array[1] == list.get(1), errorMsg);
            Assertions.assertTrue(array[4] == list.get(2), errorMsg);
            Assertions.assertTrue(array[3] == list.get(3), errorMsg);
            Assertions.assertTrue(array[0] == list.get(4), errorMsg);
        }
    }

    /**
     * @tpTestDetails Test for MediaType composition
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testComposite() {
        String errorMsg = "MediaType composite error";
        MediaType[] array = {
                MediaType.valueOf("application/rss+*"),
                MediaType.valueOf("text/*"),
                MediaType.valueOf("application/*+json"),
                MediaType.valueOf("text/html"),
                MediaType.valueOf("application/*+xml"),
                MediaType.valueOf("application/xml"),
                MediaType.valueOf("application/atom+*"),
                MediaType.valueOf("*/*")
        };
        List<MediaType> list = new ArrayList<MediaType>();
        for (MediaType type : array) {
            list.add(type);
        }

        MediaTypeHelper.sortByWeight(list);

        Assertions.assertTrue(array[3] == list.get(0) || array[5] == list.get(0), errorMsg);
        Assertions.assertTrue(array[3] == list.get(1) || array[5] == list.get(1), errorMsg);
        Assertions.assertTrue(array[0] == list.get(2) || array[6] == list.get(2), errorMsg);
        Assertions.assertTrue(array[0] == list.get(3) || array[6] == list.get(3), errorMsg);
        Assertions.assertTrue(array[2] == list.get(4) || array[4] == list.get(4), errorMsg);
        Assertions.assertTrue(array[2] == list.get(5) || array[4] == list.get(5), errorMsg);
        Assertions.assertTrue(array[1] == list.get(6), errorMsg);
        Assertions.assertTrue(array[7] == list.get(7), errorMsg);
    }
}
