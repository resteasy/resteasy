package org.jboss.resteasy.specimpl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ResteasyUriInfoTest {

    @Test
    public void testUrIsCacheable() {
        assertTrue(ResteasyUriInfo.InitData.canBeCached("http://localhost/test/data"));
        assertTrue(ResteasyUriInfo.InitData.canBeCached("http://localhost/test/data?k=v"));
        assertFalse(ResteasyUriInfo.InitData.canBeCached("http://localhost/test;k=v/data"));
    }

    @Test
    public void testGetCacheKeyContainsExpectedResult() {
        String absoluteUri = "http://localhost/test/data";
        String key = ResteasyUriInfo.InitData.getCacheKey(absoluteUri, "test");
        assertTrue(key.contains("localhost"));
        assertTrue(key.contains("test"));
        assertTrue(key.contains("data"));
        assertTrue(key.length() > absoluteUri.length());
    }

    @Test
    public void testGetCacheKeyGivesDifferentKeyForDifferentContextPath() {
        String absoluteUri = "http://localhost/test/data";
        String key1 = ResteasyUriInfo.InitData.getCacheKey(absoluteUri, "test");
        String key2 = ResteasyUriInfo.InitData.getCacheKey(absoluteUri, "");
        assertNotEquals(key1, key2);
    }

}
