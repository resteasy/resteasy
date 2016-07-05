package org.jboss.resteasy.test.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.util.Types;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for RESTEASY-1295, JBEAP-4713
 * @tpSince EAP 7.0.1
 */
public class WildcardTypeTest {
    protected static final Logger logger = LogManager.getLogger(WildcardTypeTest.class.getName());

    /**
     * @tpTestDetails Tests upper bound.
     * @tpSince EAP 7.0.1
     */
    @Test
    public void testWildcardTypeUpperBound() {
        GenericType<List<? extends String>> genericType = new GenericType<List<? extends String>>() {
        };
        ParameterizedType pt = (ParameterizedType) genericType.getType();
        Type t = pt.getActualTypeArguments()[0];
        printTypes(t);
        Class<?> rawType = Types.getRawType(t);
        logger.info(rawType);
        Assert.assertEquals(String.class, rawType);
    }

    /**
     * @tpTestDetails Tests lower bound.
     * @tpSince EAP 7.0.1
     */
    @Test
    public void testWildcardTypeLowerBound() {
        GenericType<List<? super String>> genericType = new GenericType<List<? super String>>() {
        };
        ParameterizedType pt = (ParameterizedType) genericType.getType();
        Type t = pt.getActualTypeArguments()[0];
        printTypes(t);
        Class<?> rawType = Types.getRawType(t);
        logger.info(rawType);
        Assert.assertEquals(Object.class, rawType);
    }

    /**
     * @tpTestDetails Tests upper bound object.
     * @tpSince EAP 7.0.1
     */
    @Test
    public void testWildcardTypeUpperBoundObject() {
        GenericType<List<? extends Object>> genericType = new GenericType<List<? extends Object>>() {
        };
        ParameterizedType pt = (ParameterizedType) genericType.getType();
        Type t = pt.getActualTypeArguments()[0];
        printTypes(t);
        Class<?> rawType = Types.getRawType(t);
        logger.info(rawType);
        Assert.assertEquals(Object.class, rawType);
    }

    /**
     * @tpTestDetails Tests lower bound object.
     * @tpSince EAP 7.0.1
     */
    @Test
    public void testWildcardTypeLowerBoundObject() {
        GenericType<List<? super Object>> genericType = new GenericType<List<? super Object>>() {
        };
        ParameterizedType pt = (ParameterizedType) genericType.getType();
        Type t = pt.getActualTypeArguments()[0];
        printTypes(t);
        Class<?> rawType = Types.getRawType(t);
        logger.info(rawType);
        Assert.assertEquals(Object.class, rawType);
    }

    void printTypes(Type t) {
        WildcardType wt = (WildcardType) t;
        logger.info("-----------");
        logger.info("type:  " + t);
        logger.info("upper: " + (wt.getUpperBounds().length > 0 ? wt.getUpperBounds()[0] : "[]"));
        logger.info("lower: " + (wt.getLowerBounds().length > 0 ? wt.getLowerBounds()[0] : "[]"));
    }
}
