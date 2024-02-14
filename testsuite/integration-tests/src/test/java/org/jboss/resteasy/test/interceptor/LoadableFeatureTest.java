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

package org.jboss.resteasy.test.interceptor;

import java.net.URL;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class LoadableFeatureTest {

    @ArquillianResource
    public URL url;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, LoadableFeatureTest.class.getSimpleName() + ".war")
                .addClasses(
                        FeatureResource.class,
                        TestFeature.class,
                        TestDynamicFeature.class,
                        TestApplication.class)
                .addAsServiceProvider(Feature.class, TestFeature.class)
                .addAsServiceProvider(DynamicFeature.class, TestDynamicFeature.class);
    }

    @Test
    public void feature() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(TestUtil.generateUri(url, "/test/feature"))
                    .request()
                    .get();
            Assertions.assertEquals(200, response.getStatus(), response.getStatusInfo().getReasonPhrase());
            Assertions.assertEquals(TestFeature.class.getName(), response.readEntity(String.class));
        }
    }

    @Test
    public void dynamicFeature() throws Exception {
        try (Client client = ClientBuilder.newClient()) {
            final Response response = client.target(TestUtil.generateUri(url, "/test/dynamic-feature"))
                    .request()
                    .get();
            Assertions.assertEquals(200, response.getStatus(), response.getStatusInfo().getReasonPhrase());
            Assertions.assertEquals(TestDynamicFeature.class.getName(), response.readEntity(String.class));
        }
    }

    public static class TestFeature implements Feature {

        @Override
        public boolean configure(final FeatureContext context) {
            context.register((ContainerRequestFilter) requestContext -> {
                if (requestContext.getUriInfo().getPathParameters().get("name").contains("feature")) {
                    requestContext.abortWith(Response.ok(TestFeature.class.getName()).build());
                }
            });
            return true;
        }
    }

    public static class TestDynamicFeature implements DynamicFeature {

        @Override
        public void configure(final ResourceInfo resourceInfo, final FeatureContext context) {
            context.register((ContainerResponseFilter) (requestContext, responseContext) -> {
                if (requestContext.getUriInfo().getPathParameters().get("name").contains("dynamic-feature")) {
                    responseContext.setEntity(TestDynamicFeature.class.getName());
                }
            });
        }
    }

    @Path("/test")
    public static class FeatureResource {

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        @Path("/{name}")
        public Response feature(@PathParam("name") final String name) {
            return Response.ok(name).build();
        }
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {

    }
}
