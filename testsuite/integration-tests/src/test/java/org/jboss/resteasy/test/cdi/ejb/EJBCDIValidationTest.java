package org.jboss.resteasy.test.cdi.ejb;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.hibernate.validator.HibernateValidatorPermission;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.api.validation.ViolationReport;
import org.jboss.resteasy.test.cdi.ejb.resource.EJBCDIValidationApplication;
import org.jboss.resteasy.test.cdi.ejb.resource.EJBCDIValidationSingletonResource;
import org.jboss.resteasy.test.cdi.ejb.resource.EJBCDIValidationStatefulResource;
import org.jboss.resteasy.test.cdi.ejb.resource.EJBCDIValidationStatelessResource;
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
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails EJB, CDI, Validation, and RESTEasy integration test: RESTEASY-1749
 * @tpSince RESTEasy 4.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class EJBCDIValidationTest {

    private static Client client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(EJBCDIValidationTest.class.getSimpleName());
        war.addClasses(EJBCDIValidationApplication.class)
                .addClasses(EJBCDIValidationStatelessResource.class)
                .addClasses(EJBCDIValidationStatefulResource.class)
                .addClasses(EJBCDIValidationSingletonResource.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new HibernateValidatorPermission("accessPrivateMembers")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, EJBCDIValidationTest.class.getSimpleName());
    }

    @BeforeAll
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Verify correct order of validation on stateless EJBs
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testStateless() {
        // Expect property, parameter violations.
        WebTarget base = client.target(generateURL("/rest/stateless/"));
        Builder builder = base.path("post/n").request();
        Response response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
        Assertions.assertEquals(400, response.getStatus());
        String answer = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(answer);
        TestUtil.countViolations(r, 1, 0, 1, 0);

        // Valid invocation
        response = base.path("set/xyz").request().get();
        Assertions.assertEquals(204, response.getStatus());
        response.close();

        // EJB resource has been created: expect parameter violation.
        builder = base.path("post/n").request();
        builder.accept(MediaType.TEXT_PLAIN_TYPE);
        response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
        Assertions.assertEquals(400, response.getStatus());
        answer = response.readEntity(String.class);
        r = new ViolationReport(answer);
        TestUtil.countViolations(r, 0, 0, 1, 0);
    }

    /**
     * @tpTestDetails Verify correct order of validation on stateful EJBs
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testStateful() {
        // Expect property, parameter violations
        WebTarget base = client.target(generateURL("/rest/stateful/"));
        Builder builder = base.path("post/n").request();
        Response response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
        Assertions.assertEquals(400, response.getStatus());
        String answer = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(answer);
        TestUtil.countViolations(r, 1, 0, 1, 0);

        // Valid invocation
        response = base.path("set/xyz").request().get();
        Assertions.assertEquals(204, response.getStatus());
        response.close();

        // EJB resource gets created again: expect property and parameter violations.
        builder = base.path("post/n").request();
        builder.accept(MediaType.TEXT_PLAIN_TYPE);
        response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
        Assertions.assertEquals(400, response.getStatus());
        answer = response.readEntity(String.class);
        r = new ViolationReport(answer);
        TestUtil.countViolations(r, 1, 0, 1, 0);
    }

    /**
     * @tpTestDetails Verify correct order of validation on singleton EJBs
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testSingleton() {
        doTestSingleton(1); // Expect property violation when EJB resource gets created.
        doTestSingleton(0); // EJB resource has been created: expect no property violation.
    }

    void doTestSingleton(int propertyViolations) {
        // Expect property, parameter violations
        WebTarget base = client.target(generateURL("/rest/singleton/"));
        Builder builder = base.path("post/n").request();
        Response response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
        Assertions.assertEquals(400, response.getStatus());
        String answer = response.readEntity(String.class);
        ViolationReport r = new ViolationReport(answer);
        TestUtil.countViolations(r, propertyViolations, 0, 1, 0);

        // Valid invocation
        response = base.path("set/xyz").request().get();
        Assertions.assertEquals(204, response.getStatus());
        response.close();

        // EJB resource has been created: expect parameter violation.
        builder = base.path("post/n").request();
        builder.accept(MediaType.TEXT_PLAIN_TYPE);
        response = builder.post(Entity.entity("-1", MediaType.APPLICATION_JSON_TYPE));
        Assertions.assertEquals(400, response.getStatus());
        answer = response.readEntity(String.class);
        r = new ViolationReport(answer);
        TestUtil.countViolations(r, 0, 0, 1, 0);
    }
}
