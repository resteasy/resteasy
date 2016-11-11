package org.jboss.resteasy.client.core;

import org.jboss.resteasy.core.interception.ReaderInterceptorRegistry;
import org.jboss.resteasy.core.interception.WriterInterceptorRegistry;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("unchecked")
public class ClientInterceptorRepositoryImpl implements ClientInterceptorRepository
{

   private static enum InterceptorType
   {
      MessageBodyReader(ReaderInterceptor.class),
      MessageBodyWriter(
           WriterInterceptor.class),
      ClientExecution(
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

   protected ReaderInterceptor[] getReaderInterceptors()
   {
      return getArray(ReaderInterceptor.class);
   }

   protected WriterInterceptor[] getWriterInterceptors()
   {
      return getArray(WriterInterceptor.class);
   }

   protected ClientExecutionInterceptor[] getExecutionInterceptors()
   {
      return getArray(ClientExecutionInterceptor.class);
   }

   private <T> T[] getArray(Class<T> type)
   {
      LinkedList<T> interceptors = getInterceptors(type);
      return (T[]) interceptors.toArray((T[]) Array.newInstance(type,
              interceptors.size()));
   }

   protected void setReaderInterceptors(
           ReaderInterceptor[] readerInterceptors)
   {
      setData(InterceptorType.MessageBodyReader, readerInterceptors);
   }

   protected void setWriterInterceptors(
           WriterInterceptor[] writerInterceptors)
   {
      setData(InterceptorType.MessageBodyWriter, writerInterceptors);
   }

   protected void setExecutionInterceptors(
           ClientExecutionInterceptor[] executionInterceptors)
   {
      setData(InterceptorType.ClientExecution, executionInterceptors);
   }

   protected void setExecutionInterceptors(
           Collection<ClientExecutionInterceptor> executionInterceptorList)
   {
      setData(InterceptorType.ClientExecution, executionInterceptorList);
   }

   public LinkedList<ReaderInterceptor> getReaderInterceptorList()
   {
      return getInterceptors(InterceptorType.MessageBodyReader);
   }

   public LinkedList<WriterInterceptor> getWriterInterceptorList()
   {
      return getInterceptors(InterceptorType.MessageBodyWriter);
   }

   public LinkedList<ClientExecutionInterceptor> getExecutionInterceptorList()
   {
      return getInterceptors(InterceptorType.ClientExecution);
   }

   protected <T> LinkedList<T> getInterceptors(Class<T> clazz)
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
                 new ReaderInterceptorRegistry.ReaderInterceptorFacade((MessageBodyReaderInterceptor)interceptor));
         registered = true;
      }
      if (interceptor instanceof MessageBodyWriterInterceptor)
      {
         getWriterInterceptorList().add(
                 new WriterInterceptorRegistry.WriterInterceptorFacade((MessageBodyWriterInterceptor) interceptor));
         registered = true;
      }

      if (!registered)
      {
         throw new RuntimeException(Messages.MESSAGES.entityNotOfUnderstoodType());
      }
   }

}
