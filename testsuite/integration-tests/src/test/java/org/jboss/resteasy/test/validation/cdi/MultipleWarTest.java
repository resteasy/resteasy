package org.jboss.resteasy.test.validation.cdi;

import static org.junit.Assert.assertEquals;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.hibernate.validator.HibernateValidatorPermission;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.validation.ResteasyViolationExceptionImpl;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.cdi.resource.MultipleWarResource;
import org.jboss.resteasy.test.validation.cdi.resource.MultipleWarSumConstraint;
import org.jboss.resteasy.test.validation.cdi.resource.MultipleWarSumValidator;
import org.jboss.resteasy.test.validation.resource.ValidationCoreFooReaderWriter;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1058
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
public class MultipleWarTest {
    static final String RESPONSE_ERROR_MSG = "Response has wrong content";
    static final String WRONG_ERROR_MSG = "Expected validation error is not in response";
    ResteasyClient client;

    @Deployment(name = "war1", order = 1)
    public static Archive<?> createTestArchive1() {
        WebArchive war1 = TestUtil.prepareArchive(MultipleWarTest.class.getSimpleName() + "1")
                .addClasses(MultipleWarResource.class)
                .addClasses(MultipleWarSumConstraint.class, MultipleWarSumValidator.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        war1.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new HibernateValidatorPermission("accessPrivateMembers")), "permissions.xml");
        return war1;
    }

    @Deployment(name = "war2", order = 2)
    public static Archive<?> createTestArchive2() {
        WebArchive war2 = TestUtil.prepareArchive(MultipleWarTest.class.getSimpleName() + "2")
                .addClasses(MultipleWarResource.class)
                .addClasses(MultipleWarSumConstraint.class, MultipleWarSumValidator.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        war2.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new HibernateValidatorPermission("accessPrivateMembers")), "permissions.xml");
        return war2;
    }

    @Before
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient().register(ValidationCoreFooReaderWriter.class);
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private static String generateURL(String path, int i) {
        return PortProviderUtil.generateURL(path, MultipleWarTest.class.getSimpleName() + i);
    }

    /**
     * @tpTestDetails Check validation of invalid inputs
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInputsInvalid() throws Exception {
        WebTarget request1 = client.target(generateURL("/test/0/0/0", 1));
        WebTarget request2 = client.target(generateURL("/test/0/0/0", 2));
        Response response;
        for (int i = 1; i < 2; i++) {
            response = request1.request().get();
            String answer = response.readEntity(String.class);
            assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(answer));
            TestUtil.countViolations(e, 4, 2, 1, 1, 0);
            ResteasyConstraintViolation cv = TestUtil.getViolationByMessage(e.getPropertyViolations(),
                    "must be greater than or equal to 3");
            Assert.assertNotNull(WRONG_ERROR_MSG, cv);
            cv = TestUtil.getViolationByMessage(e.getPropertyViolations(), "must be greater than or equal to 5");
            Assert.assertNotNull(WRONG_ERROR_MSG, cv);
            cv = e.getClassViolations().iterator().next();
            Assert.assertTrue(WRONG_ERROR_MSG,
                    cv.getMessage().indexOf("org.jboss.resteasy.resteasy1058.MultipleWarSumConstraint") > 0);
            cv = e.getParameterViolations().iterator().next();
            Assert.assertTrue(WRONG_ERROR_MSG, cv.getMessage().equals("must be greater than or equal to 7"));
            response.close();

            response = request2.request().get();
            answer = response.readEntity(String.class);
            assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
            e = new ResteasyViolationExceptionImpl(String.class.cast(answer));
            TestUtil.countViolations(e, 4, 2, 1, 1, 0);
            cv = TestUtil.getViolationByMessage(e.getPropertyViolations(), "must be greater than or equal to 3");
            Assert.assertNotNull(WRONG_ERROR_MSG, cv);
            cv = TestUtil.getViolationByMessage(e.getPropertyViolations(), "must be greater than or equal to 5");
            Assert.assertNotNull(WRONG_ERROR_MSG, cv);
            cv = e.getClassViolations().iterator().next();
            Assert.assertTrue(WRONG_ERROR_MSG,
                    cv.getMessage().indexOf("org.jboss.resteasy.resteasy1058.MultipleWarSumConstraint") > 0);
            cv = e.getParameterViolations().iterator().next();
            Assert.assertTrue(WRONG_ERROR_MSG, cv.getMessage().equals("must be greater than or equal to 7"));
            response.close();
        }
    }

    /**
     * @tpTestDetails Check validation of invalid return value
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReturnValueInvalid() throws Exception {
        WebTarget request1 = client.target(generateURL("/test/5/7/9", 1));
        WebTarget request2 = client.target(generateURL("/test/5/7/9", 2));
        Response response;
        for (int i = 1; i < 2; i++) {
            response = request1.request().get();
            String answer = response.readEntity(String.class);
            assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(answer));
            TestUtil.countViolations(e, 1, 0, 0, 0, 1);
            ResteasyConstraintViolation cv = e.getReturnValueViolations().iterator().next();
            Assert.assertTrue(WRONG_ERROR_MSG, cv.getMessage().equals("must be less than or equal to 0"));
            response.close();

            response = request2.request().get();
            answer = response.readEntity(String.class);
            assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, response.getStatus());
            e = new ResteasyViolationExceptionImpl(String.class.cast(answer));
            TestUtil.countViolations(e, 1, 0, 0, 0, 1);
            cv = e.getReturnValueViolations().iterator().next();
            Assert.assertTrue(WRONG_ERROR_MSG, cv.getMessage().equals("must be less than or equal to 0"));
            response.close();
        }
    }
}
