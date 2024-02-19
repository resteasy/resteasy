package org.jboss.resteasy.test.request;

import static org.hamcrest.CoreMatchers.containsString;

import org.hamcrest.MatcherAssert;
import org.jboss.resteasy.plugins.delegates.NewCookieHeaderDelegate;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Requests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for JBEAP-4712
 * @tpSince RESTEasy 3.0.17
 */
//@Disabled("RESTEASY-3450")
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
        MatcherAssert.assertThat(errorMessage, stringCookie, containsString("a="));
        MatcherAssert.assertThat(errorMessage, stringCookie, containsString("Version="));
    }
}
