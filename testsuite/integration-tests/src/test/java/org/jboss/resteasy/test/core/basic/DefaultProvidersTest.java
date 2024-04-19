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
import java.net.URL;
import java.util.PropertyPermission;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
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
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RequestScoped
public class DefaultProvidersTest {

    @Inject
    private Providers providers;
    @Inject
    private Client client;
    @ArquillianResource
    private URL url;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, DefaultProvidersTest.class.getSimpleName() + ".war")
                .addClasses(
                        TestApplication.class,
                        ExceptionResource.class,
                        UnsupportedOperationExceptionMapper.class,
                        TestUtil.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                // This can be removed if WFARQ-118 is resolved
                .addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                        // Required for Arquillian
                        new ReflectPermission("suppressAccessChecks"),
                        new PropertyPermission("arquillian.*", "read"),
                        new RuntimePermission("accessClassInPackage.sun.reflect.annotation"),
                        // Required for JUnit
                        new RuntimePermission("accessDeclaredMembers")),
                        "permissions.xml");
    }

    @Test
    public void defaultExceptionMapper() {
        Assertions.assertNotNull(providers.getExceptionMapper(RuntimeException.class),
                "Expected a default exception mapper");
    }

    @Test
    public void defaultException() throws Exception {
        final Response response = client.target(TestUtil.generateUri(url, "exception"))
                .request()
                .get();
        Assertions.assertEquals(Response.Status.INTERNAL_SERVER_ERROR, response.getStatusInfo());
        Assertions.assertEquals(ExceptionResource.EXCEPTION_MESSAGE, response.readEntity(String.class));
    }

    @Test
    public void waeException() throws Exception {
        final Response response = client.target(TestUtil.generateUri(url, "/exception/wae"))
                .request()
                .get();
        Assertions.assertEquals(ExceptionResource.WAE_RESPONSE.getStatusInfo(), response.getStatusInfo());
        Assertions.assertEquals(ExceptionResource.WAE_RESPONSE.readEntity(String.class), response.readEntity(String.class));
    }

    @Test
    public void defaultExceptionMapperNotUsed() throws Exception {
        final ExceptionMapper<UnsupportedOperationException> mapper = providers
                .getExceptionMapper(UnsupportedOperationException.class);
        Assertions.assertTrue(mapper instanceof UnsupportedOperationExceptionMapper,
                "Mapper was not an instance of UnsupportedOperationException: " + mapper);
        final Response response = client.target(TestUtil.generateUri(url, "/exception/not-impl"))
                .request()
                .get();
        Assertions.assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
        Assertions.assertEquals("Path /exception/not-impl was not found", response.readEntity(String.class));
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {

    }

    @Provider
    @ConstrainedTo(RuntimeType.SERVER)
    public static class UnsupportedOperationExceptionMapper implements ExceptionMapper<UnsupportedOperationException> {
        @Inject
        private UriInfo uriInfo;

        @Override
        public Response toResponse(final UnsupportedOperationException exception) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(String.format("Path %s was not found", uriInfo.getPath()))
                    .build();
        }
    }
}
