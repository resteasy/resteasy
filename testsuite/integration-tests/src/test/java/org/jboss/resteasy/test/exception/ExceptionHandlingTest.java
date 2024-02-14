package org.jboss.resteasy.test.exception;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.exception.resource.ExceptionHandlingProvider;
import org.jboss.resteasy.test.exception.resource.ExceptionHandlingResource;
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
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ExceptionHandlingTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ExceptionHandlingTest.class.getSimpleName());
        war.addClass(ExceptionHandlingTest.class);
        return TestUtil.finishContainerPrepare(war, null, ExceptionHandlingResource.class, ExceptionHandlingProvider.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ExceptionHandlingTest.class.getSimpleName());
    }

    @Path("/")
    public interface ThrowsExceptionInterface {
        @Path("test")
        @POST
        void post() throws Exception;
    }

    /**
     * @tpTestDetails POST request is sent by client via client proxy. The resource on the server throws exception,
     *                which is handled by ExceptionMapper.
     * @tpPassCrit The response with expected Exception text is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testThrowsException() throws Exception {

        ThrowsExceptionInterface proxy = client.target(generateURL("/")).proxy(ThrowsExceptionInterface.class);
        try {
            proxy.post();
        } catch (InternalServerErrorException e) {
            Response response = e.getResponse();
            String errorText = response.readEntity(String.class);
            Assertions.assertNotNull("Missing the expected exception text", errorText);
        }

    }

}
