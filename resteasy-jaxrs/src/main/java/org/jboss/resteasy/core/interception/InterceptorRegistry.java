package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.i18n.Messages;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.util.PickConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
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
 */
@SuppressWarnings("unchecked")
public class InterceptorRegistry<T>
{
   protected static interface InterceptorFactory
   {
      Object createInterceptor();

      String getPrecedence();

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

   }


   protected class SingletonInterceptorFactory extends AbstractInterceptorFactory
   {
      private Object target;

      public SingletonInterceptorFactory(Object target)
      {
         this.target = target;
         setPrecedence(target.getClass());
      }

      public Object createInterceptor()
      {
         return target;
      }

   }

   protected class PerMethodInterceptorFactory extends AbstractInterceptorFactory
   {
      private ConstructorInjector constructorInjector;

      public PerMethodInterceptorFactory(Class clazz)
      {
         Constructor<?> constructor = PickConstructor.pickSingletonConstructor(clazz);
         if (constructor == null)
         {
            throw new RuntimeException(Messages.MESSAGES.unableToFindPublicConstructorForInterceptor(clazz.getName()));
         }
         constructorInjector = providerFactory.getInjectorFactory().createConstructor(constructor);
         setPrecedence(clazz);
      }

      public Object createInterceptor()
      {
         return constructorInjector.construct();
      }
   }

   protected ResteasyProviderFactory providerFactory;
   protected Class<T> intf;
   protected List<InterceptorFactory> interceptors = new ArrayList<InterceptorFactory>();
   protected Map<String, Integer> precedenceOrder = new HashMap<String, Integer>();
   protected List<String> precedenceList = new ArrayList<String>();
   protected List<InterceptorRegistryListener> listeners = new ArrayList<InterceptorRegistryListener>();

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