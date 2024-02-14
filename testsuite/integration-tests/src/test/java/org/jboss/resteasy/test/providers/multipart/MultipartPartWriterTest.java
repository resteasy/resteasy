/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2021 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.providers.multipart;

import java.net.URL;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedInput;
import org.jboss.resteasy.test.providers.multipart.resource.GreetAsync;
import org.jboss.resteasy.test.providers.multipart.resource.Greeter;
import org.jboss.resteasy.test.providers.multipart.resource.GreeterAsyncMessageBodyWriter;
import org.jboss.resteasy.test.providers.multipart.resource.GreeterMessageBodyWriter;
import org.jboss.resteasy.test.providers.multipart.resource.GreeterResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests which use a non-{@linkplain org.jboss.resteasy.spi.AsyncMessageBodyWriter} and a
 * {@linkplain org.jboss.resteasy.spi.AsyncMessageBodyWriter} for parts to ensure the
 * {@link org.jboss.resteasy.plugins.providers.multipart.MultipartWriter} can use writers of both sync and async.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class MultipartPartWriterTest {

    private static Client client;
    @ArquillianResource
    private URL url;

    @AfterAll
    public static void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    @Deployment
    public static Archive<?> createDeployment() {
        WebArchive war = TestUtil.prepareArchive(MultipartPartWriterTest.class.getSimpleName())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addClasses(Greeter.class, GreetAsync.class);
        return TestUtil.finishContainerPrepare(war, null,
                GreeterMessageBodyWriter.class, GreeterAsyncMessageBodyWriter.class, GreeterResource.class);
    }

    @BeforeAll
    public static void initClient() {
        client = ClientBuilder.newClient();
    }

    @Test
    public void sync() throws Exception {
        final Response response = client.target(TestUtil.generateUri(url, "greet"))
                .request()
                .get();
        Assertions.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        final MultipartRelatedInput input = response.readEntity(MultipartRelatedInput.class);
        Assertions.assertNotNull(input);
        Assertions.assertEquals("Hello Sync", input.getRootPart().getBodyAsString());
    }

    @Test
    public void async() throws Exception {
        final Response response = client.target(TestUtil.generateUri(url, "greet/async"))
                .request()
                .get();
        Assertions.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        final MultipartRelatedInput input = response.readEntity(MultipartRelatedInput.class);
        Assertions.assertNotNull(input);
        Assertions.assertEquals("Hello Async", input.getRootPart().getBodyAsString());
    }
}
