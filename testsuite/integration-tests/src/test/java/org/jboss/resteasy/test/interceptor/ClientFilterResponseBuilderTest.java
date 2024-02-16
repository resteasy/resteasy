package org.jboss.resteasy.test.interceptor;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
// import org.jboss.resteasy.test.interceptor.resource.ResponseBuilderCustomRequestFilter;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.interceptor.resource.PriorityExecutionResource;
import org.jboss.resteasy.test.interceptor.resource.ResponseBuilderCustomResponseFilter;
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
 * Demonstrates that a Response filter can process the entity data in a response object
 * and the entity can be properly accessed by the client call.
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ClientFilterResponseBuilderTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(
                ClientFilterResponseBuilderTest.class.getSimpleName());
        war.addClasses(ResponseBuilderCustomResponseFilter.class,
                PriorityExecutionResource.class);
        return TestUtil.finishContainerPrepare(war, null);
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
        return PortProviderUtil.generateURL(path,
                ClientFilterResponseBuilderTest.class.getSimpleName());
    }

    @Test
    public void testResponse() throws Exception {
        try {
            client.register(ResponseBuilderCustomResponseFilter.class);
            Response response = client.target(generateURL("/test")).request().get();
            Object resultObj = response.getEntity();
            String result = response.readEntity(String.class);
            int status = response.getStatus();
            Assertions.assertEquals("test", result);
            Assertions.assertEquals(200, status);
        } catch (ProcessingException pe) {
            Assertions.fail(pe.getMessage());
        }
    }
}
