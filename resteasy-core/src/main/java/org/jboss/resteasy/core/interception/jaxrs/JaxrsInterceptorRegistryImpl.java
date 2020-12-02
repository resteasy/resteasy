package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistryListener;

import javax.annotation.Priority;
import jakarta.ws.rs.NameBinding;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Application;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class JaxrsInterceptorRegistryImpl<T> implements JaxrsInterceptorRegistry<T>
{
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

   public static int getPriority(Class<?> declaring)
   {
      Priority priority = (Priority) declaring.getAnnotation(Priority.class);
      if (priority != null) return priority.value();
      return Priorities.USER;
   }

   public abstract class AbstractInterceptorFactory implements InterceptorFactory
   {
      protected final Class declaring;
      protected int order = Priorities.USER;
      protected List<Class<? extends Annotation>> nameBound;
      protected volatile boolean initialized;
      protected boolean ignorePrematch;

      public AbstractInterceptorFactory(final Class declaring)
      {
         this.declaring = getNonSyntheticClass(declaring);
      }

      protected void setPrecedence(Class<?> declaring)
      {
         nameBound = getNameBound(declaring);
         order = getPriority(declaring);
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

      public void setIgnorePrematch(boolean ignorePrematch)
      {
         this.ignorePrematch = ignorePrematch;
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
         if (!ignorePrematch && declaring.isAnnotationPresent(PreMatching.class)) return null;
         if (targetClass != null && target != null)
         {
            if (nameBound.size() > 0)
            {
               Application application = ResteasyContext.getContextData(Application.class);
               // must match all namebound annotations
               for (Class<? extends Annotation> annotation : nameBound)
               {
                  if (!targetClass.isAnnotationPresent(annotation) &&
                          !target.isAnnotationPresent(annotation)
                          && (application == null || !getNonSyntheticClass(application.getClass()).isAnnotationPresent(annotation)))
                  {
                     return null;
                  }
               }
               // we matched all of them
               Object intercept = getInterceptor();
               if (intercept == null)
                  throw new NullPointerException(Messages.MESSAGES.interceptorNullFromClass(this.getClass().getName()));
               return new Match(intercept, order);
            }
            else
            {
               Object intercept = getInterceptor();
               if (intercept == null)
                  throw new NullPointerException(Messages.MESSAGES.interceptorNullFromClass(this.getClass().getName()));
               return new Match(intercept, order);
            }
         } else if (nameBound.size() == 0)
         {
            Object intercept = getInterceptor();
            if (intercept == null)
               throw new NullPointerException(Messages.MESSAGES.interceptorNullFromClass(this.getClass().getName()));
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
      private Object interceptor;

      public OnDemandInterceptorFactory(final Class declaring)
      {
         super(declaring);
         setPrecedence(declaring);
      }

      @Override
      protected void initialize()
      {
         final Object currentInterceptor = createInterceptor();
         providerFactory.injectProperties(currentInterceptor);
         // No need to make it volatile as we have a volatile write just after it
         interceptor = currentInterceptor;
         initialized = true;
      }

      protected void checkInitialize()
      {
         if (!initialized)
         {
            synchronized (this)
            {
               if (!initialized)
               {
                  initialize();
               }
            }
         }
      }


      @Override
      protected Object getInterceptor()
      {
         checkInitialize();
         // No need to make it volatile as we have a volatile read just before it
         return interceptor;
      }
   }

   protected class SingletonInterceptorFactory extends AbstractInterceptorFactory
   {
      protected Object interceptor;

      public SingletonInterceptorFactory(final Class declaring, final Object interceptor)
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

   protected List<InterceptorFactory> interceptors = new CopyOnWriteArrayList<InterceptorFactory>();
   protected List<JaxrsInterceptorRegistryListener> listeners = new CopyOnWriteArrayList<JaxrsInterceptorRegistryListener>();
   protected ResteasyProviderFactory providerFactory;
   protected Class<T> intf;
   protected volatile T[] cachedPreMatch;
   protected volatile T[] cachedPostMatch;

   public JaxrsInterceptorRegistryImpl(final ResteasyProviderFactory providerFactory, final Class<T> intf)
   {
      this.providerFactory = providerFactory;
      this.intf = intf;
   }

   public JaxrsInterceptorRegistry<T> clone(ResteasyProviderFactory factory)
   {
      JaxrsInterceptorRegistryImpl<T> clone = new JaxrsInterceptorRegistryImpl(factory, intf);
      clone.interceptors.addAll(interceptors);
      clone.cachedPreMatch = this.cachedPreMatch;
      clone.cachedPostMatch = this.cachedPostMatch;
      return clone;
   }

   public Class<T> getIntf()
   {
      return intf;
   }

   public List<JaxrsInterceptorRegistryListener> getListeners()
   {
      return listeners;
   }

   public T[] preMatch()
   {
      T[] currentPreMatch = cachedPreMatch;
      if (currentPreMatch == null)
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
         currentPreMatch = createArray(matches);
         cachedPreMatch = currentPreMatch;
      }
      return currentPreMatch;
   }


   public T[] postMatch(Class declaring, AccessibleObject target)
   {
      if (declaring == null && target == null) {
         T[] currentPostMatch = cachedPostMatch;
         if (currentPostMatch != null) {
            return currentPostMatch;
         }
      }

      List<Match> matches = new ArrayList<Match>();
      for (InterceptorFactory factory : interceptors) {
         Match match = factory.postMatch(declaring, target);
         if (match != null) {
            matches.add(match);
         }
      }

      final T[] array = createArray(matches);

      if(declaring == null && target == null) {
         cachedPostMatch = array;
      }

      return array;
   }

   private T[] createArray(List<Match> matches)
   {
      sort(matches);
      T[] array = (T[]) Array.newInstance(intf, matches.size());
      for (int i = 0; i < array.length; i++)
      {
         array[i] = (T) matches.get(i).interceptor;
      }
      return array;
   }

   protected void sort(List<Match> matches)
   {
      Collections.sort(matches, new AscendingPrecedenceComparator());
   }

   public synchronized void register(InterceptorFactory factory)
   {
      interceptors.add(factory);
      cachedPreMatch = null;
      cachedPostMatch = null;
      for (JaxrsInterceptorRegistryListener listener : listeners)
      {
         listener.registryUpdated(this, factory);
      }
   }

   public synchronized void registerClass(Class<? extends T> declaring)
   {
      register(new OnDemandInterceptorFactory(declaring));
   }

   public synchronized void registerClass(Class<? extends T> declaring, int priority)
   {
      OnDemandInterceptorFactory factory = new OnDemandInterceptorFactory(declaring);
      factory.setOrder(priority);
      register(factory);
   }

   public synchronized void registerSingleton(T interceptor)
   {
      register(new SingletonInterceptorFactory(interceptor.getClass(), interceptor));
   }

   public synchronized void registerSingleton(T interceptor, int priority)
   {
      SingletonInterceptorFactory factory = new SingletonInterceptorFactory(interceptor.getClass(), interceptor);
      factory.setOrder(priority);
      register(factory);
   }

   private Class<?> getNonSyntheticClass(Class<?> clazz)
   {
      while (clazz.isSynthetic())
      {
         clazz = clazz.getSuperclass();
      }
      return clazz;
   }
}
