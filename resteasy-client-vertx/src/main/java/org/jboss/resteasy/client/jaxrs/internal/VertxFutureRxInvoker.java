package org.jboss.resteasy.client.jaxrs.internal;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.RxInvoker;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import io.vertx.core.Future;

public interface VertxFutureRxInvoker extends RxInvoker<Future> {

    @Override
    Future<Response> get();

    @Override
    <T> Future<T> get(Class<T> responseType);

    @Override
    <T> Future<T> get(GenericType<T> responseType);

    @Override
    Future<Response> put(Entity<?> entity);

    @Override
    <T> Future<T> put(Entity<?> entity, Class<T> responseType);

    @Override
    <T> Future<T> put(Entity<?> entity, GenericType<T> responseType);

    @Override
    Future<Response> post(Entity<?> entity);

    @Override
    <T> Future<T> post(Entity<?> entity, Class<T> responseType);

    @Override
    <T> Future<T> post(Entity<?> entity, GenericType<T> responseType);

    @Override
    Future<Response> delete();

    @Override
    <T> Future<T> delete(Class<T> responseType);

    @Override
    <T> Future<T> delete(GenericType<T> responseType);

    @Override
    Future<Response> head();

    @Override
    Future<Response> options();

    @Override
    <T> Future<T> options(Class<T> responseType);

    @Override
    <T> Future<T> options(GenericType<T> responseType);

    @Override
    Future<Response> trace();

    @Override
    <T> Future<T> trace(Class<T> responseType);

    @Override
    <T> Future<T> trace(GenericType<T> responseType);

    @Override
    Future<Response> method(String name);

    @Override
    <T> Future<T> method(String name, Class<T> responseType);

    @Override
    <T> Future<T> method(String name, GenericType<T> responseType);

    @Override
    Future<Response> method(String name, Entity<?> entity);

    @Override
    <T> Future<T> method(String name, Entity<?> entity, Class<T> responseType);

    @Override
    <T> Future<T> method(String name, Entity<?> entity, GenericType<T> responseType);
}
