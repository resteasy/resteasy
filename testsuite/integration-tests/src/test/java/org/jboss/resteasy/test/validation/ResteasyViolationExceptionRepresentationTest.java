package org.jboss.resteasy.test.validation;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionConstraint;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionLengthConstraint;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionLengthValidator;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionMinMaxValidator;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionObject;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionReaderWriter;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionResourceWithFiveViolations;
import org.jboss.resteasy.test.validation.resource.ViolationExceptionResourceWithReturnValues;
import org.jboss.resteasy.util.HttpResponseCodes;
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

import static org.jboss.resteasy.utils.PortProviderUtil.generateURL;

/**
 * @tpSubChapter Validator provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResteasyViolationExceptionRepresentationTest {

    protected static final Logger logger = LogManager.getLogger(ResteasyViolationExceptionRepresentationTest.class.getName());
    static ResteasyClient client;

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

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
        client.register(ViolationExceptionReaderWriter.class);
    }

    @After
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Server send wrong content", foo, response.readEntity(ViolationExceptionObject.class));

        // Valid imposed constraint
        foo = new ViolationExceptionObject("abcde");
        response = client.target(generateURL("/imposed", TEST_RETURN_VALUES)).request().post(Entity.entity(foo, "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.bufferEntity();
        Assert.assertEquals("Server send wrong content", foo, response.readEntity(ViolationExceptionObject.class));

        // Valid native and imposed constraints.
        foo = new ViolationExceptionObject("abc");
        response = client.target(generateURL("/nativeAndImposed", TEST_RETURN_VALUES)).request()
                .post(Entity.entity(foo, "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Server send wrong content", foo, response.readEntity(ViolationExceptionObject.class));

        // Invalid native constraint
        response = client.target(generateURL("/native", TEST_RETURN_VALUES)).request()
                .post(Entity.entity(new ViolationExceptionObject("abcdef"), "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assert.assertNotNull("Header of response should not be null", header);
        Assert.assertTrue("Validation header is not correct", Boolean.valueOf(header));
        Object entity = response.readEntity(String.class);
        logger.info("Entity from response: " + entity);
        ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
        logger.info("Received exception: " + e.toString());
        TestUtil.countViolations(e, 1, 0, 0, 0, 0, 1);
        ResteasyConstraintViolation cv = e.getReturnValueViolations().iterator().next();
        Assert.assertEquals("Exception has wrong message", cv.getMessage(), "s must have length: 1 <= length <= 3");
        Assert.assertEquals("Exception has wrong value", "Foo[abcdef]", cv.getValue());

        // Invalid imposed constraint
        response = client.target(generateURL("/imposed", TEST_RETURN_VALUES)).request()
                .post(Entity.entity(new ViolationExceptionObject("abcdef"), "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assert.assertNotNull("Header of response should not be null", header);
        Assert.assertTrue("Validation header is not correct", Boolean.valueOf(header));
        entity = response.readEntity(String.class);
        logger.info("Entity from response: " + entity);
        e = new ResteasyViolationException(String.class.cast(entity));
        TestUtil.countViolations(e, 1, 0, 0, 0, 0, 1);
        cv = e.getReturnValueViolations().iterator().next();
        Assert.assertEquals("Exception has wrong message", cv.getMessage(), "s must have length: 3 <= length <= 5");
        Assert.assertEquals("Exception has wrong value", "Foo[abcdef]", cv.getValue());

        // Invalid native and imposed constraints
        response = client.target(generateURL("/nativeAndImposed", TEST_RETURN_VALUES)).request()
                .post(Entity.entity(new ViolationExceptionObject("abcdef"), "application/foo"));
        Assert.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assert.assertNotNull("Header of response should not be null", header);
        Assert.assertTrue("Validation header is not correct", Boolean.valueOf(header));
        entity = response.readEntity(String.class);
        logger.info("Entity from response: " + entity);
        e = new ResteasyViolationException(String.class.cast(entity));
        TestUtil.countViolations(e, 2, 0, 0, 0, 0, 2);
        Iterator<ResteasyConstraintViolation> it = e.getReturnValueViolations().iterator();
        ResteasyConstraintViolation cv1 = it.next();
        ResteasyConstraintViolation cv2 = it.next();
        if (!cv1.toString().contains("1")) {
            ResteasyConstraintViolation temp = cv1;
            cv1 = cv2;
            cv2 = temp;
        }
        Assert.assertEquals("Exception has wrong message", cv1.getMessage(), "s must have length: 1 <= length <= 3");
        Assert.assertEquals("Exception has wrong value", "Foo[abcdef]", cv1.getValue());
        Assert.assertEquals("Exception has wrong message", cv2.getMessage(), "s must have length: 3 <= length <= 5");
        Assert.assertEquals("Exception has wrong value", "Foo[abcdef]", cv2.getValue());
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Server send wrong content", foo, response.readEntity(ViolationExceptionObject.class));

        // Invalid: Should have 1 each of field, property, class, and parameter violations,
        //          and no return value violations.
        foo = new ViolationExceptionObject("p");
        response = client.target(generateURL("/a/z/unused/unused", TEST_VIOLATIONS_BEFORE_RETURN_VALUE)).request()
                .post(Entity.entity(foo, "application/foo"));
        logger.info("response: " + response);
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Object entity = response.readEntity(String.class);
        logger.info("entity: " + entity);
        String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assert.assertNotNull("Header of response should not be null", header);
        Assert.assertTrue("Validation header is not correct", Boolean.valueOf(header));
        ResteasyViolationException e = new ResteasyViolationException(String.class.cast(entity));
        logger.info("exception: " + e.toString());
        TestUtil.countViolations(e, 4, 1, 1, 1, 1, 0);
        ResteasyConstraintViolation cv = e.getFieldViolations().iterator().next();
        Assert.assertEquals("Exception has wrong message", "size must be between 2 and 4", cv.getMessage());
        Assert.assertEquals("Exception has wrong value", "a", cv.getValue());
        cv = e.getPropertyViolations().iterator().next();
        Assert.assertEquals("Exception has wrong message", "size must be between 3 and 5", cv.getMessage());
        Assert.assertEquals("Exception has wrong value", "z", cv.getValue());
        cv = e.getClassViolations().iterator().next();
        Assert.assertEquals("Exception has wrong message", "Concatenation of s and t must have length > 5", cv.getMessage());
        logger.info("value: " + cv.getValue());
        Assert.assertTrue("Exception has wrong value", cv.getValue().startsWith("org.jboss.resteasy.test.validation.resource.ViolationExceptionResourceWithFiveViolations@"));
        cv = e.getParameterViolations().iterator().next();
        Assert.assertEquals("Exception has wrong message", "s must have length: 3 <= length <= 5", cv.getMessage());
        Assert.assertEquals("Exception has wrong value", "Foo[p]", cv.getValue());
    }
}
