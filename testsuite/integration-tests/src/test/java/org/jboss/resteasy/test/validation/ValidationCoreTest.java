package org.jboss.resteasy.test.validation;

import java.util.Iterator;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
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
 * @tpTestCaseDetails Regression test for RESTEASY-923
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ValidationCoreTest {
    static final String RESPONSE_ERROR_MSG = "Response has wrong content";
    static final String WRONG_ERROR_MSG = "Expected validation error is not in response";
    ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ValidationCoreTest.class.getSimpleName())
                .addClasses(ValidationCoreFoo.class, ValidationCoreFooConstraint.class, ValidationCoreFooReaderWriter.class,
                        ValidationCoreFooValidator.class)
                .addClasses(ValidationCoreClassConstraint.class, ValidationCoreClassValidator.class)
                .addClasses(ValidationCoreResourceWithAllViolationTypes.class, ValidationCoreResourceWithReturnValues.class)
                .addAsResource("META-INF/services/jakarta.ws.rs.ext.Providers");
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
        return PortProviderUtil.generateURL(path, ValidationCoreTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test native, imposed and both validation of return values. Also test negative scenarios.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReturnValues() throws Exception {
        ValidationCoreFoo foo = new ValidationCoreFoo("a");
        Assertions.assertNotNull(client);
        Response response = client.target(generateURL("/return/native")).request().post(Entity.entity(foo, "application/foo"));
        // Valid native constraint
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationCoreFoo.class),
                RESPONSE_ERROR_MSG);
        response.close();

        // Valid imposed constraint
        foo = new ValidationCoreFoo("abcde");
        response = client.target(generateURL("/return/imposed")).request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationCoreFoo.class),
                RESPONSE_ERROR_MSG);
        response.close();

        // Valid native and imposed constraints.
        foo = new ValidationCoreFoo("abc");
        response = client.target(generateURL("/return/nativeAndImposed")).request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationCoreFoo.class),
                RESPONSE_ERROR_MSG);
        response.close();

        {
            // Invalid native constraint
            response = client.target(generateURL("/return/native")).request()
                    .post(Entity.entity(new ValidationCoreFoo("abcdef"), "application/foo"));
            String entity = response.readEntity(String.class);
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String header = response.getHeaderString(Validation.VALIDATION_HEADER);
            Assertions.assertNotNull("Validation header is missing", header);
            Assertions.assertTrue(Boolean.valueOf(header), "Wrong validation header");
            ResteasyViolationException e = new ResteasyViolationExceptionImpl(entity);
            ResteasyConstraintViolation violation = e.getReturnValueViolations().iterator().next();
            Assertions.assertTrue(violation.getMessage().equals("s must have length: 1 <= length <= 3"),
                    WRONG_ERROR_MSG);
            Assertions.assertEquals("ValidationCoreFoo[abcdef]", violation.getValue(),
                    WRONG_ERROR_MSG);
            response.close();
        }

        {
            // Invalid imposed constraint
            response = client.target(generateURL("/return/imposed")).request()
                    .post(Entity.entity(new ValidationCoreFoo("abcdef"), "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String header = response.getHeaderString(Validation.VALIDATION_HEADER);
            Assertions.assertNotNull(header, "Validation header is missing");
            Assertions.assertTrue(Boolean.valueOf(header), "Wrong validation header");
            String entity = response.readEntity(String.class);
            ViolationReport r = new ViolationReport(entity);
            TestUtil.countViolations(r, 0, 0, 0, 1);
            ResteasyConstraintViolation violation = r.getReturnValueViolations().iterator().next();
            Assertions.assertTrue(violation.getMessage().equals("s must have length: 3 <= length <= 5"),
                    WRONG_ERROR_MSG);
            Assertions.assertEquals("ValidationCoreFoo[abcdef]", violation.getValue(),
                    WRONG_ERROR_MSG);
            response.close();
        }

        {
            // Invalid native and imposed constraints
            response = client.target(generateURL("/return/nativeAndImposed")).request()
                    .post(Entity.entity(new ValidationCoreFoo("abcdef"), "application/foo"));
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String header = response.getHeaderString(Validation.VALIDATION_HEADER);
            Assertions.assertNotNull(header, "Validation header is missing");
            Assertions.assertTrue(Boolean.valueOf(header), "Wrong validation header");
            String entity = response.readEntity(String.class);
            ViolationReport r = new ViolationReport(entity);
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
                    WRONG_ERROR_MSG);
            Assertions.assertEquals("ValidationCoreFoo[abcdef]", cv1.getValue(), WRONG_ERROR_MSG);
            Assertions.assertTrue(cv2.getMessage().equals("s must have length: 3 <= length <= 5"),
                    WRONG_ERROR_MSG);
            Assertions.assertEquals("ValidationCoreFoo[abcdef]", cv2.getValue(), WRONG_ERROR_MSG);
        }
    }

    /**
     * @tpTestDetails Test violations before returning some value.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testViolationsBeforeReturnValue() throws Exception {
        // Valid
        ValidationCoreFoo foo = new ValidationCoreFoo("pqrs");
        Response response = client.target(generateURL("/all/abc/wxyz")).request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationCoreFoo.class),
                RESPONSE_ERROR_MSG);
        response.close();

        // Invalid: Should have 1 each of field, property, class, and parameter violations,
        //          and no return value violations.
        foo = new ValidationCoreFoo("p");
        response = client.target(generateURL("/all/a/z")).request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, "Validation header is missing");
        Assertions.assertTrue(Boolean.valueOf(header), "Wrong validation header");
        String entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(entity);
        TestUtil.countViolations(r, 2, 1, 1, 0);
        ResteasyConstraintViolation violation = TestUtil.getViolationByMessage(r.getPropertyViolations(),
                "size must be between 2 and 4");
        Assertions.assertNotNull(violation, WRONG_ERROR_MSG);
        Assertions.assertEquals("a", violation.getValue(), WRONG_ERROR_MSG);
        violation = TestUtil.getViolationByMessage(r.getPropertyViolations(), "size must be between 3 and 5");
        Assertions.assertNotNull(violation, WRONG_ERROR_MSG);
        Assertions.assertEquals("z", violation.getValue(), WRONG_ERROR_MSG);
        violation = r.getClassViolations().iterator().next();
        Assertions.assertEquals("Concatenation of s and t must have length > 5", violation.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertTrue(violation.getValue()
                .startsWith("org.jboss.resteasy.test.validation.resource.ValidationCoreResourceWithAllViolationTypes@"),
                WRONG_ERROR_MSG);
        violation = r.getParameterViolations().iterator().next();
        Assertions.assertEquals("s must have length: 3 <= length <= 5", violation.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationCoreFoo[p]", violation.getValue(),
                WRONG_ERROR_MSG);
        response.close();
    }
}
