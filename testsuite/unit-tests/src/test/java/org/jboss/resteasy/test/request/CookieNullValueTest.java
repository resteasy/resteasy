package org.jboss.resteasy.test.request;

import org.jboss.resteasy.plugins.delegates.NewCookieHeaderDelegate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Requests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for JBEAP-4712, RESTEASY-3556
 * @tpSince RESTEasy 3.0.17
 */
public class CookieNullValueTest {

    /**
     * @tpTestDetails Test for creating of NewCookieHeaderDelegate.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testCookie() {
        String errorMessage = "NewCookieHeaderDelegate returns wrong data";
        NewCookieHeaderDelegate delegate = new NewCookieHeaderDelegate();
        Object o = delegate.fromString("a=");
        String stringCookie = o.toString();
        Assertions.assertTrue(stringCookie.contains("a="), errorMessage);
        Assertions.assertTrue(stringCookie.contains("Version="), errorMessage);
    }
    
    /**
     * @tpTestDetails Test for creating of NewCookieHeaderDelegate with NewCookie6265
     * @tpSince RESTEasy 7.0.1.Final
     */
    @Test
    public void testCookie6265() {
        String errorMessage = "NewCookieHeaderDelegate returns wrong data";
        NewCookieHeaderDelegate delegate = new NewCookieHeaderDelegate();
        Object o = delegate.fromString("a=;x=y");
        String stringCookie = o.toString();
        Assertions.assertTrue(stringCookie.contains("a="), errorMessage);
        Assertions.assertFalse(stringCookie.contains("Version="), errorMessage);
    }
}
