package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.plugins.providers.sse.OutboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.SseBroadcasterImpl;
import org.junit.Assert;
import org.junit.Test;

/***
 * 
 * @author Nicolas NESMON
 *
 */
public class SseBroadcasterTest {

	@Test
	public void Should_ThrowIllegalStateException_When_BroadcasterIsClosed() throws Exception {
		SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();
		sseBroadcasterImpl.close();

		try {
			sseBroadcasterImpl.broadcast(new OutboundSseEventImpl.BuilderImpl().data("Test").build());
			Assert.fail("Should have thrown IllegalStateException");
		} catch (IllegalStateException e) {
		}

		try {
			sseBroadcasterImpl.onClose(sseEventSink -> {
			});
			Assert.fail("Should have thrown IllegalStateException");
		} catch (IllegalStateException e) {
		}

		try {
			sseBroadcasterImpl.onError((sseEventSink, error) -> {
			});
			Assert.fail("Should have thrown IllegalStateException");
		} catch (IllegalStateException e) {
		}

		try {
			sseBroadcasterImpl.register(newSseEventSink());
			Assert.fail("Should have thrown IllegalStateException");
		} catch (IllegalStateException e) {
		}
	}

	@Test
	public void Should_closeAllRegisteredEventSinkAndInvokeCloseListeners_When_BroadcasterIsBeingClosed()
			throws Exception {
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
			Assert.fail("All close listeners should have been notified");
		}
		Assert.assertTrue(sseEventSink1.isClosed());
		Assert.assertTrue(sseEventSink2.isClosed());
	}

	@Test
	public void Should_InvokeCloseAndErrorListeners_When_EventSinkHasBeenClosedOnServerSide() throws Exception {
		SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

		SseEventSink sseEventSink = newSseEventSink();
		sseBroadcasterImpl.register(sseEventSink);
		sseEventSink.close();
		Assert.assertTrue(sseEventSink.isClosed());

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
			Assert.fail("All close and error listeners should have been notified");
		}
	}

	@Test
	public void Should_InvokeCloseAndErrorListeners_When_EventSinkHasBeenClosedOnClientSide() throws Exception {
		SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

		SseEventSink sseEventSink = newSseEventSink(new IOException());
		sseBroadcasterImpl.register(sseEventSink);
		Assert.assertFalse(sseEventSink.isClosed());

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
			Assert.fail("All close and error listeners should have been notified");
		}
	}

	@Test
	public void Should_InvokeErrorListeners_On_BroadcastingErrorOtherThanIOException() throws Exception {
		SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

		SseEventSink sseEventSink = newSseEventSink(new RuntimeException());
		sseBroadcasterImpl.register(sseEventSink);
		Assert.assertFalse(sseEventSink.isClosed());

		CountDownLatch countDownLatch = new CountDownLatch(2);
		sseBroadcasterImpl.onClose(ses -> {
			Assert.fail("Close listeners should not have been notified");
		});
		sseBroadcasterImpl.onError((ses, error) -> {
			countDownLatch.countDown();
		});
		sseBroadcasterImpl.onError((ses, error) -> {
			countDownLatch.countDown();
		});

		sseBroadcasterImpl.broadcast(new OutboundSseEventImpl.BuilderImpl().data("Test").build());
		if (!countDownLatch.await(5, TimeUnit.SECONDS)) {
			Assert.fail("All error listeners should have been notified");
		}
	}

	private SseEventSink newSseEventSink() {
		return newSseEventSink(null);
	}

	private SseEventSink newSseEventSink(Throwable error) {
		return new SseEventSink() {

			private boolean closed;

			@Override
			public CompletionStage<?> send(OutboundSseEvent event) {
				if(closed){
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
