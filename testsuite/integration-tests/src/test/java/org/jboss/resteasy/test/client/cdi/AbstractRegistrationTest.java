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

package org.jboss.resteasy.test.client.cdi;

import java.net.URI;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.client.cdi.resources.TestBean;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
abstract class AbstractRegistrationTest {

    @ArquillianResource
    URI uri;

    /**
     * Tests that the returned JSON contains a non-null {@code uriInfo} and {@code testBean} entry. Each entry should
     * have the value of {@code /test} which is the value returned from {@link UriInfo#getPath()}.
     *
     * @throws Exception if an error occurs while testing
     */
    @Test
    public void injectedBeanAndUriInfo() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(TestUtil.generateUri(uri, "/test"))
                    .request()
                    .get();
            Assertions.assertTrue(response.bufferEntity(), "Failed to buffer entity");
            Assertions.assertEquals(Response.Status.OK,
                    response.getStatusInfo(),
                    "Unexpected response: " + response.getEntity());
            final JsonObject json = response.readEntity(JsonObject.class);
            Assertions.assertFalse(json.isNull("uriInfo"), "Expected \"uriInfo\" to not be null: " + json);
            Assertions.assertEquals("/test", json.getString("uriInfo"));
            Assertions.assertFalse(json.isNull("testBean"), "Expected \"testBean\" to not be null: " + json);
            Assertions.assertEquals("/test", json.getString("testBean"));
        }
    }

    static WebArchive createDeployment(final Class<? extends AbstractRegistrationTest> type) {
        return ShrinkWrap.create(WebArchive.class, type.getSimpleName() + ".war")
                .addClasses(
                        AbstractRegistrationTest.class,
                        FailingResource.class,
                        TestApplication.class,
                        TestBean.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    /**
     * A resource which throws a {@link WebApplicationException} if invoked.
     *
     * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
     */
    @Path("/test")
    @ApplicationScoped
    @Produces(MediaType.APPLICATION_JSON)
    public static class FailingResource {

        /**
         * Throws a {@link WebApplicationException}.
         *
         * @return nothing
         */
        @GET
        public Response get() {
            throw new WebApplicationException("Should not have made it here.");
        }
    }
}
