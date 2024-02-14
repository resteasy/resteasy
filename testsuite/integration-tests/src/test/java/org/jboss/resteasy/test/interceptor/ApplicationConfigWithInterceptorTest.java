package org.jboss.resteasy.test.interceptor;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.interceptor.resource.AddHeaderContainerResponseFilter;
import org.jboss.resteasy.test.interceptor.resource.ApplicationConfigWithInterceptorResource;
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
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.20
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ApplicationConfigWithInterceptorTest {
    private static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ApplicationConfigWithInterceptorTest.class.getSimpleName());
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, ApplicationConfigWithInterceptorResource.class,
                AddHeaderContainerResponseFilter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ApplicationConfigWithInterceptorTest.class.getSimpleName());
    }

    @BeforeAll
    public static void before() throws Exception {
        client = ResteasyClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    @Test
    public void testNormalReturn() throws Exception {
        doTest("/my/good", 200);
    }

    @Test
    public void testWebApplicationExceptionWithResponse() throws Exception {
        doTest("/my/bad", 409);
    }

    @Test
    public void testNoContentResponse() throws Exception {
        doTest("/my/123", 204, false);
    }

    private void doTest(String path, int expectedStatus) throws Exception {
        doTest(path, expectedStatus, true);
    }

    private void doTest(String path, int expectedStatus, boolean get) throws Exception {
        Builder builder = client.target(generateURL(path)).request();
        Response response = get ? builder.get() : builder.delete();
        Assertions.assertEquals(expectedStatus, response.getStatus());
        Assertions.assertNotNull(response.getHeaderString("custom-header"));
        response.close();
    }
}
