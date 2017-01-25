package org.jboss.resteasy.test.interceptor;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.interceptor.resource.CustomException;
import org.jboss.resteasy.test.interceptor.resource.ThrowCustomExceptionResponseFilter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.20
 * @tpTestCaseDetails Throw custom exception from a ClientResponseFilter [RESTEASY-1591]
 */
@RunWith(Arquillian.class)
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

    @Before
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @After
    public void cleanup() {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResponseFilterCustomExceptionTest.class.getSimpleName());
    }
    /**
     * @tpTestDetails Use ClientResponseFilter
     * @tpSince RESTEasy 3.1.0
     */
    @Test(expected = CustomException.class)
    public void testThrowCustomException() throws Exception {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        factory.register(ThrowCustomExceptionResponseFilter.class);
        client.register(ThrowCustomExceptionResponseFilter.class);
        client.target(generateURL("/testCustomException")).request().post(Entity.text("testCustomException"));
    }
}
