/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2023 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.resteasy1630;

import java.io.File;
import java.net.URI;

import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * See the Servlet 3.0 spec, section 8.2.4 for implementation and processing the details
 * of ServletContainerInitializer.
 *
 * Resteasy's implementation of ServletContainerInitializer is declared in the
 * META-INF/services directory of archive org.jboss.resteasy:resteasy-servlet-initializer
 * as required by the spec. This archive MUST be included in the generated WAR file
 * so the server can find and call it. Shrinkwrap's Maven class and .addAsLibraries
 * method is used to achieve this.
 *
 * This test checks that the implementation properly handles a jaxrs app that provides
 * resource and provider classes but no web.xml file.
 */

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ServletInitializerTwoTest {

    @Deployment
    public static Archive<?> createTestArchiveTwo() {
        File pomFile = Maven.resolver().loadPomFromFile("pom.xml").resolve("org.jboss.resteasy:resteasy-servlet-initializer")
                .withoutTransitivity().asSingleFile();

        WebArchive war = ShrinkWrap.create(WebArchive.class, "RESTEASY-1630-two.war")
                .addClasses(TestApplicationPath.class)
                .addAsLibraries(pomFile)
                .addClasses(TestResource.class);
        return war;
    }

    @ArquillianResource
    URI baseUri;

    /**
     * No web.xml provided in app. The server must auto scan for files.
     *
     * @throws Exception
     */
    @Test
    public void testEndpoint() throws Exception {
        Response response = ResteasyClientBuilder.newClient()
                .target(baseUri.toString() + "two/test/17").request().get();
        //        System.out.println("Status: " + response.getStatus());
        String entity = response.readEntity(String.class);
        //        System.out.println("Result: " + entity);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("17", entity);
    }
}
