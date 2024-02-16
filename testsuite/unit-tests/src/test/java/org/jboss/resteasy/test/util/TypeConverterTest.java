package org.jboss.resteasy.test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.jboss.resteasy.util.TypeConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for {@link org.jboss.resteasy.util.TypeConverter#getType(Class, String)}.
 * @tpSince RESTEasy 3.0.16
 */
public class TypeConverterTest {

    private static final String CONVERSION_ERROR = "Wrong type of converted value";

    /**
     * @tpTestDetails Boolean conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBooleanTypes() {
        assertTrue(TypeConverter.getType(Boolean.class, "T"), CONVERSION_ERROR);
        assertTrue(TypeConverter.getType(Boolean.class, "t"), CONVERSION_ERROR);
        assertTrue(TypeConverter.getType(Boolean.class, "Y"), CONVERSION_ERROR);
        assertTrue(TypeConverter.getType(Boolean.class, "y"), CONVERSION_ERROR);
        assertTrue(TypeConverter.getType(Boolean.class, "Yes"), CONVERSION_ERROR);
        assertTrue(TypeConverter.getType(Boolean.class, "YES"), CONVERSION_ERROR);
        assertTrue(TypeConverter.getType(Boolean.class, "TRUE"), CONVERSION_ERROR);
        assertTrue(TypeConverter.getType(Boolean.class, "true"), CONVERSION_ERROR);

        assertFalse(TypeConverter.getType(Boolean.class, "F"), CONVERSION_ERROR);
        assertFalse(TypeConverter.getType(Boolean.class, "f"), CONVERSION_ERROR);
        assertFalse(TypeConverter.getType(Boolean.class, "N"), CONVERSION_ERROR);
        assertFalse(TypeConverter.getType(Boolean.class, "n"), CONVERSION_ERROR);
        assertFalse(TypeConverter.getType(Boolean.class, "No"), CONVERSION_ERROR);
        assertFalse(TypeConverter.getType(Boolean.class, "No"), CONVERSION_ERROR);
        assertFalse(TypeConverter.getType(Boolean.class, "FALSE"), CONVERSION_ERROR);
        assertFalse(TypeConverter.getType(Boolean.class, "False"), CONVERSION_ERROR);
    }

    /**
     * @tpTestDetails Integer and int conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIntegerTypes() {
        assertEquals(11, (int) TypeConverter.getType(int.class, "11"), CONVERSION_ERROR);
        assertEquals(11, (int) TypeConverter.getType(Integer.class, "11"), CONVERSION_ERROR);
    }

    /**
     * @tpTestDetails Double conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDoubleTypes() {
        assertEquals(20.15d, TypeConverter.getType(double.class, "20.15"), 0,
                CONVERSION_ERROR);
        assertEquals(20.15d, TypeConverter.getType(Double.class, "20.15"), 0,
                CONVERSION_ERROR);
    }

    /**
     * @tpTestDetails Float conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFloatTypes() {
        assertEquals(23.44f, TypeConverter.getType(float.class, "23.44"), 0, CONVERSION_ERROR);
        assertEquals(23.44f, TypeConverter.getType(Float.class, "23.44"), 0, CONVERSION_ERROR);
    }

    /**
     * @tpTestDetails Long conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLongTypes() {
        assertEquals(23L, (long) TypeConverter.getType(long.class, "23"), CONVERSION_ERROR);
        assertEquals(23L, (long) TypeConverter.getType(Long.class, "23"), CONVERSION_ERROR);
    }

    /**
     * @tpTestDetails character conversion.
     */
    @Test
    public void testCharacterTypes() {
        assertEquals('A', TypeConverter.getType(Character.class, "A").charValue(), CONVERSION_ERROR);
        assertEquals('A', (char) TypeConverter.getType(char.class, "A"), CONVERSION_ERROR);
    }

    /**
     * @tpTestDetails Date conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDate() {
        try {
            TypeConverter.getType(Date.class, "07/04/2008");
            Assertions.fail("Exception was excepted.");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

}
