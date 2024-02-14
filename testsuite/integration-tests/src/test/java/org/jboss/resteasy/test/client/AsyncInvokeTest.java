package org.jboss.resteasy.test.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.resource.AsyncInvokeResource;
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
public class AsyncInvokeTest extends ClientTestBase {

    @java.lang.annotation.Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @HttpMethod("PATCH")
    public @interface PATCH {
    }

    static Client client;
    static Client nioClient;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(AsyncInvokeTest.class.getSimpleName());
        war.addClass(AsyncInvokeTest.class);
        war.addClass(ClientTestBase.class);
        return TestUtil.finishContainerPrepare(war, null, AsyncInvokeResource.class);
    }

    @BeforeEach
    public void init() {
        client = ClientBuilder.newClient();

        nioClient = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).useAsyncHttpEngine().build();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
        nioClient.close();
    }

    /**
     * @tpTestDetails Client sends async GET requests. First request expects Response object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncGetTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().get();
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("get", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().async().get(String.class);
            String entity = future.get();
            Assertions.assertEquals("get", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async DELETE requests. First request expects Response object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncDeleteTest() throws Exception {

        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().delete();
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("delete", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().async().delete(String.class);
            String entity = future.get();
            Assertions.assertEquals("delete", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async PUT requests. First request expects Response object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncPutTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().put(Entity.text("hello"));
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("put hello", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().async().put(Entity.text("hello"),
                    String.class);
            String entity = future.get();
            Assertions.assertEquals("put hello", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async POST requests. First request expects Response object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncPostTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().post(Entity.text("hello"));
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("post hello", entity);

        }

        {
            Future<String> future = client.target(generateURL("/test")).request().async().post(Entity.text("hello"),
                    String.class);
            String entity = future.get();
            Assertions.assertEquals("post hello", entity);

        }
    }

    /**
     * @tpTestDetails Client sends async custom PATCH requests. First request expects Response object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncCustomMethodTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().method("PATCH",
                    Entity.text("hello"));
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("patch hello", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().async().method("PATCH", Entity.text("hello"),
                    String.class);
            String entity = future.get();
            Assertions.assertEquals("patch hello", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async GET requests using Asynchronous InvocationCallback. First request expects Response
     *                object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncCallbackGetTest() throws Exception {
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<Response> future = nioClient.target(generateURL("/test")).request().async()
                    .get(new InvocationCallback<Response>() {
                        @Override
                        public void completed(Response response) {
                            String entity = response.readEntity(String.class);
                            Assertions.assertEquals("get", entity);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable error) {
                        }
                    });
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                    "Asynchronous invocation didn't use custom implemented Invocation callback");

        }

        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<String> future = nioClient.target(generateURL("/test")).request().async()
                    .get(new InvocationCallback<String>() {
                        @Override
                        public void completed(String entity) {
                            Assertions.assertEquals("get", entity);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable error) {
                        }
                    });
            String entity = future.get();
            Assertions.assertEquals("get", entity);
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                    "Asynchronous invocation didn't use custom implemented Invocation callback");
        }
    }

    /**
     * @tpTestDetails Client sends async DELETE requests using Asynchronous InvocationCallback. First request expects Response
     *                object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncCallbackDeleteTest() throws Exception {

        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<Response> future = nioClient.target(generateURL("/test")).request().async()
                    .delete(new InvocationCallback<Response>() {
                        @Override
                        public void completed(Response response) {
                            String entity = response.readEntity(String.class);
                            Assertions.assertEquals("delete", entity);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable error) {
                        }
                    });
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                    "Asynchronous invocation didn't use custom implemented Invocation callback");
        }

        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<String> future = nioClient.target(generateURL("/test")).request().async()
                    .delete(new InvocationCallback<String>() {
                        @Override
                        public void completed(String s) {
                            Assertions.assertEquals("delete", s);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable error) {
                        }
                    });
            String entity = future.get();
            Assertions.assertEquals("delete", entity);
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                    "Asynchronous invocation didn't use custom implemented Invocation callback");
        }
    }

    /**
     * @tpTestDetails Client sends async PUT requests using Asynchronous InvocationCallback. First request expects Response
     *                object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncCallbackPutTest() throws Exception {
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().put(Entity.text("hello"),
                    new InvocationCallback<Response>() {
                        @Override
                        public void completed(Response response) {
                            String entity = response.readEntity(String.class);
                            Assertions.assertEquals("put hello", entity);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable error) {
                        }
                    });
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                    "Asynchronous invocation didn't use custom implemented Invocation callback");

        }
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<String> future = nioClient.target(generateURL("/test")).request().async().put(Entity.text("hello"),
                    new InvocationCallback<String>() {
                        @Override
                        public void completed(String s) {
                            Assertions.assertEquals("put hello", s);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable error) {
                        }
                    });
            String entity = future.get();
            Assertions.assertEquals("put hello", entity);
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                    "Asynchronous invocation didn't use custom implemented Invocation callback");

        }
    }

    /**
     * @tpTestDetails Client sends async POST requests using Asynchronous InvocationCallback. First request expects Response
     *                object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncCallbackPostTest() throws Exception {
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().post(Entity.text("hello"),
                    new InvocationCallback<Response>() {
                        @Override
                        public void completed(Response response) {
                            String entity = response.readEntity(String.class);
                            Assertions.assertEquals("post hello", entity);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable error) {
                        }
                    });
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                    "Asynchronous invocation didn't use custom implemented Invocation callback");

        }
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<String> future = nioClient.target(generateURL("/test")).request().async().post(Entity.text("hello"),
                    new InvocationCallback<String>() {
                        @Override
                        public void completed(String s) {
                            Assertions.assertEquals("post hello", s);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable error) {
                        }
                    });
            String entity = future.get();
            Assertions.assertEquals("post hello", entity);
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                    "Asynchronous invocation didn't use custom implemented Invocation callback");

        }
    }

    /**
     * @tpTestDetails Client sends async custom PATCH requests using Asynchronous InvocationCallback. First request expects
     *                Response object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncCallbackCustomMethodTest() throws Exception {
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().method("PATCH",
                    Entity.text("hello"), new InvocationCallback<Response>() {
                        @Override
                        public void completed(Response response) {
                            String entity = response.readEntity(String.class);
                            Assertions.assertEquals("patch hello", entity);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable error) {
                        }
                    });
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                    "Asynchronous invocation didn't use custom implemented Invocation callback");

        }
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<String> future = nioClient.target(generateURL("/test")).request().async().method("PATCH",
                    Entity.text("hello"), new InvocationCallback<String>() {
                        @Override
                        public void completed(String s) {
                            Assertions.assertEquals("patch hello", s);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable error) {
                        }
                    });
            String entity = future.get();
            Assertions.assertEquals("patch hello", entity);
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS),
                    "Asynchronous invocation didn't use custom implemented Invocation callback");

        }
    }

    @Test
    public void AsyncCallbackExceptionHandlingTest() throws Exception {
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<Response> future = nioClient.target(generateURL("/test")).request().async()
                    .get(new InvocationCallback<Response>() {
                        @Override
                        public void completed(Response response) {
                            String entity = response.readEntity(String.class);
                            Assertions.assertEquals("get", entity);
                            latch.countDown();
                            throw new RuntimeException("for the test of it");
                        }

                        @Override
                        public void failed(Throwable error) {
                        }
                    });
            Assertions.assertTrue(latch.await(15, TimeUnit.SECONDS));
            Response res = future.get();
            Assertions.assertEquals(200, res.getStatus()); // must not see the runtimeexception of the callback
        }

        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<String> future = nioClient.target(generateURL("/test")).request().async()
                    .get(new InvocationCallback<String>() {
                        @Override
                        public void completed(String s) {
                            Assertions.assertEquals("get", s);
                            latch.countDown();
                            throw new RuntimeException("for the test of it");
                        }

                        @Override
                        public void failed(Throwable error) {
                        }
                    });
            Assertions.assertTrue(latch.await(15, TimeUnit.SECONDS));
            String entity = future.get();
            Assertions.assertEquals("get", entity); // must not see the runtimeexception of the callback
        }
    }

    /**
     * @tpTestDetails Client sends async GET requests using submit() method to send request as asynchronous
     *                (opposite of invoke() method). First request expects Response object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void SubmitGetTest() throws Exception {

        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().buildGet().submit();
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("get", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().buildGet().submit(String.class);
            String entity = future.get();
            Assertions.assertEquals("get", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async DELETE requests using submit() method to send request as asynchronous
     *                (opposite of invoke() method). First request expects Response object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void SubmitDeleteTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().buildDelete().submit();
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("delete", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().buildDelete().submit(String.class);
            String entity = future.get();
            Assertions.assertEquals("delete", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async PUT requests using submit() method to send request as asynchronous
     *                (opposite of invoke() method). First request expects Response object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void SubmitPutTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().buildPut(Entity.text("hello")).submit();
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("put hello", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().buildPut(Entity.text("hello"))
                    .submit(String.class);
            String entity = future.get();
            Assertions.assertEquals("put hello", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async POST requests using submit() method to send request as asynchronous
     *                (opposite of invoke() method). First request expects Response object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void SubmitPostTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().buildPost(Entity.text("hello")).submit();
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("post hello", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().buildPost(Entity.text("hello"))
                    .submit(String.class);
            String entity = future.get();
            Assertions.assertEquals("post hello", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async custom PATCH requests using submit() method to send request as asynchronous
     *                (opposite of invoke() method). First request expects Response object in return,
     *                the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void SubmitCustomMethodTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().build("PATCH", Entity.text("hello"))
                    .submit();
            Response res = future.get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assertions.assertEquals("patch hello", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().build("PATCH", Entity.text("hello"))
                    .submit(String.class);
            String entity = future.get();
            Assertions.assertEquals("patch hello", entity);
        }
    }
}
