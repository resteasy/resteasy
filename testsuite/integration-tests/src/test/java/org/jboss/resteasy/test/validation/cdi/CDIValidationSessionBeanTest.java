package org.jboss.resteasy.test.validation.cdi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.api.validation.ResteasyConstraintViolation;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.plugins.validation.ResteasyViolationExceptionImpl;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.validation.cdi.resource.CDIValidationSessionBeanProxy;
import org.jboss.resteasy.test.validation.cdi.resource.CDIValidationSessionBeanResource;
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
 * @tpTestCaseDetails Regression test for RESTEASY-1008
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class CDIValidationSessionBeanTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(CDIValidationSessionBeanTest.class.getSimpleName())
                .addClass(CDIValidationSessionBeanProxy.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, CDIValidationSessionBeanResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CDIValidationSessionBeanTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Check for invalid parameter
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInvalidParam() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        Invocation.Builder request = client.target(generateURL("/test/resource/0")).request();
        ClientResponse response = (ClientResponse) request.get();
        String answer = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        ResteasyViolationException e = new ResteasyViolationExceptionImpl(String.class.cast(answer));
        TestUtil.countViolations(e, 1, 0, 0, 1, 0);
        ResteasyConstraintViolation cv = e.getParameterViolations().iterator().next();
        Assertions.assertTrue(cv.getMessage().equals("must be greater than or equal to 7"),
                "Expected validation error is not in response");
        client.close();
    }
}
