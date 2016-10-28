package org.jboss.resteasy.test.cdi.basic;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.cdi.basic.resource.OutOfBandResource;
import org.jboss.resteasy.test.cdi.basic.resource.OutOfBandResourceIntf;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1049.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class OutOfBandTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive("RESTEASY-1008")
                .addClasses(OutOfBandResourceIntf.class, OutOfBandResource.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
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
