package org.jboss.resteasy.client.jaxrs.internal;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.engines.ReactiveClientHttpEngine;

public interface UnitRxInvoker<U> extends RxInvoker<ReactiveClientHttpEngine.Unit<?>>
{
   @Override
   ReactiveClientHttpEngine.Unit<Response> get();

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> get(Class<T> responseType);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> get(GenericType<T> responseType);

   @Override
   ReactiveClientHttpEngine.Unit<Response> put(Entity<?> entity);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> put(Entity<?> entity, Class<T> clazz);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> put(Entity<?> entity, GenericType<T> type);

   @Override
   ReactiveClientHttpEngine.Unit<Response> post(Entity<?> entity);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> post(Entity<?> entity, Class<T> clazz);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> post(Entity<?> entity, GenericType<T> type);

   @Override
   ReactiveClientHttpEngine.Unit<Response> delete();

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> delete(Class<T> responseType);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> delete(GenericType<T> responseType);

   @Override
   ReactiveClientHttpEngine.Unit<Response> head();

   @Override
   ReactiveClientHttpEngine.Unit<Response> options();

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> options(Class<T> responseType);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> options(GenericType<T> responseType);

   @Override
   ReactiveClientHttpEngine.Unit<Response> trace();

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> trace(Class<T> responseType);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> trace(GenericType<T> responseType);

   @Override
   ReactiveClientHttpEngine.Unit<Response> method(String name);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> method(String name, Class<T> responseType);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> method(String name, GenericType<T> responseType);

   @Override
   ReactiveClientHttpEngine.Unit<Response> method(String name, Entity<?> entity);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> method(String name, Entity<?> entity, Class<T> responseType);

   @Override
   <T> ReactiveClientHttpEngine.Unit<T> method(String name, Entity<?> entity, GenericType<T> responseType);

}
