package org.jboss.resteasy.test.util;

import org.jboss.resteasy.core.ExtendedCacheControl;
import org.jboss.resteasy.plugins.delegates.CacheControlDelegate;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.ext.RuntimeDelegate;

import static org.junit.Assert.assertTrue;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for javax.ws.rs.core.CacheControl; class.
 * @tpSince RESTEasy 3.0.16
 */
public class CacheControlTest {

    /**
     * @tpTestDetails Check serialization
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void cacheControlSerialization() {
        RuntimeDelegate.HeaderDelegate<CacheControl> hdcc = ResteasyProviderFactory.getInstance()
                .createHeaderDelegate(CacheControl.class);
        CacheControl control = new CacheControl();
        control.setMaxAge(1000);
        control.setSMaxAge(500);
        control.setNoTransform(false);
        control.setPrivate(true);

        String toString = hdcc.toString(control);
        CacheControl serialized = hdcc.fromString(toString);

        String errMsg = "Wrong serialization";
        Assert.assertTrue(errMsg, serialized.getMaxAge() == 1000);
        Assert.assertTrue(errMsg, serialized.getSMaxAge() == 500);
        Assert.assertFalse(errMsg, serialized.isNoTransform());
        Assert.assertTrue(errMsg, serialized.isPrivate());
    }


    void assertEqual(String errorMsg, CacheControl first, CacheControl second) {
        Assert.assertEquals(errorMsg, first.isMustRevalidate(), second.isMustRevalidate());
        Assert.assertEquals(errorMsg, first.isNoCache(), second.isNoCache());
        Assert.assertEquals(errorMsg, first.isNoStore(), second.isNoStore());
        Assert.assertEquals(errorMsg, first.isNoTransform(), second.isNoTransform());
        Assert.assertEquals(errorMsg, first.isPrivate(), second.isPrivate());
        Assert.assertEquals(errorMsg, first.isProxyRevalidate(), second.isProxyRevalidate());
        Assert.assertEquals(errorMsg, first.isPrivate(), second.isPrivate());
        Assert.assertEquals(errorMsg, first.getMaxAge(), second.getMaxAge());
        Assert.assertEquals(errorMsg, first.getSMaxAge(), second.getSMaxAge());
        Assert.assertEquals(errorMsg, first.getNoCacheFields().size(), second.getNoCacheFields().size());
        Assert.assertEquals(errorMsg, first.getPrivateFields().size(), second.getPrivateFields().size());
        for (int i = 0; i < first.getNoCacheFields().size(); i++) {
            Assert.assertEquals(errorMsg, first.getNoCacheFields().get(i), second.getNoCacheFields().get(i));
        }
        for (int i = 0; i < first.getPrivateFields().size(); i++) {
            Assert.assertEquals(errorMsg, first.getPrivateFields().get(i), second.getPrivateFields().get(i));
        }
        Assert.assertEquals(errorMsg, first.getCacheExtension().size(), second.getCacheExtension().size());
        for (String key : first.getCacheExtension().keySet()) {
            Assert.assertEquals(errorMsg, first.getCacheExtension().get(key), second.getCacheExtension().get(key));
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
        assertTrue("Missing no-cache property in String representation of CacheControl",
                value.contains("no-cache"));
        assertTrue("Missing no-store property in String representation of CacheControl",
                value.contains("no-store"));
        assertTrue("Missing private property in String representation of CacheControl",
                value.contains("private"));
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
        assertTrue("Missing no-cache property in String representation of ExtendedCacheControl",
                value.contains("no-cache"));
        assertTrue("Missing no-store property in String representation of ExtendedCacheControl",
                value.contains("no-store"));
        assertTrue("Missing private property in String representation of ExtendedCacheControl",
                value.contains("public"));
    }

}
