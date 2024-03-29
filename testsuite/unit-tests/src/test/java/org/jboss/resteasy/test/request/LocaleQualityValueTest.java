package org.jboss.resteasy.test.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jboss.resteasy.core.request.AcceptHeaders;
import org.jboss.resteasy.core.request.QualityValue;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Requests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for AcceptHeaders and QualityValue classes, local value.
 * @tpSince RESTEasy 3.0.16
 */
public class LocaleQualityValueTest {

    private static void assertList(String header, Locale[] fields, QualityValue[] qualities) {
        Map<Locale, QualityValue> map = AcceptHeaders.getLocaleQualityValues(header);
        List<Locale> expectedKeys = Arrays.asList(fields);
        List<QualityValue> expectedValues = Arrays.asList(qualities);
        assertEquals(expectedKeys, new ArrayList<>(map.keySet()), "Wrong keys in map");
        assertEquals(expectedValues, new ArrayList<>(map.values()), "Wrong values in map");
    }

    /**
     * @tpTestDetails Test for simple values.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void simple() {
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

    /**
     * @tpTestDetails Test for chinese localization.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void chineseLocalization() {
        String header = "zh, *";
        Locale[] fields = { Locale.CHINESE, null };
        QualityValue[] qualities = {
                QualityValue.DEFAULT,
                QualityValue.DEFAULT
        };
        assertList(header, fields, qualities);
    }

    /**
     * @tpTestDetails Test for english localization and default value.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void undefined() {
        String header = "en, en-US, en-cockney, i-cherokee, x-pig-latin";
        Locale[] fields = { Locale.ENGLISH, Locale.US };
        QualityValue[] qualities = {
                QualityValue.DEFAULT,
                QualityValue.DEFAULT
        };
        assertList(header, fields, qualities);
    }

    /**
     * @tpTestDetails Test empty quality values.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void empty() {
        final String ERROR_MSG = "Local quality values should not be null";
        assertNull(AcceptHeaders.getLocaleQualityValues(null), ERROR_MSG);
        assertNull(AcceptHeaders.getLocaleQualityValues(""), ERROR_MSG);
        assertNull(AcceptHeaders.getLocaleQualityValues(" "), ERROR_MSG);
    }

}
