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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.core.basic.resource.ExceptionResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests that the default {@link ExceptionMapper} is disabled.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
@RequestScoped
public class DisabledDefaultExceptionThrowableMapperMapperTest extends DisabledDefaultExceptionMapperTest {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class, DisabledDefaultExceptionThrowableMapperMapperTest.class.getSimpleName() + ".war")
                .addClasses(
                        DisabledDefaultExceptionMapperTest.class,
                        ExceptionResource.class,
                        TestUtil.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                // This can be removed if WFARQ-118 is resolved
                .addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
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
        final ExceptionMapper<?> mapper = providers.getExceptionMapper(RuntimeException.class);
        Assert.assertTrue(String.format("Expected mapper %s to be instance of %s.", mapper, ThrowableExceptionMapper.class),
                mapper instanceof ThrowableExceptionMapper);
    }

    @Test
    public void defaultException() throws Exception {
        final Response response = client.target(TestUtil.generateUri(url, "exception"))
                .request()
                .get();
        Assert.assertEquals(Response.Status.NOT_IMPLEMENTED, response.getStatusInfo());
        final String value = response.readEntity(String.class);
        Assert.assertTrue(String.format("Expected exception message %s in %s", ExceptionResource.EXCEPTION_MESSAGE, value),
                value.contains(ExceptionResource.EXCEPTION_MESSAGE));
    }

    @Provider
    public static class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

        @Override
        public Response toResponse(final Throwable exception) {
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity(toString(exception))
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .build();
        }

        private static String toString(final Throwable t) {
            try (
                    StringWriter writer = new StringWriter();
                    PrintWriter pw = new PrintWriter(writer)) {
                t.printStackTrace(pw);
                return writer.toString();
            } catch (IOException ignore) {
            }
            return String.format("Failed to print stack trace for %s: %s", t.getClass()
                    .getName(), t.getLocalizedMessage());
        }
    }
}
