package org.jboss.resteasy.reactor;

import java.util.Set;
import java.util.TreeSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.core.Response;

import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ReactiveClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.engines.ReactorNettyClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ConnectionObserver;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.context.Context;

public class ReactorTest
{
   private static NettyJaxrsServer server;

   private static CountDownLatch latch;
   private static AtomicReference<Object> value = new AtomicReference<Object>();
   private static final Logger LOG = Logger.getLogger(NettyJaxrsServer.class);

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      server.getDeployment().getActualResourceClasses().add(ReactorResource.class);
      server.getDeployment().getActualProviderClasses().add(ReactorInjector.class);
      server.getDeployment().start();
      server.getDeployment().registration();
      server.start();
   }

   @AfterClass
   public static void afterClass() throws Exception
   {
      server.stop();
      server = null;
   }

   private ResteasyClient client;

   @Before
   public void before()
   {
      final ReactorNettyClientHttpEngine reactorEngine =
          new ReactorNettyClientHttpEngine(
              HttpClient.create(),
              new DefaultChannelGroup(new DefaultEventExecutor()),
              ConnectionProvider.newConnection()
          );
      client = ((ResteasyClientBuilder)ClientBuilder.newBuilder())
          .httpEngine(reactorEngine)
            .readTimeout(5, TimeUnit.SECONDS)
            .connectionCheckoutTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();
      value.set(null);
      latch = new CountDownLatch(1);
   }

   @After
   public void after()
   {
      client.close();
   }

   @Test
   public void testMono() throws Exception {
      assertEquals(0, ReactorResource.monoEndpointCounter.get());
      Mono<Response> mono = client.target(generateURL("/mono")).request().rx(MonoRxInvoker.class).get();
      Thread.sleep(1_000);
      // Make HTTP call does not happen until a subscription happens.
      assertEquals(0, ReactorResource.monoEndpointCounter.get());
      mono.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
      latch.await();
      assertEquals(1, ReactorResource.monoEndpointCounter.get());
      assertEquals("got it", value.get());
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
      assertArrayEquals(new String[] {"one", "two"}, data.toArray());
   }

   @Test
   public void testSubscriberContext()
   {
      final String ctxKey = "secret";
      final List<Integer> secrets = new ArrayList<>();

      // With the `Publisher` bridge, the end user's subscriber context is available when the
      // reactor-netty client is instantiated.  This can be useful for things like trace logging.
      final HttpClient reactorClient =
          HttpClient.create().doOnRequest((req, conn) ->
              req.currentContext().<Integer>getOrEmpty(ctxKey).ifPresent(secrets::add)
          );

      final ReactorNettyClientHttpEngine reactorEngine =
          new ReactorNettyClientHttpEngine(
              reactorClient,
              new DefaultChannelGroup(new DefaultEventExecutor()),
              ConnectionProvider.newConnection()
          );

      final ResteasyClient client = ((ResteasyClientBuilder)ClientBuilder.newBuilder())
          .httpEngine(reactorEngine)
          .readTimeout(5, TimeUnit.SECONDS)
          .connectionCheckoutTimeout(5, TimeUnit.SECONDS)
          .connectTimeout(5, TimeUnit.SECONDS)
          .build();

      final Supplier<Mono<String>> getFn = () ->
          client.target(generateURL("/mono")).request().rx(MonoRxInvoker.class).get(String.class);

      Mono<String> mono =
          getFn.get()
              .flatMap(resp1 ->
                  getFn.get().flatMap(resp2 ->
                      getFn.get().map(resp3 -> String.join("-", Arrays.asList(resp1, resp2, resp3)))
                          .subscriberContext(Context.of(ctxKey, 24))
                  )
              ).subscriberContext(ctx -> ctx.put(ctxKey, 42));

      assertThat(mono.block(), equalTo("got it-got it-got it"));
      assertThat(secrets, equalTo(Arrays.asList(42, 42, 24)));
   }

   @Test
   public void testTimeoutOverridePerRequest() throws Exception
   {
      // This also tests that the client will eagerly close the connection
      // in the case of a business logic timeout.
      final Duration serverResponseDelay = Duration.ofSeconds(60);
      final CountDownLatch serverConnDisconnectingEvent = new CountDownLatch(1);
      final DisposableServer server =
          HttpServer.create()
              .childObserve((conn, state) -> {
                 if (state == ConnectionObserver.State.DISCONNECTING) {
                    serverConnDisconnectingEvent.countDown();
                 }
              })
              .handle((req, resp) -> resp.sendString(Mono.just("I'm delayed!").delayElement(serverResponseDelay)))
              .bindNow();

       try {
           final CountDownLatch latch = new CountDownLatch(1);

           final HttpClient reactorClient = HttpClient.create();

           final ReactorNettyClientHttpEngine reactorEngine =
               new ReactorNettyClientHttpEngine(
                   reactorClient,
                   new DefaultChannelGroup(new DefaultEventExecutor()),
                   ConnectionProvider.builder("clientconns").maxConnections(1).build()
               );

           final AtomicReference<Exception> innerTimeoutException = new AtomicReference<>();

           final ReactiveClientHttpEngine wrappedEngine = new ReactiveClientHttpEngine() {
               private <T> Mono<T> recordTimeout(final Mono<T> m) {
                   return m.doOnError(TimeoutException.class, innerTimeoutException::set);
               }

               public <T> Mono<T> submitRx(ClientInvocation request, boolean buffered, ResultExtractor<T> extractor) {
                   return recordTimeout(reactorEngine.submitRx(request, buffered, extractor));
               }

               public <T> Mono<T> fromCompletionStage(CompletionStage<T> cs) {
                   return recordTimeout(reactorEngine.fromCompletionStage(cs));
               }

               public <T> Mono<T> just(T t) {
                   return recordTimeout(reactorEngine.just(t));
               }

               public Mono error(Exception e) {
                   return recordTimeout(reactorEngine.error(e));
               }

               public <T> Future<T> submit(ClientInvocation request, boolean buffered, InvocationCallback<T> callback, ResultExtractor<T> extractor) {
                   return reactorEngine.submit(request, buffered, callback, extractor);
               }

               public <K> CompletableFuture<K> submit(ClientInvocation request, boolean buffered, ResultExtractor<K> extractor, ExecutorService executorService) {
                   return reactorEngine.submit(request, buffered, extractor, executorService);
               }

               public SSLContext getSslContext() {
                   return reactorEngine.getSslContext();
               }

               public HostnameVerifier getHostnameVerifier() {
                   return reactorEngine.getHostnameVerifier();
               }

               public Response invoke(Invocation request) {
                   return reactorEngine.invoke(request);
               }

               public void close() {
                   reactorEngine.close();
               }
           };

           final Duration innerTimeout = Duration.ofSeconds(5);
           final ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder())
               .httpEngine(wrappedEngine)
               .readTimeout(innerTimeout.toMillis(), TimeUnit.MILLISECONDS)
               .build();

           client.target("http://localhost:" + server.port() + "/")
               .request()
               .rx(MonoRxInvoker.class)
               .get(String.class)
               .timeout(Duration.ofMillis(500))
               .subscribe(
                   ignore -> {
                       fail("Should have got timeout exception");
                   },
                   t -> {
                       if (!(t instanceof TimeoutException)) {
                           assertThat(t.getMessage(), containsString("signal within 500ms")); // crappy assertion:(
                       }
                       latch.countDown();
                   },
                   latch::countDown
               );

           assertNull("Inner timeout should not have occurred!", innerTimeoutException.get());
           assertTrue("Test timed out", latch.await(innerTimeout.multipliedBy(2).toMillis(), TimeUnit.MILLISECONDS));
           assertTrue("Server disconnect didn't happen.", serverConnDisconnectingEvent.await(
               serverResponseDelay.dividedBy(2).toMillis(), TimeUnit.MILLISECONDS));
       } finally {
           server.disposeNow();
       }
   }

   @Test
   public void testInjection()
   {
      Integer data = client.target(generateURL("/injection")).request().get(Integer.class);
      assertEquals((Integer)42, data);

      data = client.target(generateURL("/injection-async")).request().get(Integer.class);
      assertEquals((Integer)42, data);
   }

}
