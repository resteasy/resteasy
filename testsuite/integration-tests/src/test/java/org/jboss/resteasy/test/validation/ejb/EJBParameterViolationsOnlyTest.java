package org.jboss.resteasy.test.validation.ejb;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.api.validation.Validation;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.validation.ejb.resource.EJBParameterViolationsOnlyAbstractDataObject;
import org.jboss.resteasy.test.validation.ejb.resource.EJBParameterViolationsOnlyDataObject;
import org.jboss.resteasy.test.validation.ejb.resource.EJBParameterViolationsOnlyResourceIntf;
import org.jboss.resteasy.test.validation.ejb.resource.EJBParameterViolationsOnlySingletonResource;
import org.jboss.resteasy.test.validation.ejb.resource.EJBParameterViolationsOnlyStatefulResource;
import org.jboss.resteasy.test.validation.ejb.resource.EJBParameterViolationsOnlyStatelessResource;
import org.jboss.resteasy.test.validation.ejb.resource.EJBParameterViolationsOnlyTestApplication;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Test situation where EJBs have parameter violations but no class, field, or property violations.
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-2503
 * @tpSince RESTEasy 4.5
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class EJBParameterViolationsOnlyTest {

    private static ResteasyClient client;
    private static EJBParameterViolationsOnlyDataObject validDataObject;
    private static EJBParameterViolationsOnlyDataObject invalidDataObject;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(EJBParameterViolationsOnlyTest.class.getSimpleName())
                .addClasses(
                        EJBParameterViolationsOnlyTestApplication.class,
                        EJBParameterViolationsOnlyDataObject.class,
                        EJBParameterViolationsOnlyAbstractDataObject.class,
                        EJBParameterViolationsOnlyResourceIntf.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null,
                EJBParameterViolationsOnlyStatelessResource.class,
                EJBParameterViolationsOnlyStatefulResource.class,
                EJBParameterViolationsOnlySingletonResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, EJBParameterViolationsOnlyTest.class.getSimpleName());
    }

    @BeforeAll
    public static void beforeClass() {
        client = (ResteasyClient) ClientBuilder.newClient();

        // Create valid data object.
        validDataObject = new EJBParameterViolationsOnlyDataObject();
        validDataObject.setDirection("north");
        validDataObject.setSpeed(10);

        // Create data object with constraint violations.
        invalidDataObject = new EJBParameterViolationsOnlyDataObject();
        invalidDataObject.setDirection("north");
        invalidDataObject.setSpeed(0);
    }

    @AfterAll
    public static void afterClass() {
        client.close();
    }

    /**
     * @tpTestDetails Run tests for stateless EJB
     * @tpSince RESTEasy 4.5
     */
    @Test
    public void testStateless() throws Exception {
        doValidationTest(client.target(generateURL("/app/stateless")));
    }

    /**
     * @tpTestDetails Run tests for stateful EJB
     * @tpSince RESTEasy 4.5
     */
    @Test
    public void testStateful() throws Exception {
        doValidationTest(client.target(generateURL("/app/stateful")));
    }

    /**
     * @tpTestDetails Run tests for singleton EJB
     * @tpSince RESTEasy 4.5
     */
    @Test
    public void testSingleton() throws Exception {
        doValidationTest(client.target(generateURL("/app/singleton")));
    }

    void doValidationTest(WebTarget target) throws Exception {
        // Invoke with valid data object.
        Invocation.Builder request = target.path("validation").request().accept(MediaType.TEXT_PLAIN);
        Response response = request.post(Entity.entity(validDataObject, MediaType.APPLICATION_JSON), Response.class);
        Assertions.assertEquals(200, response.getStatus());

        // Reset flag indicating method has been executed.
        boolean used = target.path("used").request().get(boolean.class);
        Assertions.assertTrue(used);
        target.path("reset").request().get();

        // Invoke with invalid data object.
        Response response2 = request.post(Entity.entity(invalidDataObject, MediaType.APPLICATION_JSON), Response.class);
        Assertions.assertEquals(400, response2.getStatus());
        Assertions.assertEquals("true", response2.getHeaderString(Validation.VALIDATION_HEADER));
        ViolationReport report = response2.readEntity(ViolationReport.class);
        TestUtil.countViolations(report, 0, 0, 1, 0);
        used = target.path("used").request().get(boolean.class);
        Assertions.assertFalse(used);
    }
}
