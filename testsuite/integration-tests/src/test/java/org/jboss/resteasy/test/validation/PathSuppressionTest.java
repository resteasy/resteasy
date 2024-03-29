package org.jboss.resteasy.test.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.validation.ResteasyViolationExceptionImpl;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.resource.PathSuppressionClassConstraint;
import org.jboss.resteasy.test.validation.resource.PathSuppressionClassValidator;
import org.jboss.resteasy.test.validation.resource.PathSuppressionResource;
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
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-945
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class PathSuppressionTest {

    static ResteasyClient client;

    public static Archive<?> generateArchive(String deploymentName, String suppressPath) {
        WebArchive war = TestUtil.prepareArchive(deploymentName)
                .addClasses(PathSuppressionResource.class)
                .addClasses(PathSuppressionClassConstraint.class, PathSuppressionClassValidator.class);
        Map<String, String> contextParam = new HashMap<>();
        if (suppressPath != null) {
            contextParam.put("resteasy.validation.suppress.path", suppressPath);
        }
        return TestUtil.finishContainerPrepare(war, contextParam, PathSuppressionResource.class);
    }

    @BeforeAll
    public static void before() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    @Deployment(name = "default", order = 1)
    public static Archive<?> createTestArchiveDefault() {
        return generateArchive("RESTEASY-945-default", null);
    }

    @Deployment(name = "false", order = 2)
    public static Archive<?> createTestArchiveFalse() {
        return generateArchive("RESTEASY-945-false", "false");
    }

    @Deployment(name = "true")
    public static Archive<?> createTestArchiveTrue() {
        return generateArchive("RESTEASY-945-true", "true");
    }

    /**
     * @tpTestDetails Check input parameters for default value of resteasy.validation.suppress.path
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInputSuppressPathDefault() throws Exception {
        doTestInputViolations("default", "s", "t", "", new String[] { "test.arg0", "test.u" });
    }

    /**
     * @tpTestDetails Check input parameters for false value of resteasy.validation.suppress.path
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInputSuppressPathFalse() throws Exception {
        doTestInputViolations("false", "s", "t", "", new String[] { "test.arg0", "test.u" });
    }

    /**
     * @tpTestDetails Check input parameters for true value of resteasy.validation.suppress.path
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInputSuppressPathTrue() throws Exception {
        doTestInputViolations("true", "*", "*", "*", "*");
    }

    /**
     * @tpTestDetails Check return value for default value of resteasy.validation.suppress.path
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReturnValueSuppressPathDefault() throws Exception {
        doTestReturnValueViolations("default", "test.<return value>");
    }

    /**
     * @tpTestDetails Check return value for false value of resteasy.validation.suppress.path
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReturnValueSuppressPathFalse() throws Exception {
        doTestReturnValueViolations("false", "test.<return value>");
    }

    /**
     * @tpTestDetails Check return value for true value of resteasy.validation.suppress.path
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReturnSuppressPathTrue() throws Exception {
        doTestReturnValueViolations("true", "*");
    }

    public void doTestInputViolations(String suppress, String fieldPath, String propertyPath, String classPath,
            String... parameterPaths) throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/all/a/b/c", "RESTEASY-945-" + suppress)).request()
                .get();
        Object header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertTrue(header instanceof String, "Header has wrong format");
        Assertions.assertTrue(Boolean.valueOf(String.class.cast(header)), "Header has wrong format");
        String answer = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        ViolationReport report = new ViolationReport(new ResteasyViolationExceptionImpl(String.class.cast(answer)));
        TestUtil.countViolations(report, 2, 1, 1, 0);
        ResteasyConstraintViolation violation = TestUtil.getViolationByPath(report.getPropertyViolations(), fieldPath);
        Assertions.assertNotNull(violation, "Expected validation error is not in response");
        violation = TestUtil.getViolationByPath(report.getPropertyViolations(), propertyPath);
        Assertions.assertNotNull(violation, "Expected validation error is not in response");
        violation = report.getClassViolations().iterator().next();
        Assertions.assertEquals(classPath, violation.getPath(),
                "Expected validation error is not in response");

        violation = report.getParameterViolations().iterator().next();
        Assertions.assertTrue(Arrays.asList(parameterPaths).contains(violation.getPath()),
                "Expected validation error is not in response: " + parameterPaths);
        response.close();
    }

    public void doTestReturnValueViolations(String suppress, String returnValuePath) throws Exception {
        Response response = client.target(PortProviderUtil.generateURL("/all/aa/bbb/cccc", "RESTEASY-945-" + suppress))
                .request().get();
        Object header = response.getHeaderString(Validation.VALIDATION_HEADER);
        Assertions.assertTrue(header instanceof String, "Header has wrong format");
        Assertions.assertTrue(Boolean.valueOf(String.class.cast(header)), "Header has wrong format");
        String answer = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        ViolationReport report = new ViolationReport(new ResteasyViolationExceptionImpl(String.class.cast(answer)));
        TestUtil.countViolations(report, 0, 0, 0, 1);
        ResteasyConstraintViolation violation = report.getReturnValueViolations().iterator().next();
        Assertions.assertEquals(returnValuePath, violation.getPath(), "Expected validation error is not in response");
        response.close();
    }
}
