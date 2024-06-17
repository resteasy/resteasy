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

package org.jboss.resteasy.test.core.basic;

import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.setup.DisableDefaultExceptionMapperSetupTask;
import org.jboss.resteasy.test.core.basic.resource.ExceptionResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * Tests that the default {@link ExceptionMapper} is disabled by setting the {@code dev.resteasy.exception.mapper}
 * system property to false.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ServerSetup(DisableDefaultExceptionMapperSetupTask.class)
@ExtendWith(ArquillianExtension.class)
@RequestScoped
public class DisabledDefaultExceptionNoThrowableMapperTest extends DisabledDefaultExceptionMapperTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class, DisabledDefaultExceptionNoThrowableMapperTest.class.getSimpleName() + ".war")
                .addClasses(
                        DisabledDefaultExceptionMapperTest.class,
                        ExceptionResource.class,
                        TestUtil.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                // These can be removed if WFARQ-118 is resolved
                .addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                        // Required for Arquillian
                        new ReflectPermission("suppressAccessChecks"),
                        new PropertyPermission("arquillian.*", "read"),
                        new RuntimePermission("accessClassInPackage.sun.reflect.annotation"),
                        // Required for JUnit
                        new RuntimePermission("accessDeclaredMembers")),
                        "permissions.xml");
    }

    /**
     * Tests that the default exception mapper does not exist.
     */
    @Test
    public void defaultExceptionMapper() {
        Assertions.assertNull(providers.getExceptionMapper(RuntimeException.class),
                "Expected not to have a default exception mapper");
    }

    /**
     * Test that the exception falls through and an UnhandledException is thrown.
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void noDefaultExceptionMapper() throws Exception {
        final Response response = client.target(TestUtil.generateUri(url, "exception"))
                .request()
                .get();
        Assertions.assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
        // We should end up with a stack trace in the response by default. There should be an UnhandledException and the
        // exception thrown.
        final String value = response.readEntity(String.class);
        Assertions.assertTrue(value.contains(ExceptionResource.EXCEPTION_MESSAGE),
                String.format("Expected %s to be in the result: %s", ExceptionResource.EXCEPTION_MESSAGE, value));
        Assertions.assertTrue(value.contains("UnhandledException"),
                String.format("Expected UnhandledException to be in the result: %s", value));
    }
}
