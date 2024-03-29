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

import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.wildfly.arquillian.junit.annotations.WildFlyArquillian;

/**
 * Tests an {@link Application} annotated with {@link ApplicationPath} as root resource. The value of the annotation is
 * a simple slash.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@WildFlyArquillian
@RunAsClient
public class UriInfoMatchedResourceTemplateAppSlashTest extends UriInfoMatchedResourceTemplateTest {

    private static final String APPLICATION_PATH = "/";
    private static final String RESOURCE_PATH = "uri-info";

    @Deployment
    public static WebArchive deployment() {
        return createDeployment(UriInfoMatchedResourceTemplateAppSlashTest.class)
                .addClasses(TestApplication.class, UriInfoResourceImpl.class);
    }

    @Override
    protected String applicationPath() {
        return APPLICATION_PATH;
    }

    @Override
    protected String resourcePath() {
        return RESOURCE_PATH;
    }

    @ApplicationPath(APPLICATION_PATH)
    public static class TestApplication extends Application {
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
