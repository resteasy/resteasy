package org.jboss.resteasy.test.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.core.ExtendedCacheControl;
import org.jboss.resteasy.plugins.delegates.CacheControlDelegate;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for jakarta.ws.rs.core.CacheControl; class.
 * @tpSince RESTEasy 3.0.16
 */
public class CacheControlTest {

    /**
     * @tpTestDetails Check serialization
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void cacheControlSerialization() {
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        RegisterBuiltin.register(factory);
        RuntimeDelegate.HeaderDelegate<CacheControl> hdcc = factory
                .createHeaderDelegate(CacheControl.class);
        CacheControl control = new CacheControl();
        control.setMaxAge(1000);
        control.setSMaxAge(500);
        control.setNoTransform(false);
        control.setPrivate(true);

        String toString = hdcc.toString(control);
        CacheControl serialized = hdcc.fromString(toString);

        String errMsg = "Wrong serialization";
        Assertions.assertTrue(serialized.getMaxAge() == 1000, errMsg);
        Assertions.assertTrue(serialized.getSMaxAge() == 500, errMsg);
        Assertions.assertFalse(serialized.isNoTransform(), errMsg);
        Assertions.assertTrue(serialized.isPrivate(), errMsg);
    }

    void assertEqual(String errorMsg, CacheControl first, CacheControl second) {
        Assertions.assertEquals(first.isMustRevalidate(), second.isMustRevalidate(), errorMsg);
        Assertions.assertEquals(first.isNoCache(), second.isNoCache(), errorMsg);
        Assertions.assertEquals(first.isNoStore(), second.isNoStore(), errorMsg);
        Assertions.assertEquals(first.isNoTransform(), second.isNoTransform(), errorMsg);
        Assertions.assertEquals(first.isPrivate(), second.isPrivate(), errorMsg);
        Assertions.assertEquals(first.isProxyRevalidate(), second.isProxyRevalidate(), errorMsg);
        Assertions.assertEquals(first.isPrivate(), second.isPrivate(), errorMsg);
        Assertions.assertEquals(first.getMaxAge(), second.getMaxAge(), errorMsg);
        Assertions.assertEquals(first.getSMaxAge(), second.getSMaxAge(), errorMsg);
        Assertions.assertEquals(first.getNoCacheFields().size(), second.getNoCacheFields().size(), errorMsg);
        Assertions.assertEquals(first.getPrivateFields().size(), second.getPrivateFields().size(), errorMsg);
        for (int i = 0; i < first.getNoCacheFields().size(); i++) {
            Assertions.assertEquals(first.getNoCacheFields().get(i), second.getNoCacheFields().get(i), errorMsg);
        }
        for (int i = 0; i < first.getPrivateFields().size(); i++) {
            Assertions.assertEquals(first.getPrivateFields().get(i), second.getPrivateFields().get(i), errorMsg);
        }
        Assertions.assertEquals(first.getCacheExtension().size(), second.getCacheExtension().size(), errorMsg);
        for (String key : first.getCacheExtension().keySet()) {
            Assertions.assertEquals(first.getCacheExtension().get(key), second.getCacheExtension().get(key), errorMsg);
        }
    }

    /**
     * @tpTestDetails Conversion from and to String
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCacheControl() {
        CacheControlDelegate delegate = new CacheControlDelegate();
        {
            CacheControl cc = new CacheControl();
            cc.setNoCache(false);
            cc.setNoTransform(true);
            cc.setPrivate(true);
            cc.setMustRevalidate(true);
            cc.setProxyRevalidate(true);
            CacheControl cc2 = delegate.fromString(delegate.toString(cc));
            assertEqual("Incorrect conversion from CacheControl to String", cc, cc2);

        }

        {
            CacheControl cc = new CacheControl();
            cc.setNoCache(true);
            cc.getNoCacheFields().add("bill");
            cc.getNoCacheFields().add("marc");
            cc.setPrivate(true);
            cc.getPrivateFields().add("yo");
            cc.getCacheExtension().put("foo", "bar");
            cc.setMaxAge(25);
            cc.setSMaxAge(25);
            CacheControl cc2 = delegate.fromString(delegate.toString(cc));
            assertEqual("Incorrect conversion from CacheControl to String", cc, cc2);

        }
    }

    /**
     * @tpTestDetails Check properties are correct in converted String
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEveryDirectiveAppearsInStringifiedVersion() { // TCK requires this
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setPrivate(true);
        cc.setNoStore(true);
        String value = cc.toString();
        assertTrue(value.contains("no-cache"),
                "Missing no-cache property in String representation of CacheControl");
        assertTrue(value.contains("no-store"),
                "Missing no-store property in String representation of CacheControl");
        assertTrue(value.contains("private"),
                "Missing private property in String representation of CacheControl");
    }

    /**
     * @tpTestDetails Check properties are correct in converted String for ExtendedCacheControl class
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testExtendedCacheControl() {
        ExtendedCacheControl cc = new ExtendedCacheControl();
        cc.setNoCache(true);
        cc.setPublic(true);
        cc.setNoStore(true);
        String value = cc.toString();
        assertTrue(value.contains("no-cache"),
                "Missing no-cache property in String representation of ExtendedCacheControl");
        assertTrue(value.contains("no-store"),
                "Missing no-store property in String representation of ExtendedCacheControl");
        assertTrue(value.contains("public"),
                "Missing private property in String representation of ExtendedCacheControl");
    }

}
