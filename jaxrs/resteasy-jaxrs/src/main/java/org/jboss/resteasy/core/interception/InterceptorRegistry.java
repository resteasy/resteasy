package org.jboss.resteasy.core.interception;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jboss.resteasy.core.PropertyInjectorImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class InterceptorRegistry<T>
{
   protected static interface InterceptorFactory
   {
      Object createInterceptor();
   }

   protected static class SingletonInterceptorFactory implements InterceptorFactory
   {
      private Object target;

      public SingletonInterceptorFactory(Object target)
      {
         this.target = target;
      }

      public Object createInterceptor()
      {
         return target;
      }
   }

   protected static class PerMethodInterceptorFactory implements InterceptorFactory
   {
      private Class clazz;

      public PerMethodInterceptorFactory(Class clazz)
      {
         this.clazz = clazz;
      }

      public Object createInterceptor()
      {
         try
         {
            return clazz.newInstance();
         }
         catch (InstantiationException e)
         {
            throw new RuntimeException(e);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
      }
   }

   protected ResteasyProviderFactory providerFactory;
   protected Class<T> intf;
   protected List<InterceptorFactory> interceptors = new ArrayList<InterceptorFactory>();

   public InterceptorRegistry(Class<T> intf, ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      this.intf = intf;
   }

   public T[] bind(Class declaring, AccessibleObject target)
   {
	  List<T> list = bindForList(declaring, target);
	  return list.toArray((T[]) Array.newInstance(intf, list.size()));
   }

   public List<T> bindForList(Class declaring, AccessibleObject target) {
	  List<T> list = new ArrayList<T>();
      for (InterceptorFactory factory : interceptors)
      {
         Object interceptor = factory.createInterceptor();

         if (interceptor instanceof AcceptedByMethod)
         {
            if (target == null || !(target instanceof Method)) continue;

            AcceptedByMethod accepted = (AcceptedByMethod) interceptor;
            if (accepted.accept(declaring, (Method) target))
            {
               addNewInterceptor(list, interceptor);
            }
         }
         else
         {
            addNewInterceptor(list, interceptor);
         }
      }
      return list;
}

   protected void addNewInterceptor(List<T> list, Object interceptor)
   {
      PropertyInjectorImpl injector = new PropertyInjectorImpl(interceptor.getClass(), providerFactory);
      injector.inject(interceptor);
      list.add((T) interceptor);
   }

   public void register(Class clazz)
   {
      interceptors.add(new PerMethodInterceptorFactory(clazz));
   }

   public void register(T interceptor)
   {
      interceptors.add(new SingletonInterceptorFactory(interceptor));
   }


   public void registerFirst(Class clazz)
   {
      interceptors.add(0, new PerMethodInterceptorFactory(clazz));
   }

   public void registerFirst(T interceptor)
   {
      interceptors.add(0, new SingletonInterceptorFactory(interceptor));
   }
}