package org.jboss.resteasy.test.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.RxInvoker;
import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.client.SyncInvoker;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.internal.CompletionStageRxInvokerImpl;
import org.jboss.resteasy.test.client.resource.TestResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * @date March 9, 2016
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class RxInvokerTest extends ClientTestBase {
    private static final GenericType<String> STRING_TYPE = new GenericType<String>() {
    };

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(RxInvokerTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, TestResource.class);
    }

    public static class TestRxInvokerProvider implements RxInvokerProvider<TestRxInvoker> {

        @Override
        public boolean isProviderFor(Class<?> clazz) {
            return clazz.isAssignableFrom(TestRxInvoker.class);
        }

        @Override
        public TestRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService) {
            return new TestRxInvoker(syncInvoker, executorService);
        }

    }

    public static class TestRxInvoker extends CompletionStageRxInvokerImpl {
        public static volatile boolean used;

        public TestRxInvoker(final SyncInvoker builder) {
            super(builder);
            used = true;
        }

        public TestRxInvoker(final SyncInvoker builder, final ExecutorService executor) {
            super(builder, executor);
            used = true;
        }
    }

    static Client newClient(boolean useCustomInvoker) {
        TestRxInvoker.used = false;
        final Client client;
        if (useCustomInvoker) {
            client = ClientBuilder.newClient().register(TestRxInvokerProvider.class, RxInvokerProvider.class);
        } else {
            client = ClientBuilder.newClient();
        }
        return client;
    }

    @Test
    public void testRxClientGet() throws Exception {
        doTestRxClientGet(false);
        doTestRxClientGet(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientGet(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/get")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.get();
        String response = cs.get().readEntity(String.class);
        Assertions.assertEquals("get", response);
        Assertions.assertEquals(useCustomInvoker, TestRxInvoker.used);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientGetClass() throws Exception {
        doTestRxClientGetClass(false);
        doTestRxClientGetClass(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientGetClass(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/get")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.get(String.class);
        Assertions.assertEquals("get", cs.get());
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientGetGenericType() throws Exception {
        doTestRxClientGetGenericType(false);
        doTestRxClientGetGenericType(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientGetGenericType(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/get")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.get(STRING_TYPE);
        Assertions.assertEquals("get", cs.get());
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientPut() throws Exception {
        doTestRxClientPut(false);
        doTestRxClientPut(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientPut(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/put")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker
                .put(Entity.entity("put", MediaType.TEXT_PLAIN_TYPE));
        String response = cs.get().readEntity(String.class);
        Assertions.assertEquals("put", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientPutClass() throws Exception {
        doTestRxClientPutClass(false);
        doTestRxClientPutClass(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientPutClass(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/put")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.put(Entity.entity("put", MediaType.TEXT_PLAIN_TYPE),
                String.class);
        String response = cs.get();
        Assertions.assertEquals("put", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientPutGenericType() throws Exception {
        doTestRxClientPutGenericType(false);
        doTestRxClientPutGenericType(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientPutGenericType(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/put")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.put(Entity.entity("put", MediaType.TEXT_PLAIN_TYPE),
                STRING_TYPE);
        String response = cs.get();
        Assertions.assertEquals("put", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientPost() throws Exception {
        doTestRxClientPost(false);
        doTestRxClientPost(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientPost(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/post")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker
                .post(Entity.entity("post", MediaType.TEXT_PLAIN_TYPE));
        String response = cs.get().readEntity(String.class);
        Assertions.assertEquals("post", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientPostClass() throws Exception {
        doTestRxClientPostClass(false);
        doTestRxClientPostClass(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientPostClass(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/post")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker
                .post(Entity.entity("post", MediaType.TEXT_PLAIN_TYPE), String.class);
        String response = cs.get();
        Assertions.assertEquals("post", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientPostGenericType() throws Exception {
        dotestRxClientPostGenericType(false);
        dotestRxClientPostGenericType(true);
    }

    @SuppressWarnings("unchecked")
    void dotestRxClientPostGenericType(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/post")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker
                .post(Entity.entity("post", MediaType.TEXT_PLAIN_TYPE), STRING_TYPE);
        String response = cs.get();
        Assertions.assertEquals("post", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientDelete() throws Exception {
        doTestRxClientDelete(false);
        doTestRxClientDelete(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientDelete(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/delete")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.delete();
        String response = cs.get().readEntity(String.class);
        Assertions.assertEquals("delete", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientDeleteClass() throws Exception {
        doTestRxClientDeleteClass(false);
        doTestRxClientDeleteClass(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientDeleteClass(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/delete")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.delete(String.class);
        Assertions.assertEquals("delete", cs.get());
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientDeleteGenericType() throws Exception {
        doTestRxClientDeleteGenericType(false);
        doTestRxClientDeleteGenericType(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientDeleteGenericType(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/delete")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.delete(STRING_TYPE);
        Assertions.assertEquals("delete", cs.get());
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientHead() throws Exception {
        doTestRxClientHead(false);
        doTestRxClientHead(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientHead(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/head")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.head();
        Response response = cs.get();
        Assertions.assertEquals(204, response.getStatus());
        Assertions.assertEquals("head", response.getStringHeaders().getFirst("key"));
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientOptions() throws Exception {
        doTestRxClientOptions(false);
        doTestRxClientOptions(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientOptions(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/options")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.options();
        String response = cs.get().readEntity(String.class);
        Assertions.assertEquals("options", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientOptionsClass() throws Exception {
        doTestRxClientOptionsClass(false);
        doTestRxClientOptionsClass(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientOptionsClass(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/options")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.options(String.class);
        Assertions.assertEquals("options", cs.get());
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientOptionsGenericType() throws Exception {
        doTestRxClientOptionsGenericType(false);
        doTestRxClientOptionsGenericType(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientOptionsGenericType(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/options")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.options(STRING_TYPE);
        Assertions.assertEquals("options", cs.get());
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientTrace() throws Exception {
        doTestRxClientTrace(false);
        doTestRxClientTrace(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientTrace(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/trace")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.trace();
        String response = cs.get().readEntity(String.class);
        Assertions.assertEquals("trace", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientTraceClass() throws Exception {
        doTestRxClientTraceClass(false);
        doTestRxClientTraceClass(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientTraceClass(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/trace")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.trace(String.class);
        Assertions.assertEquals("trace", cs.get());
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientTraceGenericType() throws Exception {
        doTestRxClientTraceGenericType(false);
        doTestRxClientTraceGenericType(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientTraceGenericType(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/trace")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.trace(STRING_TYPE);
        Assertions.assertEquals("trace", cs.get());
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientMethod() throws Exception {
        doTestRxClientMethod(false);
        doTestRxClientMethod(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientMethod(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/method")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.method("METHOD");
        String response = cs.get().readEntity(String.class);
        Assertions.assertEquals("method", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientMethodClass() throws Exception {
        doTestRxClientMethodClass(false);
        doTestRxClientMethodClass(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientMethodClass(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/method")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", String.class);
        Assertions.assertEquals("method", cs.get());
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientMethodGenericType() throws Exception {
        doTestRxClientMethodGenericType(false);
        doTestRxClientMethodGenericType(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientMethodGenericType(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/method")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD", STRING_TYPE);
        Assertions.assertEquals("method", cs.get());
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientMethodEntity() throws Exception {
        doTestRxClientMethodEntity(false);
        doTestRxClientMethodEntity(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientMethodEntity(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/methodEntity")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<Response> cs = (CompletableFuture<Response>) invoker.method("METHOD",
                Entity.entity("methodEntity", MediaType.TEXT_PLAIN_TYPE));
        String response = cs.get().readEntity(String.class);
        Assertions.assertEquals("methodEntity", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientMethodClassEntity() throws Exception {
        doTestRxClientMethodClassEntity(false);
        doTestRxClientMethodClassEntity(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientMethodClassEntity(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/methodEntity")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD",
                Entity.entity("methodEntity", MediaType.TEXT_PLAIN_TYPE), String.class);
        String response = cs.get();
        Assertions.assertEquals("methodEntity", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    @Test
    public void testRxClientMethodGenericTypeEntity() throws Exception {
        doTestRxClientMethodGenericTypeEntity(false);
        doTestRxClientMethodGenericTypeEntity(true);
    }

    @SuppressWarnings("unchecked")
    void doTestRxClientMethodGenericTypeEntity(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/methodEntity")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        CompletableFuture<String> cs = (CompletableFuture<String>) invoker.method("METHOD",
                Entity.entity("methodEntity", MediaType.TEXT_PLAIN_TYPE), STRING_TYPE);
        String response = cs.get();
        Assertions.assertEquals("methodEntity", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }

    /**
     * @tpTestDetails end-point method returns String data after some delay (3s)
     *                client use RxInvoker. Data should not be prepared right after CompletionStage object are returned from
     *                client
     *                CompletionStage should return correct data after 3s delay
     * @tpSince RESTEasy 3.5
     */
    @Test
    public void testGetDataWithDelay() throws Exception {
        doTestGetDataWithDelay(false);
        doTestGetDataWithDelay(true);
    }

    @SuppressWarnings("unchecked")
    void doTestGetDataWithDelay(boolean useCustomInvoker) throws Exception {
        final Client client = newClient(useCustomInvoker);
        Builder builder = client.target(generateURL("/sleep")).request();
        RxInvoker<?> invoker = useCustomInvoker ? builder.rx(TestRxInvoker.class) : builder.rx();
        Future<String> future = ((CompletableFuture<String>) invoker.get(String.class)).toCompletableFuture();
        Assertions.assertFalse(future.isDone());
        String response = future.get();
        Assertions.assertEquals("get", response);
        Assertions.assertEquals(useCustomInvoker, invoker instanceof TestRxInvoker && TestRxInvoker.used);
        client.close();
    }
}
