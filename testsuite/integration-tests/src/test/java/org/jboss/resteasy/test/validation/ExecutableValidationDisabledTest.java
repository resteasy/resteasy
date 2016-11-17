package org.jboss.resteasy.test.validation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.validation.resource.ValidationCoreClassConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCoreClassValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFoo;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithAllViolationTypes;
import org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithReturnValues;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test basic validation with disable executable-validation in validation.xml file. Validation should not be active.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ExecutableValidationDisabledTest {
    static final String RESPONSE_ERROR_MSG = "Response has wrong content";
    static final String WRONG_ERROR_MSG = "Expected validation error is not in response";
    ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ExecutableValidationDisabledTest.class.getSimpleName())
                .addClasses(ValidationCoreFoo.class, ValidationCoreFooConstraint.class, ValidationCoreFooReaderWriter.class, ValidationCoreFooValidator.class)
                .addClasses(ValidationCoreClassConstraint.class, ValidationCoreClassValidator.class)
                .addClasses(ValidationCoreResourceWithAllViolationTypes.class, ValidationCoreResourceWithReturnValues.class)
                .addAsResource("META-INF/services/javax.ws.rs.ext.Providers")
                .addAsResource(ExecutableValidationDisabledTest.class.getPackage(), "ExecutableValidationDisabledValidationDisabled.xml", "META-INF/validation.xml");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build().register(ValidationCoreFooReaderWriter.class);
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ExecutableValidationDisabledTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test disabled validation of returned value.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReturnValues() throws Exception {
        // Valid native constraint
        ValidationCoreFoo foo = new ValidationCoreFoo("a");
        Response response = client.target(generateURL("/return/native")).request().post(Entity.entity(foo, "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(RESPONSE_ERROR_MSG, foo, response.readEntity(ValidationCoreFoo.class));
        response.close();

        // Valid imposed constraint
        foo = new ValidationCoreFoo("abcde");
        response = client.target(generateURL("/return/imposed")).request().post(Entity.entity(foo, "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(RESPONSE_ERROR_MSG, foo, response.readEntity(ValidationCoreFoo.class));
        response.close();

        // Valid native and imposed constraints.
        foo = new ValidationCoreFoo("abc");
        response = client.target(generateURL("/return/nativeAndImposed")).request().post(Entity.entity(foo, "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(RESPONSE_ERROR_MSG, foo, response.readEntity(ValidationCoreFoo.class));
        response.close();

        {
            // Invalid native constraint
            // BUT EXECUTABLE VALIDATION IS DISABLE.
            foo = new ValidationCoreFoo("abcdef");
            response = client.target(generateURL("/return/native")).request().post(Entity.entity(foo, "application/foo"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(RESPONSE_ERROR_MSG, foo, response.readEntity(ValidationCoreFoo.class));
            response.close();
        }

        {
            // Invalid imposed constraint
            // BUT EXECUTABLE VALIDATION IS DISABLE.
            foo = new ValidationCoreFoo("abcdef");
            response = client.target(generateURL("/return/imposed")).request().post(Entity.entity(foo, "application/foo"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(RESPONSE_ERROR_MSG, foo, response.readEntity(ValidationCoreFoo.class));
            response.close();
        }

        {
            // Invalid native and imposed constraints
            // BUT EXECUTABLE VALIDATION IS DISABLE.
            foo = new ValidationCoreFoo("abcdef");
            response = client.target(generateURL("/return/nativeAndImposed")).request().post(Entity.entity(foo, "application/foo"));
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals(RESPONSE_ERROR_MSG, foo, response.readEntity(ValidationCoreFoo.class));
            response.close();
        }
    }

    /**
     * @tpTestDetails Test disabled validation before return value evaluation.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testViolationsBeforeReturnValue() throws Exception {
        // Valid
        ValidationCoreFoo foo = new ValidationCoreFoo("pqrs");
        Response response = client.target(generateURL("/all/abc/wxyz")).request().post(Entity.entity(foo, "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(RESPONSE_ERROR_MSG, foo, response.readEntity(ValidationCoreFoo.class));
        response.close();

        // Invalid: Should have 1 each of field, property, class, and parameter violations,
        //          and no return value violations.
        // BUT EXECUTABLE VALIDATION IS DISABLE. There will be no parameter violation.
        response = client.target(generateURL("/all/a/z")).request().post(Entity.entity(foo, "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assert.assertNotNull("Missing validation header", header);
        Assert.assertTrue("Wrong value of validation header", Boolean.valueOf(header));
        String entity = response.readEntity(String.class);
        ResteasyViolationException e = new ResteasyViolationException(entity);
        TestUtil.countViolations(e, 3, 1, 1, 1, 0, 0);
        ResteasyConstraintViolation violation = e.getFieldViolations().iterator().next();
        Assert.assertEquals(WRONG_ERROR_MSG, "size must be between 2 and 4", violation.getMessage());
        Assert.assertEquals(WRONG_ERROR_MSG, "a", violation.getValue());
        violation = e.getPropertyViolations().iterator().next();
        Assert.assertEquals(WRONG_ERROR_MSG, "size must be between 3 and 5", violation.getMessage());
        Assert.assertEquals(WRONG_ERROR_MSG, "z", violation.getValue());
        violation = e.getClassViolations().iterator().next();
        Assert.assertEquals(WRONG_ERROR_MSG, "Concatenation of s and t must have length > 5", violation.getMessage());
        Assert.assertTrue(WRONG_ERROR_MSG, violation.getValue().startsWith("org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithAllViolationTypes@"));
        response.close();
    }
}
