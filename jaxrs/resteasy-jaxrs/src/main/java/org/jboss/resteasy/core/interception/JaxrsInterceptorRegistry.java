package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;

import javax.ws.rs.BindingPriority;
import javax.ws.rs.NameBinding;
import javax.ws.rs.container.PreMatching;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class JaxrsInterceptorRegistry<T>
{
   public static class Match
   {
      public Match(Object interceptor, int order)
      {
         this.interceptor = interceptor;
         this.order = order;
      }

      final public Object interceptor;
      final public int order;
   }

   public static interface InterceptorFactory
   {
      Match preMatch();

      Match postMatch(Class declaring, AccessibleObject target);
   }

   protected static List<Class<? extends Annotation>> getNameBound(Class<?> declaring)
   {
      List<Class<? extends Annotation>> nameBound = new ArrayList<Class<? extends Annotation>>();
      for (Annotation annotation : declaring.getAnnotations())
      {
         if (annotation.annotationType().isAnnotationPresent(NameBinding.class))
         {
             nameBound.add(annotation.annotationType());
         }
      }
      return nameBound;
   }

   public static int getBindingPriority(Class<?> declaring)
   {
      BindingPriority priority = (BindingPriority) declaring.getAnnotation(BindingPriority.class);
      if (priority != null) return priority.value();
      return BindingPriority.USER;
   }

   public abstract class AbstractInterceptorFactory implements InterceptorFactory
   {
      protected final Class declaring;
      protected int order = BindingPriority.USER;
      protected List<Class<? extends Annotation>> nameBound;
      protected volatile boolean initialized;

      public AbstractInterceptorFactory(Class declaring)
      {
         this.declaring = declaring;
      }

      protected void setPrecedence(Class<?> declaring)
      {
         nameBound = getNameBound(declaring);
         order = getBindingPriority(declaring);
      }

      protected abstract void initialize();

      protected abstract Object getInterceptor();

      protected void checkInitialize()
      {
         boolean isInitialized = initialized;
         if (!isInitialized)
         {
            synchronized (this)
            {
               isInitialized = initialized;
               if (isInitialized) return;
               isInitialized = initialized = true;
               initialize();
            }
         }
      }

      public void setOrder(int order)
      {
         this.order = order;
      }

      @Override
      public Match preMatch()
      {
         if (declaring.isAnnotationPresent(PreMatching.class)) return new Match(getInterceptor(), order);
         return null;
      }

      @Override
      public Match postMatch(Class targetClass, AccessibleObject target)
      {
         if (declaring.isAnnotationPresent(PreMatching.class)) return null;
         if (targetClass != null && target != null)
         {
            if (nameBound.size() > 0)
            {
               for (Class<? extends Annotation> annotation : nameBound)
               {
                  if (targetClass.isAnnotationPresent(annotation) ||
                          target.isAnnotationPresent(annotation))
                  {
                     Object intercept = getInterceptor();
                     if (intercept == null)
                        throw new NullPointerException("interceptor null from class: " + this.getClass().getName());
                     return new Match(intercept, order);
                  }
               }
               return null;
            } else
            {
               Object intercept = getInterceptor();
               if (intercept == null)
                  throw new NullPointerException("interceptor null from class: " + this.getClass().getName());
               return new Match(intercept, order);
            }
         } else if (nameBound.size() == 0)
         {
            Object intercept = getInterceptor();
            if (intercept == null)
               throw new NullPointerException("interceptor null from class: " + this.getClass().getName());
            return new Match(intercept, order);
         } else
         {
            return null;
         }
      }

      protected Object createInterceptor()
      {
         return providerFactory.injectedInstance(declaring);
      }
   }


   protected class OnDemandInterceptorFactory extends AbstractInterceptorFactory
   {
      protected volatile Object interceptor;

      public OnDemandInterceptorFactory(Class declaring)
      {
         super(declaring);
         setPrecedence(declaring);
      }

      @Override
      protected void initialize()
      {
         interceptor = createInterceptor();
         providerFactory.injectProperties(interceptor);
         initialized = true;
      }

      protected void checkInitialize()
      {
         Object tmp = interceptor;
         if (tmp == null)
         {
            synchronized (this)
            {
               tmp = interceptor;
               if (tmp == null)
               {
                  initialize();
                  tmp = interceptor;
               }
            }
         }
      }


      @Override
      protected Object getInterceptor()
      {
         checkInitialize();
         return interceptor;
      }
   }

   protected class SingletonInterceptorFactory extends AbstractInterceptorFactory
   {
      protected Object interceptor;

      public SingletonInterceptorFactory(Class declaring, Object interceptor)
      {
         super(declaring);
         this.interceptor = interceptor;
         setPrecedence(declaring);
      }

      @Override
      protected void initialize()
      {
         providerFactory.injectProperties(interceptor);
      }

      @Override
      protected Object getInterceptor()
      {
         checkInitialize();
         return interceptor;
      }
   }


   public abstract class AbstractLegacyInterceptorFactory extends AbstractInterceptorFactory
   {
      protected LegacyPrecedence precedence;

      protected AbstractLegacyInterceptorFactory(Class declaring, LegacyPrecedence precedence)
      {
         super(declaring);
         this.precedence = precedence;
      }

      @Override
      protected void setPrecedence(Class<?> declaring)
      {
         order = precedence.calculateOrder(declaring);
      }

      @Override
      public Match preMatch()
      {
         return null;
      }

      public Object getLegacyMatch(Class declaring, AccessibleObject target)
      {
         Object interceptor = getInterceptor();
         if (interceptor instanceof AcceptedByMethod)
         {
            if (target == null || !(target instanceof Method)) return null;
            Method method = (Method) target;
            if (((AcceptedByMethod) interceptor).accept(declaring, method))
            {
               return interceptor;
            } else
            {
               return null;
            }
         }
         return interceptor;
      }

   }

   protected class LegacySingletonInterceptorFactory extends AbstractLegacyInterceptorFactory
   {
      protected Object interceptor;

      public LegacySingletonInterceptorFactory(Class declaring, Object interceptor, LegacyPrecedence precedence)
      {
         super(declaring, precedence);
         this.interceptor = interceptor;
         setPrecedence(declaring);
      }

      @Override
      protected void initialize()
      {
         providerFactory.injectProperties(interceptor);
      }

      @Override
      protected Object getInterceptor()
      {
         checkInitialize();
         return interceptor;
      }
   }

   protected class LegacyPerMethodInterceptorFactory extends AbstractLegacyInterceptorFactory
   {

      public LegacyPerMethodInterceptorFactory(Class declaring, LegacyPrecedence precedence)
      {
         super(declaring, precedence);
         setPrecedence(declaring);
      }

      @Override
      protected void initialize()
      {
      }

      @Override
      protected Object getInterceptor()
      {
         Object interceptor = createInterceptor();
         providerFactory.injectProperties(interceptor);
         return interceptor;
      }
   }

   protected List<InterceptorFactory> interceptors = new ArrayList<InterceptorFactory>();
   protected List<JaxrsInterceptorRegistryListener> listeners = new ArrayList<JaxrsInterceptorRegistryListener>();
   protected ResteasyProviderFactory providerFactory;
   protected Class<T> intf;

   public JaxrsInterceptorRegistry(ResteasyProviderFactory providerFactory, Class<T> intf)
   {
      this.providerFactory = providerFactory;
      this.intf = intf;
   }

   public JaxrsInterceptorRegistry<T> clone(ResteasyProviderFactory factory)
   {
      JaxrsInterceptorRegistry<T> clone = new JaxrsInterceptorRegistry(factory, intf);
      clone.interceptors.addAll(interceptors);
      return clone;
   }

   public Class<T> getIntf()
   {
      return intf;
   }

   public class PrecedenceComparator implements Comparator<Match>
   {
      public int compare(Match match, Match match2)
      {
         return match.order - match2.order;
      }
   }

   public List<JaxrsInterceptorRegistryListener> getListeners()
   {
      return listeners;
   }

   public T[] preMatch()
   {
      List<Match> matches = new ArrayList<Match>();
      for (InterceptorFactory factory : interceptors)
      {
         Match match = factory.preMatch();
         if (match != null)
         {
            matches.add(match);
         }
      }
      return createArray(matches);
   }


   public T[] postMatch(Class declaring, AccessibleObject target)
   {
      List<Match> matches = new ArrayList<Match>();
      for (InterceptorFactory factory : interceptors)
      {
         Match match = factory.postMatch(declaring, target);
         if (match != null)
         {
            matches.add(match);
         }
      }
      return createArray(matches);
   }

   private T[] createArray(List<Match> matches)
   {
      Collections.sort(matches, new PrecedenceComparator());
      T[] array = (T[]) Array.newInstance(intf, matches.size());
      for (int i = 0; i < array.length; i++)
      {
         array[i] = (T) matches.get(i).interceptor;
      }
      return array;
   }

   public void register(InterceptorFactory factory)
   {
      interceptors.add(factory);
      for (JaxrsInterceptorRegistryListener listener : listeners)
      {
         listener.registryUpdated(this);
      }
   }

   public void registerClass(Class<? extends T> declaring)
   {
      register(new OnDemandInterceptorFactory(declaring));
   }

   public void registerClass(Class<? extends T> declaring, int bindingPriority)
   {
      OnDemandInterceptorFactory factory = new OnDemandInterceptorFactory(declaring);
      if (bindingPriority > Integer.MIN_VALUE) factory.setOrder(bindingPriority);
      register(factory);
   }

   public void registerSingleton(T interceptor)
   {
      register(new SingletonInterceptorFactory(interceptor.getClass(), interceptor));
   }

   public void registerSingleton(T interceptor, int bindingPriority)
   {
      SingletonInterceptorFactory factory = new SingletonInterceptorFactory(interceptor.getClass(), interceptor);
      if (bindingPriority > Integer.MIN_VALUE) factory.setOrder(bindingPriority);
      register(factory);
   }
}