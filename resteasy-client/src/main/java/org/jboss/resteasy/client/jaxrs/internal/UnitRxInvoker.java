package org.jboss.resteasy.client.jaxrs.internal;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.engines.ReactiveClientHttpEngine;

public interface UnitRxInvoker<U> extends RxInvoker<ReactiveClientHttpEngine.Unit<?, U>>
{
   @Override
   ReactiveClientHttpEngine.Unit<Response, U> get();

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> get(Class<T> responseType);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> get(GenericType<T> responseType);

   @Override
   ReactiveClientHttpEngine.Unit<Response, U> put(Entity<?> entity);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> put(Entity<?> entity, Class<T> clazz);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> put(Entity<?> entity, GenericType<T> type);

   @Override
   ReactiveClientHttpEngine.Unit<Response, U> post(Entity<?> entity);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> post(Entity<?> entity, Class<T> clazz);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> post(Entity<?> entity, GenericType<T> type);

   @Override
   ReactiveClientHttpEngine.Unit<Response, U> delete();

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> delete(Class<T> responseType);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> delete(GenericType<T> responseType);

   @Override
   ReactiveClientHttpEngine.Unit<Response, U> head();

   @Override
   ReactiveClientHttpEngine.Unit<Response, U> options();

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> options(Class<T> responseType);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> options(GenericType<T> responseType);

   @Override
   ReactiveClientHttpEngine.Unit<Response, U> trace();

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> trace(Class<T> responseType);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> trace(GenericType<T> responseType);

   @Override
   ReactiveClientHttpEngine.Unit<Response, U> method(String name);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> method(String name, Class<T> responseType);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> method(String name, GenericType<T> responseType);

   @Override
   ReactiveClientHttpEngine.Unit<Response, U> method(String name, Entity<?> entity);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> method(String name, Entity<?> entity, Class<T> responseType);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T, U> method(String name, Entity<?> entity, GenericType<T> responseType);

}
