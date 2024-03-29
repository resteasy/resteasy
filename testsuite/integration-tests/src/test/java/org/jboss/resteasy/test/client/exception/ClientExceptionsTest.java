package org.jboss.resteasy.test.client.exception;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.client.ClientTestBase;
import org.jboss.resteasy.test.client.exception.resource.ClientExceptionsCustomException;
import org.jboss.resteasy.test.client.exception.resource.ClientExceptionsCustomExceptionRequestFilter;
import org.jboss.resteasy.test.client.exception.resource.ClientExceptionsCustomExceptionResponseFilter;
import org.jboss.resteasy.test.client.exception.resource.ClientExceptionsData;
import org.jboss.resteasy.test.client.exception.resource.ClientExceptionsIOExceptionReaderInterceptor;
import org.jboss.resteasy.test.client.exception.resource.ClientExceptionsIOExceptionRequestFilter;
import org.jboss.resteasy.test.client.exception.resource.ClientExceptionsIOExceptionResponseFilter;
import org.jboss.resteasy.test.client.exception.resource.ClientExceptionsResource;
import org.jboss.resteasy.test.client.exception.resource.ClientExceptionsRuntimeExceptionRequestFilter;
import org.jboss.resteasy.test.client.exception.resource.ClientExceptionsRuntimeExceptionResponseFilter;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:kanovotn@redhat.com">Katerina Novotna</a>
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test if client throws exceptions as described in JAXRS-2.0 specification:
 *                    "When a provider method throws an exception, the JAX-RS client runtime will map it to an instance of
 *                    ProcessingException if thrown while processing a request, and to a ResponseProcessingException
 *                    if thrown while processing a response."
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ClientExceptionsTest extends ClientTestBase {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientExceptionsTest.class.getSimpleName());
        war.addClass(ClientExceptionsData.class);
        return TestUtil.finishContainerPrepare(war, null, ClientExceptionsResource.class);
    }

    @BeforeEach
    public void before() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void close() {
        client.close();
    }

    /**
     * @tpTestDetails Send a request for entity which requires special MessageBodyReader which is not available.
     *                The exception is raised before sending request to the server
     * @tpPassCrit ProcessingException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void noMessageBodyReaderExistsTest() {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/") + "senddata");
                    base.request().post(Entity.xml(new ClientExceptionsData("test", "test")));
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails Send a request and try to read Response for entity which requires special MessageBodyReader,
     *                which is not available. The response contains entity of type Data.
     *                The exception is raised when trying to read response from the server.
     * @tpPassCrit ProcessingException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void noMessageBodyReaderExistsReadEntityTest() throws Exception {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/") + "data");

                    Response response = base.request().accept("application/xml").get();
                    response.readEntity(ClientExceptionsData.class);
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails Send a request and try to read Response for entity which requires special MessageBodyReader,
     *                which is not available. The response contains header with CONTENT_LENGTH zero and no Data entity.
     * @tpPassCrit ProcessingException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void responseWithContentLengthZero() {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/") + "empty");

                    Response response = base.request().accept("application/xml").get();
                    response.readEntity(ClientExceptionsData.class);
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails WebTarget registers ReaderInterceptor and sends entity to a server. Then tries to read
     *                the response, but get interrupted by exception happening in ReaderInterceptor.
     * @tpPassCrit ProcessingException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void interceptorThrowsExceptionTest() {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/") + "post");
                    Response response = base.register(ClientExceptionsIOExceptionReaderInterceptor.class).request("text/plain")
                            .post(Entity.text("data"));

                    response.readEntity(ClientExceptionsData.class);
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails WebTarget registers ClientResponseFilter and sends request to a server. The processing of the response
     *                gets
     *                interrupted in the ClientResponseFilter by IOException and processing ends with
     *                ResponseProcessingException.
     * @tpPassCrit ResponseProcessingException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test()
    public void responseFilterThrowsIOExceptionTest() {
        ResponseProcessingException thrown = Assertions.assertThrows(ResponseProcessingException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/") + "get");
                    base.register(ClientExceptionsIOExceptionResponseFilter.class).request("text/plain").get();
                });
        Assertions.assertTrue(thrown instanceof ResponseProcessingException);
    }

    /**
     * @tpTestDetails WebTarget registers ClientRequestFilter and sends request to a server. The processing of the request gets
     *                interrupted in the ClientRequestFilter by IOException and processing ends with ProcessingException.
     * @tpPassCrit ProcessingException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test()
    public void requestFilterThrowsIOExceptionTest() {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/") + "get");
                    base.register(ClientExceptionsIOExceptionRequestFilter.class).request("text/plain").get();
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails WebTarget registers ClientResponseFilter and sends request to a server. The processing of the response
     *                gets interrupted by RuntimeException in the ClientResponseFilter and processing ends with
     *                ResponseProcessingException.
     * @tpPassCrit ResponseProcessingException is raised
     * @tpSince RESTEasy 3.0.24
     */
    @Test()
    public void responseFilterThrowsRuntimeExceptionTest() {
        /*
         * WebTarget base = client.target(generateURL("/") + "get");
         * base.register(ClientExceptionsRuntimeExceptionResponseFilter.class).request("text/plain").get();
         */
        ResponseProcessingException thrown = Assertions.assertThrows(ResponseProcessingException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/") + "get");
                    base.register(ClientExceptionsRuntimeExceptionResponseFilter.class).request("text/plain").get();
                });
        Assertions.assertTrue(thrown instanceof ResponseProcessingException);
    }

    /**
     * @tpTestDetails WebTarget registers ClientRequestFilter and sends request to a server. The processing of the request gets
     *                interrupted in the ClientRequestFilter by RuntimeException and processing ends with ProcessingException.
     * @tpPassCrit ProcessingException is raised
     * @tpSince RESTEasy 3.0.24
     */
    @Test()
    public void requestFilterThrowsRuntimeExceptionTest() {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    WebTarget base = client.target(generateURL("/") + "get");
                    base.register(ClientExceptionsRuntimeExceptionRequestFilter.class).request("text/plain").get();
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails WebTarget registers ClientResponseFilter and sends request to a server. The processing of the response
     *                gets interrupted by ClientExceptionsCustomException in the ClientResponseFilter and processing ends with
     *                ResponseProcessingException.
     * @tpTrackerLink RESTEASY-1591
     * @tpPassCrit ResponseProcessingException is raised
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void responseFilterThrowsCustomExceptionTest() {
        WebTarget base = client.target(generateURL("/") + "get");
        try {
            base.register(ClientExceptionsCustomExceptionResponseFilter.class).request("text/plain").get();
        } catch (ResponseProcessingException ex) {
            Assertions.assertEquals(ClientExceptionsCustomException.class.getCanonicalName() + ": custom message",
                    ex.getMessage());
        } catch (Throwable ex) {
            Assertions.fail(
                    "The exception thrown by client was not instance of jakarta.ws.rs.client.ResponseProcessingException");
        }
    }

    /**
     * @tpTestDetails WebTarget registers ClientRequestFilter and sends request to a server. The processing of the request gets
     *                interrupted in the ClientRequestFilter by ClientExceptionsCustomException and processing ends with
     *                ProcessingException.
     * @tpTrackerLink RESTEASY-1685, RESTEASY-1591
     * @tpPassCrit ProcessingException is raised
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void requestFilterThrowsCustomExceptionTest() {
        WebTarget base = client.target(generateURL("/") + "get");
        try {
            base.register(ClientExceptionsCustomExceptionRequestFilter.class).request("text/plain").get();
        } catch (ProcessingException ex) {
            Assertions.assertEquals(ClientExceptionsCustomException.class.getCanonicalName() + ": custom message",
                    ex.getMessage());
        } catch (Throwable ex) {
            Assertions.fail("The exception thrown by client was not instance of jakarta.ws.rs.ProcessingException");
        }
    }

}
