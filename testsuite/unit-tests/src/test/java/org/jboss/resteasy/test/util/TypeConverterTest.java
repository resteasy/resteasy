package org.jboss.resteasy.test.util;

import org.jboss.resteasy.util.TypeConverter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


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
        assertTrue(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "T"));
        assertTrue(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "t"));
        assertTrue(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "Y"));
        assertTrue(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "y"));
        assertTrue(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "Yes"));
        assertTrue(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "YES"));
        assertTrue(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "TRUE"));
        assertTrue(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "true"));

        assertFalse(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "F"));
        assertFalse(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "f"));
        assertFalse(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "N"));
        assertFalse(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "n"));
        assertFalse(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "No"));
        assertFalse(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "No"));
        assertFalse(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "FALSE"));
        assertFalse(CONVERSION_ERROR, TypeConverter.getType(Boolean.class, "False"));
    }

    /**
     * @tpTestDetails Integer and int conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIntegerTypes() {
        assertEquals(CONVERSION_ERROR, 11, (int) TypeConverter.getType(int.class, "11"));
        assertEquals(CONVERSION_ERROR, 11, (int) TypeConverter.getType(Integer.class, "11"));
    }

    /**
     * @tpTestDetails Double conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDoubleTypes() {
        assertEquals(CONVERSION_ERROR, 20.15d, TypeConverter.getType(double.class, "20.15"), 0);
        assertEquals(CONVERSION_ERROR, 20.15d, TypeConverter.getType(Double.class, "20.15"), 0);
    }

    /**
     * @tpTestDetails Float conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFloatTypes() {
        assertEquals(CONVERSION_ERROR, 23.44f, TypeConverter.getType(float.class, "23.44"), 0);
        assertEquals(CONVERSION_ERROR, 23.44f, TypeConverter.getType(Float.class, "23.44"), 0);
    }

    /**
     * @tpTestDetails Long conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLongTypes() {
        assertEquals(CONVERSION_ERROR, 23L, (long) TypeConverter.getType(long.class, "23"));
        assertEquals(CONVERSION_ERROR, 23L, (long) TypeConverter.getType(Long.class, "23"));
    }

   /**
    * @tpTestDetails character conversion.
    */
   @Test
    public void testCharacterTypes() {
        assertEquals(CONVERSION_ERROR, 'A', TypeConverter.getType(Character.class, "A").charValue());
        assertEquals(CONVERSION_ERROR, 'A', (char)TypeConverter.getType(char.class, "A"));
    }

    /**
     * @tpTestDetails Date conversion.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDate() {
        try {
            TypeConverter.getType(Date.class, "07/04/2008");
            Assert.fail("Exception was excepted.");
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

}
