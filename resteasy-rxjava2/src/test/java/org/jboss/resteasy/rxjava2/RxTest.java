package org.jboss.resteasy.rxjava2;

import java.net.URI;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

@RestBootstrap(RxTest.TestApplication.class)
public class RxTest {

    private static CountDownLatch latch;
    private static AtomicReference<Object> value = new AtomicReference<Object>();
    private static final Logger LOG = Logger.getLogger(RxTest.class);

    @BeforeEach
    public void setup() {
        latch = new CountDownLatch(1);
    }

    @Inject
    private Client client;

    @Test
    public void testSingle(@RequestPath("/single") final URI uri) throws Exception {
        Single<Response> single = client.target(uri).request().rx(SingleRxInvoker.class).get();
        single.subscribe((Response r) -> {
            value.set(r.readEntity(String.class));
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
        Assertions.assertEquals("got it", value.get());
    }

    @Test
    public void testSingleContext(@RequestPath("/context/single") final URI uri) throws Exception {
        Single<Response> single = client.target(uri).request().rx(SingleRxInvoker.class).get();
        single.subscribe((Response r) -> {
            value.set(r.readEntity(String.class));
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);
        Assertions.assertEquals("got it", value.get());
    }

    @Test
    public void testObservable(@RequestPath("/observable") final URI uri) throws Exception {
        ObservableRxInvoker invoker = client.target(uri).request().rx(ObservableRxInvoker.class);
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
    public void testObservableContext(@RequestPath("/context/observable") final URI uri) throws Exception {
        ObservableRxInvoker invoker = ClientBuilder.newClient().target(uri).request()
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
    public void testFlowable(@RequestPath("/flowable") final URI uri) throws Exception {
        FlowableRxInvoker invoker = client.target(uri).request().rx(FlowableRxInvoker.class);
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
    public void testFlowablecontext(@RequestPath("/context/flowable") final URI uri) throws Exception {
        FlowableRxInvoker invoker = client.target(uri).request().rx(FlowableRxInvoker.class);
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
    public void testChunked(@RequestPath("/chunked") final URI uri) throws Exception {
        Invocation.Builder request = client.target(uri).request();
        Response response = request.get();
        String entity = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("onetwo", entity);
    }

    @Test
    public void testInjection(@RequestPath("/injection") final URI injectionUri,
            @RequestPath("/injection-async") final URI injectionAsyncUri) throws Exception {
        Integer data = client.target(injectionUri).request().get(Integer.class);
        Assertions.assertEquals((Integer) 42, data);

        data = client.target(injectionAsyncUri).request().get(Integer.class);
        Assertions.assertEquals((Integer) 42, data);
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(RxResource.class, RxInjector.class);
        }
    }
}
