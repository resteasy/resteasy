package org.jboss.resteasy.test.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.BadRequestException;

import org.jboss.resteasy.core.request.QualityValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        assertEquals(0, QualityValue.valueOf("0").intValue(), ERROR_MSG);
        assertEquals(0, QualityValue.valueOf("0.").intValue(), ERROR_MSG);
        assertEquals(0, QualityValue.valueOf("0.0").intValue(), ERROR_MSG);
        assertEquals(0, QualityValue.valueOf("0.00").intValue(), ERROR_MSG);
        assertEquals(0, QualityValue.valueOf("0.000").intValue(), ERROR_MSG);
    }

    /**
     * @tpTestDetails Conversion of one number.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void one() {
        assertEquals(1000, QualityValue.valueOf("1").intValue(), ERROR_MSG);
        assertEquals(1000, QualityValue.valueOf("1.").intValue(), ERROR_MSG);
        assertEquals(1000, QualityValue.valueOf("1.0").intValue(), ERROR_MSG);
        assertEquals(1000, QualityValue.valueOf("1.00").intValue(), ERROR_MSG);
        assertEquals(1000, QualityValue.valueOf("1.000").intValue(), ERROR_MSG);
    }

    /**
     * @tpTestDetails Fraction conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void fractions() {
        assertEquals(1, QualityValue.valueOf("0.001").intValue(), ERROR_MSG);
        assertEquals(12, QualityValue.valueOf("0.012").intValue(), ERROR_MSG);
        assertEquals(123, QualityValue.valueOf("0.123").intValue(), ERROR_MSG);
        assertEquals(120, QualityValue.valueOf("0.12").intValue(), ERROR_MSG);
        assertEquals(100, QualityValue.valueOf("0.1").intValue(), ERROR_MSG);
    }

    /**
     * @tpTestDetails Check equivalent values.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void equivalent() {
        assertEquals(QualityValue.valueOf("0.1"), QualityValue.valueOf("0.10"), ERROR_MSG);
        assertNotEquals(QualityValue.valueOf("1."), QualityValue.valueOf("0.999"), ERROR_MSG);
    }

    /**
     * @tpTestDetails Check compareTo method.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void comparison() {
        assertTrue(QualityValue.LOWEST.compareTo(QualityValue.HIGHEST) < 0, ERROR_MSG);
        assertTrue(QualityValue.DEFAULT.compareTo(QualityValue.HIGHEST) == 0, ERROR_MSG);
        assertTrue(QualityValue.LOWEST.compareTo(QualityValue.NOT_ACCEPTABLE) > 0, ERROR_MSG);
    }

    /**
     * @tpTestDetails Check 1.001 value.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void tooLarge() {
        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class,
                () -> {
                    QualityValue.valueOf("1.001");
                });
        Assertions.assertTrue(thrown instanceof BadRequestException);
    }

    /**
     * @tpTestDetails Check -0.001 value (exception expected).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void tooSmall() {
        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class,
                () -> {
                    QualityValue.valueOf("-0.001");
                });
        Assertions.assertTrue(thrown instanceof BadRequestException);
    }

    /**
     * @tpTestDetails Check 0.1234 value (exception expected).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void tooLong() {
        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class,
                () -> {
                    QualityValue.valueOf("0.1234");
                });
        Assertions.assertTrue(thrown instanceof BadRequestException);
    }

    /**
     * @tpTestDetails Check "" value (exception expected).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void tooShort() {
        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class,
                () -> {
                    QualityValue.valueOf("");
                });
        Assertions.assertTrue(thrown instanceof BadRequestException);
    }

    /**
     * @tpTestDetails Check 0,2F value (exception expected).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void wrongContent() {
        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class,
                () -> {
                    QualityValue.valueOf("0,2F");
                });
        Assertions.assertTrue(thrown instanceof BadRequestException);
    }

    /**
     * @tpTestDetails Check default value.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void nullContent() {
        assertEquals(QualityValue.DEFAULT, QualityValue.valueOf(null), ERROR_MSG);
    }

    /**
     * @tpTestDetails Check numbers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void numbers() {
        QualityValue x = QualityValue.valueOf("0.08");
        assertEquals(80, x.intValue(), ERROR_MSG);
        assertEquals(80L, x.longValue(), ERROR_MSG);
        assertEquals(0.08f, x.floatValue(), 0, ERROR_MSG);
        assertEquals(0.08d, x.doubleValue(), 0, ERROR_MSG);
    }

}
