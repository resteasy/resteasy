package org.jboss.resteasy.reactor;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.PublisherRxInvokerImpl;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletionStage;

public class MonoRxInvokerImpl extends PublisherRxInvokerImpl implements MonoRxInvoker {

    public MonoRxInvokerImpl(final ClientInvocationBuilder builder) {
        super(builder);
    }

    @Override
    protected <T> Mono<T> toPublisher(final CompletionStage<T> completable) {
        return Mono.fromCompletionStage(completable);
    }

    @Override
    public Mono<Response> get() {
        return Mono.from(super.get());
    }

    @Override
    public <T> Mono<T> get(Class<T> responseType) {
        return Mono.from(super.get(responseType));
    }

    @Override
    public <T> Mono<T> get(GenericType<T> responseType) {
        return Mono.from(super.get(responseType));
    }

    @Override
    public Mono<Response> put(Entity<?> entity) {
        return Mono.from(super.put(entity));
    }

    @Override
    public <T> Mono<T> put(Entity<?> entity, Class<T> clazz) {
        return Mono.from(super.put(entity, clazz));
    }

    @Override
    public <T> Mono<T> put(Entity<?> entity, GenericType<T> type) {
        return Mono.from(super.put(entity, type));
    }

    @Override
    public Mono<Response> post(Entity<?> entity) {
        return Mono.from(super.post(entity));
    }

    @Override
    public <T> Mono<T> post(Entity<?> entity, Class<T> clazz) {
        return Mono.from(super.post(entity, clazz));
    }

    @Override
    public <T> Mono<T> post(Entity<?> entity, GenericType<T> type) {
        return Mono.from(super.post(entity, type));
    }

    @Override
    public Mono<Response> delete() {
        return Mono.from(super.delete());
    }

    @Override
    public <T> Mono<T> delete(Class<T> responseType) {
        return Mono.from(super.delete(responseType));
    }

    @Override
    public <T> Mono<T> delete(GenericType<T> responseType) {
        return Mono.from(super.delete(responseType));
    }

    @Override
    public Mono<Response> head() {
        return Mono.from(super.head());
    }

    @Override
    public Mono<Response> options() {
        return Mono.from(super.options());
    }

    @Override
    public <T> Mono<T> options(Class<T> responseType) {
        return Mono.from(super.options(responseType));
    }

    @Override
    public <T> Mono<T> options(GenericType<T> responseType) {
        return Mono.from(super.options(responseType));
    }

    @Override
    public Mono<Response> trace() {
        return Mono.from(super.trace());
    }

    @Override
    public <T> Mono<T> trace(Class<T> responseType) {
        return Mono.from(super.trace(responseType));
    }

    @Override
    public <T> Mono<T> trace(GenericType<T> responseType) {
        return Mono.from(super.trace(responseType));
    }

    @Override
    public Mono<Response> method(String name) {
        return Mono.from(super.method(name));
    }

    @Override
    public <T> Mono<T> method(String name, Class<T> responseType) {
        return Mono.from(super.method(name, responseType));
    }

    @Override
    public <T> Mono<T> method(String name, GenericType<T> responseType) {
        return Mono.from(super.method(name, responseType));
    }

    @Override
    public Mono<Response> method(String name, Entity<?> entity) {
        return Mono.from(super.method(name, entity));
    }

    @Override
    public <T> Mono<T> method(String name, Entity<?> entity, Class<T> responseType) {
        return Mono.from(super.method(name, entity, responseType));
    }

    @Override
    public <T> Mono<T> method(String name, Entity<?> entity, GenericType<T> responseType) {
        return Mono.from(super.method(name, entity, responseType));
    }
}
