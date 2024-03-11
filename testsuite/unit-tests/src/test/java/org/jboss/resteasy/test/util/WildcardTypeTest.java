package org.jboss.resteasy.test.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

import jakarta.ws.rs.core.GenericType;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.util.Types;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for RESTEASY-1295, JBEAP-4713
 * @tpSince RESTEasy 3.0.17
 */
public class WildcardTypeTest {
    protected final Logger logger = Logger.getLogger(WildcardTypeTest.class.getName());

    /**
     * @tpTestDetails Tests upper bound.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testWildcardTypeUpperBound() {
        GenericType<List<? extends String>> genericType = new GenericType<List<? extends String>>() {
        };
        ParameterizedType pt = (ParameterizedType) genericType.getType();
        Type t = pt.getActualTypeArguments()[0];
        printTypes(t);
        Class<?> rawType = Types.getRawType(t);
        Assertions.assertEquals(String.class, rawType);
    }

    /**
     * @tpTestDetails Tests lower bound.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testWildcardTypeLowerBound() {
        GenericType<List<? super String>> genericType = new GenericType<List<? super String>>() {
        };
        ParameterizedType pt = (ParameterizedType) genericType.getType();
        Type t = pt.getActualTypeArguments()[0];
        printTypes(t);
        Class<?> rawType = Types.getRawType(t);
        Assertions.assertEquals(Object.class, rawType);
    }

    /**
     * @tpTestDetails Tests upper bound object.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testWildcardTypeUpperBoundObject() {
        GenericType<List<? extends Object>> genericType = new GenericType<List<? extends Object>>() {
        };
        ParameterizedType pt = (ParameterizedType) genericType.getType();
        Type t = pt.getActualTypeArguments()[0];
        printTypes(t);
        Class<?> rawType = Types.getRawType(t);
        Assertions.assertEquals(Object.class, rawType);
    }

    /**
     * @tpTestDetails Tests lower bound object.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testWildcardTypeLowerBoundObject() {
        GenericType<List<? super Object>> genericType = new GenericType<List<? super Object>>() {
        };
        ParameterizedType pt = (ParameterizedType) genericType.getType();
        Type t = pt.getActualTypeArguments()[0];
        printTypes(t);
        Class<?> rawType = Types.getRawType(t);
        Assertions.assertEquals(Object.class, rawType);
    }

    void printTypes(Type t) {
        WildcardType wt = (WildcardType) t;
        logger.debug("-----------");
        logger.debug("type:  " + t);
        logger.debug("upper: " + (wt.getUpperBounds().length > 0 ? wt.getUpperBounds()[0] : "[]"));
        logger.debug("lower: " + (wt.getLowerBounds().length > 0 ? wt.getLowerBounds()[0] : "[]"));
    }
}
