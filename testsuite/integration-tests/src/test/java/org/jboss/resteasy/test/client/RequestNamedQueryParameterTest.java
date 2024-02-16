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

package org.jboss.resteasy.test.client;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:dkafetzi@redhat.com">Dimitris Kafetzis</a>
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 7.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class RequestNamedQueryParameterTest {

    @Path("someResource")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public interface TestResource {

        @GET
        Response query(@QueryParam("listA") List<String> listA, @QueryParam("listB") List<String> listB,
                @QueryParam("listC") List<String> listC);
    }

    @ArquillianResource
    private URI uri;

    @Deployment
    public static Archive<?> deploy() {
        return TestUtil.prepareArchive(ClientInvocationBuilderTest.class.getSimpleName())
                .addClasses(TestResource.class);
    }

    /**
     * @tpTestDetails Check if empty named query parameters of a request are properly handled.
     * @tpPassCrit The query for this request invocation should be empty.
     *             The empty Named Query Parameters should be Ignored
     *             (no stray or duplicate '&'s should be present)
     * @tpSince RESTEasy 7.0.0
     */
    @Test
    public void testWithEmptyNamedQueryParameters() {
        try (ResteasyClient client = createClient();
                Response response = client.target(uri)
                        .proxy(TestResource.class)
                        .query(List.of(), List.of(), List.of())) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("", response.readEntity(String.class));
        }
    }

    /**
     * @tpTestDetails Check if empty named query parameters of a request are properly handled, This time the parameters are
     *                mixed, some are empty some have contents.
     * @tpPassCrit The query for this request invocation contains only the parameters with content.
     *             The empty Named Query Parameters should be Ignored
     *             (no stray or duplicate '&'s should be present)
     * @tpSince RESTEasy 7.0.0
     */
    @Test
    public void testWithMixedNamedQueryParameters() {
        try (ResteasyClient client = createClient();
                Response response = client.target(uri)
                        .proxy(TestResource.class)
                        .query(List.of("stuff1", "stuff2"), List.of(),
                                List.of("stuff1", "stuff2"))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("listA=stuff1&listA=stuff2&listC=stuff1&listC=stuff2", response.readEntity(String.class));
        }
    }

    /**
     * @tpTestDetails Sanity check to make sure that normal behaviour where all the parameters have content did not break.
     * @tpPassCrit The query for this request invocation should have the contents from all the parameters.
     *
     * @tpSince RESTEasy 7.0.0
     */
    @Test
    public void testWithFullNamedQueryParameters() {
        try (ResteasyClient client = createClient();
                Response response = client.target(uri)
                        .proxy(TestResource.class)
                        .query(List.of("stuff1", "stuff2"),
                                List.of("stuff1", "stuff2"), List.of("stuff1", "stuff2"))) {
            Assertions.assertEquals(Response.Status.OK, response.getStatusInfo());
            Assertions.assertEquals("listA=stuff1&listA=stuff2&listB=stuff1&listB=stuff2&listC=stuff1&listC=stuff2",
                    response.readEntity(String.class));
        }
    }

    @SuppressWarnings("resource")
    private static ResteasyClient createClient() {
        return (ResteasyClient) ClientBuilder.newClient()
                .register(new AssertFilter());
    }

    private static class AssertFilter implements ClientRequestFilter {

        @Override
        public void filter(ClientRequestContext requestContext) {
            requestContext.abortWith(Response.ok(requestContext.getUri().getQuery()).build());
        }
    }

}
