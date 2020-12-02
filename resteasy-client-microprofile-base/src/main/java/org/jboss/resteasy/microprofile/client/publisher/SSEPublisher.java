package org.jboss.resteasy.microprofile.client.publisher;

import org.jboss.logging.Logger;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.providers.sse.SseEventInputImpl;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.sse.InboundSseEvent;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Publisher implementation emitting server-sent-event downstream.
 *
 * @param <T> the type of event.
 */
@SuppressWarnings("ReactiveStreamsPublisherImplementation")
public class SSEPublisher<T> implements Publisher<T> {

    private static final Runnable CLEARED = () -> {
        // sentinel indicating we are done.
    };

    private final SseEventInputImpl input;
    private final Type genericType;
    private final Providers providers;
    private final ExecutorService executor;

    private static final Logger LOGGER = Logger.getLogger(SSEPublisher.class);

    public SSEPublisher(final Type genericType, final Providers providers, final SseEventInputImpl input, final ExecutorService es) {
        this.genericType = genericType;
        this.input = input;
        this.providers = providers;
        this.executor = es;
    }

    @Override
    public void subscribe(final Subscriber<? super T> downstream) {
        SSEProcessor<? super T> processor = new SSEProcessor<>(downstream,
                Integer.getInteger("resteasy.microprofile.sseclient.buffersize", 512));
        downstream.onSubscribe(processor);
        pump(processor, input);
    }

    /**
     * Reads the server-sent event from the {@code input} and pass them to the processor.
     * The processor handles the downstream requests and buffer/drop items according to them.
     *
     * @param processor the stream
     * @param input     the SSE input
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void pump(final SSEProcessor processor, final SseEventInputImpl input) {
        Map<Class<?>, Object> contextDataMap = ResteasyContext.getContextDataMap();
        Runnable readEventTask = new Runnable() {
            @Override
            public void run() {
                ResteasyContext.pushContextDataMap(contextDataMap);
                Type typeArgument;
                InboundSseEvent event;
                if (genericType instanceof ParameterizedType) {
                    typeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                    if (typeArgument.equals(InboundSseEvent.class)) {
                        try {
                            while ((event = input.read(providers)) != null) {
                                processor.emit(event);
                            }
                        } catch (Exception e) {
                            processor.onError(e);
                            return;
                        }
                    processor.onCompletion();
                    } else {
                        try {
                            while ((event = input.read(providers)) != null) {
                                processor.emit(event.readData((Class) typeArgument));
                            }
                        } catch (Exception e) {
                            processor.onError(e);
                            return;
                        }
                    }
                    processor.onCompletion();
                }
            }
        };
        try {
            executor.execute(readEventTask);
        } catch (RejectedExecutionException e) {
            LOGGER.warnf("Executor %s rejected emit event task", executor);
            new Thread(readEventTask, "SseClientPublisherNewThread").start();
        }
    }

    /**
     * Processor receiving SSE items from the source and dealing with downstream requests.
     * The items are buffers, and older events are dropped if the buffer is full.
     *
     * @param <T> the type of event
     */
    private static class SSEProcessor<T> implements Subscription {
        private final AtomicLong requested = new AtomicLong();
        private final Subscriber<T> downstream;

        private final Queue<T> queue;
        private final int bufferSize;
        private Throwable failure;
        private volatile boolean done;
        private final AtomicInteger wip = new AtomicInteger();
        private final AtomicReference<Runnable> onTermination;

        SSEProcessor(final Subscriber<T> downstream, final int bufferSize) {
            this.downstream = downstream;
            this.bufferSize = bufferSize;
            this.queue = new SpscLinkedArrayQueue<>(bufferSize);
            this.onTermination = new AtomicReference<>();
        }

        public void emit(T t) {
            if (done || isCancelled()) {
                return;
            }

            if (t == null) {
                throw new NullPointerException("Reactive Streams Rule 2.13 violated: The received item is `null`");
            }

            if (queue.size() == bufferSize) {
                T item = queue.poll();
                LOGGER.debugf("Dropping server-sent-event '%s' due to lack of downstream requests", item);
            }
            queue.offer(t);

            drain();
        }

        @Override
        public void request(long n) {
            if (n > 0) {
                Subscriptions.add(requested, n);
                drain();
            } else {
                cancel();
                downstream.onError(new IllegalArgumentException(
                        "Reactive Streams Rule 3.9 violated: request must be positive, but was " + n));
            }
        }

        @Override
        public final void cancel() {
            cleanup();
        }

        public boolean isCancelled() {
            return onTermination.get() == CLEARED;
        }

        void drain() {
            if (wip.getAndIncrement() != 0) {
                return;
            }

            int missed = 1;
            final Queue<T> q = queue;

            do {
                long requests = requested.get();
                long emitted = 0L;

                while (emitted != requests) {
                    // Be sure to clear the queue after cancellation or termination.
                    if (isCancelled()) {
                        q.clear();
                        return;
                    }

                    boolean d = done;
                    T event = q.poll();
                    boolean empty = event == null;

                    // No event and done - completing.
                    if (d && empty) {
                        if (failure != null) {
                            sendErrorToDownstream(failure);
                        } else {
                            sendCompletionToDownstream();
                        }
                        return;
                    }

                    if (empty) {
                        break;
                    }

                    // Passing the item downstream, and incrementing the emitted counter.
                    try {
                        downstream.onNext(event);
                    } catch (Throwable x) {
                        cancel();
                    }
                    emitted++;
                }

                // We have emitted all the items we could possibly do without violating the protocol.
                if (emitted == requests) {
                    // Be sure to clear the queue after cancellation or termination.
                    if (isCancelled()) {
                        q.clear();
                        return;
                    }

                    // Re-check for completion.
                    boolean d = done;
                    boolean empty = q.isEmpty();
                    if (d && empty) {
                        if (failure != null) {
                            sendErrorToDownstream(failure);
                        } else {
                            sendCompletionToDownstream();
                        }
                        return;
                    }
                }

                // Update `requested`
                if (emitted != 0) {
                    Subscriptions.produced(requested, emitted);
                }

                missed = wip.addAndGet(-missed);
            } while (missed != 0);
        }

        protected void onCompletion() {
            done = true;
            drain();
        }

        protected void onError(Throwable e) {
            if (done || isCancelled()) {
                return;
            }

            if (e == null) {
                throw new NullPointerException("Reactive Streams Rule 2.13 violated: The received error is `null`");
            }

            this.failure = e;
            done = true;

            drain();
        }

        private void cleanup() {
            Runnable action = onTermination.getAndSet(CLEARED);
            if (action != null && action != CLEARED) {
                action.run();
            }
        }

        private void sendCompletionToDownstream() {
            if (isCancelled()) {
                return;
            }

            try {
                downstream.onComplete();
            } finally {
                cleanup();
            }
        }

        private void sendErrorToDownstream(Throwable e) {
            if (e == null) {
                e = new NullPointerException("Reactive Streams Rule 2.13 violated: The received error is `null`");
            }
            if (isCancelled()) {
                return;
            }
            try {
                downstream.onError(e);
            } finally {
                cleanup();
            }
        }
    }

}
