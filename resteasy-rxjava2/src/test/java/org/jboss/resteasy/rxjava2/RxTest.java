package org.jboss.resteasy.rxjava2;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class RxTest {
    private static NettyJaxrsServer server;

    private static CountDownLatch latch;
    private static AtomicReference<Object> value = new AtomicReference<Object>();
    private static final Logger LOG = Logger.getLogger(NettyJaxrsServer.class);

    @BeforeAll
    public static void beforeClass() throws Exception {
        server = new NettyJaxrsServer();
        server.setPort(TestPortProvider.getPort());
        server.setRootResourcePath("/");
        server.getDeployment().getActualResourceClasses().add(RxResource.class);
        server.getDeployment().getActualProviderClasses().add(RxInjector.class);
        server.getDeployment().start();
        server.getDeployment().registration();
        server.start();
    }

    @AfterAll
    public static void afterClass() throws Exception {
        server.stop();
        server = null;
    }

    private ResteasyClient client;

    @BeforeEach
    public void before() {
        client = ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                .readTimeout(5, TimeUnit.SECONDS)
                .connectionCheckoutTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        value.set(null);
        latch = new CountDownLatch(1);
    }

    @AfterEach
    public void after() {
        client.close();
    }

    @Test
    public void testSingle() throws Exception {
        Single<Response> single = client.target(generateURL("/single")).request().rx(SingleRxInvoker.class).get();
        single.subscribe((Response r) -> {
            value.set(r.readEntity(String.class));
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
        Assertions.assertEquals("got it", value.get());
    }

    @Test
    public void testSingleContext() throws Exception {
        Single<Response> single = client.target(generateURL("/context/single")).request().rx(SingleRxInvoker.class).get();
        single.subscribe((Response r) -> {
            value.set(r.readEntity(String.class));
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
        Assertions.assertEquals("got it", value.get());
    }

    @Test
    public void testObservable() throws Exception {
        ObservableRxInvoker invoker = client.target(generateURL("/observable")).request().rx(ObservableRxInvoker.class);
        @SuppressWarnings("unchecked")
        Observable<String> observable = (Observable<String>) invoker.get();
        Set<String> data = new TreeSet<>(); //FIXME [RESTEASY-2778] Intermittent flow / flux test failure
        observable.subscribe(
                (String s) -> data.add(s),
                (Throwable t) -> LOG.error(t.getMessage(), t),
                () -> latch.countDown());
        latch.await(5, TimeUnit.SECONDS);
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data.toArray());
    }

    @Test
    public void testObservableContext() throws Exception {
        ObservableRxInvoker invoker = ClientBuilder.newClient().target(generateURL("/context/observable")).request()
                .rx(ObservableRxInvoker.class);
        @SuppressWarnings("unchecked")
        Observable<String> observable = (Observable<String>) invoker.get();
        Set<String> data = new TreeSet<>(); //FIXME [RESTEASY-2778] Intermittent flow / flux test failure
        observable.subscribe(
                (String s) -> data.add(s),
                (Throwable t) -> LOG.error(t.getMessage(), t),
                () -> latch.countDown());
        latch.await(5, TimeUnit.SECONDS);
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data.toArray());
    }

    @Test
    public void testFlowable() throws Exception {
        FlowableRxInvoker invoker = client.target(generateURL("/flowable")).request().rx(FlowableRxInvoker.class);
        @SuppressWarnings("unchecked")
        Flowable<String> flowable = (Flowable<String>) invoker.get();
        Set<String> data = new TreeSet<>(); //FIXME [RESTEASY-2778] Intermittent flow / flux test failure
        flowable.subscribe(
                (String s) -> data.add(s),
                (Throwable t) -> LOG.error(t.getMessage(), t),
                () -> latch.countDown());
        latch.await(5, TimeUnit.SECONDS);
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data.toArray());
    }

    @Test
    public void testFlowablecontext() throws Exception {
        FlowableRxInvoker invoker = client.target(generateURL("/context/flowable")).request().rx(FlowableRxInvoker.class);
        @SuppressWarnings("unchecked")
        Flowable<String> flowable = (Flowable<String>) invoker.get();
        Set<String> data = new TreeSet<>(); //FIXME [RESTEASY-2778] Intermittent flow / flux test failure
        flowable.subscribe(
                (String s) -> data.add(s),
                (Throwable t) -> LOG.error(t.getMessage(), t),
                () -> {
                    latch.countDown();
                    LOG.info("onComplete()");
                });
        latch.await(5, TimeUnit.SECONDS);
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data.toArray());
    }

    // @Test
    public void testChunked() throws Exception {
        Invocation.Builder request = client.target(generateURL("/chunked")).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("onetwo", entity);
    }

    @Test
    public void testInjection() {
        Integer data = client.target(generateURL("/injection")).request().get(Integer.class);
        Assertions.assertEquals((Integer) 42, data);

        data = client.target(generateURL("/injection-async")).request().get(Integer.class);
        Assertions.assertEquals((Integer) 42, data);
    }
}
