package org.jboss.resteasy.test.validation;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.validation.ResteasyViolationExceptionImpl;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.ValidationCoreClassConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCoreClassValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFoo;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithAllViolationTypes;
import org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithReturnValues;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test basic validation with disable executable-validation in validation.xml file. Validation should not be
 *                    active.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ExecutableValidationDisabledTest {
    static final String RESPONSE_ERROR_MSG = "Response has wrong content";
    static final String WRONG_ERROR_MSG = "Expected validation error is not in response";
    ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ExecutableValidationDisabledTest.class.getSimpleName())
                .addClasses(ValidationCoreFoo.class, ValidationCoreFooConstraint.class, ValidationCoreFooReaderWriter.class,
                        ValidationCoreFooValidator.class)
                .addClasses(ValidationCoreClassConstraint.class, ValidationCoreClassValidator.class)
                .addClasses(ValidationCoreResourceWithAllViolationTypes.class, ValidationCoreResourceWithReturnValues.class)
                .addAsResource("META-INF/services/jakarta.ws.rs.ext.Providers")
                .addAsResource(ExecutableValidationDisabledTest.class.getPackage(),
                        "ExecutableValidationDisabledValidationDisabled.xml", "META-INF/validation.xml");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient().register(ValidationCoreFooReaderWriter.class);
    }

    @AfterEach
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
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationCoreFoo.class), RESPONSE_ERROR_MSG);
        response.close();

        // Valid imposed constraint
        foo = new ValidationCoreFoo("abcde");
        response = client.target(generateURL("/return/imposed")).request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationCoreFoo.class), RESPONSE_ERROR_MSG);
        response.close();

        // Valid native and imposed constraints.
        foo = new ValidationCoreFoo("abc");
        response = client.target(generateURL("/return/nativeAndImposed")).request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationCoreFoo.class), RESPONSE_ERROR_MSG);
        response.close();

        {
            // Invalid native constraint
            // BUT EXECUTABLE VALIDATION IS DISABLE.
            foo = new ValidationCoreFoo("abcdef");
            response = client.target(generateURL("/return/native")).request().post(Entity.entity(foo, "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals(foo, response.readEntity(ValidationCoreFoo.class), RESPONSE_ERROR_MSG);
            response.close();
        }

        {
            // Invalid imposed constraint
            // BUT EXECUTABLE VALIDATION IS DISABLE.
            foo = new ValidationCoreFoo("abcdef");
            response = client.target(generateURL("/return/imposed")).request().post(Entity.entity(foo, "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals(foo, response.readEntity(ValidationCoreFoo.class), RESPONSE_ERROR_MSG);
            response.close();
        }

        {
            // Invalid native and imposed constraints
            // BUT EXECUTABLE VALIDATION IS DISABLE.
            foo = new ValidationCoreFoo("abcdef");
            response = client.target(generateURL("/return/nativeAndImposed")).request()
                    .post(Entity.entity(foo, "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals(foo, response.readEntity(ValidationCoreFoo.class), RESPONSE_ERROR_MSG);
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
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationCoreFoo.class), RESPONSE_ERROR_MSG);
        response.close();

        // Invalid: Should have 1 each of field, property, class, and parameter violations,
        //          and no return value violations.
        // BUT EXECUTABLE VALIDATION IS DISABLE. There will be no parameter violation.
        response = client.target(generateURL("/all/a/z")).request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, "Missing validation header");
        Assertions.assertTrue(Boolean.valueOf(header), "Wrong value of validation header");
        String entity = response.readEntity(String.class);
        ResteasyViolationException e = new ResteasyViolationExceptionImpl(entity);
        TestUtil.countViolations(e, 3, 2, 1, 0, 0);
        ResteasyConstraintViolation violation = TestUtil.getViolationByMessage(e.getPropertyViolations(),
                "size must be between 2 and 4");
        Assertions.assertNotNull(violation, WRONG_ERROR_MSG);
        Assertions.assertEquals("a", violation.getValue(), WRONG_ERROR_MSG);
        violation = TestUtil.getViolationByMessage(e.getPropertyViolations(), "size must be between 3 and 5");
        Assertions.assertNotNull(violation, WRONG_ERROR_MSG);
        Assertions.assertEquals("z", violation.getValue(), WRONG_ERROR_MSG);
        violation = e.getClassViolations().iterator().next();
        Assertions.assertEquals("Concatenation of s and t must have length > 5", violation.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertTrue(violation.getValue()
                .startsWith("org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithAllViolationTypes@"),
                WRONG_ERROR_MSG);
        response.close();
    }
}
