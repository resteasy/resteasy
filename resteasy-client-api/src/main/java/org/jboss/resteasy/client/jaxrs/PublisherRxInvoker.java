package org.jboss.resteasy.client.jaxrs;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.RxInvoker;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.reactivestreams.Publisher;

public interface PublisherRxInvoker extends RxInvoker<Publisher<?>>
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
