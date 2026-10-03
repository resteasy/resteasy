/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.form;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.form.resource.ImmutableClassFormParam;
import org.jboss.resteasy.test.form.resource.MutableClassFormParam;
import org.jboss.resteasy.test.form.resource.RecordBeanParamResource;
import org.jboss.resteasy.test.form.resource.RecordDefaultsParam;
import org.jboss.resteasy.test.form.resource.RecordFormParam;
import org.jboss.resteasy.test.form.resource.RecordMixedParam;
import org.jboss.resteasy.test.form.resource.RecordNullableParam;
import org.jboss.resteasy.test.form.resource.RecordPrimitivesParam;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for Java Records support with @BeanParam
 * @tpSince RESTEasy 7.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class RecordBeanParamTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(RecordBeanParamTest.class.getSimpleName());
        war.addClasses(
                RecordFormParam.class,
                RecordMixedParam.class,
                RecordDefaultsParam.class,
                RecordNullableParam.class,
                RecordPrimitivesParam.class,
                ImmutableClassFormParam.class,
                MutableClassFormParam.class);
        return TestUtil.finishContainerPrepare(war, null, RecordBeanParamResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, RecordBeanParamTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test Record with @FormParam annotations using @BeanParam
     * @tpSince RESTEasy 7.0.0
     */
    @Test
    public void testRecordWithFormParam() throws Exception {
        try (ResteasyClient client = (ResteasyClient) ClientBuilder.newClient()) {
            Form form = new Form()
                    .param("name", "John Doe")
                    .param("age", "30")
                    .param("email", "john@example.com");

            Response response = client.target(generateURL("/record/form"))
                    .request()
                    .post(Entity.form(form));

            Assertions.assertEquals(200, response.getStatus());
            String result = response.readEntity(String.class);
            Assertions.assertEquals("John Doe:30:john@example.com", result);
        }
    }

    /**
     * @tpTestDetails Test Record with mixed parameter types (@FormParam, @QueryParam, @HeaderParam)
     * @tpSince RESTEasy 7.0.0
     */
    @Test
    public void testRecordWithMixedParams() throws Exception {
        try (ResteasyClient client = (ResteasyClient) ClientBuilder.newClient()) {
            Form form = new Form()
                    .param("name", "Jane Smith")
                    .param("age", "25");

            Response response = client.target(generateURL("/record/mixed?country=USA"))
                    .request()
                    .header("X-User-Agent", "TestClient")
                    .post(Entity.form(form));

            Assertions.assertEquals(200, response.getStatus());
            String result = response.readEntity(String.class);
            Assertions.assertEquals("Jane Smith:25:USA:TestClient", result);
        }
    }

    /**
     * @tpTestDetails Test immutable class with constructor injection (non-Record)
     * @tpSince RESTEasy 7.0.0
     */
    @Test
    public void testImmutableClassWithConstructorInjection() throws Exception {
        try (ResteasyClient client = (ResteasyClient) ClientBuilder.newClient()) {
            Form form = new Form()
                    .param("username", "testuser")
                    .param("password", "secret123");

            Response response = client.target(generateURL("/record/immutable"))
                    .request()
                    .post(Entity.form(form));

            Assertions.assertEquals(200, response.getStatus());
            String result = response.readEntity(String.class);
            Assertions.assertEquals("testuser:secret123", result);
        }
    }

    /**
     * @tpTestDetails Test Record with default values
     * @tpSince RESTEasy 7.0.0
     */
    @Test
    public void testRecordWithDefaultValues() throws Exception {
        try (ResteasyClient client = (ResteasyClient) ClientBuilder.newClient()) {
            Form form = new Form()
                    .param("name", "Test User");
            // age is not provided, should use default

            Response response = client.target(generateURL("/record/defaults"))
                    .request()
                    .post(Entity.form(form));

            Assertions.assertEquals(200, response.getStatus());
            String result = response.readEntity(String.class);
            Assertions.assertEquals("Test User:18", result);
        }
    }

    /**
     * @tpTestDetails Test Record with null values
     * @tpSince RESTEasy 7.0.0
     */
    @Test
    public void testRecordWithNullValues() throws Exception {
        try (ResteasyClient client = (ResteasyClient) ClientBuilder.newClient()) {
            Form form = new Form()
                    .param("name", "Test User");
            // email is not provided, should be null

            Response response = client.target(generateURL("/record/nullable"))
                    .request()
                    .post(Entity.form(form));

            Assertions.assertEquals(200, response.getStatus());
            String result = response.readEntity(String.class);
            Assertions.assertEquals("Test User:null", result);
        }
    }

    /**
     * @tpTestDetails Test Record with primitive types
     * @tpSince RESTEasy 7.0.0
     */
    @Test
    public void testRecordWithPrimitives() throws Exception {
        try (ResteasyClient client = (ResteasyClient) ClientBuilder.newClient()) {
            Form form = new Form()
                    .param("count", "42")
                    .param("active", "true")
                    .param("score", "98.5");

            Response response = client.target(generateURL("/record/primitives"))
                    .request()
                    .post(Entity.form(form));

            Assertions.assertEquals(200, response.getStatus());
            String result = response.readEntity(String.class);
            Assertions.assertEquals("42:true:98.5", result);
        }
    }

    /**
     * Test traditional mutable class with property injection (no-arg constructor + setters).
     * This verifies backward compatibility - the PROPERTY injection path should still work.
     *
     * @tpTestDetails Test mutable class with no-arg constructor and setters
     * @tpSince RESTEasy 7.0.0
     */
    @Test
    public void testMutableClassPropertyInjection() throws Exception {
        try (ResteasyClient client = (ResteasyClient) ClientBuilder.newClient()) {
            Form form = new Form()
                    .param("name", "Alice")
                    .param("age", "25")
                    .param("email", "alice@example.com");

            Response response = client.target(generateURL("/record/mutable"))
                    .request()
                    .post(Entity.form(form));

            Assertions.assertEquals(200, response.getStatus());
            String result = response.readEntity(String.class);
            Assertions.assertEquals("Alice:25:alice@example.com", result);
        }
    }

}
