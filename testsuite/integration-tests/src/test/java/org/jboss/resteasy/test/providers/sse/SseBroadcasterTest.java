package org.jboss.resteasy.test.providers.sse;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.providers.sse.OutboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.SseBroadcasterImpl;
import org.jboss.resteasy.plugins.providers.sse.SseEventOutputImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/***
 *
 * @author Nicolas NESMON
 *
 */
public class SseBroadcasterTest {

    // We are expecting this test to throw an IllegalStateException every time a
    // method from SseBroadcasterImpl is invoked on a closed instance.
    @Test
    public void testIllegalStateExceptionForClosedBroadcaster() throws Exception {
        SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();
        sseBroadcasterImpl.close();

        try {
            sseBroadcasterImpl.broadcast(new OutboundSseEventImpl.BuilderImpl().data("Test").build());
            Assertions.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
        }

        try {
            sseBroadcasterImpl.onClose(sseEventSink -> {
            });
            Assertions.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
        }

        try {
            sseBroadcasterImpl.onError((sseEventSink, error) -> {
            });
            Assertions.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
        }

        try {
            sseBroadcasterImpl.register(newSseEventSink());
            Assertions.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
        }
    }

    // We are expecting this test to close all registered event sinks and invoke
    // close listeners when broadcaster is closed
    @Test
    public void testClose() throws Exception {
        SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

        SseEventSink sseEventSink1 = newSseEventSink();
        sseBroadcasterImpl.register(sseEventSink1);
        SseEventSink sseEventSink2 = newSseEventSink();
        sseBroadcasterImpl.register(sseEventSink2);

        CountDownLatch countDownLatch = new CountDownLatch(4);
        sseBroadcasterImpl.onClose(sseEventSink -> {
            countDownLatch.countDown();
        });
        sseBroadcasterImpl.onClose(sseEventSink -> {
            countDownLatch.countDown();
        });

        sseBroadcasterImpl.close();
        if (!countDownLatch.await(3, TimeUnit.SECONDS)) {
            Assertions.fail("All close listeners should have been notified");
        }
        Assertions.assertTrue(sseEventSink1.isClosed());
        Assertions.assertTrue(sseEventSink2.isClosed());
    }

    // We are expecting this test to invoke both close and error listeners when
    // event sink has been closed on server side
    @Test
    public void testCloseAndErrorListenersForClosedEventSink() throws Exception {
        SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

        SseEventSink sseEventSink = newSseEventSink();
        sseBroadcasterImpl.register(sseEventSink);
        sseEventSink.close();
        Assertions.assertTrue(sseEventSink.isClosed());

        CountDownLatch countDownLatch = new CountDownLatch(3);
        sseBroadcasterImpl.onClose(ses -> {
            countDownLatch.countDown();
        });
        sseBroadcasterImpl.onError((ses, error) -> {
            countDownLatch.countDown();
        });
        sseBroadcasterImpl.onError((ses, error) -> {
            countDownLatch.countDown();
        });

        sseBroadcasterImpl.broadcast(new OutboundSseEventImpl.BuilderImpl().data("Test").build());
        if (!countDownLatch.await(3, TimeUnit.SECONDS)) {
            Assertions.fail("All close and error listeners should have been notified");
        }
    }

    // We are expecting this test to invoke both close and error listeners when
    // event sink has been closed on client side (disconnected)
    @Test
    public void testCloseAndErrorListenersForDisconnectedEventSink() throws Exception {
        SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

        SseEventSink sseEventSink = newSseEventSink(new IOException());
        sseBroadcasterImpl.register(sseEventSink);
        Assertions.assertFalse(sseEventSink.isClosed());

        CountDownLatch countDownLatch = new CountDownLatch(3);
        sseBroadcasterImpl.onClose(ses -> {
            countDownLatch.countDown();
        });
        sseBroadcasterImpl.onError((ses, error) -> {
            countDownLatch.countDown();
        });
        sseBroadcasterImpl.onError((ses, error) -> {
            countDownLatch.countDown();
        });

        sseBroadcasterImpl.broadcast(new OutboundSseEventImpl.BuilderImpl().data("Test").build());
        if (!countDownLatch.await(3, TimeUnit.SECONDS)) {
            Assertions.fail("All close and error listeners should have been notified");
        }
    }

    // We are expecting this test to only invoke error listeners on broadcasting
    // error other than IOException
    @Test
    public void testErrorListeners() throws Exception {
        SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

        SseEventSink sseEventSink = newSseEventSink(new RuntimeException());
        sseBroadcasterImpl.register(sseEventSink);
        Assertions.assertFalse(sseEventSink.isClosed());

        AtomicBoolean onCloseListenerInvoked = new AtomicBoolean(false);
        sseBroadcasterImpl.onClose(ses -> {
            onCloseListenerInvoked.set(true);
        });

        CountDownLatch countDownLatch = new CountDownLatch(2);
        sseBroadcasterImpl.onError((ses, error) -> {
            countDownLatch.countDown();
        });
        sseBroadcasterImpl.onError((ses, error) -> {
            countDownLatch.countDown();
        });

        sseBroadcasterImpl.broadcast(new OutboundSseEventImpl.BuilderImpl().data("Test").build());
        if (!countDownLatch.await(5, TimeUnit.SECONDS)) {
            Assertions.fail("All error listeners should have been notified");
        }
        if (onCloseListenerInvoked.get()) {
            Assertions.fail("Close listeners should not have been notified");
        }
    }

    @BeforeEach
    public void before() {
        HttpRequest request = mock(HttpRequest.class);
        ResteasyAsynchronousContext resteasyAsynchronousContext = mock(ResteasyAsynchronousContext.class);
        doReturn(resteasyAsynchronousContext).when(request).getAsyncContext();

        //prevent NPE in SseEventOutputImpl ctr
        ResteasyContext.pushContext(org.jboss.resteasy.spi.HttpRequest.class, request);
    }

    @Test
    public void testRemoveDisconnectedEventSink() throws Exception {
        final Map<Class<?>, Object> testContext = new HashMap<>();
        testContext.put(HttpRequest.class, new MockHttpRequest() {
        });
        testContext.put(HttpResponse.class, new MockHttpResponse());
        ResteasyContext.pushContextDataMap(testContext);
        SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

        final ConcurrentLinkedQueue<SseEventSink> outputQueue = getOutputQueue(sseBroadcasterImpl);
        CountDownLatch countDownLatch = new CountDownLatch(2);

        //we want to test against actual SseEventOutputImpl
        final SseEventSink sseEventSink1 = new SseEventOutputImpl(null);
        final SseEventSink sseEventSink2 = new SseEventOutputImpl(null);

        sseBroadcasterImpl.register(sseEventSink1);
        sseBroadcasterImpl.register(sseEventSink2);

        sseBroadcasterImpl.onClose(ses -> {
            countDownLatch.countDown();
        });

        sseBroadcasterImpl.onError((ses, error) -> {
            //error is an NPE thrown by SseEventOutputImpl#send
            countDownLatch.countDown();
        });

        sseEventSink2.close();

        sseBroadcasterImpl.broadcast(new OutboundSseEventImpl.BuilderImpl().data("Test").build());

        if (!countDownLatch.await(5, TimeUnit.SECONDS)) {
            fail("All close listeners should have been notified");
        } else {
            Assertions.assertTrue(outputQueue.size() == 1);
            Assertions.assertSame(outputQueue.peek(), sseEventSink1);
        }

        ResteasyContext.removeContextDataLevel();
    }

    @SuppressWarnings("unchecked")
    private ConcurrentLinkedQueue<SseEventSink> getOutputQueue(SseBroadcasterImpl sseBroadcasterImpl)
            throws NoSuchFieldException, IllegalAccessException {
        Field fld = SseBroadcasterImpl.class.getDeclaredField("outputQueue");
        fld.setAccessible(true);
        return (ConcurrentLinkedQueue<SseEventSink>) fld.get(sseBroadcasterImpl);
    }

    @org.junit.jupiter.api.AfterEach
    public void after() {
        //revert contextual data
        ResteasyContext.pushContext(org.jboss.resteasy.spi.HttpRequest.class, null);
    }

    private SseEventSink newSseEventSink() {
        return newSseEventSink(null);
    }

    private SseEventSink newSseEventSink(Throwable error) {
        return new SseEventSink() {

            private boolean closed;

            @Override
            public CompletionStage<?> send(OutboundSseEvent event) {
                if (closed) {
                    throw new IllegalStateException();
                }
                CompletableFuture<Object> completableFuture = new CompletableFuture<>();
                if (error == null) {
                    completableFuture.complete(null);
                } else {
                    completableFuture.completeExceptionally(error);
                }
                return completableFuture;
            }

            @Override
            public boolean isClosed() {
                return closed;
            }

            @Override
            public void close() {
                closed = true;
            }
        };
    }

}
