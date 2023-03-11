package org.jboss.resteasy.reactor;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.PublisherRxInvokerImpl;
import org.reactivestreams.Publisher;

import reactor.core.publisher.Mono;

public class MonoRxInvokerImpl implements MonoRxInvoker {

    private final MonoPublisherInvoker monoInvoker;

    static class MonoPublisherInvoker extends PublisherRxInvokerImpl {
        MonoPublisherInvoker(final ClientInvocationBuilder builder) {
            super(builder);
        }

        @Override
        protected <T> Publisher<T> toPublisher(CompletionStage<T> completable) {
            return Mono.fromCompletionStage(completable);
        }
    }

    public MonoRxInvokerImpl(final ClientInvocationBuilder builder) {
        monoInvoker = new MonoPublisherInvoker(Objects.requireNonNull(builder));
    }

    @Override
    public Mono<Response> get() {
        return Mono.from(monoInvoker.get());
    }

    @Override
    public <T> Mono<T> get(Class<T> responseType) {
        return Mono.from(monoInvoker.get(responseType));
    }

    @Override
    public <T> Mono<T> get(GenericType<T> responseType) {
        return Mono.from(monoInvoker.get(responseType));
    }

    @Override
    public Mono<Response> put(Entity<?> entity) {
        return Mono.from(monoInvoker.put(entity));
    }

    @Override
    public <T> Mono<T> put(Entity<?> entity, Class<T> clazz) {
        return Mono.from(monoInvoker.put(entity, clazz));
    }

    @Override
    public <T> Mono<T> put(Entity<?> entity, GenericType<T> type) {
        return Mono.from(monoInvoker.put(entity, type));
    }

    @Override
    public Mono<Response> post(Entity<?> entity) {
        return Mono.from(monoInvoker.post(entity));
    }

    @Override
    public <T> Mono<T> post(Entity<?> entity, Class<T> clazz) {
        return Mono.from(monoInvoker.post(entity, clazz));
    }

    @Override
    public <T> Mono<T> post(Entity<?> entity, GenericType<T> type) {
        return Mono.from(monoInvoker.post(entity, type));
    }

    @Override
    public Mono<Response> delete() {
        return Mono.from(monoInvoker.delete());
    }

    @Override
    public <T> Mono<T> delete(Class<T> responseType) {
        return Mono.from(monoInvoker.delete(responseType));
    }

    @Override
    public <T> Mono<T> delete(GenericType<T> responseType) {
        return Mono.from(monoInvoker.delete(responseType));
    }

    @Override
    public Mono<Response> head() {
        return Mono.from(monoInvoker.head());
    }

    @Override
    public Mono<Response> options() {
        return Mono.from(monoInvoker.options());
    }

    @Override
    public <T> Mono<T> options(Class<T> responseType) {
        return Mono.from(monoInvoker.options(responseType));
    }

    @Override
    public <T> Mono<T> options(GenericType<T> responseType) {
        return Mono.from(monoInvoker.options(responseType));
    }

    @Override
    public Mono<Response> trace() {
        return Mono.from(monoInvoker.trace());
    }

    @Override
    public <T> Mono<T> trace(Class<T> responseType) {
        return Mono.from(monoInvoker.trace(responseType));
    }

    @Override
    public <T> Mono<T> trace(GenericType<T> responseType) {
        return Mono.from(monoInvoker.trace(responseType));
    }

    @Override
    public Mono<Response> method(String name) {
        return Mono.from(monoInvoker.method(name));
    }

    @Override
    public <T> Mono<T> method(String name, Class<T> responseType) {
        return Mono.from(monoInvoker.method(name, responseType));
    }

    @Override
    public <T> Mono<T> method(String name, GenericType<T> responseType) {
        return Mono.from(monoInvoker.method(name, responseType));
    }

    @Override
    public Mono<Response> method(String name, Entity<?> entity) {
        return Mono.from(monoInvoker.method(name, entity));
    }

    @Override
    public <T> Mono<T> method(String name, Entity<?> entity, Class<T> responseType) {
        return Mono.from(monoInvoker.method(name, entity, responseType));
    }

    @Override
    public <T> Mono<T> method(String name, Entity<?> entity, GenericType<T> responseType) {
        return Mono.from(monoInvoker.method(name, entity, responseType));
    }
}
