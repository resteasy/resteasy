package org.jboss.resteasy.client.core;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.resteasy.core.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.core.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.core.interception.MessageBodyWriterInterceptor;

@SuppressWarnings("unchecked")
public class ClientInterceptorRepositoryImpl implements
		ClientInterceptorRepository
{

	public static enum InterceptorType
	{
		MessageBodyReader(MessageBodyReaderInterceptor.class), MessageBodyWriter(
				MessageBodyWriterInterceptor.class), ClientExecution(
				ClientExecutionInterceptor.class);

		Class<?> clazz;

		public static InterceptorType getInterceptorTypeFor(Class<?> type)
		{
			for (InterceptorType interceptorType : InterceptorType.values())
			{
				if (type == interceptorType.clazz)
				{
					return interceptorType;
				}
			}
			return null;
		}

		InterceptorType(Class<?> clazz)
		{
			this.clazz = clazz;
		}
	}

	private Map<InterceptorType, LinkedList<?>> interceptorLists = new HashMap<InterceptorType, LinkedList<?>>();

	public MessageBodyReaderInterceptor[] getReaderInterceptors()
	{
		return getArray(MessageBodyReaderInterceptor.class);
	}

	public MessageBodyWriterInterceptor[] getWriterInterceptors()
	{
		return getArray(MessageBodyWriterInterceptor.class);
	}

	public ClientExecutionInterceptor[] getExecutionInterceptors()
	{
		return getArray(ClientExecutionInterceptor.class);
	}

	private <T> T[] getArray(Class<T> type)
	{
		LinkedList<T> interceptors = getInterceptors(type);
		return (T[]) interceptors.toArray((T[]) Array.newInstance(type,
				interceptors.size()));
	}

	public void setReaderInterceptors(
			MessageBodyReaderInterceptor[] readerInterceptors)
	{
		setData(InterceptorType.MessageBodyReader, readerInterceptors);
	}

	public void setWriterInterceptors(
			MessageBodyWriterInterceptor[] writerInterceptors)
	{
		setData(InterceptorType.MessageBodyWriter, writerInterceptors);
	}

	public void setExecutionInterceptors(
			ClientExecutionInterceptor[] executionInterceptors)
	{
		setData(InterceptorType.ClientExecution, executionInterceptors);
	}

	public void setReaderInterceptors(
			Collection<MessageBodyReaderInterceptor> readerInterceptorList)
	{
		setData(InterceptorType.MessageBodyReader, readerInterceptorList);
	}

	public void setWriterInterceptors(
			Collection<MessageBodyWriterInterceptor> writerInterceptorList)
	{
		setData(InterceptorType.MessageBodyWriter, writerInterceptorList);
	}

	public void setExecutionInterceptors(
			Collection<ClientExecutionInterceptor> executionInterceptorList)
	{
		setData(InterceptorType.ClientExecution, executionInterceptorList);
	}

	public LinkedList<MessageBodyReaderInterceptor> getReaderInterceptorList()
	{
		return getInterceptors(InterceptorType.MessageBodyReader);
	}

	public LinkedList<MessageBodyWriterInterceptor> getWriterInterceptorList()
	{
		return getInterceptors(InterceptorType.MessageBodyWriter);
	}

	public LinkedList<ClientExecutionInterceptor> getExecutionInterceptorList()
	{
		return getInterceptors(InterceptorType.ClientExecution);
	}

	public <T> LinkedList<T> getInterceptors(Class<T> clazz)
	{
		InterceptorType interceptorType = InterceptorType
				.getInterceptorTypeFor(clazz);
		if (interceptorType == null)
			return null;
		return getInterceptors(interceptorType);
	}

	protected synchronized LinkedList getInterceptors(
			InterceptorType interceptorType)
	{
		LinkedList interceptors = interceptorLists.get(interceptorType);
		if (interceptors == null)
		{
			interceptorLists.put(interceptorType, interceptors = new LinkedList());
		}
		return interceptors;
	}

	private void setData(InterceptorType type, Object[] arr)
	{
		setData(type, Arrays.asList(arr));
	}

	private void setData(InterceptorType type, Collection newList)
	{
		LinkedList list = getInterceptors(type);
		list.clear();
		list.addAll(newList);
	}

	public void copyClientInterceptorsTo(ClientInterceptorRepositoryImpl copyTo)
	{
		for (Entry<InterceptorType, LinkedList<?>> entry : interceptorLists
				.entrySet())
		{
			LinkedList copyToInterceptors = copyTo.getInterceptors(entry.getKey());
			LinkedList copyFromInterceptors = this.getInterceptors(entry.getKey());
			copyToInterceptors.addAll(copyFromInterceptors);
		}
	}

	public void prefixClientInterceptorsTo(ClientInterceptorRepositoryImpl copyTo)
	{
		for (Entry<InterceptorType, LinkedList<?>> entry : interceptorLists
				.entrySet())
		{
			LinkedList copyToInterceptors = copyTo.getInterceptors(entry.getKey());
			LinkedList copyFromInterceptors = this.getInterceptors(entry.getKey());
			for (Object interceptor : copyFromInterceptors)
			{
				copyToInterceptors.addFirst(interceptor);
			}
		}
	}

   public void registerInterceptor(Object interceptor)
   {
      boolean registered = false;
      if (interceptor instanceof ClientExecutionInterceptor)
      {
         getExecutionInterceptorList().add(
               (ClientExecutionInterceptor) interceptor);
         registered = true;
      }
      if (interceptor instanceof MessageBodyReaderInterceptor)
      {
         getReaderInterceptorList().add(
               (MessageBodyReaderInterceptor) interceptor);
         registered = true;
      }
      if (interceptor instanceof MessageBodyWriterInterceptor)
      {
         getWriterInterceptorList().add(
               (MessageBodyWriterInterceptor) interceptor);
         registered = true;
      }

      if (!registered)
      {
         throw new RuntimeException(
               "The object you supplied to registerInterceptor is not of an understood type");
      }
   }

}
