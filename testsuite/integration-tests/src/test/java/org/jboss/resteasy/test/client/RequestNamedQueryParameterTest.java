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

import java.io.IOException;
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
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:dkafetzi@redhat.com">Dimitris Kafetzis</a>
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 6.3.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RequestNamedQueryParameterTest extends ClientTestBase {

    @Path("someResource")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public interface SomeResource {

        @GET
        void methodWithLists(@QueryParam("listA") List<String> listA, @QueryParam("listB") List<String> listB,
                             @QueryParam("listC") List<String> listC);
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientInvocationBuilderTest.class.getSimpleName());
        war.addClass(SomeResource.class);
        war.addClass(ClientTestBase.class);
        return war;
    }

    enum testType {
        NOPARAMETERS,
        MIXEDPARAMETERS,
        FULLPARAMETERS
    }

    public class AssertFilter implements ClientRequestFilter {
        final testType setting;

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            if (setting == testType.NOPARAMETERS) {
                Assert.assertEquals("", requestContext.getUri().getQuery());
            } else if (setting == testType.MIXEDPARAMETERS) {
                Assert.assertEquals("listA=stuff1&listA=stuff2&listC=stuff1&listC=stuff2", requestContext.getUri().getQuery());
            } else {
                Assert.assertEquals("listA=stuff1&listA=stuff2&listB=stuff1&listB=stuff2&listC=stuff1&listC=stuff2",
                        requestContext.getUri().getQuery());
            }
            requestContext.abortWith(Response.accepted().build());
        }

        AssertFilter(final testType setting) {
            this.setting = setting;
        }
    }

    /**
     * @tpTestDetails Check if empty named query parameters of a request are properly handled.
     * @tpPassCrit The query for this request invocation should be empty.
     *             The empty Named Query Parameters should be Ignored
     *             (no stray or duplicate '&'s should be present)
     * @tpSince RESTEasy 6.3.0
     */
    @Test
    public void testWithEmptyNamedQueryParameters() {
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        ResteasyClient client = builder.build();

        try (client) {
            client.register(new AssertFilter(testType.NOPARAMETERS));
            client.target("").proxy(SomeResource.class).methodWithLists(List.of(), List.of(), List.of());
        }

    }

    /**
     * @tpTestDetails Check if empty named query parameters of a request are properly handled, This time the parameters are
     *                mixed, some are empty some have contents.
     * @tpPassCrit The query for this request invocation contains only the parameters with content.
     *             The empty Named Query Parameters should be Ignored
     *             (no stray or duplicate '&'s should be present)
     * @tpSince RESTEasy 6.3.0
     */
    @Test
    public void testWithMixedNamedQueryParameters() {
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        ResteasyClient client = builder.build();

        try (client) {
            client.register(new AssertFilter(testType.MIXEDPARAMETERS));
            client.target("").proxy(SomeResource.class).methodWithLists(List.of("stuff1", "stuff2"),
                    List.of(), List.of("stuff1", "stuff2"));
        }

    }

    /**
     * @tpTestDetails Sanity check to make sure that normal behaviour where all the parameters have content did not break.
     * @tpPassCrit The query for this request invocation should have the contents from all the parameters.
     *
     * @tpSince RESTEasy 6.3.0
     */
    @Test
    public void testWithFullNamedQueryParameters() {
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        ResteasyClient client = builder.build();

        try (client) {
            client.register(new AssertFilter(testType.FULLPARAMETERS));
            client.target("").proxy(SomeResource.class).methodWithLists(List.of("stuff1", "stuff2"), List.of("stuff1", "stuff2"), List.of("stuff1", "stuff2"));
        }

    }

}
