package org.jboss.resteasy.test.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.core.request.AcceptHeaders;
import org.jboss.resteasy.core.request.QualityValue;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Requests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for AcceptHeaders and QualityValue classes, media type.
 * @tpSince RESTEasy 3.0.16
 */
public class MediaTypeQualityValueTest {

    private static void assertList(String header, MediaType[] fields, QualityValue[] qualities) {
        Map<MediaType, QualityValue> map = AcceptHeaders.getMediaTypeQualityValues(header);
        List<MediaType> expectedKeys = Arrays.asList(fields);
        List<QualityValue> expectedValues = Arrays.asList(qualities);
        assertEquals(expectedKeys, new ArrayList<MediaType>(map.keySet()),
                "Wrong keys in map");
        assertEquals(expectedValues, new ArrayList<QualityValue>(map.values()),
                "Wrong values in map");
    }

    /**
     * @tpTestDetails Test for simple values.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void simple() {
        String header = "audio/*; q=0.2, audio/basic";
        MediaType[] fields = {
                MediaType.valueOf("audio/*"),
                MediaType.valueOf("audio/basic")
        };
        QualityValue[] qualities = {
                QualityValue.valueOf("0.2"),
                QualityValue.DEFAULT
        };
        assertList(header, fields, qualities);
    }

    /**
     * @tpTestDetails Test for special parameters.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void parameters() {
        String header = "text/html;level=\"1\", text/html;level=2;q=0.4";
        MediaType[] fields = {
                MediaType.valueOf("text/html;level=1"),
                MediaType.valueOf("text/html;level=2")
        };
        QualityValue[] qualities = {
                QualityValue.DEFAULT,
                QualityValue.valueOf("0.4")
        };
        assertList(header, fields, qualities);
    }

    /**
     * @tpTestDetails Test for unsupported extension.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void unsupportedExtension() {
        String header = "plain/text; a=b; q=0.2; extension=unsupported";
        MediaType[] fields = { MediaType.valueOf("plain/text;a=b") };
        QualityValue[] qualities = { QualityValue.NOT_ACCEPTABLE };
        assertList(header, fields, qualities);
    }

    /**
     * @tpTestDetails Test for bad requests.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void badRequests() {
        String[] badHeaders = {
                "a",
                "a,b",
                "a/b,",
                "a/b;",
                "a/b;p",
                "a/b;p=x,",
                "a/b;p=\"x\"y",
                "a/b;p=\"x\"y,c/d",
                "a/b;p=\"x,c/d",
                "a/b;p=\"x\\\",c/d"
        };
        for (String header : badHeaders) {
            try {
                AcceptHeaders.getMediaTypeQualityValues(header);
                fail(header);
            } catch (BadRequestException e) {
            }
        }
    }

    /**
     * @tpTestDetails Test empty quality values.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void empty() {
        final String ERROR_MSG = "Local quality values should not be null";
        assertNull(AcceptHeaders.getMediaTypeQualityValues(null), ERROR_MSG);
        assertNull(AcceptHeaders.getMediaTypeQualityValues(""), ERROR_MSG);
        assertNull(AcceptHeaders.getMediaTypeQualityValues(" "), ERROR_MSG);
    }

}
