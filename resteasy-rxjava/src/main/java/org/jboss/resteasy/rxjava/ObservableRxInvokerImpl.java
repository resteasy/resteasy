package org.jboss.resteasy.rxjava;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.plugins.providers.sse.InboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl;
import org.jboss.resteasy.plugins.providers.sse.client.SseEventSourceImpl.SourceBuilder;

import rx.Observable;
import rx.Subscriber;

public class ObservableRxInvokerImpl implements ObservableRxInvoker
{
	private ClientInvocationBuilder syncInvoker;
	private ScheduledExecutorService executorService;
	private SseEventSourceImpl sseEventSource;
	
	public ObservableRxInvokerImpl(SyncInvoker syncInvoker, ExecutorService executorService)
	{
		if (!(syncInvoker instanceof ClientInvocationBuilder))
		{
			throw new RuntimeException("Expection ClientInvocationBuilder"); // @TODO i18n
		}
		this.syncInvoker = (ClientInvocationBuilder) syncInvoker;
		if (executorService instanceof ScheduledExecutorService)
		{
			this.executorService = (ScheduledExecutorService) executorService;	
		}
	}
	
	@Override
	public Observable<?> get()
	{
		return eventSourceToObservable(getEventSource("GET", getAccept()), String.class);
	}

	@Override
	public <R> Observable<?> get(Class<R> responseType)
	{
		return eventSourceToObservable(getEventSource("GET", getAccept()), responseType);
	}

	@Override
	public <R> Observable<?> get(GenericType<R> responseType)
	{
		return eventSourceToObservable(getEventSource("GET", getAccept()), responseType);
	}

	@Override
	public Observable<?> put(Entity<?> entity)
	{
		return eventSourceToObservable(getEventSource("PUT", entity, getAccept()), String.class);
	}

	@Override
	public <R> Observable<?> put(Entity<?> entity, Class<R> responseType)
	{
		return eventSourceToObservable(getEventSource("PUT", entity, getAccept()), responseType);
	}

	@Override
	public <R> Observable<?> put(Entity<?> entity, GenericType<R> responseType)
	{
		return eventSourceToObservable(getEventSource("PUT", entity, getAccept()), responseType);
	}

	@Override
	public Observable<?> post(Entity<?> entity)
	{
		return eventSourceToObservable(getEventSource("POST", entity, getAccept()), String.class);
	}

	@Override
	public <R> Observable<?> post(Entity<?> entity, Class<R> responseType)
	{
		return eventSourceToObservable(getEventSource("POST", entity, getAccept()), responseType);
	}

	@Override
	public <R> Observable<?> post(Entity<?> entity, GenericType<R> responseType)
	{
		return eventSourceToObservable(getEventSource("POST", entity, getAccept()), responseType);
	}

	@Override
	public Observable<?> delete()
	{
		return eventSourceToObservable(getEventSource("DELETE", getAccept()), String.class);
	}

	@Override
	public <R> Observable<?> delete(Class<R> responseType)
	{
		return eventSourceToObservable(getEventSource("DELETE", getAccept()), responseType);
	}

	@Override
	public <R> Observable<?> delete(GenericType<R> responseType)
	{
		return eventSourceToObservable(getEventSource("DELETE", getAccept()), responseType);
	}

	@Override
	public Observable<?> head()
	{
		return eventSourceToObservable(getEventSource("HEAD", getAccept()), String.class);
	}

	@Override
	public Observable<?> options()
	{
		return eventSourceToObservable(getEventSource("OPTIONS", getAccept()), String.class);
	}

	@Override
	public <R> Observable<?> options(Class<R> responseType)
	{
		return eventSourceToObservable(getEventSource("OPTIONS", getAccept()), responseType);
	}

	@Override
	public <R> Observable<?> options(GenericType<R> responseType)
	{
		return eventSourceToObservable(getEventSource("OPTIONS", getAccept()), responseType);
	}

	@Override
	public Observable<?> trace()
	{
		return eventSourceToObservable(getEventSource("TRACE", getAccept()), String.class);
	}

	@Override
	public <R> Observable<?> trace(Class<R> responseType)
	{
		return eventSourceToObservable(getEventSource("TRACE", getAccept()), responseType);
	}

	@Override
	public <R> Observable<?> trace(GenericType<R> responseType)
	{
		return eventSourceToObservable(getEventSource("TRACE", getAccept()), responseType);
	}

	@Override
	public Observable<?> method(String name)
	{
		return eventSourceToObservable(getEventSource(name, getAccept()), String.class);
	}

	@Override
	public <R> Observable<?> method(String name, Class<R> responseType)
	{
		return eventSourceToObservable(getEventSource(name, getAccept()), responseType);
	}

	@Override
	public <R> Observable<?> method(String name, GenericType<R> responseType)
	{
		return eventSourceToObservable(getEventSource(name, getAccept()), responseType);
	}

	@Override
	public Observable<?> method(String name, Entity<?> entity)
	{
		return eventSourceToObservable(getEventSource(name, entity, getAccept()), String.class);
	}

	@Override
	public <R> Observable<?> method(String name, Entity<?> entity, Class<R> responseType)
	{
		return eventSourceToObservable(getEventSource(name, entity, getAccept()), responseType);
	}

	@Override
	public <R> Observable<?> method(String name, Entity<?> entity, GenericType<R> responseType)
	{
		return eventSourceToObservable(getEventSource(name, entity, getAccept()), responseType);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("deprecation")
	<T> Observable<T> eventSourceToObservable(SseEventSource sseEventSource, Class<T> clazz)
	{
		Observable<T> observable = Observable.create(
				new Observable.OnSubscribe<T>() {
					@Override
					public void call(Subscriber<? super T> sub) {
						sseEventSource.register((InboundSseEvent e) -> {T t = e.readData(clazz, ((InboundSseEventImpl) e).getMediaType()); sub.onNext(t);});
					}
				});
		return observable;
	}
	
	@SuppressWarnings("deprecation")
	<T> Observable<T> eventSourceToObservable(SseEventSource sseEventSource, GenericType<T> type)
	{
		Observable<T> observable = Observable.create(
				new Observable.OnSubscribe<T>() {
					@Override
					public void call(Subscriber<? super T> sub) {
						sseEventSource.register((InboundSseEvent e) -> {T t = e.readData(type, ((InboundSseEventImpl) e).getMediaType()); sub.onNext(t);});
					}
				});
		return observable;
	}

	protected SseEventSource getEventSource(String verb, MediaType... mediaType)
	{
		return getEventSource(verb, null, mediaType);
	}
	
	protected SseEventSource getEventSource(String verb, Entity<?> entity, MediaType... mediaType)
	{
		SourceBuilder builder = (SourceBuilder) SseEventSource.target(syncInvoker.getTarget());
		if (executorService != null)
		{
			builder.executor(executorService);
		}
		sseEventSource = (SseEventSourceImpl) builder.build();
		sseEventSource.open(null, verb, entity, mediaType);
		return sseEventSource;
	}
	
	private MediaType[] getAccept()
	{
		if (syncInvoker instanceof ClientInvocationBuilder)
		{
			ClientInvocationBuilder builder = (ClientInvocationBuilder) syncInvoker;
			List<MediaType> accept = builder.getHeaders().getAcceptableMediaTypes();
			return accept.toArray(new MediaType[accept.size()]);
		}
		else
		{
			return null;
		}
	}
}
