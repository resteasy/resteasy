package org.jboss.resteasy.test.client;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.resource.AsyncInvokeResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AsyncInvokeTest extends ClientTestBase{

    @java.lang.annotation.Target({ElementType.METHOD})
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

    @Before
    public void init() {
        client = ClientBuilder.newClient();
        
        nioClient = new ResteasyClientBuilder().useAsyncHttpEngine().build();
    }

    @After
    public void after() throws Exception {
        client.close();
        nioClient.close();
    }

    /**
     * @tpTestDetails Client sends async GET requests. First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncGetTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().get();
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("get", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().async().get(String.class);
            String entity = future.get();
            Assert.assertEquals("get", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async DELETE requests. First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncDeleteTest() throws Exception {

        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().delete();
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("delete", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().async().delete(String.class);
            String entity = future.get();
            Assert.assertEquals("delete", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async PUT requests. First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncPutTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().put(Entity.text("hello"));
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("put hello", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().async().put(Entity.text("hello"), String.class);
            String entity = future.get();
            Assert.assertEquals("put hello", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async POST requests. First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncPostTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().post(Entity.text("hello"));
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("post hello", entity);

        }

        {
            Future<String> future = client.target(generateURL("/test")).request().async().post(Entity.text("hello"), String.class);
            String entity = future.get();
            Assert.assertEquals("post hello", entity);

        }
    }

    /**
     * @tpTestDetails Client sends async custom PATCH requests. First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncCustomMethodTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().method("PATCH", Entity.text("hello"));
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("patch hello", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().async().method("PATCH", Entity.text("hello"), String.class);
            String entity = future.get();
            Assert.assertEquals("patch hello", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async GET requests using Asynchronous InvocationCallback. First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncCallbackGetTest() throws Exception {
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().get(new InvocationCallback<Response>() {
                @Override
                public void completed(Response response) {
                    String entity = response.readEntity(String.class);
                    Assert.assertEquals("get", entity);
                    latch.countDown();
                }

                @Override
                public void failed(Throwable error) {
                }
            });
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            Assert.assertTrue("Asynchronous invocation didn't use custom implemented Invocation callback", latch.await(5, TimeUnit.SECONDS));

        }

        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<String> future = nioClient.target(generateURL("/test")).request().async().get(new InvocationCallback<String>() {
                @Override
                public void completed(String entity) {
                    Assert.assertEquals("get", entity);
                    latch.countDown();
                }

                @Override
                public void failed(Throwable error) {
                }
            });
            String entity = future.get();
            Assert.assertEquals("get", entity);
            Assert.assertTrue("Asynchronous invocation didn't use custom implemented Invocation callback", latch.await(5, TimeUnit.SECONDS));
        }
    }

    /**
     * @tpTestDetails Client sends async DELETE requests using Asynchronous InvocationCallback. First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncCallbackDeleteTest() throws Exception {

        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().delete(new InvocationCallback<Response>() {
                @Override
                public void completed(Response response) {
                    String entity = response.readEntity(String.class);
                    Assert.assertEquals("delete", entity);
                    latch.countDown();
                }

                @Override
                public void failed(Throwable error) {
                }
            });
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            Assert.assertTrue("Asynchronous invocation didn't use custom implemented Invocation callback", latch.await(5, TimeUnit.SECONDS));
        }

        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<String> future = nioClient.target(generateURL("/test")).request().async().delete(new InvocationCallback<String>() {
                @Override
                public void completed(String s) {
                    Assert.assertEquals("delete", s);
                    latch.countDown();
                }

                @Override
                public void failed(Throwable error) {
                }
            });
            String entity = future.get();
            Assert.assertEquals("delete", entity);
            Assert.assertTrue("Asynchronous invocation didn't use custom implemented Invocation callback", latch.await(5, TimeUnit.SECONDS));
        }
    }


    /**
     * @tpTestDetails Client sends async PUT requests using Asynchronous InvocationCallback. First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncCallbackPutTest() throws Exception {
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().put(Entity.text("hello"), new InvocationCallback<Response>() {
                @Override
                public void completed(Response response) {
                    String entity = response.readEntity(String.class);
                    Assert.assertEquals("put hello", entity);
                    latch.countDown();
                }

                @Override
                public void failed(Throwable error) {
                }
            });
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            Assert.assertTrue("Asynchronous invocation didn't use custom implemented Invocation callback", latch.await(5, TimeUnit.SECONDS));

        }
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<String> future = nioClient.target(generateURL("/test")).request().async().put(Entity.text("hello"), new InvocationCallback<String>() {
                @Override
                public void completed(String s) {
                    Assert.assertEquals("put hello", s);
                    latch.countDown();
                }

                @Override
                public void failed(Throwable error) {
                }
            });
            String entity = future.get();
            Assert.assertEquals("put hello", entity);
            Assert.assertTrue("Asynchronous invocation didn't use custom implemented Invocation callback", latch.await(5, TimeUnit.SECONDS));

        }
    }

    /**
     * @tpTestDetails Client sends async POST requests using Asynchronous InvocationCallback. First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncCallbackPostTest() throws Exception {
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().post(Entity.text("hello"), new InvocationCallback<Response>() {
                @Override
                public void completed(Response response) {
                    String entity = response.readEntity(String.class);
                    Assert.assertEquals("post hello", entity);
                    latch.countDown();
                }

                @Override
                public void failed(Throwable error) {
                }
            });
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            Assert.assertTrue("Asynchronous invocation didn't use custom implemented Invocation callback", latch.await(5, TimeUnit.SECONDS));

        }
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<String> future = nioClient.target(generateURL("/test")).request().async().post(Entity.text("hello"), new InvocationCallback<String>() {
                @Override
                public void completed(String s) {
                    Assert.assertEquals("post hello", s);
                    latch.countDown();
                }

                @Override
                public void failed(Throwable error) {
                }
            });
            String entity = future.get();
            Assert.assertEquals("post hello", entity);
            Assert.assertTrue("Asynchronous invocation didn't use custom implemented Invocation callback", latch.await(5, TimeUnit.SECONDS));

        }
    }

    /**
     * @tpTestDetails Client sends async custom PATCH requests using Asynchronous InvocationCallback. First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void AsyncCallbackCustomMethodTest() throws Exception {
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<Response> future = nioClient.target(generateURL("/test")).request().async().method("PATCH", Entity.text("hello"), new InvocationCallback<Response>() {
                @Override
                public void completed(Response response) {
                    String entity = response.readEntity(String.class);
                    Assert.assertEquals("patch hello", entity);
                    latch.countDown();
                }

                @Override
                public void failed(Throwable error) {
                }
            });
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            Assert.assertTrue("Asynchronous invocation didn't use custom implemented Invocation callback", latch.await(5, TimeUnit.SECONDS));

        }
        {
            final CountDownLatch latch = new CountDownLatch(1);
            Future<String> future = nioClient.target(generateURL("/test")).request().async().method("PATCH", Entity.text("hello"), new InvocationCallback<String>() {
                @Override
                public void completed(String s) {
                    Assert.assertEquals("patch hello", s);
                    latch.countDown();
                }

                @Override
                public void failed(Throwable error) {
                }
            });
            String entity = future.get();
            Assert.assertEquals("patch hello", entity);
            Assert.assertTrue( "Asynchronous invocation didn't use custom implemented Invocation callback", latch.await(5, TimeUnit.SECONDS));

        }
    }
    
    @Test
    public void AsyncCallbackExceptionHandlingTest() throws Exception {
       {
          final CountDownLatch latch = new CountDownLatch(1);
          Future<Response> future = nioClient.target(generateURL("/test")).request().async().get(new InvocationCallback<Response>()
          {
             @Override
             public void completed(Response response)
             {
                String entity = response.readEntity(String.class);
                Assert.assertEquals("get", entity);
                latch.countDown();
                throw new RuntimeException("for the test of it");
             }

             @Override
             public void failed(Throwable error)
             {
             }
          });
          Assert.assertTrue(latch.await(15, TimeUnit.SECONDS));
          Response res = future.get();
          Assert.assertEquals(200, res.getStatus()); // must not see the runtimeexception of the callback
       }

       {
          final CountDownLatch latch = new CountDownLatch(1);
          Future<String> future = nioClient.target(generateURL("/test")).request().async().get(new InvocationCallback<String>()
          {
             @Override
             public void completed(String s)
             {
                Assert.assertEquals("get", s);
                latch.countDown();
                throw new RuntimeException("for the test of it");
             }

             @Override
             public void failed(Throwable error)
             {
             }
          });
          Assert.assertTrue(latch.await(15, TimeUnit.SECONDS));
          String entity = future.get();
          Assert.assertEquals("get", entity); // must not see the runtimeexception of the callback
       }
    }


    /**
     * @tpTestDetails Client sends async GET requests using submit() method to send request as asynchronous
     * (opposite of invoke() method). First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void SubmitGetTest() throws Exception {

        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().buildGet().submit();
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("get", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().buildGet().submit(String.class);
            String entity = future.get();
            Assert.assertEquals("get", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async DELETE requests using submit() method to send request as asynchronous
     * (opposite of invoke() method). First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void SubmitDeleteTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().buildDelete().submit();
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("delete", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().buildDelete().submit(String.class);
            String entity = future.get();
            Assert.assertEquals("delete", entity);
        }
    }

    /**
     * @tpTestDetails Client sends async PUT requests using submit() method to send request as asynchronous
     * (opposite of invoke() method). First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void SubmitPutTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().buildPut(Entity.text("hello")).submit();
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("put hello", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().buildPut(Entity.text("hello")).submit(String.class);
            String entity = future.get();
            Assert.assertEquals("put hello", entity);
        }
    }


    /**
     * @tpTestDetails Client sends async POST requests using submit() method to send request as asynchronous
     * (opposite of invoke() method). First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void SubmitPostTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().buildPost(Entity.text("hello")).submit();
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("post hello", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().buildPost(Entity.text("hello")).submit(String.class);
            String entity = future.get();
            Assert.assertEquals("post hello", entity);
        }
    }


    /**
     * @tpTestDetails Client sends async custom PATCH requests using submit() method to send request as asynchronous
     * (opposite of invoke() method). First request expects Response object in return,
     * the second expects String object in return
     * @tpPassCrit Successful response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void SubmitCustomMethodTest() throws Exception {
        {
            Future<Response> future = nioClient.target(generateURL("/test")).request().build("PATCH", Entity.text("hello")).submit();
            Response res = future.get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, res.getStatus());
            String entity = res.readEntity(String.class);
            Assert.assertEquals("patch hello", entity);
        }

        {
            Future<String> future = client.target(generateURL("/test")).request().build("PATCH", Entity.text("hello")).submit(String.class);
            String entity = future.get();
            Assert.assertEquals("patch hello", entity);
        }
    }
}
