package org.jboss.resteasy.test.request;

import org.jboss.resteasy.plugins.delegates.NewCookieHeaderDelegate;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;

/**
 * @tpSubChapter Requests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for JBEAP-4712
 * @tpSince EAP 7.0.1
 */
public class CookieNullValueTest {

    /**
     * @tpTestDetails Test for creating of NewCookieHeaderDelegate.
     * @tpSince EAP 7.0.1
     */
    @Test
    public void testCookie() {
        String errorMessage = "NewCookieHeaderDelegate returns wrong data";
        NewCookieHeaderDelegate delegate = new NewCookieHeaderDelegate();
        Object o = delegate.fromString("a=");
        String stringCookie = o.toString();
        Assert.assertThat(errorMessage, stringCookie, containsString("a="));
        Assert.assertThat(errorMessage, stringCookie, containsString("Version="));
    }
}
