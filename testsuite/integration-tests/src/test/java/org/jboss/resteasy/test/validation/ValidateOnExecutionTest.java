package org.jboss.resteasy.test.validation;

import static org.jboss.resteasy.utils.PortProviderUtil.generateURL;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.validation.ResteasyViolationExceptionImpl;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.TestValidateOnExecutionErrorOneLevel_Class;
import org.jboss.resteasy.test.validation.resource.TestValidateOnExecutionErrorOneLevel_Interface;
import org.jboss.resteasy.test.validation.resource.TestValidateOnExecutionErrorTwoLevels_Class;
import org.jboss.resteasy.test.validation.resource.TestValidateOnExecutionErrorTwoLevels_Interface;
import org.jboss.resteasy.test.validation.resource.TestValidateOnExecutionErrorWithGenericMethodSubClass;
import org.jboss.resteasy.test.validation.resource.TestValidateOnExecutionErrorWithGenericMethodSuperClass;
import org.jboss.resteasy.test.validation.resource.TestValidateOnExecutionErrorWithGenericSupermethod;
import org.jboss.resteasy.test.validation.resource.TestValidateOnExecutionInterface;
import org.jboss.resteasy.test.validation.resource.TestValidateOnExecutionInterfaceWithGenericSupermethod;
import org.jboss.resteasy.test.validation.resource.TestValidateOnExecutionResource;
import org.jboss.resteasy.test.validation.resource.TestValidateOnExecutionSubInterface;
import org.jboss.resteasy.test.validation.resource.TestValidateOnExecutionSubResource;
import org.jboss.resteasy.test.validation.resource.TestValidationOnExecuteSubInterface;
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
public class ValidateOnExecutionTest {

    static ResteasyClient client;

    protected static final Logger logger = Logger.getLogger(ValidateOnExecutionTest.class.getName());

    private static final String MAIN = "main";
    private static final String INVALID_ONE_LEVEL_CLASS = "one_class";
    private static final String INVALID_TWO_LEVEL_CLASS = "two_class";
    private static final String INVALID_ONE_LEVEL_INTERFACE = "one_interface";
    private static final String INVALID_TWO_LEVEL_INTERFACE = "two_interface";
    private static final String INVALID_GENERIC_OVERRIDE_CLASS = "override_class";
    private static final String INVALID_GENERIC_OVERRIDE_INTERFACE = "override_interface";

    @ArquillianResource
    private Deployer deployer;

    public static Archive<?> deploy(String name, Class<?>... resourceClasses) throws Exception {
        WebArchive war = TestUtil.prepareArchive(name);
        war.addClass(ValidateOnExecutionTest.class);
        war.addClass(TestValidateOnExecutionErrorWithGenericMethodSuperClass.class);
        war.addClass(TestValidateOnExecutionInterface.class);
        war.addClass(TestValidateOnExecutionInterfaceWithGenericSupermethod.class);
        war.addClass(TestValidateOnExecutionResource.class);
        war.addClass(TestValidateOnExecutionSubInterface.class);
        war.addClass(TestValidationOnExecuteSubInterface.class);
        return TestUtil.finishContainerPrepare(war, null, resourceClasses);
    }

    @Deployment(name = MAIN)
    public static Archive<?> mainDeploy() throws Exception {
        return deploy(MAIN, TestValidateOnExecutionSubResource.class);
    }

    @Deployment(name = INVALID_ONE_LEVEL_CLASS, managed = false)
    public static Archive<?> oneClassDeploy() throws Exception {
        return deploy(INVALID_ONE_LEVEL_CLASS, TestValidateOnExecutionErrorOneLevel_Class.class);
    }

    @Deployment(name = INVALID_TWO_LEVEL_CLASS, managed = false)
    public static Archive<?> twoClassDeploy() throws Exception {
        return deploy(INVALID_TWO_LEVEL_CLASS, TestValidateOnExecutionErrorTwoLevels_Class.class,
                TestValidateOnExecutionSubResource.class);
    }

    @Deployment(name = INVALID_ONE_LEVEL_INTERFACE, managed = false)
    public static Archive<?> oneInterfaceDeploy() throws Exception {
        return deploy(INVALID_ONE_LEVEL_INTERFACE, TestValidateOnExecutionErrorOneLevel_Interface.class);
    }

    @Deployment(name = INVALID_TWO_LEVEL_INTERFACE, managed = false)
    public static Archive<?> twoInterfaceDeploy() throws Exception {
        return deploy(INVALID_TWO_LEVEL_INTERFACE, TestValidateOnExecutionErrorTwoLevels_Interface.class);
    }

    @Deployment(name = INVALID_GENERIC_OVERRIDE_CLASS, managed = false)
    public static Archive<?> overrideClassDeploy() throws Exception {
        return deploy(INVALID_GENERIC_OVERRIDE_CLASS, TestValidateOnExecutionErrorWithGenericMethodSubClass.class);
    }

    @Deployment(name = INVALID_GENERIC_OVERRIDE_INTERFACE, managed = false)
    public static Archive<?> overrideInterfaceDeploy() throws Exception {
        return deploy(INVALID_GENERIC_OVERRIDE_INTERFACE, TestValidateOnExecutionErrorWithGenericSupermethod.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client sends various requests. Validation exceptions is expected.
     * @tpPassCrit Violation count should be correct according to resource definition.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(MAIN)
    public void testValidateOnExecution() throws Exception {
        {
            // No method validation. Two property violations.
            Response response = client.target(generateURL("/none", MAIN)).request().post(
                    Entity.entity("abc", MediaType.TEXT_PLAIN_TYPE));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(entity));
            logger.info(String.format("Violation exception: %s", e).replace('\r', ' '));
            TestUtil.countViolations(e, 2, 2, 0, 0, 0);
        }

        {
            // No method validation. Two property violations.
            Response response = client.target(generateURL("/getterOnNonGetter", MAIN)).request().post(
                    Entity.entity("abc", MediaType.TEXT_PLAIN_TYPE));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(entity));
            logger.info(String.format("Violation exception: %s", e).replace('\r', ' '));
            TestUtil.countViolations(e, 2, 2, 0, 0, 0);
        }

        {
            // No method validation. Two property violations
            Response response = client.target(generateURL("/nonGetterOnGetter", MAIN)).request().post(
                    Entity.text(new String()));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(entity));
            logger.info(String.format("Violation exception: %s", e).replace('\r', ' '));
            TestUtil.countViolations(e, 2, 2, 0, 0, 0);
        }

        {
            // Failure.
            Response response = client.target(generateURL("/implicitOnNonGetter", MAIN)).request().post(
                    Entity.entity("abc", MediaType.TEXT_PLAIN_TYPE));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(entity));
            logger.info(String.format("Violation exception: %s", e).replace('\r', ' '));
            TestUtil.countViolations(e, 3, 2, 0, 1, 0);
        }

        {
            Response response = client.target(generateURL("/implicitOnGetter", MAIN)).request().post(
                    Entity.text(new String()));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(entity));
            logger.info(String.format("Violation exception: %s", e).replace('\r', ' '));
            TestUtil.countViolations(e, 2, 2, 0, 0, 0);
        }

        {
            Response response = client.target(generateURL("/allOnNonGetter", MAIN)).request().post(
                    Entity.entity("abc", MediaType.TEXT_PLAIN_TYPE));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(entity));
            logger.info(String.format("Violation exception: %s", e).replace('\r', ' '));
            TestUtil.countViolations(e, 3, 2, 0, 1, 0);
        }

        {
            Response response = client.target(generateURL("/allOnGetter", MAIN)).request().post(
                    Entity.text(new String()));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(entity));
            logger.info(String.format("Violation exception: %s", e).replace('\r', ' '));
            TestUtil.countViolations(e, 2, 2, 0, 0, 0);
        }

        {
            // Failure.
            Response response = client.target(generateURL("/override", MAIN)).request().post(
                    Entity.entity("abc", MediaType.TEXT_PLAIN_TYPE));
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            String entity = response.readEntity(String.class);
            ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(entity));
            logger.info(String.format("Violation exception: %s", e).replace('\r', ' '));
            TestUtil.countViolations(e, 3, 2, 0, 1, 0);
        }
    }

    /**
     * @tpTestDetails Try to deploy invalid one level class.
     * @tpPassCrit ResteasyViolationException should be throws during deploying.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testValidateOnExecutionInvalidOneLevel_Class() throws Exception {
        testErrorExpected(INVALID_ONE_LEVEL_CLASS);
    }

    /**
     * @tpTestDetails Try to deploy invalid two level class.
     * @tpPassCrit ResteasyViolationException should be throws during deploying.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testValidateOnExecutionInvalidTwoLevels_Class() throws Exception {
        testErrorExpected(INVALID_TWO_LEVEL_CLASS);
    }

    /**
     * @tpTestDetails Try to deploy invalid one level interface.
     * @tpPassCrit ResteasyViolationException should be throws during deploying.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testValidateOnExecutionInvalidOneLevel_Interface() throws Exception {
        testErrorExpected(INVALID_ONE_LEVEL_INTERFACE);
    }

    /**
     * @tpTestDetails Try to deploy invalid two level interface.
     * @tpPassCrit ResteasyViolationException should be throws during deploying.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testValidateOnExecutionInvalidTwoLevels_Interface() throws Exception {
        testErrorExpected(INVALID_TWO_LEVEL_INTERFACE);
    }

    /**
     * @tpTestDetails Try to deploy invalid class (error is in overriding).
     * @tpPassCrit ResteasyViolationException should be throws during deploying.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testValidateOnExecutionInvalidGenericOverride_Class() throws Exception {
        testErrorExpected(INVALID_GENERIC_OVERRIDE_CLASS);
    }

    /**
     * @tpTestDetails Try to deploy invalid interface (error is in overriding).
     * @tpPassCrit ResteasyViolationException should be throws during deploying.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testValidateOnExecutionInvalidGenericOverride_Interface() throws Exception {
        testErrorExpected(INVALID_GENERIC_OVERRIDE_INTERFACE);
    }

    /**
     * Try to deploy deployment. ValidationException is expected.
     */
    private void testErrorExpected(String deploymentName) {
        try {
            deployer.deploy(deploymentName);
            Assertions.fail(String.format("ValidationException expected on %s deployment", deploymentName));
        } catch (ValidationException ve) {
            // OK
        } catch (Exception e) {
            if (e.getMessage().contains("Caused by: jakarta.validation.ValidationException") ||
                    (e.getCause() != null
                            && e.getCause().getMessage().contains("Caused by: jakarta.validation.ValidationException"))) {
                // OK
                return;
            }
            Assertions.fail("Unexpected exception: " + e);
        }
    }
}
