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

package org.jboss.resteasy.test.client.other;

import java.io.File;
import java.io.FilePermission;
import java.net.SocketPermission;
import java.util.PropertyPermission;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ApplicationScoped
public class VertxClientHttpEngineTest extends ClientHttpEngineTest {

    protected VertxClientHttpEngineTest() {
        super("Vertx");
    }

    @Deployment
    public static WebArchive deployment() {
        final File[] libs = Maven.resolver()
                .resolve("org.jboss.resteasy:resteasy-client-vertx:" + System.getProperty("version.resteasy.testsuite"))
                .withTransitivity()
                .asList(File.class)
                .stream()
                .filter(f -> f.getName().matches(".*vertx.*\\.jar") || f.getName().matches(".*netty.*\\.jar"))
                .toArray(File[]::new);
        return createDeployment(VertxClientHttpEngineTest.class, libs)
                .addAsManifestResource(
                        DeploymentDescriptors.createPermissionsXmlAsset(new PropertyPermission("*", "read"),
                                new FilePermission(System.getProperty("java.io.tmpdir") + "/-", "read,write"),
                                new RuntimePermission("shutdownHooks"),
                                new RuntimePermission("setContextClassLoader"),
                                new RuntimePermission("modifyThread"),
                                new RuntimePermission("getStackTrace"),
                                new SocketPermission("127.0.0.1:8080", "connect,resolve")),
                        "permissions.xml");
    }
}
