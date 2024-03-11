package org.jboss.resteasy.test.validation.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.validation.ResteasyViolationExceptionImpl;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.cdi.resource.SessionResourceImpl;
import org.jboss.resteasy.test.validation.cdi.resource.SessionResourceLocal;
import org.jboss.resteasy.test.validation.cdi.resource.SessionResourceParent;
import org.jboss.resteasy.test.validation.cdi.resource.SessionResourceRemote;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-923
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
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
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        Response response = client.target(generateURL("/test/resource")).queryParam("param", "abc").request().get();
        String answer = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(answer));
        int c = e.getViolations().size();
        Assertions.assertTrue(c == 1 || c == 2);
        TestUtil.countViolations(e, c, 0, 0, c, 0);
        ResteasyConstraintViolation cv = e.getParameterViolations().iterator().next();
        Assertions.assertTrue(cv.getMessage().startsWith("size must be between 4 and"),
                "Expected validation error is not in response");
        Assertions.assertTrue(answer.contains("size must be between 4 and"),
                "Expected validation error is not in response");
        response.close();
        client.close();
    }
}
