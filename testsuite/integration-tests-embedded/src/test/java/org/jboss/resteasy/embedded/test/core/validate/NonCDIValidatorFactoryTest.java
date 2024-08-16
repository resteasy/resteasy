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
import org.jboss.resteasy.utils.TestUtil;
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

    //////////////////////////////////////////////////////////////////////////////

    @Test
    public void testValidate(@RequestPath("test/validate/x") final WebTarget target) {
        Invocation.Builder request = target.request();
        Response response = request.get();
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, "Validation header is missing");
        Assertions.assertTrue(Boolean.valueOf(header), "Wrong validation header");
        String entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(entity);
        TestUtil.countViolations(r, 0, 0, 1, 0);
    }
}
