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

package dev.resteasy.cloud.tests.client;

import static org.wildfly.test.cloud.common.WildflyTags.KUBERNETES;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.wildfly.test.cloud.common.TestHelper;
import org.wildfly.test.cloud.common.WildFlyCloudTestCase;
import org.wildfly.test.cloud.common.WildFlyKubernetesIntegrationTest;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */

@Tag(KUBERNETES)
@WildFlyKubernetesIntegrationTest
public class EchoIT extends WildFlyCloudTestCase {

    @Inject
    private Client client;

    @Test
    public void chineseGet() throws Exception {
        get("你好");
    }

    @Test
    public void chinesePost() throws Exception {
        post("你好");
    }

    @Test
    public void englishGet() throws Exception {
        get("Hello!");
    }

    @Test
    public void englishPost() throws Exception {
        post("Hello!");
    }

    @Test
    public void spanishGet() throws Exception {
        get("¡Hola!");
    }

    @Test
    public void spanishPost() throws Exception {
        post("¡Hola!");
    }

    private void get(final String msg) throws Exception {
        final TestHelper testHelper = getHelper();
        testHelper.doWithWebPortForward("/user/echo/get/" + URLEncoder.encode(msg, StandardCharsets.UTF_8),
                (url) -> {
                    try (Response response = client.target(url.toURI()).request().get()) {
                        final String content = response.readEntity(String.class);
                        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo(), content);
                        Assertions.assertEquals(msg, content);
                        return null;
                    }
                });
    }

    private void post(final String msg) throws Exception {
        final TestHelper testHelper = getHelper();
        testHelper.doWithWebPortForward("/user/echo/post/",
                (url) -> {
                    try (Response response = client.target(url.toURI()).request().post(Entity.text(msg))) {
                        final String content = response.readEntity(String.class);
                        Assertions.assertEquals(Response.Status.OK, response.getStatusInfo(), content);
                        Assertions.assertEquals(msg, content);
                        return null;
                    }
                });
    }
}
