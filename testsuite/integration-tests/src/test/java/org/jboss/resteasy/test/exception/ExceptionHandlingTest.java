package org.jboss.resteasy.test.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.exception.resource.ExceptionHandlingProvider;
import org.jboss.resteasy.test.exception.resource.ExceptionHandlingResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince EAP 7.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ExceptionHandlingTest {

    protected static final Logger logger = LogManager.getLogger(ExceptionHandlingTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ExceptionHandlingTest.class.getSimpleName());
        war.addClass(ExceptionHandlingTest.class);
        return TestUtil.finishContainerPrepare(war, null, ExceptionHandlingResource.class, ExceptionHandlingProvider.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
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
     * which is handled by ExceptionMapper.
     * @tpPassCrit The response with expected Exception text is returned
     * @tpSince EAP 7.0.0
     */
    @Test
    public void testThrowsException() throws Exception {

        ThrowsExceptionInterface proxy = client.target(generateURL("/")).proxy(ThrowsExceptionInterface.class);
        try {
            proxy.post();
        } catch (InternalServerErrorException e) {
            Response response = e.getResponse();
            String errorText = response.readEntity(String.class);
            logger.info("Error text: " + errorText);
            Assert.assertNotNull("Missing the expected exception text", errorText);
        }

    }

}
