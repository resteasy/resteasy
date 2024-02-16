package org.jboss.resteasy.test.interceptor;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.interceptor.resource.ReaderInterceptorContextInterceptor;
import org.jboss.resteasy.test.interceptor.resource.ReaderInterceptorContextResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Verify ReaderInterceptorContext.getHeaders() returns mutable map: RESTEASY-2298.
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.2.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ReaderInterceptorContextTest {
    private static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ReaderInterceptorContextTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ReaderInterceptorContextInterceptor.class,
                ReaderInterceptorContextResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ReaderInterceptorContextTest.class.getSimpleName());
    }

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    @Test
    public void testInterceptorHeaderMap() throws Exception {
        Response response = client.target(generateURL("/post")).request().post(Entity.entity("dummy", "text/plain"));
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("123789", response.readEntity(String.class));
    }
}
