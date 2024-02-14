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
package org.jboss.resteasy.test.core.spi;

import java.io.IOException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.spi.resource.SeparatorAnnotationAsFieldTargetEndPoint;
import org.jboss.resteasy.test.core.spi.resource.SeparatorAnnotationAsMethodTargetEndPoint;
import org.jboss.resteasy.test.core.spi.resource.SeparatorAnnotationAsParameterTargetEndPoint;
import org.jboss.resteasy.test.core.spi.resource.SeparatorAnnotationBeanParam;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Separator Annotation
 * @tpChapter Integration tests
 * @tpTestCaseDetails Testing Separator annotation, for each of its targets, see RESTEASY-3029
 * @tpSince RESTEasy 7.0.0.Alpha1-SNAPSHOT
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SeparatorAnnotationTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SeparatorAnnotationTest.class.getSimpleName())
                .addClass(SeparatorAnnotationTest.class);
        return TestUtil.finishContainerPrepare(war, null,
                SeparatorAnnotationAsParameterTargetEndPoint.class,
                SeparatorAnnotationAsFieldTargetEndPoint.class,
                SeparatorAnnotationAsMethodTargetEndPoint.class,
                SeparatorAnnotationBeanParam.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SeparatorAnnotationTest.class.getSimpleName());
    }

    @Test
    public void testSeparatorAsParameterTarget() throws IOException {
        try (Client client = ClientBuilder.newClient()) {
            Response response = client
                    .target(generateURL("/separator/parameter/1,2,3"))
                    .request()
                    .get();

            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("This is your sentence:123", response.readEntity(String.class));
        }
    }

    @Test
    public void testSeparatorAsFieldTarget() throws IOException {
        try (Client client = ClientBuilder.newClient()) {
            Response response = client
                    .target(generateURL("/separator/field/1,2,3"))
                    .request()
                    .get();

            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("This is your sentence:123", response.readEntity(String.class));
        }
    }

    @Test
    public void testSeparatorAsMethodTarget() throws IOException {
        try (Client client = ClientBuilder.newClient()) {
            Response response = client
                    .target(generateURL("/separator/method/1,2,3"))
                    .request()
                    .get();

            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("This is your sentence:123", response.readEntity(String.class));
        }
    }
}
