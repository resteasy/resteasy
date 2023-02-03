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

import java.net.URL;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.client.cdi.resources.TestBean;
import org.jboss.resteasy.utils.TestApplication;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
abstract class AbstractRegistrationTest {

    @ArquillianResource
    URL url;

    /**
     * Tests that the returned JSON contains a non-null {@code uriInfo} and {@code testBean} entry. Each entry should
     * have the value of {@code /test} which is the value returned from {@link UriInfo#getPath()}.
     *
     * @throws Exception if an error occurs while testing
     */
    @Test
    public void injectedBeanAndUriInfo() throws Exception {
        final Client client = ClientBuilder.newClient();
        try {
            final Response response = client.target(TestUtil.generateUri(url, "/test"))
                    .request()
                    .get();
            Assert.assertTrue("Failed to buffer entity", response.bufferEntity());
            Assert.assertEquals("Unexpected response: " + response.getEntity(), Response.Status.OK, response.getStatusInfo());
            final JsonObject json = response.readEntity(JsonObject.class);
            Assert.assertFalse("Expected \"uriInfo\" to not be null: " + json, json.isNull("uriInfo"));
            Assert.assertEquals("/test", json.getString("uriInfo"));
            Assert.assertFalse("Expected \"testBean\" to not be null: " + json, json.isNull("testBean"));
            Assert.assertEquals("/test", json.getString("testBean"));
        } finally {
            client.close();
        }
    }

    static WebArchive createDeployment(final Class<? extends AbstractRegistrationTest> type) {
        return ShrinkWrap.create(WebArchive.class, type.getSimpleName() + ".war")
                .addClasses(
                        AbstractRegistrationTest.class,
                        FailingResource.class,
                        TestApplication.class,
                        TestBean.class
                )
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
