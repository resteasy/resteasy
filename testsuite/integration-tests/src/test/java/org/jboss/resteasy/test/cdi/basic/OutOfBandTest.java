package org.jboss.resteasy.test.cdi.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.cdi.basic.resource.OutOfBandResource;
import org.jboss.resteasy.test.cdi.basic.resource.OutOfBandResourceIntf;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1049.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class OutOfBandTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive("RESTEASY-1008")
                .addClasses(OutOfBandResourceIntf.class, OutOfBandResource.class);
        return war;
    }

    /**
     * @tpTestDetails JAX-RS resource methods can be called outside the context of a servlet request, leading to NPEs.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTimerInterceptor() throws Exception {
        Client client = ClientBuilder.newClient();

        // Schedule timer.
        WebTarget base = client.target(PortProviderUtil.generateURL("/timer/schedule", "RESTEASY-1008"));
        Response response = base.request().get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        // Verify timer expired and timer interceptor was executed.
        base = client.target(PortProviderUtil.generateURL("/timer/test", "RESTEASY-1008"));
        response = base.request().get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        client.close();
    }
}
