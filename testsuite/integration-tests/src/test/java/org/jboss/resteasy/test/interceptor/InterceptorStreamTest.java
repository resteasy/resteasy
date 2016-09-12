package org.jboss.resteasy.test.interceptor;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.interceptor.resource.InterceptorStreamCustom;
import org.jboss.resteasy.test.interceptor.resource.InterceptorStreamResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.1.0
 * @tpTestCaseDetails Change InputStream and OutputStream in ReaderInterceptor and WriterInterceptor
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InterceptorStreamTest {
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(InterceptorStreamTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, InterceptorStreamResource.class, InterceptorStreamCustom.class);
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
        return PortProviderUtil.generateURL(path, InterceptorStreamTest.class.getSimpleName());
    }
    /**
     * @tpTestDetails Use ReaderInterceptor and WriterInterceptor together
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testPriority() throws Exception {
        Response response = client.target(generateURL("/test")).request().post(Entity.text("test"));
        response.bufferEntity();
        Assert.assertEquals("Wrong response status, interceptors don't work correctly", HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("Wrong content of response, interceptors don't work correctly", "writer_interceptor_testtest", response.readEntity(String.class));

    }
}
