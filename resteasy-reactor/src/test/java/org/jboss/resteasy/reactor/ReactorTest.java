package org.jboss.resteasy.reactor;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.resteasy.junit.extension.annotations.RestBootstrap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@RestBootstrap(value = ReactorTest.TestApplication.class)
public class ReactorTest {

    private static CountDownLatch latch;
    private static AtomicReference<Object> value = new AtomicReference<Object>();
    private static final Logger LOG = Logger.getLogger(ReactorTest.class);

    @BeforeAll
    public static void beforeClass() throws Exception {
    }

    @AfterAll
    public static void afterClass() throws Exception {
    }

    @Inject
    private Client client;

    @BeforeEach
    public void before() {
        value.set(null);
        latch = new CountDownLatch(1);
    }

    @Test
    public void testMono() throws Exception {
        Assertions.assertEquals(0, ReactorResource.monoEndpointCounter.get());
        Mono<Response> mono = client.target(generateURL("/mono")).request().rx(MonoRxInvoker.class).get();
        mono.subscribe((Response r) -> {
            value.set(r.readEntity(String.class));
            latch.countDown();
        });
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        Assertions.assertEquals(1, ReactorResource.monoEndpointCounter.get());
        Assertions.assertEquals("got it", value.get());
    }

    @Test
    public void testFlux() throws Exception {
        FluxRxInvoker invoker = client.target(generateURL("/flux")).request().rx(FluxRxInvoker.class);
        @SuppressWarnings("unchecked")
        Flux<String> flux = (Flux<String>) invoker.get();
        Set<String> data = new TreeSet<>(); //FIXME [RESTEASY-2778] Intermittent flow / flux test failure
        flux.subscribe(
                (String s) -> data.add(s),
                (Throwable t) -> LOG.error(t.getMessage(), t),
                () -> latch.countDown());
        latch.await();
        Assertions.assertArrayEquals(new String[] { "one", "two" }, data.toArray());
    }

    @Test
    public void testSubscriberContext() {
        final String ctxKey = "secret";

        final Supplier<Mono<String>> getFn = () -> client.target(generateURL("/mono")).request().rx(MonoRxInvoker.class)
                .get(String.class);

        Mono<String> mono = getFn.get()
                .flatMap(
                        resp1 -> getFn.get()
                                .flatMap(resp2 -> getFn.get()
                                        .map(resp3 -> String.join("-", Arrays.asList(resp1, resp2, resp3)))
                                        .contextWrite(Context.of(ctxKey, 24))))
                .contextWrite(ctx -> ctx.put(ctxKey, 42));

        Assertions.assertEquals("got it-got it-got it", mono.block());
    }

    @Test
    public void testInjection() {
        Integer data = client.target(generateURL("/injection")).request().get(Integer.class);
        Assertions.assertEquals((Integer) 24, data);

        data = client.target(generateURL("/injection-async")).request().get(Integer.class);
        Assertions.assertEquals((Integer) 42, data);
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(ReactorResource.class, ReactorInjector.class);
        }
    }
}
