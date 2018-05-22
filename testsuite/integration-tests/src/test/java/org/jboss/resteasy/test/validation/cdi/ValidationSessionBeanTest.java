package org.jboss.resteasy.test.validation.cdi;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.validation.cdi.resource.SessionResourceImpl;
import org.jboss.resteasy.test.validation.cdi.resource.SessionResourceLocal;
import org.jboss.resteasy.test.validation.cdi.resource.SessionResourceParent;
import org.jboss.resteasy.test.validation.cdi.resource.SessionResourceRemote;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-923
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ValidationSessionBeanTest {
    @SuppressWarnings(value = "unchecked")
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ValidationSessionBeanTest.class.getSimpleName())
                .addClasses(SessionResourceParent.class)
                .addClasses(SessionResourceLocal.class, SessionResourceRemote.class, SessionResourceImpl.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ValidationSessionBeanTest.class.getSimpleName());
    }

    @Test
    public void testInvalidParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Response response = client.target(generateURL("/test/resource")).queryParam("param", "abc").request().get();
        String answer = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        ResteasyViolationException e = new ResteasyViolationException(String.class.cast(answer));
        int c = e.getViolations().size();
        Assert.assertTrue(c == 1 || c == 2);
        TestUtil.countViolations(e, c, 0, 0, 0, c, 0);
        ResteasyConstraintViolation cv = e.getParameterViolations().iterator().next();
        Assert.assertTrue("Expected validation error is not in response", cv.getMessage().startsWith("size must be between 4 and"));
        Assert.assertTrue("Expected validation error is not in response", answer.contains("size must be between 4 and"));
        response.close();
        client.close();
    }
}
