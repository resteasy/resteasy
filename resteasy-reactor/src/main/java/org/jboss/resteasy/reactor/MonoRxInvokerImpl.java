package org.jboss.resteasy.reactor;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.internal.PublisherRxInvoker;
import reactor.core.publisher.Mono;

@SuppressWarnings("unchecked")
public class MonoRxInvokerImpl implements MonoRxInvoker
{
   private final PublisherRxInvoker publisherRxInvoker;

   public MonoRxInvokerImpl(final PublisherRxInvoker publisherRxInvoker)
   {
      this.publisherRxInvoker = publisherRxInvoker;
   }

   @Override
   public Mono<Response> get() {
      return Mono.from(publisherRxInvoker.get());
   }

   @Override
   public <T> Mono<T> get(final Class<T> responseType) {
      return Mono.from(publisherRxInvoker.get(responseType));
   }

   @Override
   public <T> Mono<T> get(final GenericType<T> responseType) {
      return Mono.from(publisherRxInvoker.get(responseType));
   }

   @Override
   public Mono<Response> put(final Entity<?> entity) {
      return Mono.from(publisherRxInvoker.put(entity));
   }

   @Override
   public <T> Mono<T> put(final Entity<?> entity, final Class<T> clazz) {
      return Mono.from(publisherRxInvoker.put(entity, clazz));
   }

   @Override
   public <T> Mono<T> put(final Entity<?> entity, final GenericType<T> type) {
      return Mono.from(publisherRxInvoker.put(entity, type));
   }

   @Override
   public Mono<Response> post(final Entity<?> entity) {
      return Mono.from(publisherRxInvoker.post(entity));
   }

   @Override
   public <T> Mono<T> post(final Entity<?> entity, final Class<T> clazz) {
      return Mono.from(publisherRxInvoker.post(entity, clazz));
   }

   @Override
   public <T> Mono<T> post(final Entity<?> entity, final GenericType<T> type) {
      return Mono.from(publisherRxInvoker.post(entity, type));
   }

   @Override
   public Mono<Response> delete() {
      return Mono.from(publisherRxInvoker.delete());
   }

   @Override
   public <T> Mono<T> delete(final Class<T> responseType) {
      return Mono.from(publisherRxInvoker.delete(responseType));
   }

   @Override
   public <T> Mono<T> delete(final GenericType<T> responseType) {
      return Mono.from(publisherRxInvoker.delete(responseType));
   }

   @Override
   public Mono<Response> head() {
      return Mono.from(publisherRxInvoker.head());
   }

   @Override
   public Mono<Response> options() {
      return Mono.from(publisherRxInvoker.options());
   }

   @Override
   public <T> Mono<T> options(final Class<T> responseType) {
      return Mono.from(publisherRxInvoker.options(responseType));
   }

   @Override
   public <T> Mono<T> options(final GenericType<T> responseType) {
      return Mono.from(publisherRxInvoker.options(responseType));
   }

   @Override
   public Mono<Response> trace() {
      return Mono.from(publisherRxInvoker.trace());
   }

   @Override
   public <T> Mono<T> trace(final Class<T> responseType) {
      return Mono.from(publisherRxInvoker.trace(responseType));
   }

   @Override
   public <T> Mono<T> trace(final GenericType<T> responseType) {
      return Mono.from(publisherRxInvoker.trace(responseType));
   }

   @Override
   public Mono<Response> method(final String name) {
      return Mono.from(publisherRxInvoker.method(name));
   }

   @Override
   public <T> Mono<T> method(final String name, final Class<T> responseType) {
      return Mono.from(publisherRxInvoker.method(name, responseType));
   }

   @Override
   public <T> Mono<T> method(final String name, final GenericType<T> responseType) {
      return Mono.from(publisherRxInvoker.method(name, responseType));
   }

   @Override
   public Mono<Response> method(final String name, final Entity<?> entity) {
      return Mono.from(publisherRxInvoker.method(name, entity));
   }

   @Override
   public <T> Mono<T> method(final String name, final Entity<?> entity, final Class<T> responseType) {
      return Mono.from(publisherRxInvoker.method(name, entity, responseType));
   }

   @Override
   public <T> Mono<T> method(final String name, final Entity<?> entity, final GenericType<T> responseType) {
      return Mono.from(publisherRxInvoker.method(name, entity, responseType));
   }
}
