package org.jboss.resteasy.test.validation;

import static org.jboss.resteasy.utils.PortProviderUtil.generateURL;

import java.util.List;

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
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.validation.ResteasyViolationExceptionImpl;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionClassValidator;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionCrazyConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionCrazyValidator;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionIncorrectConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionMapper;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionOtherConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionOtherValidationException;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionOtherValidationException2;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionOtherValidationException3;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionOtherValidator;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionResourceCrazy;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionResourceWithIncorrectConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionResourceWithInvalidConstraintGroup;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionResourceWithOther;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionSubResourceWithInvalidOverride;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionSuperResource;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionTestGroup1;
import org.jboss.resteasy.test.validation.resource.ValidationExceptionTestGroup2;
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
public class ValidationExceptionsTest {

    protected static final Logger logger = Logger.getLogger(ValidationExceptionsTest.class.getName());
    static ResteasyClient client;

    private static final String DEF_EXCEPTION = "constraintDefinitionException";
    private static final String CUSTOM_DEF_EXCEPTION = "customConstraintDefinitionException";
    private static final String DECL_EXCEPTION = "constraintDeclarationException";
    private static final String CUSTOM_DECL_EXCEPTION = "customConstraintDeclarationException";
    private static final String GROUP_DEF_EXCEPTION = "groupDefinitionException";
    private static final String CUSTOM_GROUP_DEF_EXCEPTION = "customGroupDefinitionException";
    private static final String OTHER_EXCEPTION = "otherException";
    private static final String CRAZY_EXCEPTION = "crazyException";
    private static final String ERROR_MESSAGE = "Expected other response";
    private static final String ERROR_HEADER_MESSAGE = "Header was null";
    private static final String ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE = "validation-expcetion header was expected to be true";

    public static Archive<?> deploy(String name, Class<?>... resourceClasses) throws Exception {
        WebArchive war = TestUtil.prepareArchive(name);
        war.addClass(ValidationExceptionClassValidator.class);
        war.addClass(ValidationExceptionCrazyConstraint.class);
        war.addClass(ValidationExceptionCrazyValidator.class);
        war.addClass(ValidationExceptionIncorrectConstraint.class);
        war.addClass(ValidationExceptionOtherConstraint.class);
        war.addClass(ValidationExceptionOtherValidationException.class);
        war.addClass(ValidationExceptionOtherValidationException2.class);
        war.addClass(ValidationExceptionOtherValidationException3.class);
        war.addClass(ValidationExceptionOtherValidator.class);
        war.addClass(ValidationExceptionTestGroup1.class);
        war.addClass(ValidationExceptionTestGroup2.class);
        war.addClass(ValidationExceptionMapper.class);
        return TestUtil.finishContainerPrepare(war, null, resourceClasses);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    @Deployment(name = DEF_EXCEPTION)
    public static Archive<?> constraintDefinitionExceptionDeploy() throws Exception {
        return deploy(DEF_EXCEPTION, ValidationExceptionResourceWithIncorrectConstraint.class);
    }

    @Deployment(name = CUSTOM_DEF_EXCEPTION)
    public static Archive<?> customConstraintDefinitionExceptionDeploy() throws Exception {
        return deploy(CUSTOM_DEF_EXCEPTION, ValidationExceptionResourceWithIncorrectConstraint.class,
                ValidationExceptionResourceWithIncorrectConstraint.ConstraintDefinitionExceptionMapper.class);
    }

    @Deployment(name = DECL_EXCEPTION)
    public static Archive<?> constraintDeclarationExceptionDeploy() throws Exception {
        return deploy(DECL_EXCEPTION, ValidationExceptionSubResourceWithInvalidOverride.class,
                ValidationExceptionSuperResource.class);
    }

    @Deployment(name = CUSTOM_DECL_EXCEPTION)
    public static Archive<?> customConstraintDeclarationExceptionDeploy() throws Exception {
        return deploy(CUSTOM_DECL_EXCEPTION, ValidationExceptionSubResourceWithInvalidOverride.class,
                ValidationExceptionSuperResource.class,
                ValidationExceptionSubResourceWithInvalidOverride.ConstraintDeclarationExceptionMapper.class);
    }

    @Deployment(name = GROUP_DEF_EXCEPTION)
    public static Archive<?> groupDefinitionExceptionDeploy() throws Exception {
        return deploy(GROUP_DEF_EXCEPTION, ValidationExceptionResourceWithInvalidConstraintGroup.class);
    }

    @Deployment(name = CUSTOM_GROUP_DEF_EXCEPTION)
    public static Archive<?> customGroupDefinitionExceptionDeploy() throws Exception {
        return deploy(CUSTOM_GROUP_DEF_EXCEPTION, ValidationExceptionResourceWithInvalidConstraintGroup.class,
                ValidationExceptionResourceWithInvalidConstraintGroup.GroupDefinitionExceptionMapper.class);
    }

    @Deployment(name = OTHER_EXCEPTION)
    public static Archive<?> otherExceptionDeploy() throws Exception {
        return deploy(OTHER_EXCEPTION, ValidationExceptionResourceWithOther.class);
    }

    @Deployment(name = CRAZY_EXCEPTION)
    public static Archive<?> crazyExceptionDeploy() throws Exception {
        return deploy(CRAZY_EXCEPTION, ValidationExceptionResourceCrazy.class);
    }

    /**
     * @tpTestDetails Resource with incorrect constraint, constrain definitiont exception is expected
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(DEF_EXCEPTION)
    public void testConstraintDefinitionException() throws Exception {
        Response response = client.target(generateURL("/", DEF_EXCEPTION)).request().post(null);
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
        Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
        String entity = response.readEntity(String.class);
        logger.info("entity: " + entity);
        Assertions.assertTrue(entity.contains("ConstraintDefinitionException"), ERROR_MESSAGE);
    }

    @Test
    @OperateOnDeployment(CUSTOM_DEF_EXCEPTION)
    public void testCustomConstraintDefinitionException() throws Exception {
        Response response = client.target(generateURL("/", CUSTOM_DEF_EXCEPTION)).request().post(null);
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
        Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
        String entity = response.readEntity(String.class);
        logger.info("entity: " + entity);
        Assertions.assertTrue(entity.contains("ConstraintDefinitionException"), ERROR_MESSAGE);
        Assertions.assertTrue(
                entity.contains(ValidationExceptionResourceWithIncorrectConstraint.ConstraintDefinitionExceptionMapper.class
                        .getName()),
                ERROR_MESSAGE);
    }

    /**
     * @tpTestDetails Resource with incorrect constraint declaration, constraint definition exception is expected
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(DECL_EXCEPTION)
    public void testConstraintDeclarationException() throws Exception {
        Response response = client.target(generateURL("/", DECL_EXCEPTION)).request().post(null);
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR,
                response.getStatus(),
                TestUtil.getErrorMessageForKnownIssue("JBEAP-3459"));
        String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
        Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
        String entity = response.readEntity(String.class);
        logger.info("entity: " + entity);
        Assertions.assertTrue(entity.contains("ConstraintDeclarationException"), ERROR_MESSAGE);
    }

    @Test
    @OperateOnDeployment(CUSTOM_DECL_EXCEPTION)
    public void testCustomConstraintDeclarationException() throws Exception {
        Response response = client.target(generateURL("/", CUSTOM_DECL_EXCEPTION)).request().post(null);
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus(),
                TestUtil.getErrorMessageForKnownIssue("JBEAP-3459"));
        String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
        Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
        String entity = response.readEntity(String.class);
        logger.info("entity: " + entity);
        Assertions.assertTrue(entity.contains("ConstraintDeclarationException"), ERROR_MESSAGE);
        Assertions.assertTrue(
                entity.contains(ValidationExceptionSubResourceWithInvalidOverride.ConstraintDeclarationExceptionMapper.class
                        .getName()),
                ERROR_MESSAGE);
    }

    /**
     * @tpTestDetails Resource with incorrect group definition, group definition exception is expected
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(GROUP_DEF_EXCEPTION)
    public void testGroupDefinitionException() throws Exception {
        Response response = client.target(generateURL("/", GROUP_DEF_EXCEPTION)).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
        Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
        String entity = response.readEntity(String.class);
        logger.info("entity: " + entity);
        Assertions.assertTrue(entity.contains("GroupDefinitionException"), ERROR_MESSAGE);
    }

    @Test
    @OperateOnDeployment(CUSTOM_GROUP_DEF_EXCEPTION)
    public void testCustomGroupDefinitionException() throws Exception {
        Response response = client.target(generateURL("/", CUSTOM_GROUP_DEF_EXCEPTION)).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
        Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
        String entity = response.readEntity(String.class);
        logger.info("entity: " + entity);
        Assertions.assertTrue(entity.contains("GroupDefinitionException"), ERROR_MESSAGE);
        Assertions.assertTrue(entity.contains(
                ValidationExceptionResourceWithInvalidConstraintGroup.GroupDefinitionExceptionMapper.class.getName()),
                ERROR_MESSAGE);
    }

    /**
     * @tpTestDetails Tests for: Exception thrown during validation of field, Exception thrown during validation of parameter,
     *                Exception thrown during validation of return value, Exception thrown by resource method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(OTHER_EXCEPTION)
    public void testOtherValidationException() throws Exception {

        {
            Response response = client.target(generateURL("/parameter/fail", OTHER_EXCEPTION)).request()
                    .post(Entity.text("abc"));
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
            Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
            Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
            String entity = response.readEntity(String.class);
            logger.info("entity: " + entity);
            Assertions.assertTrue(entity.contains("ValidationException"), ERROR_MESSAGE);
            Assertions.assertTrue(entity.contains("OtherValidationException"), ERROR_MESSAGE);
        }

        {
            Response response = client.target(generateURL("/parameter/ok", OTHER_EXCEPTION)).request().post(Entity.text("abc"));
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
            Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
            Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
            String entity = response.readEntity(String.class);
            logger.info("entity: " + entity);
            Assertions.assertTrue(entity.contains("ValidationException"), ERROR_MESSAGE);
            Assertions.assertTrue(entity.contains("OtherValidationException"), ERROR_MESSAGE);
        }

        {
            Response response = client.target(generateURL("/return/ok", OTHER_EXCEPTION)).request().post(Entity.text("abc"));
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
            Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
            Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
            String entity = response.readEntity(String.class);
            logger.info("entity: " + entity);
            Assertions.assertTrue(entity.contains("ValidationException"), ERROR_MESSAGE);
            Assertions.assertTrue(entity.contains("OtherValidationException"), ERROR_MESSAGE);
        }

        {
            Response response = client.target(generateURL("/execution/ok", OTHER_EXCEPTION)).request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
            Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
            Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
            String entity = response.readEntity(String.class);
            logger.info("last entity: " + entity);
            Assertions.assertTrue(entity.contains("OtherValidationException"), ERROR_MESSAGE);
            Assertions.assertTrue(entity.contains("OtherValidationException2"), ERROR_MESSAGE);
            Assertions.assertTrue(entity.contains("OtherValidationException3"), ERROR_MESSAGE);
        }

    }

    /**
     * @tpTestDetails Resource with crazy message in constraint
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment(CRAZY_EXCEPTION)
    public void testCrazyMessage() throws Exception {
        Response response = client.target(generateURL("/", CRAZY_EXCEPTION)).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String header = response.getStringHeaders().getFirst(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, ERROR_HEADER_MESSAGE);
        Assertions.assertTrue(Boolean.valueOf(header), ERROR_HEADER_VALIDATION_EXCEPTION_MESSAGE);
        ResteasyViolationException resteasyViolationException = new ResteasyViolationExceptionImpl(
                response.readEntity(String.class));
        List<ResteasyConstraintViolation> classViolations = resteasyViolationException.getClassViolations();
        Assertions.assertEquals(1, classViolations.size());
        Assertions.assertEquals(classViolations.get(0).getMessage(), ValidationExceptionCrazyConstraint.DEFAULT_MESSAGE);
        logger.info("entity: " + resteasyViolationException);
    }
}
