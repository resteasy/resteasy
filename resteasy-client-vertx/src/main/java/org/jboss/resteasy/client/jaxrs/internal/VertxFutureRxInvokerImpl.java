package org.jboss.resteasy.client.jaxrs.internal;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.SyncInvoker;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class VertxFutureRxInvokerImpl implements VertxFutureRxInvoker {

    private final ClientInvocationBuilder builder;

    public VertxFutureRxInvokerImpl(SyncInvoker syncInvoker) {
        this.builder = (ClientInvocationBuilder) syncInvoker;
    }

    private ClientInvocation createClientInvocation(String method, Entity<?> entity) {
        ClientInvocation invocation = builder.createClientInvocation(builder.invocation);
        invocation.setMethod(method);
        invocation.setEntity(entity);
        return invocation;
    }

    @Override
    public Future<Response> get() {
        return method(HttpMethod.GET);
    }

    @Override
    public <T> Future<T> get(Class<T> responseType) {
        return method(HttpMethod.GET, responseType);
    }

    @Override
    public <T> Future<T> get(GenericType<T> responseType) {
        return method(HttpMethod.GET, responseType);
    }

    @Override
    public Future<Response> put(Entity<?> entity) {
        return method(HttpMethod.PUT, entity);
    }

    @Override
    public <T> Future<T> put(Entity<?> entity, Class<T> responseType) {
        return method(HttpMethod.PUT, entity, responseType);
    }

    @Override
    public <T> Future<T> put(Entity<?> entity, GenericType<T> responseType) {
        return method(HttpMethod.PUT, entity, responseType);
    }

    @Override
    public Future<Response> post(Entity<?> entity) {
        return method(HttpMethod.POST, entity);
    }

    @Override
    public <T> Future<T> post(Entity<?> entity, Class<T> responseType) {
        return method(HttpMethod.POST, entity, responseType);
    }

    @Override
    public <T> Future<T> post(Entity<?> entity, GenericType<T> responseType) {
        return method(HttpMethod.POST, entity, responseType);
    }

    @Override
    public Future<Response> delete() {
        return method(HttpMethod.DELETE);
    }

    @Override
    public <T> Future<T> delete(Class<T> responseType) {
        return method(HttpMethod.DELETE, responseType);
    }

    @Override
    public <T> Future<T> delete(GenericType<T> responseType) {
        return method(HttpMethod.DELETE, responseType);
    }

    @Override
    public Future<Response> head() {
        return method(HttpMethod.HEAD);
    }

    @Override
    public Future<Response> options() {
        return method(HttpMethod.OPTIONS);
    }

    @Override
    public <T> Future<T> options(Class<T> responseType) {
        return method(HttpMethod.OPTIONS, responseType);
    }

    @Override
    public <T> Future<T> options(GenericType<T> responseType) {
        return method(HttpMethod.OPTIONS, responseType);
    }

    @Override
    public Future<Response> trace() {
        return method("TRACE");
    }

    @Override
    public <T> Future<T> trace(Class<T> responseType) {
        return method("TRACE", responseType);
    }

    @Override
    public <T> Future<T> trace(GenericType<T> responseType) {
        return method("TRACE", responseType);
    }

    @Override
    public Future<Response> method(String name) {
        Context vertxContext = Vertx.currentContext();
        if (vertxContext != null) {
            return Future.fromCompletionStage(createClientInvocation(name, null).submitCF(), vertxContext);
        } else {
            return Future.fromCompletionStage(createClientInvocation(name, null).submitCF());
        }
    }

    @Override
    public <T> Future<T> method(String name, Class<T> responseType) {
        Context vertxContext = Vertx.currentContext();
        if (vertxContext != null) {
            return Future.fromCompletionStage(createClientInvocation(name, null).submitCF(responseType), vertxContext);
        } else {
            return Future.fromCompletionStage(createClientInvocation(name, null).submitCF(responseType));
        }
    }

    @Override
    public <T> Future<T> method(String name, GenericType<T> responseType) {
        Context vertxContext = Vertx.currentContext();
        if (vertxContext != null) {
            return Future.fromCompletionStage(createClientInvocation(name, null).submitCF(responseType), vertxContext);
        } else {
            return Future.fromCompletionStage(createClientInvocation(name, null).submitCF(responseType));
        }
    }

    @Override
    public Future<Response> method(String name, Entity<?> entity) {
        Context vertxContext = Vertx.currentContext();
        if (vertxContext != null) {
            return Future.fromCompletionStage(createClientInvocation(name, entity).submitCF(), vertxContext);
        } else {
            return Future.fromCompletionStage(createClientInvocation(name, entity).submitCF());
        }
    }

    @Override
    public <T> Future<T> method(String name, Entity<?> entity, Class<T> responseType) {
        Context vertxContext = Vertx.currentContext();
        if (vertxContext != null) {
            return Future.fromCompletionStage(createClientInvocation(name, entity).submitCF(responseType), vertxContext);
        } else {
            return Future.fromCompletionStage(createClientInvocation(name, entity).submitCF(responseType));
        }
    }

    @Override
    public <T> Future<T> method(String name, Entity<?> entity, GenericType<T> responseType) {
        Context vertxContext = Vertx.currentContext();
        if (vertxContext != null) {
            return Future.fromCompletionStage(createClientInvocation(name, entity).submitCF(responseType), vertxContext);
        } else {
            return Future.fromCompletionStage(createClientInvocation(name, entity).submitCF(responseType));
        }
    }
}
