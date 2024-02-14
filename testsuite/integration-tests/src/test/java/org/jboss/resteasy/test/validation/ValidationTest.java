package org.jboss.resteasy.test.validation;

import java.util.Iterator;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.ValidationClassConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationClassValidator;
import org.jboss.resteasy.test.validation.resource.ValidationFoo;
import org.jboss.resteasy.test.validation.resource.ValidationFooConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationFooReaderWriter;
import org.jboss.resteasy.test.validation.resource.ValidationFooValidator;
import org.jboss.resteasy.test.validation.resource.ValidationResourceWithAllViolationTypes;
import org.jboss.resteasy.test.validation.resource.ValidationResourceWithReturnValues;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Validator provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test - RESTEASY-1054
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ValidationTest {

    static Client client;
    protected final Logger logger = Logger.getLogger(ValidationTest.class.getName());
    private static final String ERR_ENTITY_MESSAGE = "The entity returned from the server is not the expected one";
    private static final String ERR_CONSTRAINT_MESSAGE = "The entity parameters are out of allowed values defined by validator";
    private static final String ERROR_HEADER_MESSAGE = "Header was null";
    private static final String ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE = "validation-exception header was expected to be true";

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ValidationTest.class.getSimpleName());
    }

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
        client.register(ValidationFooReaderWriter.class);
    }

    @AfterAll
    public static void cleanup() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ValidationTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ValidationResourceWithAllViolationTypes.class,
                ValidationResourceWithReturnValues.class, ValidationFooReaderWriter.class, ValidationFooValidator.class,
                ValidationFooConstraint.class, ValidationFoo.class, ValidationClassValidator.class,
                ValidationClassConstraint.class);
    }

    /**
     * @tpTestDetails Tests for Valid native constraint, Valid imposed constraint, Valid native and imposed constraints,
     *                Invalid native constraint, Invalid imposed constraint, Invalid native and imposed constraints
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReturnValues() throws Exception {
        // Valid native constraint
        WebTarget target = client.target(generateURL("/return/native"));
        ValidationFoo validationFoo = new ValidationFoo("a");
        Response response = target.request().post(Entity.entity(validationFoo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(validationFoo, response.readEntity(ValidationFoo.class),
                ERR_ENTITY_MESSAGE);

        // Valid imposed constraint
        target = client.target(generateURL("/return/imposed"));
        validationFoo = new ValidationFoo("abcde");
        response = target.request().post(Entity.entity(validationFoo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(validationFoo, response.readEntity(ValidationFoo.class),
                ERR_ENTITY_MESSAGE);

        // Valid native and imposed constraints.
        target = client.target(generateURL("/return/nativeAndImposed"));
        validationFoo = new ValidationFoo("abc");
        response = target.request().post(Entity.entity(validationFoo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(validationFoo, response.readEntity(ValidationFoo.class),
                ERR_ENTITY_MESSAGE);

        {
            // Invalid native constraint
            target = client.target(generateURL("/return/native"));
            response = target.request().accept(MediaType.APPLICATION_XML).post(Entity.entity(new ValidationFoo("abcdef"),
                    "application/foo"));
            ViolationReport r = response.readEntity(ViolationReport.class);
            logger.info("entity: " + r);
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
            Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
            Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
            ResteasyConstraintViolation violation = r.getReturnValueViolations().iterator().next();
            logger.info("violation: " + violation);
            Assertions.assertTrue(violation.getMessage().equals("s must have length: 1 <= length <= 3"),
                    ERR_CONSTRAINT_MESSAGE);
            Assertions.assertEquals("ValidationFoo[abcdef]", violation.getValue(),
                    ERR_ENTITY_MESSAGE);
        }

        {
            // Invalid imposed constraint
            target = client.target(generateURL("/return/imposed"));
            response = target.request().accept(MediaType.APPLICATION_XML).post(Entity.entity(new ValidationFoo("abcdef"),
                    "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
            Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
            Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
            ViolationReport r = response.readEntity(ViolationReport.class);
            logger.info("entity: " + r);
            TestUtil.countViolations(r, 0, 0, 0, 1);
            ResteasyConstraintViolation violation = r.getReturnValueViolations().iterator().next();
            logger.info("violation: " + violation);
            Assertions.assertTrue(violation.getMessage().equals("s must have length: 3 <= length <= 5"),
                    ERR_CONSTRAINT_MESSAGE);
            Assertions.assertEquals("ValidationFoo[abcdef]", violation.getValue(),
                    ERR_ENTITY_MESSAGE);
        }

        {
            // Invalid native and imposed constraints
            target = client.target(generateURL("/return/nativeAndImposed"));
            response = target.request().accept(MediaType.APPLICATION_XML).post(Entity.entity(new ValidationFoo("abcdef"),
                    "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
            Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
            Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
            ViolationReport r = response.readEntity(ViolationReport.class);
            logger.info("entity: " + r);
            TestUtil.countViolations(r, 0, 0, 0, 2);
            Iterator<ResteasyConstraintViolation> it = r.getReturnValueViolations().iterator();
            ResteasyConstraintViolation cv1 = it.next();
            ResteasyConstraintViolation cv2 = it.next();
            if (cv1.getMessage().indexOf('1') < 0) {
                ResteasyConstraintViolation temp = cv1;
                cv1 = cv2;
                cv2 = temp;
            }
            Assertions.assertTrue(cv1.getMessage().equals("s must have length: 1 <= length <= 3"),
                    ERR_CONSTRAINT_MESSAGE);
            Assertions.assertEquals("ValidationFoo[abcdef]", cv1.getValue(), ERR_ENTITY_MESSAGE);
            Assertions.assertTrue(cv2.getMessage().equals("s must have length: 3 <= length <= 5"),
                    ERR_CONSTRAINT_MESSAGE);
            Assertions.assertEquals("ValidationFoo[abcdef]", cv2.getValue(), ERR_ENTITY_MESSAGE);
        }
    }

    /**
     * @tpTestDetails Tests that resteasy correctly report expected constraint violations
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testViolationsBeforeReturnValue() throws Exception {
        // Valid
        WebTarget target = client.target(generateURL("/all/abc/wxyz"));
        ValidationFoo validationFoo = new ValidationFoo("pqrs");
        Response response = target.request().post(Entity.entity(validationFoo,
                "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(validationFoo, response.readEntity(ValidationFoo.class),
                ERR_ENTITY_MESSAGE);

        // Invalid: Should have 1 each of field, property, class, and parameter violations,
        //          and no return value violations.
        validationFoo = new ValidationFoo("p");
        target = client.target(generateURL("/all/a/z"));
        response = target.request().accept(MediaType.APPLICATION_XML).post(Entity.entity(validationFoo,
                "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Object header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assertions.assertTrue(header instanceof String, ERROR_HEADER_MESSAGE);
        Assertions.assertTrue(Boolean.valueOf(String.class.cast(header)), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
        ViolationReport r = response.readEntity(ViolationReport.class);
        logger.info("report: " + r);
        logger.info("testViolationsBeforeReturnValue(): exception:");
        TestUtil.countViolations(r, 2, 1, 1, 0);
        ResteasyConstraintViolation violation = TestUtil.getViolationByMessage(r.getPropertyViolations(),
                "size must be between 2 and 4");
        Assertions.assertNotNull(violation, ERR_CONSTRAINT_MESSAGE);
        Assertions.assertEquals("a", violation.getValue(), ERR_ENTITY_MESSAGE);
        violation = TestUtil.getViolationByMessage(r.getPropertyViolations(), "size must be between 3 and 5");
        Assertions.assertNotNull(violation, ERR_CONSTRAINT_MESSAGE);
        Assertions.assertEquals("z", violation.getValue(), ERR_ENTITY_MESSAGE);
        violation = r.getClassViolations().iterator().next();
        logger.info("violation: " + violation);
        Assertions.assertEquals("Concatenation of s and t must have length > 5",
                violation.getMessage(), ERR_CONSTRAINT_MESSAGE);
        logger.info("violation value: " + violation.getValue());
        Assertions.assertTrue(violation.getValue()
                .startsWith("org.jboss.resteasy.test.validation.resource.ValidationResourceWithAllViolationTypes@"));
        violation = r.getParameterViolations().iterator().next();
        logger.info("violation: " + violation);
        Assertions.assertEquals("s must have length: 3 <= length <= 5", violation.getMessage(),
                ERR_CONSTRAINT_MESSAGE);
        Assertions.assertEquals("ValidationFoo[p]", violation.getValue(),
                ERR_ENTITY_MESSAGE);
    }
}
