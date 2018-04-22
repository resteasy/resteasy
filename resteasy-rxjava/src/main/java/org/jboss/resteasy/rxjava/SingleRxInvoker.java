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
   public Single<Response> get();

   @Override
   public <T> Single<T> get(Class<T> responseType);

   @Override
   public <T> Single<T> get(GenericType<T> responseType);

   @Override
   public Single<Response> put(Entity<?> entity);

   @Override
   public <T> Single<T> put(Entity<?> entity, Class<T> clazz);

   @Override
   public <T> Single<T> put(Entity<?> entity, GenericType<T> type);

   @Override
   public Single<Response> post(Entity<?> entity);

   @Override
   public <T> Single<T> post(Entity<?> entity, Class<T> clazz);

   @Override
   public <T> Single<T> post(Entity<?> entity, GenericType<T> type);

   @Override
   public Single<Response> delete();

   @Override
   public <T> Single<T> delete(Class<T> responseType);

   @Override
   public <T> Single<T> delete(GenericType<T> responseType);

   @Override
   public Single<Response> head();

   @Override
   public Single<Response> options();

   @Override
   public <T> Single<T> options(Class<T> responseType);

   @Override
   public <T> Single<T> options(GenericType<T> responseType);

   @Override
   public Single<Response> trace();

   @Override
   public <T> Single<T> trace(Class<T> responseType);

   @Override
   public <T> Single<T> trace(GenericType<T> responseType);

   @Override
   public Single<Response> method(String name);

   @Override
   public <T> Single<T> method(String name, Class<T> responseType);

   @Override
   public <T> Single<T> method(String name, GenericType<T> responseType);

   @Override
   public Single<Response> method(String name, Entity<?> entity);

   @Override
   public <T> Single<T> method(String name, Entity<?> entity, Class<T> responseType);

   @Override
   public <T> Single<T> method(String name, Entity<?> entity, GenericType<T> responseType);

}
