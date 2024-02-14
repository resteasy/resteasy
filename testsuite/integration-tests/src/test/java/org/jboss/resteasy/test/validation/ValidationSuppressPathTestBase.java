package org.jboss.resteasy.test.validation;

import java.util.Arrays;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.validation.ResteasyViolationExceptionImpl;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFoo;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class ValidationSuppressPathTestBase {
    static final String RESPONSE_ERROR_MSG = "Response has wrong content";
    static final String WRONG_ERROR_MSG = "Expected validation error is not in response";

    ResteasyClient client;

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient().register(ValidationCoreFooReaderWriter.class);
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    public void doTestInputViolations(String fieldPath, String propertyPath, String classPath, String... parameterPaths)
            throws Exception {
        ValidationCoreFoo foo = new ValidationCoreFoo("p");
        Response response = client.target(PortProviderUtil.generateURL("/all/a/z", "Validation-test")).request()
                .post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, "Validation header is missing");
        Assertions.assertTrue(Boolean.valueOf(header), "Wrong validation header");
        String entity = response.readEntity(String.class);
        ResteasyViolationException e = new ResteasyViolationExceptionImpl(entity);
        TestUtil.countViolations(e, 4, 2, 1, 1, 0);
        Assertions.assertNotNull(TestUtil.getViolationByPath(e.getPropertyViolations(), fieldPath), WRONG_ERROR_MSG);
        Assertions.assertNotNull(TestUtil.getViolationByPath(e.getPropertyViolations(), propertyPath), WRONG_ERROR_MSG);
        ResteasyConstraintViolation violation = e.getClassViolations().iterator().next();
        Assertions.assertEquals(classPath, violation.getPath(), WRONG_ERROR_MSG);
        violation = e.getParameterViolations().iterator().next();
        Assertions.assertTrue(Arrays.asList(parameterPaths).contains(violation.getPath()), WRONG_ERROR_MSG + parameterPaths);
        response.close();
    }

    public void doTestReturnValueViolations(String returnValuePath) throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/return/native", "Validation-test")).request()
                .post(Entity.entity(new ValidationCoreFoo("abcdef"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, "Validation header is missing");
        Assertions.assertTrue(Boolean.valueOf(header), "Wrong validation header");
        String entity = response.readEntity(String.class);
        ResteasyViolationException e = new ResteasyViolationExceptionImpl(entity);
        ResteasyConstraintViolation violation = e.getReturnValueViolations().iterator().next();
        Assertions.assertEquals(returnValuePath, violation.getPath(), WRONG_ERROR_MSG);
        response.close();
    }
}
