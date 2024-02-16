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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.regex.resource.ProxyPathParamRegexResource;
import org.jboss.resteasy.test.regex.resource.RegexInterface;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
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
public class PathParamRegexTest {

    static Client client;

    @ArquillianResource
    private URI uri;

    @BeforeEach
    public void setUp() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PathParamRegexTest.class.getSimpleName());
        war.addClass(RegexInterface.class);
        return TestUtil.finishContainerPrepare(war, null,
                ProxyPathParamRegexResource.class);
    }

    private URI generatePathURL(String path) throws URISyntaxException {
        return TestUtil.generateUri(uri, path);
    }

    /**
     * As a control check that a simple query works.
     */
    @Test
    public void queryControlTest() throws Exception {
        WebTarget target = client.target(generatePathURL("/encoded/query?m=q%20p"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("QueryParamq%20p", entity, "Wrong string returned ");
        response.close();
    }

    /**
     * As a control check that a simple reqex using ? is processed correctly
     */
    @Test
    public void questionMarkControlTest() throws Exception {
        WebTarget target = client.target(generatePathURL("/w"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("simplew", entity, "Wrong string returned ");
        response.close();
    }

    /**
     * @tpTestDetails Checks whether question mark in regular expression in second path param is correctly evaluated.
     * @tpPassCrit Expected string is returned
     */
    @Test
    public void testQuestionMarkInMultiplePathParamRegex() throws Exception {
        WebTarget target = client.target(generatePathURL("/xpath/x"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("xpathx", entity, "Wrong string returned ");
        response.close();
    }

    /**
     * Path regex expression and query syntax can contain a ?. Verify both scenarios
     * are handled properly.
     */
    @Test
    public void questionMarkAndQueryTest() throws Exception {
        WebTarget target = client.target(generatePathURL("/regex/query/x/cust"))
                .queryParam("m", "q p");
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("path=x:query=q p", entity, "Wrong string returned ");
        response.close();
    }

    /**
     * Test 2 params each using a regex expression that uses a ?.
     */
    @Test
    public void twoRegexQuestionMarkTest() throws Exception {
        WebTarget target = client.target(generatePathURL("/two/xZ/Y"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("lower=xZ:upper=Y", entity, "Wrong string returned ");
        response.close();
    }

    /**
     * Verify regex qualifier, *, is handled.
     */
    @Test
    public void asteriskQualifierTest() throws Exception {
        WebTarget target = client.target(generatePathURL("/asterisk/amw/xpath"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("string=amw:path=xpath", entity, "Wrong string returned ");
        response.close();
    }

    /**
     * Verify regex qualifier, {}, is handled correctly
     */
    @Test
    public void curlyBracketQualifierTest() throws Exception {

        WebTarget target = client.target(generatePathURL("/bracket/abc/xpath"));
        Response response = target.request().get();
        String entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("string=abc:path=xpath", entity, "Wrong string returned ");
        response.close();
    }

}
