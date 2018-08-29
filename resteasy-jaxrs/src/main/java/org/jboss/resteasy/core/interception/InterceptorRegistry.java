package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy interceptor facility introduced in release 2.x
 * is replaced by the JAX-RS 2.0 compliant interceptor facility in release 3.0.x.
 * 
 * @see <a href="https://jcp.org/en/jsr/detail?id=339">jaxrs-api</a>
 */
@Deprecated
@SuppressWarnings("unchecked")
public class InterceptorRegistry<T>
{
   protected interface InterceptorFactory
   {
      Object preMatch();
      Object postMatch(Class declaring, AccessibleObject target);
      int getOrder();
   }

   protected abstract class AbstractInterceptorFactory implements InterceptorFactory
   {

      protected String precedence = "DEFAULT";
      protected int order = 100000;

      protected void setPrecedence(Class<?> clazz)
      {
         Precedence precedence = clazz.getAnnotation(Precedence.class);
         if (precedence != null)
         {
            this.precedence = precedence.value();
            Integer o = precedenceOrder.get(this.precedence);
            if (o == null) throw new RuntimeException(Messages.MESSAGES.unknownInterceptorPrecedence(this.precedence));
            this.order = o;
         }
         else
         {
            for (Annotation annotation : clazz.getAnnotations())
            {
               precedence = annotation.annotationType().getAnnotation(Precedence.class);
               if (precedence != null)
               {
                  this.precedence = precedence.value();
                  Integer o = precedenceOrder.get(this.precedence);
                  if (o == null) throw new RuntimeException(Messages.MESSAGES.unknownInterceptorPrecedence(this.precedence));
                  this.order = o;
                  break;
               }
            }
         }
      }

      public String getPrecedence()
      {
         return precedence;
      }

      public int getOrder()
      {
         return order;
      }

      protected Object binding(Class declaring, AccessibleObject target, Object inter)
      {
         if (inter instanceof AcceptedByMethod)
         {
            if (target == null || !(target instanceof Method)) return null;
            Method method = (Method)target;
            if (((AcceptedByMethod)inter).accept(declaring, method))
            {
               return inter;
            }
            else
            {
               return null;
            }
         }
         return inter;
      }
   }


   protected class SingletonInterceptorFactory extends AbstractInterceptorFactory
   {
      private Object interceptor;

      public SingletonInterceptorFactory(Object interceptor)
      {
         this.interceptor = interceptor;
         setPrecedence(interceptor.getClass());
      }

      @Override
      public Object preMatch()
      {
         return null;
      }

      @Override
      public Object postMatch(Class declaring, AccessibleObject target)
      {
         final Object inter = interceptor;
         return binding(declaring, target, inter);
      }

   }

   protected class PerMethodInterceptorFactory extends AbstractInterceptorFactory
   {
      private ConstructorInjector constructorInjector;

      public PerMethodInterceptorFactory(Class clazz)
      {
         constructorInjector = providerFactory.createConstructorInjector(clazz);
         setPrecedence(clazz);
      }

      @Override
      public Object preMatch()
      {
         return null;
      }

      @Override
      public Object postMatch(Class declaring, AccessibleObject target)
      {
         final Object inter = constructorInjector.construct();
         return binding(declaring, target, inter);
      }
   }

   protected ResteasyProviderFactory providerFactory;
   protected Class<T> intf;
   protected List<InterceptorFactory> interceptors = new ArrayList<InterceptorFactory>();
   protected Map<String, Integer> precedenceOrder = new HashMap<String, Integer>();
   protected List<String> precedenceList = new ArrayList<String>();
   protected List<InterceptorRegistryListener> listeners = new ArrayList<InterceptorRegistryListener>();

   public InterceptorRegistry<T> cloneTo(ResteasyProviderFactory factory)
   {
      InterceptorRegistry<T> clone = new InterceptorRegistry<T>(intf, factory);
      clone.interceptors.addAll(interceptors);
      clone.precedenceOrder.putAll(precedenceOrder);
      clone.precedenceList.addAll(precedenceList);
      return clone;
   }

   public class PrecedenceComparator implements Comparator<InterceptorFactory>
   {
      public int compare(InterceptorFactory factory, InterceptorFactory factory2)
      {
         return factory.getOrder() - factory2.getOrder();
      }
   }

   public List<InterceptorRegistryListener> getListeners()
   {
      return listeners;
   }

   public Class<T> getIntf()
   {
      return intf;
   }

   public InterceptorRegistry(Class<T> intf, ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      this.intf = intf;
      precedenceOrder.put("DEFAULT", 100000);
   }

   protected void recalculateOrder()
   {
      precedenceOrder.clear();
      for (int i = 0; i < precedenceList.size(); i++)
      {
         precedenceOrder.put(precedenceList.get(i), i);
      }
      precedenceOrder.put("DEFAULT", 100000);
   }

   public void appendPrecedence(String precedence)
   {
      precedenceList.add(precedence);
      recalculateOrder();
   }

   public void insertPrecedenceAfter(String after, String newPrecedence)
   {
      for (int i = 0; i < precedenceList.size(); i++)
      {
         if (precedenceList.get(i).equals(after))
         {
            precedenceList.add(i + 1, newPrecedence);
            break;
         }
      }
      recalculateOrder();
   }

   public void insertPrecedenceBefore(String after, String newPrecedence)
   {
      for (int i = 0; i < precedenceList.size(); i++)
      {
         if (precedenceList.get(i).equals(after))
         {
            precedenceList.add(i, newPrecedence);
            break;
         }
      }
      recalculateOrder();
   }

   public T[] bind(Class declaring, AccessibleObject target)
   {
      List<T> list = bindForList(declaring, target);
      return list.toArray((T[]) Array.newInstance(intf, list.size()));
   }

   public List<T> bindForList(Class declaring, AccessibleObject target)
   {
      List<T> list = new ArrayList<T>();
      for (InterceptorFactory factory : interceptors)
      {
         Object interceptor = factory.postMatch(declaring, target);
         if (interceptor != null) addNewInterceptor(list, interceptor);
      }
      return list;
   }

   protected void addNewInterceptor(List<T> list, Object interceptor)
   {
      providerFactory.injectProperties(interceptor);
      list.add((T) interceptor);
   }

   public void register(Class<? extends T> clazz)
   {
      interceptors.add(new PerMethodInterceptorFactory(clazz));
      Collections.sort(interceptors, new PrecedenceComparator());
      for (InterceptorRegistryListener listener : listeners)
      {
         listener.registryUpdated(this);
      }
   }

   public void register(T interceptor)
   {
      interceptors.add(new SingletonInterceptorFactory(interceptor));
      Collections.sort(interceptors, new PrecedenceComparator());
      for (InterceptorRegistryListener listener : listeners)
      {
         listener.registryUpdated(this);
      }
   }

}