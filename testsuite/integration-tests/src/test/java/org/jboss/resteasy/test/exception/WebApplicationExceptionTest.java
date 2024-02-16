package org.jboss.resteasy.test.exception;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.exception.resource.WebApplicationExceptionResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Exceptions
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for jakarta.ws.rs.WebApplicationException class
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class WebApplicationExceptionTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(WebApplicationExceptionTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, WebApplicationExceptionResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, WebApplicationExceptionTest.class.getSimpleName());
    }

    private void basicTest(String path, int code) {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget base = client.target(generateURL(path));
        Response response = base.request().get();
        Assertions.assertEquals(code, response.getStatus());
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Test for exception without error entity
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testException() {
        basicTest("/exception", HttpResponseCodes.SC_UNAUTHORIZED);
    }

    /**
     * @tpTestDetails Test for exception with error entity.
     *                Regression test for RESTEASY-24
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testExceptionWithEntity() {
        basicTest("/exception/entity", HttpResponseCodes.SC_UNAUTHORIZED);
    }

}
