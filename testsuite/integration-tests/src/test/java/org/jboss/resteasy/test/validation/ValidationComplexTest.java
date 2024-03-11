package org.jboss.resteasy.test.validation;

import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.logging.LoggingPermission;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.ValidationComplexA;
import org.jboss.resteasy.test.validation.resource.ValidationComplexArrayOfStrings;
import org.jboss.resteasy.test.validation.resource.ValidationComplexB;
import org.jboss.resteasy.test.validation.resource.ValidationComplexClassConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationComplexClassConstraint2;
import org.jboss.resteasy.test.validation.resource.ValidationComplexClassInheritanceSubConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationComplexClassInheritanceSuperConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationComplexClassValidator;
import org.jboss.resteasy.test.validation.resource.ValidationComplexClassValidator2;
import org.jboss.resteasy.test.validation.resource.ValidationComplexClassValidatorSubInheritance;
import org.jboss.resteasy.test.validation.resource.ValidationComplexClassValidatorSuperInheritance;
import org.jboss.resteasy.test.validation.resource.ValidationComplexCrossParameterConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationComplexCrossParameterValidator;
import org.jboss.resteasy.test.validation.resource.ValidationComplexFoo;
import org.jboss.resteasy.test.validation.resource.ValidationComplexFooConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationComplexFooReaderWriter;
import org.jboss.resteasy.test.validation.resource.ValidationComplexFooValidator;
import org.jboss.resteasy.test.validation.resource.ValidationComplexInterface;
import org.jboss.resteasy.test.validation.resource.ValidationComplexInterfaceSub;
import org.jboss.resteasy.test.validation.resource.ValidationComplexInterfaceSuper;
import org.jboss.resteasy.test.validation.resource.ValidationComplexListOfArrayOfStrings;
import org.jboss.resteasy.test.validation.resource.ValidationComplexListOfStrings;
import org.jboss.resteasy.test.validation.resource.ValidationComplexMapOfListOfArrayOfStrings;
import org.jboss.resteasy.test.validation.resource.ValidationComplexMapOfStrings;
import org.jboss.resteasy.test.validation.resource.ValidationComplexOneString;
import org.jboss.resteasy.test.validation.resource.ValidationComplexOtherGroup;
import org.jboss.resteasy.test.validation.resource.ValidationComplexOtherGroupConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationComplexOtherGroupValidator;
import org.jboss.resteasy.test.validation.resource.ValidationComplexProxyInterface;
import org.jboss.resteasy.test.validation.resource.ValidationComplexProxyResource;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithAllFivePotentialViolations;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithArray;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithClassConstraint;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithClassConstraintInterface;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithFieldAndProperty;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithGraph;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithInvalidField;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithList;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithMap;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithMapOfListOfArrayOfStrings;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithOtherGroups;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithParameters;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithProperty;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithReturnValues;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithSubLocators;
import org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithValidField;
import org.jboss.resteasy.test.validation.resource.ValidationComplexSubResourceWithCrossParameterConstraint;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.TimeoutUtil;
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
 * @tpTestCaseDetails Complex basic test for Resteasy Validator Provider
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
public class ValidationComplexTest {

    static final String RESPONSE_ERROR_MSG = "Response has wrong content";
    static final String WRONG_ERROR_MSG = "Expected validation error is not in response";

    private static Logger logger = Logger.getLogger(ValidationComplexTest.class);

    private static final String BASIC_DEPLOYMENT = "basicDeployment";
    private static final String CUSTOM_OBJECT_DEPLOYMENT = "customObjectDeployment";
    private static final String ASYNC_CUSTOM_OBJECT_DEPLOYMENT = "asyncCustomObjectDeployment";

    ResteasyClient client;

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient().register(ValidationComplexFooReaderWriter.class);
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private static String generateURL(String type, String path, String testName) {
        return PortProviderUtil.generateURL(path, String.format("%s%s", type, testName));
    }

    public static WebArchive addBasicClasses(WebArchive war) {
        war.addClasses(ValidationComplexA.class, ValidationComplexArrayOfStrings.class, ValidationComplexB.class,
                ValidationComplexMapOfStrings.class,
                ValidationComplexListOfStrings.class, ValidationComplexMapOfListOfArrayOfStrings.class,
                ValidationComplexOneString.class,
                ValidationComplexListOfArrayOfStrings.class, ValidationComplexProxyInterface.class,
                ValidationComplexOtherGroupConstraint.class, ValidationComplexOtherGroup.class,
                ValidationComplexClassValidator.class, ValidationComplexClassInheritanceSubConstraint.class,
                ValidationComplexClassValidatorSubInheritance.class,
                ValidationComplexInterfaceSuper.class, ValidationComplexInterface.class,
                ValidationComplexCrossParameterConstraint.class,
                ValidationComplexCrossParameterValidator.class, ValidationComplexFooReaderWriter.class,
                ValidationComplexFoo.class, ValidationComplexClassConstraint.class,
                ValidationComplexInterfaceSub.class, ValidationComplexClassInheritanceSuperConstraint.class,
                ValidationComplexClassValidatorSuperInheritance.class,
                ValidationComplexClassConstraint2.class, ValidationComplexClassValidator2.class,
                ValidationComplexOtherGroupValidator.class);
        // Arquillian in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new LoggingPermission("control", ""),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("node", "read"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new PropertyPermission("quarkus.tester", "read"),
                new ReflectPermission("suppressAccessChecks"),
                new RuntimePermission("accessDeclaredMembers"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve")), "permissions.xml");
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        return war;
    }

    public static WebArchive addCustomObjectClasses(WebArchive war) {
        war.addClasses(ValidationComplexFoo.class, ValidationComplexResourceWithAllFivePotentialViolations.class,
                ValidationComplexResourceWithValidField.class, ValidationComplexResourceWithInvalidField.class,
                ValidationComplexResourceWithProperty.class, ValidationComplexFooConstraint.class,
                ValidationComplexFooValidator.class);
        return war;
    }

    public static Archive<?> basicDeployment(Class<?>... clazz) {
        WebArchive war = TestUtil.prepareArchive(BASIC_DEPLOYMENT + clazz[0].getSimpleName());
        war = addBasicClasses(war);
        return TestUtil.finishContainerPrepare(war, null, clazz);
    }

    public static Archive<?> customObjectDeployment(Class<?> clazz) {
        WebArchive war = TestUtil.prepareArchive(CUSTOM_OBJECT_DEPLOYMENT + clazz.getSimpleName());
        war = addBasicClasses(war);
        war = addCustomObjectClasses(war);
        return TestUtil.finishContainerPrepare(war, null, clazz, ValidationComplexFooReaderWriter.class);
    }

    public static Archive<?> asyncCustomObjectDeployment(Class<?> clazz) {
        WebArchive war = TestUtil.prepareArchive(ASYNC_CUSTOM_OBJECT_DEPLOYMENT + clazz.getSimpleName());
        war = addBasicClasses(war);
        war = addCustomObjectClasses(war);
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put("resteasy.async.job.service.enabled", "true");
        return TestUtil.finishContainerPrepare(war, contextParams, clazz, ValidationComplexFooReaderWriter.class);
    }

    @Deployment(name = "basicDeploymentTestResourceWithValidField")
    public static Archive<?> basicDeploymentTestResourceWithValidField() {
        return basicDeployment(ValidationComplexResourceWithValidField.class);
    }

    @Deployment(name = "basicDeploymentTestResourceWithInvalidField")
    public static Archive<?> basicDeploymentTestResourceWithInvalidField() {
        return basicDeployment(ValidationComplexResourceWithInvalidField.class);
    }

    @Deployment(name = "basicDeploymentTestResourceWithProperty")
    public static Archive<?> basicDeploymentTestResourceWithProperty() {
        return basicDeployment(ValidationComplexResourceWithProperty.class);
    }

    @Deployment(name = "basicDeploymentTestResourceWithFieldAndProperty")
    public static Archive<?> basicDeploymentTestResourceWithFieldAndProperty() {
        return basicDeployment(ValidationComplexResourceWithFieldAndProperty.class);
    }

    @Deployment(name = "basicDeploymentTestResourceWithClassConstraint")
    public static Archive<?> basicDeploymentTestResourceWithClassConstraint() {
        return basicDeployment(ValidationComplexResourceWithClassConstraint.class,
                ValidationComplexResourceWithClassConstraintInterface.class);
    }

    @Deployment(name = "basicDeploymentTestResourceWithGraph")
    public static Archive<?> basicDeploymentTestResourceWithGraph() {
        return basicDeployment(ValidationComplexResourceWithGraph.class);
    }

    @Deployment(name = "basicDeploymentTestResourceWithArray")
    public static Archive<?> basicDeploymentTestResourceWithArray() {
        return basicDeployment(ValidationComplexResourceWithArray.class);
    }

    @Deployment(name = "basicDeploymentTestResourceWithMap")
    public static Archive<?> basicDeploymentTestResourceWithMap() {
        return basicDeployment(ValidationComplexResourceWithMap.class);
    }

    @Deployment(name = "basicDeploymentTestResourceWithList")
    public static Archive<?> basicDeploymentTestResourceWithList() {
        return basicDeployment(ValidationComplexResourceWithList.class);
    }

    @Deployment(name = "basicDeploymentTestResourceWithMapOfListOfArrayOfStrings")
    public static Archive<?> basicDeploymentTestResourceWithMapOfListOfArrayOfStrings() {
        return basicDeployment(ValidationComplexResourceWithMapOfListOfArrayOfStrings.class);
    }

    @Deployment(name = "customObjectDeploymentTestResourceWithParameters")
    public static Archive<?> customObjectDeploymentTestResourceWithParameters() {
        return customObjectDeployment(ValidationComplexResourceWithParameters.class);
    }

    @Deployment(name = "customObjectDeploymentTestResourceWithReturnValues")
    public static Archive<?> customObjectDeploymentTestResourceWithReturnValues() {
        return customObjectDeployment(ValidationComplexResourceWithReturnValues.class);
    }

    @Deployment(name = "customObjectDeploymentTestResourceWithAllFivePotentialViolations")
    public static Archive<?> customObjectDeploymentTestResourceWithAllFivePotentialViolations() {
        return customObjectDeployment(ValidationComplexResourceWithAllFivePotentialViolations.class);
    }

    @Deployment(name = "basicDeploymentInterfaceTestSub")
    public static Archive<?> basicDeploymentInterfaceTestSub() {
        return basicDeployment(ValidationComplexInterfaceSub.class);
    }

    @Deployment(name = "customObjectDeploymentTestResourceWithSubLocators")
    public static Archive<?> customObjectDeploymentTestResourceWithSubLocators() {
        return customObjectDeployment(ValidationComplexResourceWithSubLocators.class);
    }

    @Deployment(name = "asyncCustomObjectDeploymentTestResourceWithAllFivePotentialViolations")
    public static Archive<?> asyncCustomObjectDeploymentTestResourceWithAllFivePotentialViolations() {
        return asyncCustomObjectDeployment(ValidationComplexResourceWithAllFivePotentialViolations.class);
    }

    @Deployment(name = "basicDeploymentTestSubResourceWithCrossParameterConstraint")
    public static Archive<?> basicDeploymentTestSubResourceWithCrossParameterConstraint() {
        return basicDeployment(ValidationComplexSubResourceWithCrossParameterConstraint.class);
    }

    @Deployment(name = "basicDeploymentTestProxyResource")
    public static Archive<?> basicDeploymentTestProxyResource() {
        return basicDeployment(ValidationComplexProxyResource.class);
    }

    @Deployment(name = "basicDeploymentTestResourceWithOtherGroups")
    public static Archive<?> basicDeploymentTestResourceWithOtherGroups() {
        return basicDeployment(ValidationComplexResourceWithOtherGroups.class,
                ValidationComplexResourceWithClassConstraintInterface.class);
    }

    /**
     * @tpTestDetails Valid field test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFieldValid() throws Exception {
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/", ValidationComplexResourceWithValidField.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Invalid field test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFieldInvalid() throws Exception {
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/", ValidationComplexResourceWithInvalidField.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Object entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 1, 0, 0, 0);
        ResteasyConstraintViolation cv = r.getPropertyViolations().iterator().next();
        logger.info("cv: " + cv);
        Assertions.assertEquals("size must be between 2 and 4", cv.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("abcde", cv.getValue(), WRONG_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Valid property test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPropertyValid() throws Exception {
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/abc/unused",
                        ValidationComplexResourceWithProperty.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Invalid property test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPropertyInvalid() throws Exception {
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/abcdef/unused",
                        ValidationComplexResourceWithProperty.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(entity);
        TestUtil.countViolations(r, 1, 0, 0, 0);
        ResteasyConstraintViolation cv = r.getPropertyViolations().iterator().next();
        Assertions.assertEquals("size must be between 2 and 4", cv.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("abcdef", cv.getValue(), WRONG_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Field and property validation test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFieldAndProperty() throws Exception {
        // Valid
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/abc/wxyz",
                        ValidationComplexResourceWithFieldAndProperty.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Invalid
        response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/a/uvwxyz",
                        ValidationComplexResourceWithFieldAndProperty.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(entity);
        TestUtil.countViolations(r, 2, 0, 0, 0);
        ResteasyConstraintViolation cv = TestUtil.getViolationByMessage(r.getPropertyViolations(),
                "size must be between 2 and 4");
        Assertions.assertNotNull(cv, WRONG_ERROR_MSG);
        Assertions.assertEquals("a", cv.getValue(), RESPONSE_ERROR_MSG);
        cv = TestUtil.getViolationByMessage(r.getPropertyViolations(), "size must be between 3 and 5");
        Assertions.assertNotNull(cv, WRONG_ERROR_MSG);
        Assertions.assertEquals("uvwxyz", cv.getValue(), RESPONSE_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Check class constraints
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testClassConstraint() throws Exception {
        // Valid
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/abc/xyz",
                        ValidationComplexResourceWithClassConstraint.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Invalid
        response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/a/b",
                        ValidationComplexResourceWithClassConstraint.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(entity);
        TestUtil.countViolations(r, 0, 1, 0, 0);
        ResteasyConstraintViolation cv = r.getClassViolations().iterator().next();
        Assertions.assertEquals("Concatenation of s and t must have length > 5", cv.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationComplexResourceWithClassConstraint(\"a\", \"b\")", cv.getValue(),
                WRONG_ERROR_MSG);
        logger.info(cv.getValue());
        response.close();
    }

    /**
     * @tpTestDetails Graph test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGraph() throws Exception {
        // Valid
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/abcd/vwxyz", ValidationComplexResourceWithGraph.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Invalid
        response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/abc/xyz", ValidationComplexResourceWithGraph.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Object entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 2, 0, 0, 0);

        Iterator<ResteasyConstraintViolation> it = r.getPropertyViolations().iterator();
        ResteasyConstraintViolation cv1 = it.next();
        ResteasyConstraintViolation cv2 = it.next();
        if (cv1.getValue().equals("xyz")) {
            ResteasyConstraintViolation tmp = cv1;
            cv1 = cv2;
            cv2 = tmp;
        }
        Assertions.assertTrue(cv1.getMessage().startsWith("size must be between 4 and"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("abc", cv1.getValue(), WRONG_ERROR_MSG);
        Assertions.assertTrue(cv2.getMessage().startsWith("size must be between 5 and"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("xyz", cv2.getValue(), WRONG_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Validation of array
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testArray() throws Exception {
        // Valid
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/abcde", ValidationComplexResourceWithArray.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Invalid
        response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/abc", ValidationComplexResourceWithArray.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Object entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 1, 0, 0, 0);
        ResteasyConstraintViolation cv = r.getPropertyViolations().iterator().next();
        Assertions.assertTrue(cv.getMessage().startsWith("size must be between 5 and"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("abc", cv.getValue(), WRONG_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Validation of list
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testList() throws Exception {
        // Valid
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/abcde", ValidationComplexResourceWithList.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Invalid
        response = client.target(generateURL(BASIC_DEPLOYMENT, "/abc", ValidationComplexResourceWithList.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Object entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 1, 0, 0, 0);
        ResteasyConstraintViolation cv = r.getPropertyViolations().iterator().next();
        Assertions.assertTrue(cv.getMessage().startsWith("size must be between 5 and"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("abc", cv.getValue(), WRONG_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Validation of map
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMap() throws Exception {
        // Valid
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/abcde", ValidationComplexResourceWithMap.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Invalid
        response = client.target(generateURL(BASIC_DEPLOYMENT, "/abc", ValidationComplexResourceWithMap.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Object entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 1, 0, 0, 0);
        ResteasyConstraintViolation cv = r.getPropertyViolations().iterator().next();
        Assertions.assertTrue(cv.getMessage().startsWith("size must be between 5 and"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("abc", cv.getValue(), WRONG_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Validation of map of list of array of string
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMapOfListOfArrayOfStrings() throws Exception {
        // Valid
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/abcde",
                        ValidationComplexResourceWithMapOfListOfArrayOfStrings.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Invalid
        response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/abc",
                        ValidationComplexResourceWithMapOfListOfArrayOfStrings.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Object entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(String.class.cast(entity));
        logger.info("exception: " + r);
        TestUtil.countViolations(r, 1, 0, 0, 0);
        ResteasyConstraintViolation cv = r.getPropertyViolations().iterator().next();
        Assertions.assertTrue(cv.getMessage().startsWith("size must be between 5 and"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("abc", cv.getValue(), WRONG_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Test parameters validation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testParameters() throws Exception {
        // Valid native constraint
        Response response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/native",
                        ValidationComplexResourceWithParameters.class.getSimpleName()))
                .request().post(Entity.entity(new ValidationComplexFoo("a"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Valid imposed constraint
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/imposed",
                        ValidationComplexResourceWithParameters.class.getSimpleName()))
                .request().post(Entity.entity(new ValidationComplexFoo("abcde"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Valid native and imposed constraints.
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/nativeAndImposed",
                        ValidationComplexResourceWithParameters.class.getSimpleName()))
                .request().post(Entity.entity(new ValidationComplexFoo("abc"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Invalid native constraint
        // Valid native and imposed constraints.
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/native",
                        ValidationComplexResourceWithParameters.class.getSimpleName()))
                .request().post(Entity.entity(new ValidationComplexFoo("abcdef"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(entity);
        TestUtil.countViolations(r, 0, 0, 1, 0);
        ResteasyConstraintViolation cv = r.getParameterViolations().iterator().next();
        Assertions.assertTrue(cv.getMessage().equals("s must have length: 1 <= length <= 3"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationComplexFoo[abcdef]", cv.getValue(),
                RESPONSE_ERROR_MSG);
        response.close();

        // Invalid imposed constraint
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/imposed",
                        ValidationComplexResourceWithParameters.class.getSimpleName()))
                .request().post(Entity.entity(new ValidationComplexFoo("abcdef"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        entity = response.readEntity(String.class);
        r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 0, 0, 1, 0);
        cv = r.getParameterViolations().iterator().next();
        Assertions.assertTrue(cv.getMessage().equals("s must have length: 3 <= length <= 5"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationComplexFoo[abcdef]", cv.getValue(),
                RESPONSE_ERROR_MSG);
        response.close();

        // Invalid native and imposed constraints
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/nativeAndImposed",
                        ValidationComplexResourceWithParameters.class.getSimpleName()))
                .request().post(Entity.entity(new ValidationComplexFoo("abcdef"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        entity = response.readEntity(String.class);
        r = new ViolationReport(entity);
        TestUtil.countViolations(r, 0, 0, 2, 0);
        Iterator<ResteasyConstraintViolation> it = r.getParameterViolations().iterator();
        ResteasyConstraintViolation cv1 = it.next();
        ResteasyConstraintViolation cv2 = it.next();
        if (cv1.toString().indexOf('1') < 0) {
            ResteasyConstraintViolation temp = cv1;
            cv1 = cv2;
            cv2 = temp;
        }
        Assertions.assertTrue(cv1.getMessage().equals("s must have length: 1 <= length <= 3"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationComplexFoo[abcdef]", cv1.getValue(),
                RESPONSE_ERROR_MSG);
        Assertions.assertTrue(cv2.getMessage().equals("s must have length: 3 <= length <= 5"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationComplexFoo[abcdef]", cv2.getValue(),
                RESPONSE_ERROR_MSG);
        response.close();

        // Valid other parameters
        String url = generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/other/ppp",
                ValidationComplexResourceWithParameters.class.getSimpleName()); // path param
        url += ";m=mmm"; // matrix param
        url += "?q=qqq"; // query param
        Form form = new Form().param("f", "fff");
        Cookie ck1 = new Cookie.Builder("c")
                .value("ccc")
                .build();
        response = client.target(url).request().header("h", "hhh")
                .cookie(ck1).post(Entity.form(form));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Invalid other parameters
        url = generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/other/pppp",
                ValidationComplexResourceWithParameters.class.getSimpleName()); // path param
        url += ";m=mmmm"; // matrix param
        url += "?q=qqqq"; // query param
        form = new Form().param("f", "ffff");
        Cookie ck2 = new Cookie.Builder("c")
                .value("cccc")
                .build();
        response = client.target(url).request().header("h", "hhhh")
                .cookie(ck2).post(Entity.form(form));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        entity = response.readEntity(String.class);
        r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 0, 0, 6, 0);
        List<String> list = getMessages(r);
        Assertions.assertTrue(list.contains("size must be between 2 and 3; pppp"),
                WRONG_ERROR_MSG);
        Assertions.assertTrue(list.contains("size must be between 2 and 3; mmmm"),
                WRONG_ERROR_MSG);
        Assertions.assertTrue(list.contains("size must be between 2 and 3; qqqq"),
                WRONG_ERROR_MSG);
        Assertions.assertTrue(list.contains("size must be between 2 and 3; ffff"),
                WRONG_ERROR_MSG);
        Assertions.assertTrue(list.contains("size must be between 2 and 3; hhhh"),
                WRONG_ERROR_MSG);
        Assertions.assertTrue(list.contains("size must be between 2 and 3; cccc"),
                WRONG_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Test return value validation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReturnValues() throws Exception {
        // Valid native constraint
        ValidationComplexFoo foo = new ValidationComplexFoo("a");
        Response response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/native",
                        ValidationComplexResourceWithReturnValues.class.getSimpleName()))
                .request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationComplexFoo.class),
                RESPONSE_ERROR_MSG);
        response.close();

        // Valid imposed constraint
        foo = new ValidationComplexFoo("abcde");
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/imposed",
                        ValidationComplexResourceWithReturnValues.class.getSimpleName()))
                .request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationComplexFoo.class),
                RESPONSE_ERROR_MSG);
        response.close();

        // Valid native and imposed constraints.
        foo = new ValidationComplexFoo("abc");
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/nativeAndImposed",
                        ValidationComplexResourceWithReturnValues.class.getSimpleName()))
                .request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationComplexFoo.class),
                RESPONSE_ERROR_MSG);
        response.close();

        // Invalid native constraint
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/native",
                        ValidationComplexResourceWithReturnValues.class.getSimpleName()))
                .request().post(Entity.entity(new ValidationComplexFoo("abcdef"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        Object entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 0, 0, 0, 1);
        ResteasyConstraintViolation cv = r.getReturnValueViolations().iterator().next();
        Assertions.assertTrue(cv.getMessage().equals("s must have length: 1 <= length <= 3"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationComplexFoo[abcdef]", cv.getValue(),
                RESPONSE_ERROR_MSG);
        response.close();

        // Invalid imposed constraint
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/imposed",
                        ValidationComplexResourceWithReturnValues.class.getSimpleName()))
                .request().post(Entity.entity(new ValidationComplexFoo("abcdef"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        entity = response.readEntity(String.class);
        r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 0, 0, 0, 1);
        cv = r.getReturnValueViolations().iterator().next();
        Assertions.assertTrue(cv.getMessage().equals("s must have length: 3 <= length <= 5"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationComplexFoo[abcdef]", cv.getValue(),
                RESPONSE_ERROR_MSG);
        response.close();

        // Invalid native and imposed constraints
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/nativeAndImposed",
                        ValidationComplexResourceWithReturnValues.class.getSimpleName()))
                .request().post(Entity.entity(new ValidationComplexFoo("abcdef"), "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        entity = response.readEntity(String.class);
        r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 0, 0, 0, 2);
        Iterator<ResteasyConstraintViolation> it = r.getReturnValueViolations().iterator();
        ResteasyConstraintViolation cv1 = it.next();
        ResteasyConstraintViolation cv2 = it.next();
        if (cv1.toString().indexOf('1') < 0) {
            ResteasyConstraintViolation temp = cv1;
            cv1 = cv2;
            cv2 = temp;
        }
        Assertions.assertTrue(cv1.getMessage().equals("s must have length: 1 <= length <= 3"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationComplexFoo[abcdef]", cv1.getValue(),
                RESPONSE_ERROR_MSG);
        Assertions.assertTrue(cv2.getMessage().equals("s must have length: 3 <= length <= 5"),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationComplexFoo[abcdef]", cv2.getValue(),
                RESPONSE_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Check validations before checking return value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testViolationsBeforeReturnValue() throws Exception {
        // Valid
        ValidationComplexFoo foo = new ValidationComplexFoo("pqrs");
        Response response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/abc/wxyz/unused/unused",
                        ValidationComplexResourceWithAllFivePotentialViolations.class.getSimpleName()))
                .request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationComplexFoo.class));
        response.close();

        // Invalid: Should have 1 each of field, property, class, and parameter violations,
        //          and no return value violations.
        foo = new ValidationComplexFoo("p");
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/a/z/unused/unused",
                        ValidationComplexResourceWithAllFivePotentialViolations.class.getSimpleName()))
                .request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Object entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 2, 1, 1, 0);
        ResteasyConstraintViolation cv = TestUtil.getViolationByMessage(r.getPropertyViolations(),
                "size must be between 2 and 4");
        Assertions.assertNotNull(cv, WRONG_ERROR_MSG);
        Assertions.assertEquals("a", cv.getValue(), RESPONSE_ERROR_MSG);
        cv = TestUtil.getViolationByMessage(r.getPropertyViolations(), "size must be between 3 and 5");
        Assertions.assertNotNull(cv, WRONG_ERROR_MSG);
        Assertions.assertEquals("z", cv.getValue(), RESPONSE_ERROR_MSG);
        cv = r.getClassViolations().iterator().next();
        Assertions.assertEquals("Concatenation of s and t must have length > 5", cv.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertTrue(cv.getValue().startsWith(
                "org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithAllFivePotentialViolations@"),
                WRONG_ERROR_MSG);
        cv = r.getParameterViolations().iterator().next();
        Assertions.assertEquals("s must have length: 3 <= length <= 5", cv.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationComplexFoo[p]", cv.getValue(),
                RESPONSE_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Validation in inheritence classes
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @OperateOnDeployment("basicDeploymentInterfaceTestSub")
    public void testInheritence() throws Exception {
        {
            // Valid - inherited annotations
            ValidationComplexInterfaceSuper.t = "aaa";
            ValidationComplexInterfaceSub.u = "bbb";
            Response response = client
                    .target(generateURL(BASIC_DEPLOYMENT, "/inherit", ValidationComplexInterfaceSub.class.getSimpleName()))
                    .request().post(Entity.entity("ccc", MediaType.TEXT_PLAIN_TYPE));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("ccc", response.readEntity(String.class),
                    RESPONSE_ERROR_MSG);
            response.close();
        }

        {
            // Valid - overridden annotations
            ValidationComplexInterfaceSuper.t = "aaa";
            ValidationComplexInterfaceSub.u = "bbb";
            Response response = client
                    .target(generateURL(BASIC_DEPLOYMENT, "/override", ValidationComplexInterfaceSub.class.getSimpleName()))
                    .request().post(Entity.entity("ccc", MediaType.TEXT_PLAIN_TYPE));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("ccc", response.readEntity(String.class),
                    RESPONSE_ERROR_MSG);
            response.close();
        }

        {
            // Invalid - inherited class, parameter annotations
            ValidationComplexInterfaceSuper.t = "a";
            ValidationComplexInterfaceSub.u = "d";
            Response response = client
                    .target(generateURL(BASIC_DEPLOYMENT, "/inherit", ValidationComplexInterfaceSub.class.getSimpleName()))
                    .request().post(Entity.entity("e", MediaType.TEXT_PLAIN_TYPE));
            logger.info("status: " + response.getStatus());
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            Object entity = response.readEntity(String.class);
            ViolationReport r = new ViolationReport(String.class.cast(entity));
            TestUtil.countViolations(r, 0, 2, 1, 0);
            response.close();
        }

        {
            // Invalid - overridden class, parameter annotations
            ValidationComplexInterfaceSuper.t = "a";
            ValidationComplexInterfaceSub.u = "d";
            Response response = client
                    .target(generateURL(BASIC_DEPLOYMENT, "/override", ValidationComplexInterfaceSub.class.getSimpleName()))
                    .request().post(Entity.entity("e", MediaType.TEXT_PLAIN_TYPE));
            logger.info("status: " + response.getStatus());
            Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            Object entity = response.readEntity(String.class);
            ViolationReport r = new ViolationReport(String.class.cast(entity));
            TestUtil.countViolations(r, 0, 2, 1, 0);
            response.close();
        }

        {
            // Invalid - inherited return value annotations
            ValidationComplexInterfaceSuper.t = "aaa";
            ValidationComplexInterfaceSub.u = "bbb";
            Response response = client
                    .target(generateURL(BASIC_DEPLOYMENT, "/inherit", ValidationComplexInterfaceSub.class.getSimpleName()))
                    .request().post(Entity.entity("eeee", MediaType.TEXT_PLAIN_TYPE));
            logger.info("status: " + response.getStatus());
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            Object entity = response.readEntity(String.class);
            ViolationReport r = new ViolationReport(String.class.cast(entity));
            TestUtil.countViolations(r, 0, 0, 0, 1);
            response.close();
        }

        {
            // Invalid - overridden return value annotations
            ValidationComplexInterfaceSuper.t = "aaa";
            ValidationComplexInterfaceSub.u = "bbb";
            Response response = client
                    .target(generateURL(BASIC_DEPLOYMENT, "/override", ValidationComplexInterfaceSub.class.getSimpleName()))
                    .request().post(Entity.entity("eeee", MediaType.TEXT_PLAIN_TYPE));
            logger.info("status: " + response.getStatus());
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            Object entity = response.readEntity(String.class);
            ViolationReport r = new ViolationReport(String.class.cast(entity));
            TestUtil.countViolations(r, 0, 0, 0, 2);
            response.close();
        }
    }

    /**
     * @tpTestDetails Locators validation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLocators() throws Exception {
        // Sub-resource locator returns resource with valid field.
        Response response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/validField",
                        ValidationComplexResourceWithSubLocators.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Sub-resource locator returns resource with invalid field.
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/invalidField",
                        ValidationComplexResourceWithSubLocators.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(entity);
        TestUtil.countViolations(r, 1, 0, 0, 0);
        ResteasyConstraintViolation cv = r.getPropertyViolations().iterator().next();
        Assertions.assertEquals("size must be between 2 and 4", cv.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("abcde", cv.getValue(), RESPONSE_ERROR_MSG);
        response.close();

        // Sub-resource locator returns resource with valid property.
        // Note: The resource ValidationComplexResourceWithProperty has a @PathParam annotation used by a setter,
        //       but it is not used when ValidationComplexResourceWithProperty is used a sub-resource.  Hence "unused".
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/property/abc/unused",
                        ValidationComplexResourceWithSubLocators.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Sub-resource locator returns resource with invalid property.
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/property/abcdef/unused",
                        ValidationComplexResourceWithSubLocators.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        entity = response.readEntity(String.class);
        r = new ViolationReport(entity);
        TestUtil.countViolations(r, 1, 0, 0, 0);
        cv = r.getPropertyViolations().iterator().next();
        Assertions.assertEquals("size must be between 2 and 4", cv.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("abcdef", cv.getValue(), RESPONSE_ERROR_MSG);
        response.close();

        // Valid
        ValidationComplexFoo foo = new ValidationComplexFoo("pqrs");
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/everything/abc/wxyz/unused/unused",
                        ValidationComplexResourceWithSubLocators.class.getSimpleName()))
                .request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(foo, response.readEntity(ValidationComplexFoo.class),
                RESPONSE_ERROR_MSG);
        response.close();

        // Invalid: Should have 1 each of field, property, class, and parameter violations,and no return value violations.
        // Note: expect warning because ValidationComplexResourceWithAllFivePotentialViolations is being used a sub-resource and it has an injectible field:
        //       WARN org.jboss.resteasy.core.ResourceLocator - Field s of subresource org.jboss.resteasy.test.validation.ValidationComplexTest$ValidationComplexResourceWithAllFivePotentialViolations will not be injected according to spec
        foo = new ValidationComplexFoo("p");
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/everything/a/z/unused/unused",
                        ValidationComplexResourceWithSubLocators.class.getSimpleName()))
                .request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        entity = response.readEntity(String.class);
        r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 2, 1, 1, 0);
        ResteasyConstraintViolation violation = TestUtil.getViolationByMessage(r.getPropertyViolations(),
                "size must be between 2 and 4");
        Assertions.assertNotNull(violation, WRONG_ERROR_MSG);
        Assertions.assertEquals("a", violation.getValue(), WRONG_ERROR_MSG);
        violation = TestUtil.getViolationByMessage(r.getPropertyViolations(), "size must be between 3 and 5");
        Assertions.assertNotNull(violation, WRONG_ERROR_MSG);
        Assertions.assertEquals("z", violation.getValue(), WRONG_ERROR_MSG);
        cv = r.getClassViolations().iterator().next();
        Assertions.assertEquals("Concatenation of s and t must have length > 5", cv.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertTrue(cv.getValue().startsWith(
                "org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithAllFivePotentialViolations@"),
                RESPONSE_ERROR_MSG);
        response.close();

        // Sub-sub-resource locator returns resource with valid property.
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/locator/sublocator/abc",
                        ValidationComplexResourceWithSubLocators.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Sub-resource locator returns resource with invalid property.
        response = client
                .target(generateURL(CUSTOM_OBJECT_DEPLOYMENT, "/locator/sublocator/abcdef",
                        ValidationComplexResourceWithSubLocators.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        entity = response.readEntity(String.class);
        r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 1, 0, 0, 0);
        cv = r.getPropertyViolations().iterator().next();
        Assertions.assertEquals("size must be between 2 and 3", cv.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("abcdef", cv.getValue(), RESPONSE_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Validation with asynchronous requests
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAsynch() throws Exception {
        // Submit asynchronous job with violations prior to execution of resource method.
        ValidationComplexFoo foo = new ValidationComplexFoo("p");
        Response response = client
                .target(generateURL(ASYNC_CUSTOM_OBJECT_DEPLOYMENT, "/a/z/unused/unused?asynch=true",
                        ValidationComplexResourceWithAllFivePotentialViolations.class.getSimpleName()))
                .request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
        String jobUrl = response.getHeaderString(HttpHeaders.LOCATION);
        logger.info("JOB: " + jobUrl);
        response.close();

        // Get result: Should have 1 each of field, property, class, and parameter violations,
        //             and no return value violations.
        response = client.target(jobUrl).request().get();
        for (int i = 0; i < 60; i++) {
            Thread.sleep(TimeoutUtil.adjust(1000));
            response.close();
            response = client.target(jobUrl).request().get();
            if (HttpServletResponse.SC_ACCEPTED != response.getStatus()) {
                break;
            }
        }
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        Object entity = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 2, 1, 1, 0);
        ResteasyConstraintViolation cv = TestUtil.getViolationByMessage(r.getPropertyViolations(),
                "size must be between 2 and 4");
        Assertions.assertNotNull(cv, WRONG_ERROR_MSG);
        Assertions.assertEquals("a", cv.getValue(), RESPONSE_ERROR_MSG);
        cv = TestUtil.getViolationByMessage(r.getPropertyViolations(), "size must be between 3 and 5");
        Assertions.assertNotNull(cv, WRONG_ERROR_MSG);
        Assertions.assertEquals("z", cv.getValue(), RESPONSE_ERROR_MSG);
        cv = r.getClassViolations().iterator().next();
        Assertions.assertEquals("Concatenation of s and t must have length > 5", cv.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertTrue(cv.getValue().startsWith(
                "org.jboss.resteasy.test.validation.resource.ValidationComplexResourceWithAllFivePotentialViolations@"),
                RESPONSE_ERROR_MSG);
        cv = r.getParameterViolations().iterator().next();
        Assertions.assertEquals("s must have length: 3 <= length <= 5", cv.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationComplexFoo[p]", cv.getValue(),
                RESPONSE_ERROR_MSG);
        response.close();

        // Delete job.
        response = client.target(jobUrl).request().delete();
        Assertions.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Submit asynchronous job with violations in result of resource method.
        foo = new ValidationComplexFoo("pqr");
        response = client
                .target(generateURL(ASYNC_CUSTOM_OBJECT_DEPLOYMENT, "/abc/xyz/unused/unused?asynch=true",
                        ValidationComplexResourceWithAllFivePotentialViolations.class.getSimpleName()))
                .request().post(Entity.entity(foo, "application/foo"));
        Assertions.assertEquals(HttpServletResponse.SC_ACCEPTED, response.getStatus());
        jobUrl = response.getHeaderString(HttpHeaders.LOCATION);
        logger.info("JOB: " + jobUrl);
        response.close();

        // Get result: Should have no field, property, class, or parameter violations,
        //             and one return value violation.
        response = client.target(jobUrl).request().get();
        for (int i = 0; i < 60; i++) {
            Thread.sleep(TimeoutUtil.adjust(1000));
            response.close();
            response = client.target(jobUrl).request().get();
            if (HttpServletResponse.SC_ACCEPTED != response.getStatus()) {
                break;
            }
        }
        Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        entity = response.readEntity(String.class);
        r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 0, 0, 0, 1);
        cv = r.getReturnValueViolations().iterator().next();
        Assertions.assertEquals("s must have length: 4 <= length <= 5", cv.getMessage(),
                WRONG_ERROR_MSG);
        Assertions.assertEquals("ValidationComplexFoo[pqr]", cv.getValue(),
                RESPONSE_ERROR_MSG);
        response.close();

        // Delete job.
        response = client.target(jobUrl).request().delete();
        Assertions.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Validation by cross-parameter constraints
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCrossParameterConstraint() throws Exception {
        // Valid
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/2/3",
                        ValidationComplexSubResourceWithCrossParameterConstraint.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();

        // Invalid
        response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/5/7",
                        ValidationComplexSubResourceWithCrossParameterConstraint.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        String header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertNotNull(header, "Missing validation header");
        Assertions.assertTrue(Boolean.valueOf(header), "Wrong validation header");
        Object entity = response.readEntity(String.class);
        logger.info("entity: " + entity);
        ViolationReport r = new ViolationReport(String.class.cast(entity));
        TestUtil.countViolations(r, 0, 0, 1, 0);
        ResteasyConstraintViolation violation = r.getParameterViolations().iterator().next();
        logger.info("violation: " + violation);
        Assertions.assertEquals("Parameters must total <= 7", violation.getMessage(),
                WRONG_ERROR_MSG);
        logger.info("violation value: " + violation.getValue());
        Assertions.assertEquals("[5, 7]", violation.getValue(), RESPONSE_ERROR_MSG);
        response.close();
    }

    /**
     * @tpTestDetails Validation with client proxies
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxy() throws Exception {
        // Valid
        ValidationComplexProxyInterface client = this.client
                .target(generateURL(BASIC_DEPLOYMENT, "/", ValidationComplexProxyResource.class.getSimpleName()))
                .proxy(ValidationComplexProxyInterface.class);
        client.s("abcd");
        String result = client.g();
        Assertions.assertEquals("abcd", result);

        // Invalid
        client.s("abcde");
        try {
            client.g();
        } catch (InternalServerErrorException e) {
            Response response = e.getResponse();
            logger.info("status: " + response.getStatus());
            String header = response.getHeaderString(Validation.VALIDATION_HEADER);
            Assertions.assertNotNull(header, "Missing validation header");
            Assertions.assertTrue(Boolean.valueOf(header), "Wrong validation header");
            Object entity = response.readEntity(String.class);
            logger.info("entity: " + entity);
            ViolationReport r = new ViolationReport(String.class.cast(entity));
            TestUtil.countViolations(r, 0, 0, 0, 1);
            ResteasyConstraintViolation violation = r.getReturnValueViolations().iterator().next();
            logger.info("violation: " + violation);
            Assertions.assertEquals("size must be between 2 and 4", violation.getMessage(),
                    WRONG_ERROR_MSG);
            Assertions.assertEquals("abcde", violation.getValue(), RESPONSE_ERROR_MSG);
            response.close();
        } catch (Exception e) {
            throw new RuntimeException("expected InternalServerErrorException", e);
        }
    }

    /**
     * @tpTestDetails Check other groups validation
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testOtherGroups() throws Exception {
        // Test invalid field, property, parameter, and class.
        Response response = client
                .target(generateURL(BASIC_DEPLOYMENT, "/test/a/z",
                        ValidationComplexResourceWithOtherGroups.class.getSimpleName()))
                .request().post(Entity.text(new String()));
        String entity = response.readEntity(String.class);
        logger.info("entity: " + entity);
        Assertions.assertEquals("z", entity, RESPONSE_ERROR_MSG);
        response.close();
    }

    private List<String> getMessages(ViolationReport r) {
        List<String> list = new ArrayList<>();
        list.addAll(getMessagesFromList(r.getPropertyViolations()));
        list.addAll(getMessagesFromList(r.getClassViolations()));
        list.addAll(getMessagesFromList(r.getParameterViolations()));
        list.addAll(getMessagesFromList(r.getReturnValueViolations()));
        return list;
    }

    private List<String> getMessagesFromList(List<ResteasyConstraintViolation> rcvs) {
        List<String> list = new ArrayList<>();
        for (Iterator<ResteasyConstraintViolation> it = rcvs.iterator(); it.hasNext();) {
            ResteasyConstraintViolation rcv = it.next();
            list.add(rcv.getMessage() + "; " + rcv.getValue());
        }
        return list;
    }
}
