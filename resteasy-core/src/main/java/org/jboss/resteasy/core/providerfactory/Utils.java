package org.jboss.resteasy.core.providerfactory;

import java.lang.reflect.Constructor;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.LinkBuilderImpl;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.jboss.resteasy.specimpl.ResteasyUriBuilderImpl;
import org.jboss.resteasy.specimpl.VariantListBuilderImpl;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.util.PickConstructor;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class Utils
{
   private Utils() {
   }

   static boolean isA(Class target, Class type, Map<Class<?>, Integer> contracts)
   {
      if (!type.isAssignableFrom(target))
         return false;
      if (contracts == null || contracts.size() == 0)
         return true;
      for (Class<?> contract : contracts.keySet())
      {
         if (contract.equals(type))
            return true;
      }
      return false;
   }

   static boolean isA(Object target, Class type, Map<Class<?>, Integer> contracts)
   {
      return isA(target.getClass(), type, contracts);
   }

   static int getPriority(Integer override, Map<Class<?>, Integer> contracts, Class type, Class<?> component)
   {
      if (override != null)
         return override;
      if (contracts != null)
      {
         Integer p = contracts.get(type);
         if (p != null)
            return p;
      }
      // Check for weld proxy.
      component = component.isSynthetic() ? component.getSuperclass() : component;
      Priority priority = component.getAnnotation(Priority.class);
      if (priority == null)
         return Priorities.USER;
      return priority.value();
   }

   static void injectProperties(ResteasyProviderFactory rpf, Class declaring, Object obj)
   {
      rpf.getInjectorFactory().createPropertyInjector(declaring, rpf).inject(obj, false).toCompletableFuture()
            .getNow(null);
   }

   static void injectProperties(ResteasyProviderFactory rpf, Object obj)
   {
      rpf.getInjectorFactory().createPropertyInjector(obj.getClass(), rpf).inject(obj, false).toCompletableFuture()
            .getNow(null);
   }

   static void injectProperties(ResteasyProviderFactory rpf, Object obj, HttpRequest request, HttpResponse response)
   {
      rpf.getInjectorFactory().createPropertyInjector(obj.getClass(), rpf).inject(request, response, obj, false)
            .toCompletableFuture().getNow(null);
   }

   static <T> T createProviderInstance(ResteasyProviderFactory rpf, Class<? extends T> clazz)
   {
      ConstructorInjector constructorInjector = createConstructorInjector(rpf, clazz);

      T provider = (T) constructorInjector.construct(false).toCompletableFuture().getNow(null);
      return provider;
   }

   private static <T> ConstructorInjector createConstructorInjector(ResteasyProviderFactory rpf, Class<? extends T> clazz)
   {
      Constructor<?> constructor = PickConstructor.pickSingletonConstructor(clazz);
      if (constructor == null)
      {
         throw new IllegalArgumentException(
               Messages.MESSAGES.unableToFindPublicConstructorForProvider(clazz.getName()));
      }
      return rpf.getInjectorFactory().createConstructor(constructor, rpf);
   }

   static UriBuilder createUriBuilder()
   {
      return new ResteasyUriBuilderImpl();
   }

   static Response.ResponseBuilder createResponseBuilder()
   {
      return new ResponseBuilderImpl();
   }

   static Variant.VariantListBuilder createVariantListBuilder()
   {
      return new VariantListBuilderImpl();
   }

   static Link.Builder createLinkBuilder()
   {
      return new LinkBuilderImpl();
   }

   static <T> HeaderDelegate<T> createHeaderDelegate(Map<Class<?>, HeaderDelegate> headerDelegates, Class<T> tClass)
   {
      Class<?> clazz = tClass;
      while (clazz != null)
      {
         HeaderDelegate<T> delegate = headerDelegates.get(clazz);
         if (delegate != null)
         {
            return delegate;
         }
         delegate = createHeaderDelegateFromInterfaces(headerDelegates, clazz.getInterfaces());
         if (delegate != null)
         {
            return delegate;
         }
         clazz = clazz.getSuperclass();
      }

      return createHeaderDelegateFromInterfaces(headerDelegates, tClass.getInterfaces());
   }

   private static <T> HeaderDelegate<T> createHeaderDelegateFromInterfaces(Map<Class<?>, HeaderDelegate> headerDelegates, Class<?>[] interfaces)
   {
      HeaderDelegate<T> delegate = null;
      for (int i = 0; i < interfaces.length; i++)
      {
         delegate = headerDelegates.get(interfaces[i]);
         if (delegate != null)
         {
            return delegate;
         }
         delegate = createHeaderDelegateFromInterfaces(headerDelegates, interfaces[i].getInterfaces());
         if (delegate != null)
         {
            return delegate;
         }
      }
      return null;
   }
}
