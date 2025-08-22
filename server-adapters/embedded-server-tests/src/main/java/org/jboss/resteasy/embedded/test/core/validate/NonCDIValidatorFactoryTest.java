/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.embedded.test.core.validate;

import java.util.Set;

import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;

/**
 * @tpSubChapter Verify ValidatorFactory is found in absence of CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-2549
 * @tpSince RESTEasy 3.14.0
 */
@RestBootstrap(NonCDIValidatorFactoryTest.TestApp.class)
public class NonCDIValidatorFactoryTest {

    public static class TestApp extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(TestResource.class);
        }
    }

    @Path("test")
    public static class TestResource {

        @GET
        @Path("validate/{n}")
        @Produces("text/plain")
        public Response string(@Size(min = 3) String s) {
            return Response.ok("ok").build();
        }
    }

    /// ///////////////////////////////////////////////////////////////////////////

    @Test
    public void testValidate(@RequestPath("/test/validate/x") final WebTarget target) {
        Invocation.Builder request = target.request();
        Response response = request.get();
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, "Validation header is missing");
        Assertions.assertTrue(Boolean.parseBoolean(header), "Wrong validation header");
        String entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(entity);
        countViolations(r, 0, 0, 1, 0);
    }

    private static void countViolations(final ViolationReport report, final int propertyCount, final int classCount,
            final int parameterCount,
            final int returnValueCount) {
        Assertions.assertEquals(propertyCount, report.getPropertyViolations().size(),
                () -> String.format("Expected %d property violations, found %d: %s", propertyCount,
                        report.getPropertyViolations()
                                .size(),
                        report.getPropertyViolations()));
        Assertions.assertEquals(classCount, report.getClassViolations().size(),
                () -> String.format("Expected %d class violations, found %d: %s", classCount, report.getClassViolations()
                        .size(), report.getClassViolations()));
        Assertions.assertEquals(parameterCount, report.getParameterViolations()
                .size(),
                () -> String.format("Expected %d parameter violations, found %d: %s", parameterCount,
                        report.getParameterViolations()
                                .size(),
                        report.getParameterViolations()));
        Assertions.assertEquals(returnValueCount, report.getReturnValueViolations()
                .size(),
                () -> String.format("Expected %d return value violations, found %d: %s", returnValueCount,
                        report.getReturnValueViolations()
                                .size(),
                        report.getReturnValueViolations()));
    }
}
