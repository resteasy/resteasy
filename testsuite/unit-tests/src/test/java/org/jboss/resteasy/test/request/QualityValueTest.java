package org.jboss.resteasy.test.request;


import org.jboss.resteasy.core.request.QualityValue;
import org.jboss.resteasy.spi.BadRequestException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @tpSubChapter Requests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for QualityValue class and its base conversion
 * @tpSince RESTEasy 3.0.16
 */
public class QualityValueTest {

    private static final String ERROR_MSG = "Wrong conversion";
    /**
     * @tpTestDetails Conversion of zero number.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void zero() {
        assertEquals(ERROR_MSG, 0, QualityValue.valueOf("0").intValue());
        assertEquals(ERROR_MSG, 0, QualityValue.valueOf("0.").intValue());
        assertEquals(ERROR_MSG, 0, QualityValue.valueOf("0.0").intValue());
        assertEquals(ERROR_MSG, 0, QualityValue.valueOf("0.00").intValue());
        assertEquals(ERROR_MSG, 0, QualityValue.valueOf("0.000").intValue());
    }

    /**
     * @tpTestDetails Conversion of one number.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void one() {
        assertEquals(ERROR_MSG, 1000, QualityValue.valueOf("1").intValue());
        assertEquals(ERROR_MSG, 1000, QualityValue.valueOf("1.").intValue());
        assertEquals(ERROR_MSG, 1000, QualityValue.valueOf("1.0").intValue());
        assertEquals(ERROR_MSG, 1000, QualityValue.valueOf("1.00").intValue());
        assertEquals(ERROR_MSG, 1000, QualityValue.valueOf("1.000").intValue());
    }

    /**
     * @tpTestDetails Fraction conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void fractions() {
        assertEquals(ERROR_MSG, 1, QualityValue.valueOf("0.001").intValue());
        assertEquals(ERROR_MSG, 12, QualityValue.valueOf("0.012").intValue());
        assertEquals(ERROR_MSG, 123, QualityValue.valueOf("0.123").intValue());
        assertEquals(ERROR_MSG, 120, QualityValue.valueOf("0.12").intValue());
        assertEquals(ERROR_MSG, 100, QualityValue.valueOf("0.1").intValue());
    }

    /**
     * @tpTestDetails Check equivalent values.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void equivalent() {
        assertEquals(ERROR_MSG, QualityValue.valueOf("0.1"), QualityValue.valueOf("0.10"));
        assertNotEquals(ERROR_MSG, QualityValue.valueOf("1."), QualityValue.valueOf("0.999"));
    }

    /**
     * @tpTestDetails Check compareTo method.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void comparison() {
        assertTrue(ERROR_MSG, QualityValue.LOWEST.compareTo(QualityValue.HIGHEST) < 0);
        assertTrue(ERROR_MSG, QualityValue.DEFAULT.compareTo(QualityValue.HIGHEST) == 0);
        assertTrue(ERROR_MSG, QualityValue.LOWEST.compareTo(QualityValue.NOT_ACCEPTABLE) > 0);
    }

    /**
     * @tpTestDetails Check 1.001 value.
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = BadRequestException.class)
    public void tooLarge() {
        QualityValue.valueOf("1.001");
    }

    /**
     * @tpTestDetails Check -0.001 value (exception expected).
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = BadRequestException.class)
    public void tooSmall() {
        QualityValue.valueOf("-0.001");
    }

    /**
     * @tpTestDetails Check 0.1234 value (exception expected).
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = BadRequestException.class)
    public void tooLong() {
        QualityValue.valueOf("0.1234");
    }

    /**
     * @tpTestDetails Check "" value (exception expected).
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = BadRequestException.class)
    public void tooShort() {
        QualityValue.valueOf("");
    }

    /**
     * @tpTestDetails Check 0,2F value (exception expected).
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = BadRequestException.class)
    public void wrongContent() {
        QualityValue.valueOf("0,2F");
    }

    /**
     * @tpTestDetails Check default value.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void nullContent() {
        assertEquals(ERROR_MSG, QualityValue.DEFAULT, QualityValue.valueOf(null));
    }

    /**
     * @tpTestDetails Check numbers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void numbers() {
        QualityValue x = QualityValue.valueOf("0.08");
        assertEquals(ERROR_MSG, 80, x.intValue());
        assertEquals(ERROR_MSG, 80L, x.longValue());
        assertEquals(ERROR_MSG, 0.08f, x.floatValue(), 0);
        assertEquals(ERROR_MSG, 0.08d, x.doubleValue(), 0);
    }

}
