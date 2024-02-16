/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.test.regex;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.regex.resource.ProxyPathParamRegexResource;
import org.jboss.resteasy.test.regex.resource.RegexInterface;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests for RESTEASY-3291
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ProxyPathParamRegexTest {

    static ResteasyClient client;

    @BeforeEach
    public void setUp() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyPathParamRegexTest.class.getSimpleName());
        war.addClass(RegexInterface.class);
        return TestUtil.finishContainerPrepare(war, null, ProxyPathParamRegexResource.class);
    }

    private String generateURL() {
        return PortProviderUtil.generateBaseUrl(ProxyPathParamRegexTest.class.getSimpleName());
    }

    /**
     * As a control check that a simple query works.
     */
    @Test
    public void queryControlTest() {
        ResteasyWebTarget target = client.target(generateURL());

        RegexInterface proxy = target.proxy(RegexInterface.class);
        String responseString = proxy.getEncodedQueryParam("q p");

        Assertions.assertEquals("QueryParamq+p", responseString,
                "Wrong string returned by proxy interface");
    }

    /**
     * As a control check that a simple reqex using ? is processed correctly
     */
    @Test
    public void questionMarkControlTest() {

        ResteasyWebTarget target = client.target(generateURL());

        RegexInterface proxy = target.proxy(RegexInterface.class);
        String responseString = proxy.getSimplePath("w");

        Assertions.assertEquals("simplew", responseString,
                "Wrong string returned by proxy interface");
    }

    /**
     * @tpTestDetails Checks whether question mark in regular expression in second path param is correctly evaluated.
     * @tpPassCrit Expected string is returned
     */
    @Test
    public void testQuestionMarkInMultiplePathParamRegex() {

        ResteasyWebTarget target = client.target(generateURL());

        RegexInterface proxy = target.proxy(RegexInterface.class);
        String responseString = proxy.getQuestionMarkInMultiplePathParamRegex("xpath", "x");

        Assertions.assertEquals("xpathx", responseString,
                "Wrong string returned by proxy interface");
    }

    /**
     * Path regex expression and query syntax can contain a ?. Verify both scenarios
     * are handled properly.
     */
    @Test
    public void questionMarkAndQueryTest() {

        ResteasyWebTarget target = client.target(generateURL());

        RegexInterface proxy = target.proxy(RegexInterface.class);
        String responseString = proxy.getQuestionMarkAndQuery("x", "status:GOLD");

        Assertions.assertEquals("path=x:query=status:GOLD", responseString,
                "Wrong string returned by proxy interface");
    }

    /**
     * Test 2 params each using a regex expression that uses a ?.
     */
    @Test
    public void twoRegexQuestionMarkTest() {

        ResteasyWebTarget target = client.target(generateURL());

        RegexInterface proxy = target.proxy(RegexInterface.class);
        String responseString = proxy.getTwoRegexQuestionMarkTest("xZ", "Y");

        Assertions.assertEquals("lower=xZ:upper=Y", responseString,
                "Wrong string returned by proxy interface");
    }

    /**
     * Verify regex qualifier, *, is handled.
     */
    @Test
    public void asteriskQualiferTest() {

        ResteasyWebTarget target = client.target(generateURL());

        RegexInterface proxy = target.proxy(RegexInterface.class);
        String responseString = proxy.getAsteriskQualiferTest("amw", "xpath");

        Assertions.assertEquals("string=amw:path=xpath", responseString,
                "Wrong string returned by proxy interface");
    }

    /**
     * Verify regex qualifier, {}, is handled correctly
     */
    @Test
    public void curlyBracketQualifierTest() {

        ResteasyWebTarget target = client.target(generateURL());

        RegexInterface proxy = target.proxy(RegexInterface.class);
        String responseString = proxy.getCurlyBracketQualifierTest("abc", "xpath");

        Assertions.assertEquals("string=abc:path=xpath", responseString,
                "Wrong string returned by proxy interface");
    }
}
