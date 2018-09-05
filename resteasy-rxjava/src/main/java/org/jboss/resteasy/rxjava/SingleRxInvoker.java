package org.jboss.resteasy.rxjava;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import rx.Single;

/**
 * @deprecated:
 * 
 *   "RxJava 1.x is now officially end-of-life (EOL). No further developments,
 *    bugfixes, enhancements, javadoc changes or maintenance will be provided by
 *    this project after version 1.3.8." - From https://github.com/ReactiveX/RxJava/releases
 *    
 *    Please upgrade to resteasy-rxjava2 and RxJava 2.x.
 */
public interface SingleRxInvoker extends RxInvoker<Single<?>>
{
   @Override
   Single<Response> get();

   @Override
   <T> Single<T> get(Class<T> responseType);

   @Override
   <T> Single<T> get(GenericType<T> responseType);

   @Override
   Single<Response> put(Entity<?> entity);

   @Override
   <T> Single<T> put(Entity<?> entity, Class<T> clazz);

   @Override
   <T> Single<T> put(Entity<?> entity, GenericType<T> type);

   @Override
   Single<Response> post(Entity<?> entity);

   @Override
   <T> Single<T> post(Entity<?> entity, Class<T> clazz);

   @Override
   <T> Single<T> post(Entity<?> entity, GenericType<T> type);

   @Override
   Single<Response> delete();

   @Override
   <T> Single<T> delete(Class<T> responseType);

   @Override
   <T> Single<T> delete(GenericType<T> responseType);

   @Override
   Single<Response> head();

   @Override
   Single<Response> options();

   @Override
   <T> Single<T> options(Class<T> responseType);

   @Override
   <T> Single<T> options(GenericType<T> responseType);

   @Override
   Single<Response> trace();

   @Override
   <T> Single<T> trace(Class<T> responseType);

   @Override
   <T> Single<T> trace(GenericType<T> responseType);

   @Override
   Single<Response> method(String name);

   @Override
   <T> Single<T> method(String name, Class<T> responseType);

   @Override
   <T> Single<T> method(String name, GenericType<T> responseType);

   @Override
   Single<Response> method(String name, Entity<?> entity);

   @Override
   <T> Single<T> method(String name, Entity<?> entity, Class<T> responseType);

   @Override
   <T> Single<T> method(String name, Entity<?> entity, GenericType<T> responseType);

}
