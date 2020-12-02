package org.jboss.resteasy.reactor;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.RxInvoker;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import reactor.core.publisher.Mono;

public interface MonoRxInvoker extends RxInvoker<Mono<?>>
{
   @Override
   Mono<Response> get();

   @Override
   <T> Mono<T> get(Class<T> responseType);

   @Override
   <T> Mono<T> get(GenericType<T> responseType);

   @Override
   Mono<Response> put(Entity<?> entity);

   @Override
   <T> Mono<T> put(Entity<?> entity, Class<T> clazz);

   @Override
   <T> Mono<T> put(Entity<?> entity, GenericType<T> type);

   @Override
   Mono<Response> post(Entity<?> entity);

   @Override
   <T> Mono<T> post(Entity<?> entity, Class<T> clazz);

   @Override
   <T> Mono<T> post(Entity<?> entity, GenericType<T> type);

   @Override
   Mono<Response> delete();

   @Override
   <T> Mono<T> delete(Class<T> responseType);

   @Override
   <T> Mono<T> delete(GenericType<T> responseType);

   @Override
   Mono<Response> head();

   @Override
   Mono<Response> options();

   @Override
   <T> Mono<T> options(Class<T> responseType);

   @Override
   <T> Mono<T> options(GenericType<T> responseType);

   @Override
   Mono<Response> trace();

   @Override
   <T> Mono<T> trace(Class<T> responseType);

   @Override
   <T> Mono<T> trace(GenericType<T> responseType);

   @Override
   Mono<Response> method(String name);

   @Override
   <T> Mono<T> method(String name, Class<T> responseType);

   @Override
   <T> Mono<T> method(String name, GenericType<T> responseType);

   @Override
   Mono<Response> method(String name, Entity<?> entity);

   @Override
   <T> Mono<T> method(String name, Entity<?> entity, Class<T> responseType);

   @Override
   <T> Mono<T> method(String name, Entity<?> entity, GenericType<T> responseType);

}
