package org.jboss.resteasy.test.validation;

import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFoo;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.util.Arrays;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public class ValidationSuppressPathTestBase {
    static final String RESPONSE_ERROR_MSG = "Response has wrong content";
    static final String WRONG_ERROR_MSG = "Expected validation error is not in response";

    ResteasyClient client;

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build().register(ValidationCoreFooReaderWriter.class);
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    public void doTestInputViolations(String fieldPath, String propertyPath, String classPath, String... parameterPaths) throws Exception {
        ValidationCoreFoo foo = new ValidationCoreFoo("p");
        Response response = client.target(PortProviderUtil.generateURL("/all/a/z", "Validation-test")).request()
                .post(Entity.entity(foo, "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assert.assertNotNull("Validation header is missing", header);
        Assert.assertTrue("Wrong validation header", Boolean.valueOf(header));
        String entity = response.readEntity(String.class);
        ResteasyViolationException e = new ResteasyViolationException(entity);
        TestUtil.countViolations(e, 4, 1, 1, 1, 1, 0);
        ResteasyConstraintViolation violation = e.getFieldViolations().iterator().next();
        Assert.assertEquals(WRONG_ERROR_MSG, fieldPath, violation.getPath());
        violation = e.getPropertyViolations().iterator().next();
        Assert.assertEquals(WRONG_ERROR_MSG, propertyPath, violation.getPath());
        violation = e.getClassViolations().iterator().next();
        Assert.assertEquals(WRONG_ERROR_MSG, classPath, violation.getPath());
        violation = e.getParameterViolations().iterator().next();
        Assert.assertTrue(WRONG_ERROR_MSG + parameterPaths, Arrays.asList(parameterPaths).contains(violation.getPath()));
        response.close();
    }

    public void doTestReturnValueViolations(String returnValuePath) throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/return/native", "Validation-test")).request()
                .post(Entity.entity( new ValidationCoreFoo("abcdef"), "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assert.assertNotNull("Validation header is missing", header);
        Assert.assertTrue("Wrong validation header", Boolean.valueOf(header));
        String entity = response.readEntity(String.class);
        ResteasyViolationException e = new ResteasyViolationException(entity);
        ResteasyConstraintViolation violation = e.getReturnValueViolations().iterator().next();
        Assert.assertEquals(WRONG_ERROR_MSG, returnValuePath, violation.getPath());
        response.close();
    }
}
