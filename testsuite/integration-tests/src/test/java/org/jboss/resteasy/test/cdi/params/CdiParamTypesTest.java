/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.cdi.params;

import java.net.URI;
import java.util.List;
import java.util.Set;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.test.cdi.params.resources.CdiParamTypesResource;
import org.jboss.resteasy.test.cdi.params.resources.ParamApplication;
import org.jboss.resteasy.test.cdi.params.resources.ParamEnum;
import org.jboss.resteasy.test.cdi.params.resources.TypesDescriptor;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests various {@code @*Param} injections work with various required types.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ArquillianTest
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "6.2.17")
@ServerSetup(EnableEnhancedCdiSupportSetupTask.class)
class CdiParamTypesTest {

    @ArquillianResource
    private URI baseUri;

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, CdiParamTypesTest.class.getSimpleName() + ".war")
                .addClasses(ParamApplication.class, CdiParamTypesResource.class,
                        TypesDescriptor.class, ParamEnum.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
    }

    @Test
    void allTypes() {
        try (Client client = ClientBuilder.newClient()) {
            final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri)
                    .path("types/path1/path2")
                    .queryParam("list", "a", "b", "c")
                    .queryParam("set", "x", "y", "z")
                    .queryParam("sortedSet", "c", "a", "b")
                    .queryParam("array", "one", "two")
                    .queryParam("wrapper", 99)
                    .queryParam("bool", true)
                    .queryParam("enum", "VALUE_ONE")
                    .queryParam("default", "explicit")
                    .queryParam("defaultInt", 7);
            final TypesDescriptor result = client.target(uriBuilder)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(TypesDescriptor.class);
            Assertions.assertNotNull(result);

            // Collection types
            Assertions.assertNotNull(result.getListParam(), () -> String.format("List should not be null: %s", result));
            Assertions.assertEquals(3, result.getListParam().size(),
                    () -> String.format("Expected list size of 3: %s", result));
            Assertions.assertTrue(result.getListParam().containsAll(List.of("a", "b", "c")),
                    () -> String.format("Expected list to contain [a, b, c]: %s", result));

            Assertions.assertNotNull(result.getSetParam(),
                    () -> String.format("Set should not be null: %s", result));
            Assertions.assertEquals(3, result.getSetParam().size(),
                    () -> String.format("Expected set size of 3: %s", result));
            Assertions.assertTrue(result.getSetParam().containsAll(Set.of("x", "y", "z")),
                    () -> String.format("Expected set to contain [x, y, z]: %s", result));

            Assertions.assertNotNull(result.getSortedSetParam(),
                    () -> String.format("SortedSet should not be null: %s", result));
            Assertions.assertEquals(3, result.getSortedSetParam().size(),
                    () -> String.format("Expected sortedSet size of 3: %s", result));
            Assertions.assertEquals("a", result.getSortedSetParam().first(),
                    () -> String.format("SortedSet should be sorted, expected 'a' first: %s", result));

            // Array
            Assertions.assertNotNull(result.getArrayParam(),
                    () -> String.format("Array should not be null: %s", result));
            Assertions.assertArrayEquals(new String[] { "one", "two" }, result.getArrayParam(),
                    () -> String.format("Expected array [one, two]: %s", result));

            // Wrapper and primitive types
            Assertions.assertEquals(99, result.getWrapperParam(),
                    () -> String.format("Expected wrapper value of 99: %s", result));
            Assertions.assertEquals(true, result.getBooleanParam(),
                    () -> String.format("Expected boolean value of true: %s", result));

            // Enum
            Assertions.assertEquals(ParamEnum.VALUE_ONE, result.getEnumParam(),
                    () -> String.format("Expected enum VALUE_ONE: %s", result));

            // Explicit values override @DefaultValue
            Assertions.assertEquals("explicit", result.getDefaultParam(),
                    () -> String.format("Expected default string to be 'explicit': %s", result));
            Assertions.assertEquals(7, result.getDefaultIntParam(),
                    () -> String.format("Expected default int to be 7: %s", result));

            // PathSegment
            final List<String> listPaths = result.getListPaths();
            Assertions.assertEquals(2, listPaths.size(), () -> String.format("Expected two PathSegments in %s", result));
            Assertions.assertEquals("path1", listPaths.get(0),
                    () -> String.format("Expected the first PathSegment to be path1 in %s", result));
            Assertions.assertEquals("path2", listPaths.get(1),
                    () -> String.format("Expected the first PathSegment to be path2 in %s", result));
            Assertions.assertEquals("path2", result.getSinglePath(),
                    () -> String.format("Expected the single PathSegment to be path2 in %s", result));
        }
    }

    @Test
    void defaultValues() {
        try (Client client = ClientBuilder.newClient()) {
            final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri)
                    .path("types/ignored");
            final TypesDescriptor result = client.target(uriBuilder)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(TypesDescriptor.class);
            Assertions.assertNotNull(result);

            // @DefaultValue should apply when params are absent
            Assertions.assertEquals("fallback", result.getDefaultParam(),
                    () -> String.format("Expected default string 'fallback' when param is absent: %s", result));
            Assertions.assertEquals(42, result.getDefaultIntParam(),
                    () -> String.format("Expected default int 42 when param is absent: %s", result));

            // Absent optional params without @DefaultValue
            Assertions.assertNull(result.getWrapperParam(),
                    () -> String.format("Absent wrapper should be null: %s", result));
            Assertions.assertNull(result.getBooleanParam(),
                    () -> String.format("Absent boolean should be null: %s", result));
            Assertions.assertNull(result.getEnumParam(),
                    () -> String.format("Absent enum should be null: %s", result));
        }
    }
}
