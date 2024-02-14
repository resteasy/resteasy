package org.jboss.resteasy.test.interceptor;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.interceptor.resource.CustomException;
import org.jboss.resteasy.test.interceptor.resource.ThrowCustomExceptionResponseFilter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.21
 * @tpTestCaseDetails Throw custom exception from a ClientResponseFilter [RESTEASY-1591]
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResponseFilterCustomExceptionTest {
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResponseFilterCustomExceptionTest.class.getSimpleName());
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        war.addClasses(CustomException.class);
        return TestUtil.finishContainerPrepare(war, null, ThrowCustomExceptionResponseFilter.class);
    }

    static Client client;

    @BeforeEach
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void cleanup() {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResponseFilterCustomExceptionTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Use ClientResponseFilter
     * @tpSince RESTEasy 3.0.21
     */
    @Test
    public void testThrowCustomException() throws Exception {
        try {
            client.register(ThrowCustomExceptionResponseFilter.class);
            client.target(generateURL("/testCustomException")).request().post(Entity.text("testCustomException"));
        } catch (ProcessingException pe) {
            Assertions.assertEquals(CustomException.class, pe.getCause().getClass());
        }
    }
}
