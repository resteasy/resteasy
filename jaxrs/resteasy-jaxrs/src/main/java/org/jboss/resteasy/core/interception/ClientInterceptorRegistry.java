package org.jboss.resteasy.core.interception;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientInterceptorRegistry
{
   protected List bindInterceptors(List<Class> classes, List singletons, Class declaring, AccessibleObject target)
   {
      List list = new ArrayList();
      for (Class clazz : classes)
      {
         Object interceptor = null;
         try
         {
            interceptor = clazz.newInstance();
         }
         catch (InstantiationException e)
         {
            throw new RuntimeException(e);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
         bindInterceptor(interceptor, declaring, target, list);
      }
      for (Object interceptor : singletons) bindInterceptor(interceptor, declaring, target, list);
      return list;
   }

   protected void bindInterceptor(Object interceptor, Class declaring, AccessibleObject target, List list)
   {
      if (interceptor instanceof AcceptedByMethod)
      {
         if (target == null || !(target instanceof Method)) return;

         AcceptedByMethod accepted = (AcceptedByMethod) interceptor;
         if (accepted.accept(declaring, (Method) target))
         {
            list.add(interceptor);
         }
      }
      else
      {
         list.add(interceptor);
      }
   }

   private List<Class> readerInterceptorClasses = new ArrayList<Class>();
   private List<MessageBodyReaderInterceptor> readerInterceptors = new ArrayList<MessageBodyReaderInterceptor>();

   public List<Class> getMessageBodyReaderInterceptorClasses()
   {
      return readerInterceptorClasses;
   }

   public List<MessageBodyReaderInterceptor> getMessageBodyReaderInterceptorInterceptors()
   {
      return readerInterceptors;
   }

   public void registerMessageBodyReaderInterceptor(Class clazz)
   {
      readerInterceptorClasses.add(clazz);
   }

   public void registerMessageBodyReaderInterceptors(Class[] classes)
   {
      for (Class clazz : classes) registerMessageBodyReaderInterceptor(clazz);
   }

   public void registerMessageBodyReaderInterceptor(MessageBodyReaderInterceptor interceptor)
   {
      readerInterceptors.add(interceptor);
   }

   public MessageBodyReaderInterceptor[] bindMessageBodyReaderInterceptors(Class declaring, Method method)
   {
      List<MessageBodyReaderInterceptor> list = bindInterceptors(readerInterceptorClasses, readerInterceptors, declaring, method);
      return list.toArray(new MessageBodyReaderInterceptor[0]);
   }

   private List<Class> writerInterceptorClasses = new ArrayList<Class>();
   private List<MessageBodyWriterInterceptor> writerInterceptors = new ArrayList<MessageBodyWriterInterceptor>();

   public List<Class> getMessageBodyWriterInterceptorClasses()
   {
      return writerInterceptorClasses;
   }

   public List<MessageBodyWriterInterceptor> getMessageBodyWriterInterceptorInterceptors()
   {
      return writerInterceptors;
   }

   public void registerMessageBodyWriterInterceptor(Class clazz)
   {
      writerInterceptorClasses.add(clazz);
   }

   public void registerMessageBodyWriterInterceptors(Class[] classes)
   {
      for (Class clazz : classes) registerMessageBodyWriterInterceptor(clazz);
   }

   public void registerMessageBodyWriterInterceptor(MessageBodyWriterInterceptor interceptor)
   {
      writerInterceptors.add(interceptor);
   }

   public MessageBodyWriterInterceptor[] bindMessageBodyWriterInterceptors(Class declaring, Method method)
   {
      List<MessageBodyReaderInterceptor> list = bindInterceptors(writerInterceptorClasses, writerInterceptors, declaring, method);
      return list.toArray(new MessageBodyWriterInterceptor[0]);
   }

   private List<Class> executionInterceptorClasses = new ArrayList<Class>();
   private List<ClientExecutionInterceptor> executionInterceptors = new ArrayList<ClientExecutionInterceptor>();

   public List<Class> geExecutionInterceptorClasses()
   {
      return executionInterceptorClasses;
   }

   public List<ClientExecutionInterceptor> getExecutionInterceptors()
   {
      return executionInterceptors;
   }

   public void registerExecutionInterceptor(Class clazz)
   {
      executionInterceptorClasses.add(clazz);
   }

   public void registerExecutionInterceptors(Class[] classes)
   {
      for (Class clazz : classes) registerExecutionInterceptor(clazz);
   }

   public void registerExecutionInterceptor(ClientExecutionInterceptor interceptor)
   {
      executionInterceptors.add(interceptor);
   }

   public ClientExecutionInterceptor[] bindExecutionInterceptors(Class declaring, Method method)
   {
      List<MessageBodyReaderInterceptor> list = bindInterceptors(executionInterceptorClasses, executionInterceptors, declaring, method);
      return list.toArray(new ClientExecutionInterceptor[0]);
   }
}