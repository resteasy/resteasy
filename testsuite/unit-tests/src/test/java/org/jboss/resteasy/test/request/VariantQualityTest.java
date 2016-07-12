package org.jboss.resteasy.test.request;

import org.jboss.resteasy.core.request.QualityValue;
import org.jboss.resteasy.core.request.VariantQuality;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Requests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for VariantQuality class.
 * @tpSince RESTEasy 3.0.16
 */
public class VariantQualityTest {

    private static final String ERROR_MSG = "Wrong conversion";

    /**
     * @tpTestDetails Check default values.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void defaultQuality() {
        VariantQuality q = new VariantQuality();
        assertEquals(ERROR_MSG, new BigDecimal("1.00000"), q.getOverallQuality());
        q.setMediaTypeQualityValue(null);
        q.setCharacterSetQualityValue(null);
        q.setEncodingQualityValue(null);
        q.setLanguageQualityValue(null);
        assertEquals(ERROR_MSG, new BigDecimal("1.00000"), q.getOverallQuality());
    }

    /**
     * @tpTestDetails Check quality setters.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void qualitySetters() {
        VariantQuality q = new VariantQuality();
        q.setMediaTypeQualityValue(QualityValue.valueOf("0.1"));
        assertEquals(ERROR_MSG, new BigDecimal("0.10000"), q.getOverallQuality());
        q.setCharacterSetQualityValue(QualityValue.valueOf("0.2"));
        assertEquals(ERROR_MSG, new BigDecimal("0.02000"), q.getOverallQuality());
        q.setEncodingQualityValue(QualityValue.valueOf("0.4"));
        assertEquals(ERROR_MSG, new BigDecimal("0.00800"), q.getOverallQuality());
        q.setLanguageQualityValue(QualityValue.valueOf("0.8"));
        assertEquals(ERROR_MSG, new BigDecimal("0.00640"), q.getOverallQuality());
    }


    @Test
    public void round5() {
        VariantQuality q = new VariantQuality();
        q.setMediaTypeQualityValue(QualityValue.valueOf("0.004"));
        q.setEncodingQualityValue(QualityValue.valueOf("0.008"));
        assertEquals(ERROR_MSG, new BigDecimal("0.00003"), q.getOverallQuality());
    }

}
