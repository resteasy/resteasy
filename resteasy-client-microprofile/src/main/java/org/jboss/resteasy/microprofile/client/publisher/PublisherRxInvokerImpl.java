package org.jboss.resteasy.microprofile.client.publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.plugins.providers.sse.InboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl.SourceBuilder;
import org.jboss.resteasy.rxjava2.i18n.Messages;
import org.reactivestreams.Publisher;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This class defines a Flowable for processing data returned by a
 * mp-rest-client method with a return type of org.reactivestreams.Publisher.
 * Flowable is used because it provides back pressure control and it implements
 * the Publisher interface.
 * All HTTP verb methods defined in the RxInvoker interface are defined as
 * NO-OPs.  RESTEasy's mp-rest-client implementation does not use them.
 */
public class PublisherRxInvokerImpl implements PublisherRxInvoker {
    private static Object monitor = new Object();
    private ClientInvocationBuilder syncInvoker;
    private ScheduledExecutorService executorService;
    private BackpressureStrategy backpressureStrategy = BackpressureStrategy.BUFFER;

    public PublisherRxInvokerImpl(final SyncInvoker syncInvoker, final ExecutorService executorService) {
        if (!(syncInvoker instanceof ClientInvocationBuilder)) {
            throw new ProcessingException(Messages.MESSAGES.expectedClientInvocationBuilder(syncInvoker.getClass().getName()));
        }
        this.syncInvoker = (ClientInvocationBuilder) syncInvoker;
        if (executorService instanceof ScheduledExecutorService) {
            this.executorService = (ScheduledExecutorService) executorService;
        }
    }

    @Override
    public Publisher<?> method(String name) {
        return eventSourceToPublisher(getEventSource(), String.class, name, null, getAccept());
    }

    @Override
    public <R> Publisher<?> method(String name, Class<R> responseType) {
        return eventSourceToPublisher(getEventSource(), responseType, name, null, getAccept());
    }

    @Override
    public <R> Publisher<?> method(String name, GenericType<R> responseType) {
        return eventSourceToPublisher(getEventSource(), responseType, name, null, getAccept());
    }

    @Override
    public Publisher<?> method(String name, Entity<?> entity) {
        return eventSourceToPublisher(getEventSource(), String.class, name, entity, getAccept());
    }

    @Override
    public <R> Publisher<?> method(String name, Entity<?> entity, Class<R> responseType) {
        return eventSourceToPublisher(getEventSource(), responseType, name, entity, getAccept());
    }

    @Override
    public <R> Publisher<?> method(String name, Entity<?> entity, GenericType<R> responseType) {
        return eventSourceToPublisher(getEventSource(), responseType, name, entity, getAccept());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private <T> Publisher<T> eventSourceToPublisher(SseEventSourceImpl sseEventSource, Class<T> clazz, String verb, Entity<?> entity, MediaType[] mediaTypes) {
        Publisher<T> flowable = Flowable.create(
                new FlowableOnSubscribe<T>() {
                    @Override
                    public void subscribe(FlowableEmitter<T> emitter) throws Exception {
                        sseEventSource.register(
                                (InboundSseEvent e) -> {
                                    T t = e.readData(clazz, ((InboundSseEventImpl) e).getMediaType());
                                    emitter.onNext(t);
                                },
                                (Throwable t) -> emitter.onError(t),
                                () -> emitter.onComplete());
                        synchronized (monitor) {
                            if (!sseEventSource.isOpen()) {
                                sseEventSource.open(null, verb, entity, mediaTypes);
                            }
                        }
                    }
                },
                backpressureStrategy);
        return flowable;
    }

    private <T> Publisher<T> eventSourceToPublisher(SseEventSourceImpl sseEventSource, GenericType<T> type, String verb, Entity<?> entity, MediaType[] mediaTypes) {
        Publisher<T> flowable = Flowable.create(
                new FlowableOnSubscribe<T>() {

                    @Override
                    public void subscribe(FlowableEmitter<T> emitter) throws Exception {
                        sseEventSource.register(
                                (InboundSseEvent e) -> {
                                    T t = e.readData(type, ((InboundSseEventImpl) e).getMediaType());
                                    emitter.onNext(t);
                                },
                                (Throwable t) -> emitter.onError(t),
                                () -> emitter.onComplete());
                        synchronized (monitor) {
                            if (!sseEventSource.isOpen()) {
                                sseEventSource.open(null, verb, entity, mediaTypes);
                            }
                        }
                    }
                },
                backpressureStrategy);
        return flowable;
    }

    private SseEventSourceImpl getEventSource() {
        SourceBuilder builder = (SourceBuilder) SseEventSource.target(syncInvoker.getTarget());
        if (executorService != null) {
            builder.executor(executorService);
        }
        SseEventSourceImpl sseEventSource = (SseEventSourceImpl) builder.build();
        sseEventSource.setAlwaysReconnect(false);
        // mp-rest-client defines a ClientInvoker when a class proxy is to be called.
        // Make it available in sseEventSource
        ClientInvocation clientInvocation = ((ClientInvocationBuilder) syncInvoker).getClientInvocation();
        if (clientInvocation != null && clientInvocation.getClientInvoker() != null) {
            sseEventSource.setClientInvocation(clientInvocation);
        }
        return sseEventSource;
    }

    private MediaType[] getAccept() {
        if (syncInvoker instanceof ClientInvocationBuilder) {
            ClientInvocationBuilder builder = (ClientInvocationBuilder) syncInvoker;
            List<MediaType> accept = builder.getHeaders().getAcceptableMediaTypes();
            return accept.toArray(new MediaType[accept.size()]);
        } else {
            return null;
        }
    }
}