package org.jboss.resteasy.core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.annotations.Stream;
import org.jboss.resteasy.plugins.providers.sse.SseImpl;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.AsyncResponseProvider;
import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author <a href="mailto:rsigal@redhat.com">Ron Sigal</a>
 * @version $Revision: 1 $
 */
public abstract class AsyncResponseConsumer 
{
   protected Map<Class<?>, Object> contextDataMap;
   protected ResourceMethodInvoker method;
   protected SynchronousDispatcher dispatcher;
   protected ResteasyAsynchronousResponse asyncResponse;
   protected boolean isComplete;

   public AsyncResponseConsumer(ResourceMethodInvoker method)
   {
      this.method = method;
      contextDataMap = ResteasyProviderFactory.getContextDataMap();
      dispatcher = (SynchronousDispatcher) contextDataMap.get(Dispatcher.class);
      HttpRequest httpRequest = (HttpRequest) contextDataMap.get(HttpRequest.class);
      if(httpRequest.getAsyncContext().isSuspended())
         asyncResponse = httpRequest.getAsyncContext().getAsyncResponse();
      else
         asyncResponse = httpRequest.getAsyncContext().suspend();
   }
   
   public static AsyncResponseConsumer makeAsyncResponseConsumer(ResourceMethodInvoker method, AsyncResponseProvider<?> asyncResponseProvider) {
      return new CompletionStageResponseConsumer(method, asyncResponseProvider);
   }

   public static AsyncResponseConsumer makeAsyncResponseConsumer(ResourceMethodInvoker method, AsyncStreamProvider<?> asyncStreamProvider) {
	  if(method.isSse())
	  {
		  return new AsyncStreamSseResponseConsumer(method, asyncStreamProvider);
	  }
      for (Annotation annotation : method.getMethodAnnotations())
      {
         if(annotation.annotationType() == Stream.class)
         {
            return new AsyncStreamingResponseConsumer(method, asyncStreamProvider);
         }
      }
      return new AsyncStreamCollectorResponseConsumer(method, asyncStreamProvider);
   }

   protected void doComplete() {
      asyncResponse.complete();
   }
   
   synchronized final public void complete(Throwable t)
   {
      if (!isComplete)
      {
         isComplete = true;
         doComplete();
         asyncResponse.completionCallbacks(t);
         ResteasyProviderFactory.removeContextDataLevel();
      }
   }

   protected void internalResume(Object entity, Consumer<Throwable> onComplete)
   {
      ResteasyProviderFactory.pushContextDataMap(contextDataMap);
      HttpRequest httpRequest = (HttpRequest) contextDataMap.get(HttpRequest.class);
      HttpResponse httpResponse = (HttpResponse) contextDataMap.get(HttpResponse.class);

      BuiltResponse builtResponse = createResponse(entity, httpRequest);
      try
      {
         sendBuiltResponse(builtResponse, httpRequest, httpResponse, e -> {
            if(e != null)
            {
               exceptionWhileResuming(e);
            }
            onComplete.accept(e);
         });
      }
      catch (IOException e)
      {
         onComplete.accept(e);
         exceptionWhileResuming(e);
      }
   }

   private void exceptionWhileResuming(Throwable e)
   {
      try 
      {
         // OK, not funny: if this is not a handled exception, it will just be logged and rethrown, so ignore it and move on
         internalResume(e, t -> {});
      }
      catch(Throwable t2)
      {
      }
      // be done with this stream
      complete(e);
   }

   protected void sendBuiltResponse(BuiltResponse builtResponse, HttpRequest httpRequest, HttpResponse httpResponse, Consumer<Throwable> onComplete) throws IOException
   {
      // send headers only if we're not streaming, or if we're sending the first stream element
      boolean sendHeaders = sendHeaders();
      ServerResponseWriter.writeNomapResponse(builtResponse, httpRequest, httpResponse, dispatcher.getProviderFactory(), onComplete, sendHeaders);
   }

   protected abstract boolean sendHeaders();
   
   protected void internalResume(Throwable t, Consumer<Throwable> onComplete)
   {
      ResteasyProviderFactory.pushContextDataMap(contextDataMap);
      HttpRequest httpRequest = (HttpRequest) contextDataMap.get(HttpRequest.class);
      HttpResponse httpResponse = (HttpResponse) contextDataMap.get(HttpResponse.class);
      dispatcher.writeException(httpRequest, httpResponse, t, onComplete);
   }

   protected BuiltResponse createResponse(Object entity, HttpRequest httpRequest)
   {
      BuiltResponse builtResponse = null;
      if (entity == null)
      {
         builtResponse = (BuiltResponse) Response.noContent().build();
      }
      else if (entity instanceof BuiltResponse)
      {
         builtResponse = (BuiltResponse) entity;
      }
      else if (entity instanceof Response)
      {
         Response r = (Response) entity;
         Headers<Object> metadata = new Headers<Object>();
         metadata.putAll(r.getMetadata());
         builtResponse = new BuiltResponse(r.getStatus(), r.getStatusInfo().getReasonPhrase(), metadata, r.getEntity(),  method.getMethodAnnotations());
      }
      else
      {
         if (method == null)
         {
            throw new IllegalStateException(Messages.MESSAGES.unknownMediaTypeResponseEntity());
         }
         BuiltResponse jaxrsResponse = (BuiltResponse) Response.ok(entity).build();
         // it has to be a Publisher<X>, so extract the X and wrap it around a List<X>
         // FIXME: actually the provider should extract that, because it could come from another type param
         // before conversion to Publisher
         Type unwrappedType = ((ParameterizedType)method.getGenericReturnType()).getActualTypeArguments()[0];
         Type newType = adaptGenericType(unwrappedType);

         jaxrsResponse.setGenericType(newType);
         jaxrsResponse.addMethodAnnotations(method.getMethodAnnotations());
         builtResponse = jaxrsResponse;
      }

      return builtResponse;
   }

   protected Type adaptGenericType(Type unwrappedType)
   {
      return unwrappedType;
   }

   private static class CompletionStageResponseConsumer extends AsyncResponseConsumer implements BiConsumer<Object, Throwable> 
   {
      private AsyncResponseProvider<?> asyncResponseProvider;

      public CompletionStageResponseConsumer(ResourceMethodInvoker method, AsyncResponseProvider<?> asyncResponseProvider)
      {
         super(method);
         this.asyncResponseProvider = asyncResponseProvider;
      }

      @Override
      protected boolean sendHeaders()
      {
         return true;
      }
      
      @Override
      public void accept(Object t, Throwable u)
      {
         if (t != null || u == null)
         {
            internalResume(t, x -> complete(null));
         }
         else
         {
            internalResume(u, x -> complete(u));
         }
      }

      @Override
      public void subscribe(Object rtn)
      {
         @SuppressWarnings({ "unchecked", "rawtypes" })
         CompletionStage<?> stage = ((AsyncResponseProvider)asyncResponseProvider).toCompletionStage(rtn);
         stage.whenComplete(this);
      }
   }

   private abstract static class AsyncStreamResponseConsumer extends AsyncResponseConsumer implements Subscriber<Object> 
   {
      protected Subscription subscription;
      private AsyncStreamProvider<?> asyncStreamProvider;

      public AsyncStreamResponseConsumer(ResourceMethodInvoker method, AsyncStreamProvider<?> asyncStreamProvider)
      {
         super(method);
         this.asyncStreamProvider = asyncStreamProvider;
      }

      @Override
      protected void doComplete()
      {
         // we can be done by exception before we've even subscribed
         if(subscription != null)
            subscription.cancel();
         super.doComplete();
      }
      
      @Override
      public void onComplete()
      {
         complete(null);
      }

      @Override
      public void onError(Throwable t)
      {
         internalResume(t, x -> complete(t));
      }

      /**
       * Subclass to collect the next element and inform if you want more.
       * @param element the next element to collect
       * @return true if you want more elements, false if not
       */
      protected void addNextElement(Object element) 
      {
         internalResume(element, t -> {
            if(t != null)
               complete(t);
         });
      }
      
      @Override
      public void onNext(Object v)
      {
         addNextElement(v);
      }

      @Override
      public void onSubscribe(Subscription subscription)
      {
         this.subscription = subscription;
         subscription.request(1);
      }
      
      @Override
      public void subscribe(Object rtn)
      {
         @SuppressWarnings({ "unchecked", "rawtypes" })
         Publisher<?> stage = ((AsyncStreamProvider)asyncStreamProvider).toAsyncStream(rtn);
         stage.subscribe(this);
      }
   }

   private static class AsyncStreamingResponseConsumer extends AsyncStreamResponseConsumer 
   {
      private boolean sentEntity;

      public AsyncStreamingResponseConsumer(ResourceMethodInvoker method, AsyncStreamProvider<?> asyncStreamProvider)
      {
         super(method, asyncStreamProvider);
      }

      @Override
      protected void sendBuiltResponse(BuiltResponse builtResponse, HttpRequest httpRequest, HttpResponse httpResponse, Consumer<Throwable> onComplete) throws IOException
      {
         super.sendBuiltResponse(builtResponse, httpRequest, httpResponse, onComplete);
         sentEntity = true;
      }
      
      protected void addNextElement(Object element) 
      {
         internalResume(element, t -> {
            if(t != null) 
            {
               complete(t);
            }
            else
            {
               subscription.request(1);
            }
         });
      }

      @Override
      protected boolean sendHeaders()
      {
         return !sentEntity;
      }
   }
   
   private static class AsyncStreamCollectorResponseConsumer extends AsyncStreamResponseConsumer 
   {
      private List<Object> collector = new ArrayList<Object>();

      public AsyncStreamCollectorResponseConsumer(ResourceMethodInvoker method, AsyncStreamProvider<?> asyncStreamProvider)
      {
         super(method, asyncStreamProvider);
      }

      @Override
      protected boolean sendHeaders()
      {
         return true;
      }
      
      @Override
      protected void addNextElement(Object element)
      {
         collector.add(element);
         subscription.request(1);
      }      

      @Override
      public void onComplete()
      {
         internalResume(collector, t -> complete(t));
      }
      
      @Override
      protected Type adaptGenericType(Type unwrappedType)
      {
         // we want a List<returnType>
         return new ParameterizedType()
         {

            @Override
            public Type[] getActualTypeArguments() {
               return new Type[]{unwrappedType};
            }

            @Override
            public Type getOwnerType() {
               return null;
            }

            @Override
            public Type getRawType() {
               return List.class;
            }
            // FIXME: equals/hashCode/toString?
         };
      }
   }

   private static class AsyncStreamSseResponseConsumer extends AsyncStreamResponseConsumer 
   {
      private SseImpl sse;
      private SseEventSink sseEventSink;
      private volatile boolean onCompleteReceived = false;
      private volatile boolean sendingEvent = false;
      
      private AsyncStreamSseResponseConsumer(ResourceMethodInvoker method, AsyncStreamProvider<?> asyncStreamProvider)
      {
         super(method, asyncStreamProvider);
         sse = new SseImpl();
         sseEventSink = ResteasyProviderFactory.getContextData(SseEventSink.class);
      }
      
      @Override
      protected void doComplete()
      {
         // don't call super.doComplete which completes the asyncContext because Sse does that
         subscription.cancel();
         sseEventSink.close();
      }

      @Override
      protected void addNextElement(Object element)
      {
         super.addNextElement(element);
      }

      @Override
      public synchronized void onComplete()
      {
         onCompleteReceived = true;
         if(sendingEvent == false)
            super.onComplete();
      }
      
      @Override
      protected void sendBuiltResponse(BuiltResponse builtResponse, HttpRequest httpRequest, HttpResponse httpResponse, Consumer<Throwable> onComplete)
      {
         ServerResponseWriter.setResponseMediaType(builtResponse, httpRequest, httpResponse, dispatcher.getProviderFactory(), method);
         OutboundSseEvent event = sse.newEventBuilder()
            .mediaType(builtResponse.getMediaType())
            .data(builtResponse.getEntityClass(), builtResponse.getEntity())
            .build();
         sendingEvent = true;
         // we can only get onComplete after we return from this method
         try {
            sseEventSink.send(event).whenComplete((val, ex) -> {
               synchronized(this) {
                  sendingEvent = false;
                  if(onCompleteReceived)
                     super.onComplete();
                  else if(ex != null)
                  {
                     // cancel the subscription
                     complete(ex);
                     onComplete.accept(ex);
                  }
                  else
                  {
                     // we're good, ask for the next one
                     subscription.request(1);
                     onComplete.accept(ex);
                  }
               }
            });
         }catch(Exception x) {
            // most likely connection closed
            complete(x);
            onComplete.accept(x);
         }
      }

      @Override
      protected boolean sendHeaders()
      {
         // never actually called since we override sendBuiltResponse
         return false;
      }
   }

   public abstract void subscribe(Object rtn);
}
