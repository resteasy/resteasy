package org.jboss.resteasy.test.cdi.interceptors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.cdi.interceptors.resource.TimerInterceptorResource;
import org.jboss.resteasy.test.cdi.interceptors.resource.TimerInterceptorResourceIntf;
import org.jboss.resteasy.test.cdi.util.UtilityProducer;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for interceptors with timer service.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class TimerInterceptorTest {
    protected static final Logger log = Logger.getLogger(TimerInterceptorTest.class.getName());

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(TimerInterceptorTest.class.getSimpleName())
                .addClasses(UtilityProducer.class, PortProviderUtil.class)
                .addClasses(TimerInterceptorResourceIntf.class, TimerInterceptorResource.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, TimerInterceptorTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Timer is sheduled and than is called.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTimerInterceptor() throws Exception {
        Client client = ClientBuilder.newClient();

        // Schedule timer.
        WebTarget base = client.target(generateURL("/timer/schedule"));
        Response response = base.request().get();
        log.info("Status: " + response.getStatus());
        assertEquals(200, response.getStatus());
        response.close();

        // Verify timer expired and timer interceptor was executed.
        base = client.target(generateURL("/timer/test"));
        response = base.request().get();
        log.info("Status: " + response.getStatus());
        assertEquals(200, response.getStatus());
        response.close();

        client.close();
    }
}
