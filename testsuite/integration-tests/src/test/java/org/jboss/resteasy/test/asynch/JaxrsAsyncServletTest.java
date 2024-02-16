package org.jboss.resteasy.test.asynch;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlRootElementProvider;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletApp;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletAsyncResponseBlockingQueue;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletJaxrsResource;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletPrintingErrorHandler;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletResource;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletServiceUnavailableExceptionMapper;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletTimeoutHandler;
import org.jboss.resteasy.test.asynch.resource.JaxrsAsyncServletXmlData;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TimeoutUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Asynchronous RESTEasy
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for asyncHttpServlet module. Check cooperation during more requests and exception mapping.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class JaxrsAsyncServletTest {

    static ResteasyClient client;

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, AsyncServletTest.class.getSimpleName() + ".war");
        war.addClasses(JaxrsAsyncServletXmlData.class, JaxrsAsyncServletAsyncResponseBlockingQueue.class,
                JaxrsAsyncServletJaxrsResource.class,
                JaxrsAsyncServletApp.class,
                JaxrsAsyncServletTimeoutHandler.class, JaxrsAsyncServletResource.class,
                JaxrsAsyncServletPrintingErrorHandler.class,
                JaxrsAsyncServletServiceUnavailableExceptionMapper.class, JaxrsAsyncServletXmlData.class);
        war.addAsWebInfResource(AsyncPostProcessingTest.class.getPackage(), "JaxrsAsyncServletWeb.xml", "web.xml");
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AsyncServletTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Check ForbiddenException throwing during async request. Try to inject un-exist bean.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testInjectionFailure() throws Exception {
        long start = System.currentTimeMillis();
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/jaxrs/injection-failure/abcd")).request().get();
        Assertions.assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), response.getStatus());
        Assertions.assertTrue(
                response.readEntity(String.class).contains(NotFoundException.class.getName()),
                "ForbiddenException was not thrown");
        long end = System.currentTimeMillis() - start;
        Assertions.assertTrue(end < 1000, "Wrong time of request"); // should take less than 1 second
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Check ForbiddenException throwing during async request. No injection of some beans.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMethodFailure() throws Exception {
        long start = System.currentTimeMillis();
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/jaxrs/method-failure")).request().get();
        Assertions.assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), response.getStatus());
        Assertions.assertTrue(response.readEntity(String.class).contains(ForbiddenException.class.getName()),
                "ForbiddenException was not thrown");
        long end = System.currentTimeMillis() - start;
        Assertions.assertTrue(end < 1000, "Wrong time of request"); // should take less than 1 second
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Try to get xml response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAsync() throws Exception {
        Client client = ClientBuilder.newClient();
        long start = System.currentTimeMillis();
        Response response = client.target(generateURL("/jaxrs")).request().get();
        long end = System.currentTimeMillis() - start;
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("hello", response.readEntity(String.class),
                "Wrong content of response");
        // The time out is set to 2 seconds, this is a best guess test and if future failures are present this should be
        // reconsidered with some sort of offset.
        Assertions.assertTrue(end < 2000, "Wrong time of request");
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Check timeout exception
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTimeout() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/jaxrs/timeout")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_REQUEST_TIMEOUT, response.getStatus()); // exception mapper from another test overrides 503 to 408
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Test cooperation between two requests. Use 408 HTTP status.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCancel() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client.target(generateURL("/jaxrs/cancel")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_SERVICE_UNAVAILABLE, response.getStatus());
        response.close();

        // It is possible, that thread created in JaxrsAsyncServletJaxrsResource.cancel method
        // don't finish before next request is called. We need to wait some time and do this request again.
        // Default timeout is 20s
        boolean ok = false;
        for (int i = 0; i < TimeoutUtil.adjust(20); i++) {
            response = client.target(generateURL("/jaxrs/cancelled")).request().get();
            int status = response.getStatus();
            response.close();
            if (status == HttpResponseCodes.SC_NO_CONTENT) {
                ok = true;
                break;
            }
            Thread.sleep(1000);
        }
        Assertions.assertTrue(ok, "Response was not canceled correctly");
        client.close();
    }

    /**
     * @tpTestDetails Test cooperation between two requests. Use 200 HTTP status.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResumeObject() throws Exception {
        Client client = ClientBuilder.newClient();
        client.register(JAXBXmlRootElementProvider.class);
        long start = System.currentTimeMillis();
        Response response = client.target(generateURL("/jaxrs/resume/object")).request().get();
        long end = System.currentTimeMillis() - start;
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("bill",
                response.readEntity(JaxrsAsyncServletXmlData.class).getName(),
                "Wrong content of response");
        Assertions.assertTrue(end < 1500, "Wrong time of request");
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails Create response in new thread.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResumeObjectThread() throws Exception {
        Client client = ClientBuilder.newClient();
        client.register(JAXBXmlRootElementProvider.class);
        long start = System.currentTimeMillis();
        Response response = client.target(generateURL("/jaxrs/resume/object/thread")).request().get();
        long end = System.currentTimeMillis() - start;
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("bill",
                response.readEntity(JaxrsAsyncServletXmlData.class).getName(),
                "Wrong content of response");
        Assertions.assertTrue(end < 1000, "Wrong time of request"); // should take less than 1 second
        response.close();
        client.close();
    }
}
