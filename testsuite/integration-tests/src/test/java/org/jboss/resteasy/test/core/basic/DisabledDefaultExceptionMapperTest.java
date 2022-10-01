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

import java.net.URL;

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

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.setup.DisableDefaultExceptionMapperSetupTask;
import org.jboss.resteasy.test.core.basic.resource.ExceptionResource;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests that the default {@link ExceptionMapper} is disabled.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ServerSetup(DisableDefaultExceptionMapperSetupTask.class)
public abstract class DisabledDefaultExceptionMapperTest {

    @Inject
    protected Providers providers;
    @Inject
    protected Client client;
    @ArquillianResource
    protected URL url;

    /**
     * Tests we end up with the response and status code from the WAE we threw.
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void waeException() throws Exception {
        final Response response = client.target(TestUtil.generateUri(url, "/exception/wae"))
                .request()
                .get();
        Assert.assertEquals(ExceptionResource.WAE_RESPONSE.getStatusInfo(), response.getStatusInfo());
        Assert.assertEquals(ExceptionResource.WAE_RESPONSE.readEntity(String.class), response.readEntity(String.class));
    }


    /**
     * Tests that a defined exception mapper is used
     *
     * @throws Exception if an exception occurs
     */
    @Test
    public void defaultExceptionMapperNotUsed() throws Exception {
        final ExceptionMapper<UnsupportedOperationException> mapper = providers.getExceptionMapper(UnsupportedOperationException.class);
        Assert.assertTrue("Mapper was not an instance of UnsupportedOperationException: " + mapper, mapper instanceof UnsupportedOperationExceptionMapper);
        final Response response = client.target(TestUtil.generateUri(url, "/exception/not-impl"))
                .request()
                .get();
        Assert.assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
        Assert.assertEquals("Path /exception/not-impl was not found", response.readEntity(String.class));
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
