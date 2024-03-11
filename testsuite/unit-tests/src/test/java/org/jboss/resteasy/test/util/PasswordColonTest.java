package org.jboss.resteasy.test.util;

import org.jboss.resteasy.util.BasicAuthHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for RESTEASY-1176
 * @tpSince RESTEasy 3.0.16
 */
public class PasswordColonTest {
    /**
     * @tpTestDetails Test for BasicAuthHelper
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPasswordWithColon() {
        String header = BasicAuthHelper.createHeader("user", "pass:word");
        String[] credentials = BasicAuthHelper.parseHeader(header);
        Assertions.assertEquals("user", credentials[0], "Wrong user");
        Assertions.assertEquals("pass:word", credentials[1], "Wrong password");
    }
}
