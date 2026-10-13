/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.injection;

import java.net.URL;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Application;
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
 * Tests that a {@link BeanParam} aggregator which is a {@code @Dependent} CDI bean is obtained from CDI, both as an
 * injected field and as a resource method parameter.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class BeanParamInjectionTest {
    private static final String EXPECTED = "query:hello:true";
    private static final String EXPECTED_PLAIN = "query:hello:legacy:true";

    @ArquillianResource
    public URL url;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, BeanParamInjectionTest.class.getSimpleName() + ".war")
                .addClasses(
                        Greeter.class,
                        SearchParams.class,
                        PlainSearchParams.class,
                        BeanParamResource.class,
                        TestApplication.class);
    }

    /**
     * An aggregator injected into an {@code @Inject @BeanParam} field.
     */
    @Test
    public void field() throws Exception {
        assertInjected("field", EXPECTED);
    }

    /**
     * An aggregator passed as a {@code @BeanParam} resource method parameter.
     */
    @Test
    public void parameter() throws Exception {
        assertInjected("parameter", EXPECTED);
    }

    /**
     * An aggregator without a bean defining annotation injected into an {@code @Inject @BeanParam} field is made a
     * {@code @Dependent} bean.
     */
    @Test
    public void plainField() throws Exception {
        assertInjected("plain/field", EXPECTED_PLAIN);
    }

    /**
     * An aggregator without a bean defining annotation passed as a {@code @BeanParam} resource method parameter is
     * made a {@code @Dependent} bean.
     */
    @Test
    public void plainParameter() throws Exception {
        assertInjected("plain/parameter", EXPECTED_PLAIN);
    }

    private void assertInjected(final String path, final String expected) throws Exception {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client.target(TestUtil.generateUri(url, "/bean-param/%s".formatted(path)))
                        .queryParam("q", "query")
                        .queryParam("legacy", "legacy")
                        .request(MediaType.TEXT_PLAIN)
                        .get()) {
            final String entity = response.readEntity(String.class);
            Assertions.assertEquals(200, response.getStatus(), entity);
            Assertions.assertEquals(expected, entity);
        }
    }

    @ApplicationScoped
    public static class Greeter {
        public String greet() {
            return "hello";
        }
    }

    /**
     * An aggregator which is a {@code @Dependent} CDI bean.
     */
    @Dependent
    public static class SearchParams {

        @Inject
        @QueryParam("q")
        String q;

        @Inject
        Greeter greeter;

        boolean postConstructed;

        @PostConstruct
        void init() {
            postConstructed = true;
        }

        String describe() {
            return "%s:%s:%s".formatted(q, greeter == null ? null : greeter.greet(), postConstructed);
        }
    }

    /**
     * An aggregator without a bean defining annotation, with a parameter extraction annotation without
     * {@code @Inject} as written for Jakarta REST 4.0.
     */
    public static class PlainSearchParams {

        @Inject
        @QueryParam("q")
        String q;

        @Inject
        Greeter greeter;

        @QueryParam("legacy")
        String legacy;

        boolean postConstructed;

        @PostConstruct
        void init() {
            postConstructed = true;
        }

        String describe() {
            return "%s:%s:%s:%s".formatted(q, greeter == null ? null : greeter.greet(), legacy, postConstructed);
        }
    }

    @Path("/bean-param")
    @Produces(MediaType.TEXT_PLAIN)
    public static class BeanParamResource {

        @Inject
        @BeanParam
        SearchParams fieldParams;

        @Inject
        @BeanParam
        PlainSearchParams plainFieldParams;

        @GET
        @Path("/field")
        public String field() {
            return fieldParams.describe();
        }

        @GET
        @Path("/parameter")
        public String parameter(@BeanParam final SearchParams params) {
            return params.describe();
        }

        @GET
        @Path("/plain/field")
        public String plainField() {
            return plainFieldParams.describe();
        }

        @GET
        @Path("/plain/parameter")
        public String plainParameter(@BeanParam final PlainSearchParams params) {
            return params.describe();
        }
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
    }
}
