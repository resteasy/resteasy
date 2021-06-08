package org.jboss.resteasy.client.jaxrs.internal;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.reactivestreams.Publisher;

/**
 * This {@link RxInvoker} produces a {@link Publisher} that will only produce
 * a single 'response' (e.g. {@link Response} via {@link #get()}, a deserialized
 * object via {@link #get(Class)}).
 */
public interface UniPublisherRxInvoker extends RxInvoker<Publisher<?>>
{
   @Override
   Publisher<Response> get();

   @Override
   <T> Publisher<T> get(Class<T> responseType);

   @Override
   <T> Publisher<T> get(GenericType<T> responseType);

   @Override
   Publisher<Response> put(Entity<?> entity);

   @Override
   <T> Publisher<T> put(Entity<?> entity, Class<T> clazz);

   @Override
   <T> Publisher<T> put(Entity<?> entity, GenericType<T> type);

   @Override
   Publisher<Response> post(Entity<?> entity);

   @Override
   <T> Publisher<T> post(Entity<?> entity, Class<T> clazz);

   @Override
   <T> Publisher<T> post(Entity<?> entity, GenericType<T> type);

   @Override
   Publisher<Response> delete();

   @Override
   <T> Publisher<T> delete(Class<T> responseType);

   @Override
   <T> Publisher<T> delete(GenericType<T> responseType);

   @Override
   Publisher<Response> head();

   @Override
   Publisher<Response> options();

   @Override
   <T> Publisher<T> options(Class<T> responseType);

   @Override
   <T> Publisher<T> options(GenericType<T> responseType);

   @Override
   Publisher<Response> trace();

   @Override
   <T> Publisher<T> trace(Class<T> responseType);

   @Override
   <T> Publisher<T> trace(GenericType<T> responseType);

   @Override
   Publisher<Response> method(String name);

   @Override
   <T> Publisher<T> method(String name, Class<T> responseType);

   @Override
   <T> Publisher<T> method(String name, GenericType<T> responseType);

   @Override
   Publisher<Response> method(String name, Entity<?> entity);

   @Override
   <T> Publisher<T> method(String name, Entity<?> entity, Class<T> responseType);

   @Override
   <T> Publisher<T> method(String name, Entity<?> entity, GenericType<T> responseType);

}
