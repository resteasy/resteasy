/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.resource.basic;

import java.util.Map;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Disabled;
import org.wildfly.arquillian.junit.annotations.WildFlyArquillian;

/**
 * Tests that an {@link Application} without a {@link jakarta.ws.rs.ApplicationPath} annotation present. The application
 * is registered as a servlet with a servlet-mapping of {@code test/*} in a {@code web.xml}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@WildFlyArquillian
@RunAsClient
@Disabled("RESTEASY-3485")
public class UriInfoMatchedResourceTemplateAppMappingTest extends UriInfoMatchedResourceTemplateTest {

    private static final String APPLICATION_PATH = "test";
    private static final String RESOURCE_PATH = "uri-info";

    @Deployment
    public static WebArchive deployment() {
        return createDeployment(UriInfoMatchedResourceTemplateAppMappingTest.class)
                .addClasses(TestApplication.class, UriInfoResourceImpl.class)
                .addAsWebInfResource(TestUtil.createWebXml(TestApplication.class, APPLICATION_PATH + "/*", Map.of()),
                        "web.xml");
    }

    @Override
    protected String applicationPath() {
        return APPLICATION_PATH;
    }

    @Override
    protected String resourcePath() {
        return RESOURCE_PATH;
    }

    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(UriInfoResourceImpl.class, DefaultSubResource.class);
        }
    }

    @Path(RESOURCE_PATH)
    public static class UriInfoResourceImpl extends AbstractUriInfoResource {
        @Inject
        private SubResource subResource;

        @Override
        public SubResource subResource() {
            return subResource;
        }
    }
}
