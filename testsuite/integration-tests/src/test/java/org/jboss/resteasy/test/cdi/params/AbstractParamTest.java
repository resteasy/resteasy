/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params;

import java.net.URI;
import java.util.Map;

import jakarta.json.JsonArray;
import jakarta.json.JsonValue;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.test.cdi.params.resources.CdiQualifierResource;
import org.jboss.resteasy.test.cdi.params.resources.ParamApplication;
import org.jboss.resteasy.test.cdi.params.resources.ParamDescriptor;
import org.jboss.resteasy.test.cdi.params.resources.ParamResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "7.0.3")
abstract class AbstractParamTest {

    private final String contextPath;
    private final JsonValue expectedBooleanValue;

    @ArquillianResource
    protected URI baseUri;

    AbstractParamTest(final String contextPath) {
        this(contextPath, JsonValue.TRUE);
    }

    AbstractParamTest(final String contextPath, final JsonValue expectedBooleanValue) {
        this.contextPath = contextPath;
        this.expectedBooleanValue = expectedBooleanValue;
    }

    static WebArchive defaultDeployment(final Class<? extends AbstractParamTest> testClass) {
        return ShrinkWrap.create(WebArchive.class, testClass.getSimpleName() + ".war")
                .addClasses(ParamApplication.class, ParamDescriptor.class, ParamResource.class, CdiQualifierResource.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
    }

    @Test
    void constructorParameters() {
        final ParamDescriptor param = invokeRequest("constructor");
        // Check the parameters
        Assertions.assertEquals("cookieParamValue", param.getCookieParam(),
                () -> "@CookieParam parameter was not set on the constructor: %s".formatted(param));
        Assertions.assertEquals("formParamValue", param.getFormParam(),
                () -> "@FormParam parameter was not set on the constructor: %s".formatted(param));
        Assertions.assertEquals("headerParamValue", param.getHeaderParam(),
                () -> "@HeaderParam parameter was not set on the constructor: %s".formatted(param));
        Assertions.assertEquals(10, param.getMatrixParam(),
                () -> "@MatrixParam parameter was not set on the constructor: %s".formatted(param));
        Assertions.assertEquals("constructorParameters", param.getPathParam(),
                () -> "@PathParam parameter was not set on the constructor: %s".formatted(param));
        Assertions.assertEquals(100, param.getQueryParam(),
                () -> "@QueryParam parameter was not set on the constructor: %s".formatted(param));
    }

    @Test
    void fieldParameters() {
        final ParamDescriptor param = invokeRequest("field/fieldParamValue");
        // Check the parameters
        Assertions.assertEquals("fieldCookieParamValue", param.getCookieParam(),
                () -> "@CookieParam parameter was not set on the field: %s".formatted(param));
        Assertions.assertEquals("fieldFormParamValue", param.getFormParam(),
                () -> "@FormParam parameter was not set on the field: %s".formatted(param));
        Assertions.assertEquals("fieldHeaderParamValue", param.getHeaderParam(),
                () -> "@HeaderParam parameter was not set on the field: %s".formatted(param));
        Assertions.assertEquals(20, param.getMatrixParam(),
                () -> "@MatrixParam parameter was not set on the field: %s".formatted(param));
        Assertions.assertEquals("fieldParamValue", param.getPathParam(),
                () -> "@PathParam parameter was not set on the field: %s".formatted(param));
        Assertions.assertEquals(200, param.getQueryParam(),
                () -> "@QueryParam parameter was not set on the field: %s".formatted(param));
    }

    @Test
    void methodParameters() {
        final ParamDescriptor param = invokeRequest("method/methodParamValue");
        // Check the parameters
        Assertions.assertEquals("methodCookieParamValue", param.getCookieParam(),
                () -> "@CookieParam parameter was not set on the method: %s".formatted(param));
        Assertions.assertEquals("methodFormParamValue", param.getFormParam(),
                () -> "@FormParam parameter was not set on the method: %s".formatted(param));
        Assertions.assertEquals("methodHeaderParamValue", param.getHeaderParam(),
                () -> "@HeaderParam parameter was not set on the method: %s".formatted(param));
        Assertions.assertEquals(30, param.getMatrixParam(),
                () -> "@MatrixParam parameter was not set on the method: %s".formatted(param));
        Assertions.assertEquals("methodParamValue", param.getPathParam(),
                () -> "@PathParam parameter was not set on the method: %s".formatted(param));
        Assertions.assertEquals(300, param.getQueryParam(),
                () -> "@QueryParam parameter was not set on the method: %s".formatted(param));
    }

    @Test
    void methodParametersAnnotated() {
        final ParamDescriptor param = invokeRequest("method-param/methodParamValuePa");
        // Check the parameters
        Assertions.assertEquals("methodCookieParamValuePa", param.getCookieParam(),
                () -> "@CookieParam parameter was not set on the method: %s".formatted(param));
        Assertions.assertEquals("methodFormParamValuePa", param.getFormParam(),
                () -> "@FormParam parameter was not set on the method: %s".formatted(param));
        Assertions.assertEquals("methodHeaderParamValuePa", param.getHeaderParam(),
                () -> "@HeaderParam parameter was not set on the method: %s".formatted(param));
        Assertions.assertEquals(40, param.getMatrixParam(),
                () -> "@MatrixParam parameter was not set on the method: %s".formatted(param));
        Assertions.assertEquals("methodParamValuePa", param.getPathParam(),
                () -> "@PathParam parameter was not set on the method: %s".formatted(param));
        Assertions.assertEquals(400, param.getQueryParam(),
                () -> "@QueryParam parameter was not set on the method: %s".formatted(param));
    }

    @Test
    void qualifierRegistered() {
        try (Client client = ClientBuilder.newClient()) {
            final URI uri = UriBuilder.fromUri(baseUri)
                    .path("check-qualifier")
                    .build();
            final JsonArray jsonArray = client.target(uri)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(JsonArray.class);
            for (JsonValue value : jsonArray) {
                for (Map.Entry<String, JsonValue> entry : value.asJsonObject().entrySet()) {
                    final String expectation = (entry.getValue() == JsonValue.TRUE) ? "be" : "not be";
                    Assertions.assertTrue((entry.getValue() == expectedBooleanValue),
                            () -> String.format("Annotation @%s should %s marked as a qualifier.", entry.getKey(),
                                    expectation));
                }
            }
        }
    }

    private ParamDescriptor invokeRequest(final String path) {
        try (Client client = ClientBuilder.newClient()) {
            final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri)
                    .path(contextPath + "/constructorParameters/" + path)
                    .matrixParam("matrixParam", 10)
                    .matrixParam("fieldMatrixParam", 20)
                    .matrixParam("methodMatrixParam", 30)
                    .matrixParam("methodMatrixParamPa", 40)
                    .queryParam("queryParam", 100)
                    .queryParam("fieldQueryParam", 200)
                    .queryParam("methodQueryParam", 300)
                    .queryParam("methodQueryParamPa", 400);
            final Form form = new Form()
                    .param("formParam", "formParamValue")
                    .param("fieldFormParam", "fieldFormParamValue")
                    .param("methodFormParam", "methodFormParamValue")
                    .param("methodFormParamPa", "methodFormParamValuePa");
            final WebTarget target = client.target(uriBuilder);
            final ParamDescriptor param = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .cookie("cookieParam", "cookieParamValue")
                    .cookie("fieldCookieParam", "fieldCookieParamValue")
                    .cookie("methodCookieParam", "methodCookieParamValue")
                    .cookie("methodCookieParamPa", "methodCookieParamValuePa")
                    .header("headerParam", "headerParamValue")
                    .header("fieldHeaderParam", "fieldHeaderParamValue")
                    .header("methodHeaderParam", "methodHeaderParamValue")
                    .header("methodHeaderParamPa", "methodHeaderParamValuePa")
                    .post(Entity.form(form), ParamDescriptor.class);
            Assertions.assertNotNull(param, () -> "Failed to find the parameters with URI %s".formatted(uriBuilder.build()));
            return param;
        }
    }

}
