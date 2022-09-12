package org.jboss.resteasy.client.jaxrs.internal;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.PublisherRxInvoker;
import org.reactivestreams.Publisher;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public abstract class PublisherRxInvokerImpl implements PublisherRxInvoker {

    private final ClientInvocationBuilder builder;

    public PublisherRxInvokerImpl(final ClientInvocationBuilder builder) {
        this.builder = builder;
    }

    protected abstract <T> Publisher<T> toPublisher(CompletionStage<T> completable);

    private <T> Publisher<T> mkPublisher(
        final String method,
        final Entity<?> entity,
        final Function<ClientInvocation, Publisher<T>> mkPublisher
    ) {
        ClientInvocation invocation = builder.createClientInvocation(builder.invocation);
        invocation.setMethod(method);
        invocation.setEntity(entity);
        return mkPublisher.apply(invocation);
    }

    private Publisher<Response> mkPublisher(final String method, final Entity<?> entity) {
        return mkPublisher(method, entity, invocation ->
            invocation.reactive()
                .map(ClientInvocation.ReactiveInvocation::submit)
                .orElseGet(() -> toPublisher(invocation.submitCF()))
        );
    }

    private <T> Publisher<T> mkPublisher(final String method, final Entity<?> entity, final Class<T> responseType) {
        return mkPublisher(method, entity, invocation ->
            invocation.reactive()
                .map(r -> r.submit(responseType))
                .orElseGet(() -> toPublisher(invocation.submitCF(responseType)))
        );
    }

    private <T> Publisher<T> mkPublisher(final String method, final Entity<?> entity, final GenericType<T> responseType) {
        return mkPublisher(method, entity, invocation ->
            invocation.reactive()
                .map(r -> r.submit(responseType))
                .orElseGet(() -> toPublisher(invocation.submitCF(responseType)))
        );
    }

    @Override
    public Publisher<Response> get() {
        return mkPublisher(HttpMethod.GET, null);
    }

    @Override
    public <T> Publisher<T> get(final Class<T> responseType) {
        return mkPublisher(HttpMethod.GET, null, responseType);
    }

    @Override
    public <T> Publisher<T> get(final GenericType<T> responseType) {
        return mkPublisher(HttpMethod.GET, null, responseType);
    }

    @Override
    public Publisher<Response> put(final Entity<?> entity) {
        return mkPublisher(HttpMethod.PUT, entity);
    }

    @Override
    public <T> Publisher<T> put(final Entity<?> entity, final Class<T> clazz) {
        return mkPublisher(HttpMethod.PUT, entity, clazz);
    }

    @Override
    public <T> Publisher<T> put(final Entity<?> entity, final GenericType<T> type) {
        return mkPublisher(HttpMethod.PUT, entity, type);
    }

    @Override
    public Publisher<Response> post(final Entity<?> entity) {
        return mkPublisher(HttpMethod.POST, entity);
    }

    @Override
    public <T> Publisher<T> post(final Entity<?> entity, final Class<T> clazz) {
        return mkPublisher(HttpMethod.POST, entity, clazz);
    }

    @Override
    public <T> Publisher<T> post(final Entity<?> entity, final GenericType<T> type) {
        return mkPublisher(HttpMethod.POST, entity, type);
    }

    @Override
    public Publisher<Response> delete() {
        return mkPublisher(HttpMethod.DELETE, null);
    }

    @Override
    public <T> Publisher<T> delete(final Class<T> responseType) {
        return mkPublisher(HttpMethod.DELETE, null, responseType);
    }

    @Override
    public <T> Publisher<T> delete(final GenericType<T> responseType) {
        return mkPublisher(HttpMethod.DELETE, null, responseType);
    }

    @Override
    public Publisher<Response> head() {
        return mkPublisher(HttpMethod.HEAD, null);
    }

    @Override
    public Publisher<Response> options() {
        return mkPublisher(HttpMethod.OPTIONS, null);
    }

    @Override
    public <T> Publisher<T> options(final Class<T> responseType) {
        return mkPublisher(HttpMethod.OPTIONS, null, responseType);
    }

    @Override
    public <T> Publisher<T> options(final GenericType<T> responseType) {
        return mkPublisher(HttpMethod.OPTIONS, null, responseType);
    }

    @Override
    public Publisher<Response> trace() {
        return method("TRACE");
    }

    @Override
    public <T> Publisher<T> trace(final Class<T> responseType) {
        return method("TRACE", responseType);
    }

    @Override
    public <T> Publisher<T> trace(final GenericType<T> responseType) {
        return method("TRACE", responseType);
    }

    @Override
    public Publisher<Response> method(final String name) {
        return mkPublisher(name, null);
    }

    @Override
    public <T> Publisher<T> method(final String name, final Class<T> responseType) {
        return mkPublisher(name, null, responseType);
    }

    @Override
    public <T> Publisher<T> method(final String name, final GenericType<T> responseType) {
        return mkPublisher(name, null, responseType);
    }

    @Override
    public Publisher<Response> method(final String name, final Entity<?> entity) {
        return mkPublisher(name, entity);
    }

    @Override
    public <T> Publisher<T> method(final String name, final Entity<?> entity, final Class<T> responseType) {
        return mkPublisher(name, entity, responseType);
    }

    @Override
    public <T> Publisher<T> method(final String name, final Entity<?> entity, final GenericType<T> responseType) {
        return mkPublisher(name, entity, responseType);
    }
}
