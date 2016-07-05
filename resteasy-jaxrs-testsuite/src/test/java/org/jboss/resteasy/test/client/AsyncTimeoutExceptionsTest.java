package org.jboss.resteasy.test.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.junit.*;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Tests client exception handling for AsyncInvoker interface and InvocationCallBack interface.
 */
public class AsyncTimeoutExceptionsTest extends BaseResourceTest {

    static final Logger logger = Logger.getLogger(AsyncTimeoutExceptionsTest.class.getName());

    @XmlRootElement
    public static class Sticker {
        private String name;

        @XmlElement
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Path("/")
    public static class Resource {

        @GET
        @Path("/sticker")
        @Produces("application/xml")
        public Sticker sticker() throws InterruptedException {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Sticker sticker = new Sticker();
            sticker.setName("turtle");
            return sticker;
        }

        @GET
        @Path("/get")
        public Response get() throws InterruptedException {
            Thread.sleep(10000);
            return Response.ok().build();
        }
    }

    Client client;

    @Before
    public void setUp() throws Exception {
        addPerRequestResource(Resource.class);
        client = ClientBuilder.newClient();
    }

    @After
    public void close() {
        client.close();
    }

    public static class StickerCallback implements InvocationCallback<Sticker> {

        @Override
        public void completed(Sticker sticker) {
            logger.info(sticker.getName());
        }

        @Override
        public void failed(Throwable throwable) {
            if (throwable instanceof TimeoutException) {
                logger.info(throwable.toString());
            } else {
                throwable.printStackTrace();
            }
        }
    }

    public static class ResponseCallback implements InvocationCallback<Response>
    {

        @Override
        public void completed(Response response) {
            logger.info("OK");
        }

        @Override
        public void failed(Throwable throwable) {
            if (throwable instanceof TimeoutException) {
                logger.info(throwable.toString());
            } else {
                throwable.printStackTrace();
            }
        }
    }

    /*
     * Future get() method is called with timeout parameter, resulting to TimeoutException being thrown.
     * Resource invokes Thread.Sleep(), client is expected to throw TimeoutExcetion.
     */
    @Test(expected = TimeoutException.class)
    public void futureTimeOutSleepTest() throws InterruptedException, ExecutionException, TimeoutException {
        WebTarget base = client.target(generateURL("/sticker"));
        Future<Sticker> future = base.request().async().get(Sticker.class);
        Sticker stickerName = future.get(5, TimeUnit.SECONDS);
    }

    /*
     * Future get() method is called with timeout parameter, resulting to TimeoutException being thrown.
     * Resource invokes Thread.Sleep(), client is expected to throw TimeoutException.
     * The resource is supposed to return Response object.
     */
    @Test(expected = TimeoutException.class)
    public void futureTimeOutWithResponseTest() throws InterruptedException, ExecutionException, TimeoutException {
        WebTarget base = client.target(generateURL("/get"));
        Future<Response> future = base.request().async().get();
        Response response = future.get(5, TimeUnit.SECONDS);
    }

    //=============================================================================================================
    // Invocation callbacks
    //=============================================================================================================

    /*
     * Invocation callback should close all connections by itself
     * Resource invokes Thread.Sleep(), client is expected to throw TimeoutExcetion.
     */
    @Test(expected = TimeoutException.class)
    public void invocationCallbackTimeoutSleepTest() throws InterruptedException, ExecutionException, TimeoutException {
        WebTarget base = client.target(generateURL("/sticker"));
        Future<Sticker> future = base.request().async().get(new StickerCallback());
        future.get(5, TimeUnit.SECONDS);
    }

    /*
     * Invocation callback should close all connections by itself
     * Resource invokes Thread.Sleep(), client is expected to throw TimeoutException.
     * The resource is supposed to return Response object.
     */
    @Test(expected = TimeoutException.class)
    public void invocationCallbackTimeoutWithResponseTest() throws InterruptedException, ExecutionException, TimeoutException {
        WebTarget base = client.target(generateURL("/get"));
        Future<Response> future = base.request().async().get(new ResponseCallback());
        future.get(5, TimeUnit.SECONDS);
    }


}
