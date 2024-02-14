package org.jboss.resteasy.test.validation;

import static org.jboss.resteasy.utils.PortProviderUtil.generateURL;

import java.util.Iterator;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.plugins.validation.ResteasyViolationExceptionImpl;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionConstraint;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionLengthConstraint;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionLengthValidator;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionMinMaxValidator;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionObject;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionReaderWriter;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionResourceWithFiveViolations;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionResourceWithReturnValues;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Validator provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResteasyViolationExceptionRepresentationTest {

    protected static final Logger logger = Logger.getLogger(ResteasyViolationExceptionRepresentationTest.class.getName());
    static Client client;

    private static final String TEST_VIOLATIONS_BEFORE_RETURN_VALUE = "violations_test";
    private static final String TEST_RETURN_VALUES = "return_value";

    public static Archive<?> deploy(Class<?> resourceClass, String name) throws Exception {
        WebArchive war = TestUtil.prepareArchive(name);
        war.addClass(ResteasyViolationExceptionRepresentationTest.class);
        war.addClass(ViolationExceptionConstraint.class);
        war.addClass(ViolationExceptionLengthConstraint.class);
        war.addClass(ViolationExceptionLengthValidator.class);
        war.addClass(ViolationExceptionMinMaxValidator.class);
        war.addClass(ViolationExceptionObject.class);
        return TestUtil.finishContainerPrepare(war, null, resourceClass, ViolationExceptionReaderWriter.class);
    }

    @Deployment(name = TEST_RETURN_VALUES)
    public static Archive<?> testReturnValuesDeploy() throws Exception {
        return deploy(ViolationExceptionResourceWithReturnValues.class, TEST_RETURN_VALUES);
    }

    @Deployment(name = TEST_VIOLATIONS_BEFORE_RETURN_VALUE)
    public static Archive<?> testViolationsBeforeReturnValueDeploy() throws Exception {
        return deploy(ViolationExceptionResourceWithFiveViolations.class, TEST_VIOLATIONS_BEFORE_RETURN_VALUE);
    }

    @BeforeEach
    public void init() {
        client = ClientBuilder.newClient();
        client.register(ViolationExceptionReaderWriter.class);
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check correct number of return value violations.
     * @tpPassCrit Violation count should be correct according to resource definition.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(TEST_RETURN_VALUES)
    public void testReturnValues() throws Exception {
        // Valid native constraint
        ViolationExceptionObject foo = new ViolationExceptionObject("a");
        Response response = client.target(generateURL("/native", TEST_RETURN_VALUES)).request()
                .post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ViolationExceptionObject.class),
                "Server send wrong content");

        // Valid imposed constraint
        foo = new ViolationExceptionObject("abcde");
        response = client.target(generateURL("/imposed", TEST_RETURN_VALUES)).request()
                .post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.bufferEntity();
        Assertions.assertEquals(foo, response.readEntity(ViolationExceptionObject.class),
                "Server send wrong content");

        // Valid native and imposed constraints.
        foo = new ViolationExceptionObject("abc");
        response = client.target(generateURL("/nativeAndImposed", TEST_RETURN_VALUES)).request()
                .post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ViolationExceptionObject.class),
                "Server send wrong content");

        // Invalid native constraint
        response = client.target(generateURL("/native", TEST_RETURN_VALUES)).request()
                .post(Entity.entity(new ViolationExceptionObject("abcdef"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, "Header of response should not be null");
        Assertions.assertTrue(Boolean.valueOf(header), "Validation header is not correct");
        Object entity = response.readEntity(String.class);
        logger.info("Entity from response: " + entity);
        ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(entity));
        logger.info("Received exception: " + e.toString());
        TestUtil.countViolations(e, 1, 0, 0, 0, 1);
        ResteasyConstraintViolation cv = e.getReturnValueViolations().iterator().next();
        Assertions.assertEquals(cv.getMessage(), "s must have length: 1 <= length <= 3",
                "Exception has wrong message");
        Assertions.assertEquals("Foo[abcdef]", cv.getValue(), "Exception has wrong value");

        // Invalid imposed constraint
        response = client.target(generateURL("/imposed", TEST_RETURN_VALUES)).request()
                .post(Entity.entity(new ViolationExceptionObject("abcdef"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, "Header of response should not be null");
        Assertions.assertTrue(Boolean.valueOf(header), "Validation header is not correct");
        entity = response.readEntity(String.class);
        logger.info("Entity from response: " + entity);
        e = new ResteasyViolationExceptionImpl(String.class.cast(entity));
        TestUtil.countViolations(e, 1, 0, 0, 0, 1);
        cv = e.getReturnValueViolations().iterator().next();
        Assertions.assertEquals(cv.getMessage(), "s must have length: 3 <= length <= 5",
                "Exception has wrong message");
        Assertions.assertEquals("Foo[abcdef]", cv.getValue(), "Exception has wrong value");

        // Invalid native and imposed constraints
        response = client.target(generateURL("/nativeAndImposed", TEST_RETURN_VALUES)).request()
                .post(Entity.entity(new ViolationExceptionObject("abcdef"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, "Header of response should not be null");
        Assertions.assertTrue(Boolean.valueOf(header), "Validation header is not correct");
        entity = response.readEntity(String.class);
        logger.info("Entity from response: " + entity);
        e = new ResteasyViolationExceptionImpl(String.class.cast(entity));
        TestUtil.countViolations(e, 2, 0, 0, 0, 2);
        Iterator<ResteasyConstraintViolation> it = e.getReturnValueViolations().iterator();
        ResteasyConstraintViolation cv1 = it.next();
        ResteasyConstraintViolation cv2 = it.next();
        if (!cv1.toString().contains("1")) {
            ResteasyConstraintViolation temp = cv1;
            cv1 = cv2;
            cv2 = temp;
        }
        Assertions.assertEquals(cv1.getMessage(), "s must have length: 1 <= length <= 3",
                "Exception has wrong message");
        Assertions.assertEquals("Foo[abcdef]", cv1.getValue(), "Exception has wrong value");
        Assertions.assertEquals(cv2.getMessage(), "s must have length: 3 <= length <= 5",
                "Exception has wrong message");
        Assertions.assertEquals("Foo[abcdef]", cv2.getValue(), "Exception has wrong value");
    }

    /**
     * @tpTestDetails Check correct number of violations before return in resource.
     * @tpPassCrit Violation count should be correct according to resource definition.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(TEST_VIOLATIONS_BEFORE_RETURN_VALUE)
    public void testViolationsBeforeReturnValue() throws Exception {
        // Valid
        ViolationExceptionObject foo = new ViolationExceptionObject("pqrs");
        Response response = client.target(generateURL("/abc/wxyz/unused/unused", TEST_VIOLATIONS_BEFORE_RETURN_VALUE)).request()
                .post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ViolationExceptionObject.class),
                "Server send wrong content");

        // Invalid: Should have 1 each of field, property, class, and parameter violations,
        //          and no return value violations.
        foo = new ViolationExceptionObject("p");
        response = client.target(generateURL("/a/z/unused/unused", TEST_VIOLATIONS_BEFORE_RETURN_VALUE)).request()
                .post(Entity.entity(foo, "application/foo"));
        logger.info("response: " + response);
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Object entity = response.readEntity(String.class);
        logger.info("entity: " + entity);
        String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, "Header of response should not be null");
        Assertions.assertTrue(Boolean.valueOf(header), "Validation header is not correct");
        ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(entity));
        logger.info("exception: " + e.toString());
        TestUtil.countViolations(e, 4, 2, 1, 1, 0);
        ResteasyConstraintViolation violation = TestUtil.getViolationByMessage(e.getPropertyViolations(),
                "size must be between 2 and 4");
        Assertions.assertNotNull(violation, "Exception has wrong message");
        Assertions.assertEquals("a", violation.getValue(), "Exception has wrong value");
        violation = TestUtil.getViolationByMessage(e.getPropertyViolations(), "size must be between 3 and 5");
        Assertions.assertNotNull(violation, "Exception has wrong message");
        Assertions.assertEquals("z", violation.getValue(), "Exception has wrong value");
        ResteasyConstraintViolation cv = e.getClassViolations().iterator().next();
        Assertions.assertEquals("Concatenation of s and t must have length > 5",
                cv.getMessage(), "Exception has wrong message");
        logger.info("value: " + cv.getValue());
        Assertions.assertTrue(
                cv.getValue().startsWith(
                        "org.jboss.resteasy.test.validation.resource.ViolationExceptionResourceWithFiveViolations@"),
                "Exception has wrong value");
        cv = e.getParameterViolations().iterator().next();
        Assertions.assertEquals("s must have length: 3 <= length <= 5", cv.getMessage(),
                "Exception has wrong message");
        Assertions.assertEquals("Foo[p]", cv.getValue(), "Exception has wrong value");
    }
}
