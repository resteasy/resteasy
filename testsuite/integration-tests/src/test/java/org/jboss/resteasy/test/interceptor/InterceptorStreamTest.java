package org.jboss.resteasy.test.interceptor;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.interceptor.resource.InterceptorStreamCustom;
import org.jboss.resteasy.test.interceptor.resource.InterceptorStreamResource;
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
 * @tpSince RESTEasy 3.1.0
 * @tpTestCaseDetails Change InputStream and OutputStream in ReaderInterceptor and WriterInterceptor
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class InterceptorStreamTest {
    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(InterceptorStreamTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, InterceptorStreamResource.class, InterceptorStreamCustom.class);
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
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus(),
                "Wrong response status, interceptors don't work correctly");
        Assertions.assertEquals("writer_interceptor_testtest", response.readEntity(String.class),
                "Wrong content of response, interceptors don't work correctly");

    }
}
