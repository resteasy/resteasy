package org.jboss.resteasy.core.filter;

import org.jboss.resteasy.core.interception.InterceptorRegistryListener;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.touri.ObjectToURI;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.PickConstructor;

import javax.ws.rs.BindingPriority;
import javax.ws.rs.NameBinding;
import javax.ws.rs.ext.DynamicBinding;
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
public class FilterRegistry<T>
{
   protected static interface FilterFactory
   {
      Class getFilterClass();
      Object createInterceptor();
      int getOrder();
      List<Class<? extends Annotation>> getNameBindings();

   }

   protected abstract class AbstractFilterFactory implements FilterFactory
   {
      protected int order = BindingPriority.USER;
      protected List<Class<? extends Annotation>> bindings;
      protected Class clazz;

      protected AbstractFilterFactory(Class clazz)
      {
         this.clazz = clazz;
         bindings = findNameBindings(clazz);
         setPrecedence(clazz);
      }

      protected void setPrecedence(Class<?> clazz)
      {
         BindingPriority precedence = clazz.getAnnotation(BindingPriority.class);
         if (precedence != null)
         {
            this.order = precedence.value();
         }
      }

      @Override
      public Class getFilterClass()
      {
         return clazz;
      }

      public int getOrder()
      {
         return order;
      }
      @Override
      public List<Class<? extends Annotation>> getNameBindings()
      {
         return bindings;
      }

   }

   protected List<Class<? extends Annotation>> findNameBindings(Class clazz)
   {
      ArrayList<Class<? extends Annotation>> bindings = new ArrayList<Class<? extends Annotation>>();
      for (Annotation an : clazz.getAnnotations())
      {
         if (an.annotationType().isAnnotationPresent(NameBinding.class))
         {
            bindings.add(an.annotationType());
         }
      }
      return bindings;
   }


   protected class SingletonInterceptorFactory extends AbstractFilterFactory
   {
      private Object target;

      public SingletonInterceptorFactory(Object target)
      {
         super(target.getClass());
         this.target = target;
      }

      public Object createInterceptor()
      {
         return target;
      }

   }

   protected class PerMethodInterceptorFactory extends AbstractFilterFactory
   {
      private ConstructorInjector constructorInjector;

      public PerMethodInterceptorFactory(Class clazz)
      {
         super(clazz);
         Constructor<?> constructor = PickConstructor.pickSingletonConstructor(clazz);
         if (constructor == null)
         {
            throw new RuntimeException("Unable to find a public constructor for interceptor class " + clazz.getName());
         }
         constructorInjector = providerFactory.getInjectorFactory().createConstructor(constructor);
      }

      public Object createInterceptor()
      {
         return constructorInjector.construct();
      }

   }

   protected ResteasyProviderFactory providerFactory;
   protected Class<T> intf;
   protected List<FilterFactory> interceptors = new ArrayList<FilterFactory>();

   public class PrecedenceComparator implements Comparator<FilterFactory>
   {
      public int compare(FilterFactory factory, FilterFactory factory2)
      {
         return factory.getOrder() - factory2.getOrder();
      }
   }

   /* todo

   public List<InterceptorRegistryListener> getListeners()
   {
      return listeners;
   }

    */
   public Class<T> getIntf()
   {
      return intf;
   }

   public FilterRegistry(Class<T> intf, ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      this.intf = intf;
   }

   public T[] bind(Class declaring, AccessibleObject target)
   {
      List<T> list = bindForList(declaring, target);
      return list.toArray((T[]) Array.newInstance(intf, list.size()));
   }

   public List<T> bindForList(Class declaring, AccessibleObject target)
   {
      List<T> list = new ArrayList<T>();
      for (FilterFactory factory : interceptors)
      {
         if (DynamicBinding.class.isAssignableFrom(factory.getFilterClass()))
         {
            if (target == null || !(target instanceof Method)) continue;

            Object interceptor = factory.createInterceptor();
            DynamicBinding accepted = (DynamicBinding) interceptor;
            if (accepted.isBound(declaring, (Method) target))
            {
               addNewInterceptor(list, interceptor);
            }
         }
         else if (factory.getNameBindings().size() > 0)
         {
            if (target == null || !(target instanceof Method)) continue;
            for (Class<? extends Annotation> an : factory.getNameBindings())
            {
               if (declaring != null && FindAnnotation.findAnnotation(declaring.getAnnotations(), an) != null)
               {
                  Object interceptor = factory.createInterceptor();
                  addNewInterceptor(list, interceptor);
                  break;
               }
               else if (target != null && FindAnnotation.findAnnotation(target.getAnnotations(), an) != null)
               {
                  Object interceptor = factory.createInterceptor();
                  addNewInterceptor(list, interceptor);
                  break;
               }
            }
         }
         else
         {
            Object interceptor = factory.createInterceptor();
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
      /*  todo
      for (InterceptorRegistryListener listener : listeners)
      {
         listener.registryUpdated(this);
      }
      */
   }

   public void register(T interceptor)
   {
      interceptors.add(new SingletonInterceptorFactory(interceptor));
      Collections.sort(interceptors, new PrecedenceComparator());
      /*  todo
      for (InterceptorRegistryListener listener : listeners)
      {
         listener.registryUpdated(this);
      }
      */
   }

}