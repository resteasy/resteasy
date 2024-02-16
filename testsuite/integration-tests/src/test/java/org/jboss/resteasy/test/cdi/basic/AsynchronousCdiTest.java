package org.jboss.resteasy.test.cdi.basic;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.cdi.basic.resource.AsynchronousResource;
import org.jboss.resteasy.test.cdi.basic.resource.AsynchronousStateless;
import org.jboss.resteasy.test.cdi.basic.resource.AsynchronousStatelessLocal;
import org.jboss.resteasy.test.cdi.util.UtilityProducer;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter CDI
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for asynchronous behavior of RESTEasy with CDI.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AsynchronousCdiTest {

    public static final Long DELAY = 5000L;

    protected static final Logger log = Logger.getLogger(AsynchronousCdiTest.class.getName());

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AsynchronousCdiTest.class.getSimpleName());
    }

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(AsynchronousCdiTest.class.getSimpleName());
        war.addClasses(UtilityProducer.class)
                .addClasses(AsynchronousStatelessLocal.class, AsynchronousStateless.class)
                .addClasses(AsynchronousResource.class, AsynchronousCdiTest.class)
                .addAsWebInfResource(TestUtil.createBeansXml(), "beans.xml");
        return war;
    }

    /**
     * @tpTestDetails Delay is in stateless bean.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAsynchJaxRs() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget base = client.target(generateURL("/asynch/simple"));

        long start = System.currentTimeMillis();
        Response response = base.request().get();

        MatcherAssert.assertThat("Response was sent before delay elapsed", System.currentTimeMillis() - start,
                is(greaterThan(DELAY)));
        Assertions.assertEquals(200, response.getStatus());
        client.close();
    }

    /**
     * @tpTestDetails Delay is in RESTEasy resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAsynchResourceAsynchEJB() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget base = client.target(generateURL("/asynch/ejb"));

        long start = System.currentTimeMillis();
        Response response = base.request().get();

        MatcherAssert.assertThat("Response was sent before delay elapsed", System.currentTimeMillis() - start,
                is(greaterThan(DELAY)));
        Assertions.assertEquals(200, response.getStatus());
        client.close();
    }
}
