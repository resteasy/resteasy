package org.jboss.resteasy.client.jaxrs.internal;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.reactivestreams.Publisher;

@SuppressWarnings("unchecked")
public class PublisherRxInvokerImpl implements PublisherRxInvoker {

    private final ClientInvocationBuilder builder;

    public PublisherRxInvokerImpl(final ClientInvocationBuilder builder) {
        this.builder = builder;
    }

    private ClientInvocation createClientInvocation(String method, Entity<?> entity)
    {
        ClientInvocation invocation = builder.createClientInvocation(builder.invocation);
        invocation.setMethod(method);
        invocation.setEntity(entity);
        return invocation;
    }

    // TODO lots of code shared with CompletionStageRxInvokerImpl.  Isolate.
    @Override
    public Publisher<Response> get() {
        return createClientInvocation(HttpMethod.GET, null).submitRx();
    }

    @Override
    public <T> Publisher<T> get(final Class<T> responseType) {
        return createClientInvocation(HttpMethod.GET, null).submitRx(responseType);
    }

    @Override
    public <T> Publisher<T> get(final GenericType<T> responseType) {
        return createClientInvocation(HttpMethod.GET, null).submitRx(responseType);
    }

    @Override
    public Publisher<Response> put(final Entity<?> entity) {
        return createClientInvocation(HttpMethod.PUT, entity).submitRx();
    }

    @Override
    public <T> Publisher<T> put(final Entity<?> entity, final Class<T> clazz) {
        return createClientInvocation(HttpMethod.PUT, entity).submitRx(clazz);
    }

    @Override
    public <T> Publisher<T> put(final Entity<?> entity, final GenericType<T> type) {
        return createClientInvocation(HttpMethod.PUT, entity).submitRx(type);
    }

    @Override
    public Publisher<Response> post(final Entity<?> entity) {
        return createClientInvocation(HttpMethod.POST, entity).submitRx();
    }

    @Override
    public <T> Publisher<T> post(final Entity<?> entity, final Class<T> clazz) {
        return createClientInvocation(HttpMethod.POST, entity).submitRx(clazz);
    }

    @Override
    public <T> Publisher<T> post(final Entity<?> entity, final GenericType<T> type) {
        return createClientInvocation(HttpMethod.POST, entity).submitRx(type);
    }

    @Override
    public Publisher<Response> delete() {
        return createClientInvocation(HttpMethod.DELETE, null).submitRx();
    }

    @Override
    public <T> Publisher<T> delete(final Class<T> responseType) {
        return createClientInvocation(HttpMethod.DELETE, null).submitRx(responseType);
    }

    @Override
    public <T> Publisher<T> delete(final GenericType<T> responseType) {
        return createClientInvocation(HttpMethod.DELETE, null).submitRx(responseType);
    }

    @Override
    public Publisher<Response> head() {
        return createClientInvocation(HttpMethod.HEAD, null).submitRx();
    }

    @Override
    public Publisher<Response> options() {
        return createClientInvocation(HttpMethod.OPTIONS, null).submitRx();
    }

    @Override
    public <T> Publisher<T> options(final Class<T> responseType) {
        return createClientInvocation(HttpMethod.OPTIONS, null).submitRx(responseType);
    }

    @Override
    public <T> Publisher<T> options(final GenericType<T> responseType) {
        return createClientInvocation(HttpMethod.OPTIONS, null).submitRx(responseType);
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
        return createClientInvocation(name, null).submitRx();
    }

    @Override
    public <T> Publisher<T> method(final String name, final Class<T> responseType) {
        return createClientInvocation(name, null).submitRx(responseType);
    }

    @Override
    public <T> Publisher<T> method(final String name, final GenericType<T> responseType) {
        return createClientInvocation(name, null).submitRx(responseType);
    }

    @Override
    public Publisher<Response> method(final String name, final Entity<?> entity) {
        return createClientInvocation(name, entity).submitRx();
    }

    @Override
    public <T> Publisher<T> method(final String name, final Entity<?> entity, final Class<T> responseType) {
        return createClientInvocation(name, entity).submitRx(responseType);
    }

    @Override
    public <T> Publisher<T> method(final String name, final Entity<?> entity, final GenericType<T> responseType) {
        return createClientInvocation(name, entity).submitRx(responseType);
    }
}
