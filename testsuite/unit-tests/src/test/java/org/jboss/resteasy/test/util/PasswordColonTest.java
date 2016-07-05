package org.jboss.resteasy.test.util;

import org.jboss.resteasy.util.BasicAuthHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for RESTEASY-1176
 * @tpSince EAP 7.0.0
 */
public class PasswordColonTest {
    /**
     * @tpTestDetails Test for BasicAuthHelper
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testPasswordWithColon() {
        String header = BasicAuthHelper.createHeader("user", "pass:word");
        String[] credentials = BasicAuthHelper.parseHeader(header);
        Assert.assertEquals("Wrong user", "user", credentials[0]);
        Assert.assertEquals("Wrong password", "pass:word", credentials[1]);
    }
}
