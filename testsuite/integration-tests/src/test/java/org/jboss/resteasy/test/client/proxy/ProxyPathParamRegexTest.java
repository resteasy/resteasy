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

package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;

import org.jboss.resteasy.test.client.proxy.resource.ProxyPathParamRegexResource;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;


/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-2845
 * @tpSince RESTEasy 5.0.5
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProxyPathParamRegexTest {

    @Path("")
    public interface RegexInterface {
        @GET
        @Path("/{path}/{string}")
        @Produces(MediaType.TEXT_PLAIN)
        String getPath(@PathParam("path") String path, @PathParam("string") @Encoded String string);
    }

    static ResteasyClient client;

    @Before
    public void setUp(){
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception{
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyPathParamRegexTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ProxyPathParamRegexResource.class);
    }

    private String generateURL() {
        return PortProviderUtil.generateBaseUrl(ProxyPathParamRegexTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Checks whether question mark in regular expression in second path param is correctly evaluated.
     * @tpPassCrit Expected string is returned
     * @tpSince RESTEasy 5.0.5
     */
    @Test
    public void testQuestionMarkInMultiplePathParamRegex() {

        ResteasyWebTarget target = client.target(generateURL());

        ProxyPathParamRegexTest.RegexInterface proxy = target.proxy(ProxyPathParamRegexTest.RegexInterface.class);
        String responseString = proxy.getPath("path", "a");

        Assert.assertEquals("Wrong string returned by proxy interface", "patha", responseString);
    }
}
