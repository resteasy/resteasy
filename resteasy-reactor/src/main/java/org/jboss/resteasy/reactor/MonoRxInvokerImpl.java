package org.jboss.resteasy.reactor;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.PublisherRxInvoker;
import org.jboss.resteasy.client.jaxrs.internal.PublisherRxInvokerImpl;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

public class MonoRxInvokerImpl extends PublisherRxInvokerImpl implements MonoRxInvoker {

    public MonoRxInvokerImpl(ClientInvocationBuilder builder) {
        super(builder);
    }

    @Override
    protected <T> Mono<T> toPublisher(CompletionStage<T> completable) {
        return Mono.fromCompletionStage(completable);
    }

    private static <T> Mono<T> toMono(final Publisher<T> publisher) {
        return Mono.from(publisher);
    }

    @Override
    public Mono<Response> get() {
        return toMono(super.get());
    }

    @Override
    public <T> Mono<T> get(Class<T> responseType) {
        return toMono(super.get(responseType));
    }

    @Override
    public <T> Mono<T> get(GenericType<T> responseType) {
        return toMono(super.get(responseType));
    }

    @Override
    public Mono<Response> put(Entity<?> entity) {
        return toMono(super.put(entity));
    }

    @Override
    public <T> Mono<T> put(Entity<?> entity, Class<T> clazz) {
        return toMono(super.put(entity, clazz));
    }

    @Override
    public <T> Mono<T> put(Entity<?> entity, GenericType<T> type) {
        return toMono(super.put(entity, type));
    }

    @Override
    public Mono<Response> post(Entity<?> entity) {
        return toMono(super.post(entity));
    }

    @Override
    public <T> Mono<T> post(Entity<?> entity, Class<T> clazz) {
        return toMono(super.post(entity, clazz));
    }

    @Override
    public <T> Mono<T> post(Entity<?> entity, GenericType<T> type) {
        return toMono(super.post(entity, type));
    }

    @Override
    public Mono<Response> delete() {
        return toMono(super.delete());
    }

    @Override
    public <T> Mono<T> delete(Class<T> responseType) {
        return toMono(super.delete(responseType));
    }

    @Override
    public <T> Mono<T> delete(GenericType<T> responseType) {
        return toMono(super.delete(responseType));
    }

    @Override
    public Mono<Response> head() {
        return toMono(super.head());
    }

    @Override
    public Mono<Response> options() {
        return toMono(super.options());
    }

    @Override
    public <T> Mono<T> options(Class<T> responseType) {
        return toMono(super.options(responseType));
    }

    @Override
    public <T> Mono<T> options(GenericType<T> responseType) {
        return toMono(super.options(responseType));
    }

    @Override
    public Mono<Response> trace() {
        return toMono(super.trace());
    }

    @Override
    public <T> Mono<T> trace(Class<T> responseType) {
        return toMono(super.trace(responseType));
    }

    @Override
    public <T> Mono<T> trace(GenericType<T> responseType) {
        return toMono(super.trace(responseType));
    }

    @Override
    public Mono<Response> method(String name) {
        return toMono(super.method(name));
    }

    @Override
    public <T> Mono<T> method(String name, Class<T> responseType) {
        return toMono(super.method(name, responseType));
    }

    @Override
    public <T> Mono<T> method(String name, GenericType<T> responseType) {
        return toMono(super.method(name, responseType));
    }

    @Override
    public Mono<Response> method(String name, Entity<?> entity) {
        return toMono(super.method(name, entity));
    }

    @Override
    public <T> Mono<T> method(String name, Entity<?> entity, Class<T> responseType) {
        return toMono(super.method(name, entity, responseType));
    }

    @Override
    public <T> Mono<T> method(String name, Entity<?> entity, GenericType<T> responseType) {
        return toMono(super.method(name, entity, responseType));
    }
}
