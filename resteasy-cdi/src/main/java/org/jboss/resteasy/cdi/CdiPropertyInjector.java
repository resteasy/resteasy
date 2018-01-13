/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.resteasy.cdi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.inject.spi.BeanManager;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.core.PropertyInjectorImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyFactory.ClassLoaderProvider;

/**
 * JAX-RS property injection is performed twice on CDI Beans. Firstly by the JaxrsInjectionTarget
 * wrapper and then again by RESTEasy (which operates on Weld proxies instead of the underlying instances).
 * To eliminate this, we enabled the injector only for non-CDI beans (JAX-RS components outside of BDA) or 
 * CDI components that are not JAX-RS components (i.e. {@link @Form} objects).
 *
 * @author <a href="mailto:jharting@redhat.com">Jozef Hartinger</a>
 */
public class CdiPropertyInjector implements PropertyInjector
{
   private PropertyInjector delegate;
   private Class<?> clazz;
   private boolean injectorEnabled = true;
   
   public CdiPropertyInjector(PropertyInjector delegate, Class<?> clazz, Map<Class<?>, Type> sessionBeanInterface, BeanManager manager)
   {
      this.delegate = delegate;
      this.clazz = clazz;
      
      if (sessionBeanInterface.containsKey(clazz))
      {
         injectorEnabled = false;
      }
      if (!manager.getBeans(clazz).isEmpty() && Utils.isJaxrsComponent(clazz))
      {
         injectorEnabled = false;
      }
   }
   
   public CdiPropertyInjector(PropertyInjector delegate, Class<?> clazz)
   {
      this.delegate = delegate;
      this.clazz = clazz;
   }
   
   @Override
   public void inject(Object target)
   {
      if (injectorEnabled)
      {
         delegate.inject(target);
         handleApplication(target); // Inject a javassist proxy for Application.class.
      }
   }

   @Override
   public void inject(HttpRequest request, HttpResponse response, Object target) throws Failure, WebApplicationException, ApplicationException
   {
      if (injectorEnabled)
      {
         delegate.inject(request, response, target);
         handleApplication(target); // Inject a javassist proxy for Application.class.
      }
   }

   @Override
   public String toString()
   {
      return "CdiPropertyInjector (enabled: " + injectorEnabled + ") for " + clazz;
   }
   
   /**
    * Inject a javassist proxy which finds the current Application.
    */
   protected void handleApplication(Object target)
   {
      PropertyInjectorImpl injector = null;
      if (delegate instanceof PropertyInjectorImpl)
      {
         injector = (PropertyInjectorImpl) delegate;
         HashMap<Field, ValueInjector> fieldMap = injector.getFieldMap();
         for (Entry<Field, ValueInjector> entry : fieldMap.entrySet())
         {
            if (Application.class.isAssignableFrom(entry.getKey().getType()))
            {
               try
               {
                  entry.getKey().set(target, new ApplicationInjector(entry.getKey().getType()).inject());
               }
               catch (IllegalArgumentException | IllegalAccessException e)
               {
                  LogMessages.LOGGER.error(Messages.MESSAGES.unableToInjectApplication(target.getClass().getName()));
               }
            }
         }
      }
   }
   
   /**
    * Injects a javassist proxy into a @Context annotated Application field.
    */
   protected static class ApplicationInjector implements ValueInjector
   {
      private Class<?> type;

      public ApplicationInjector(Class<?> type)
      {
         this.type = type;
      }

      @Override
      public Object inject()
      {
         if (Application.class.isAssignableFrom(type))
         {
            return createApplicationProxy(type);
         }
         return null;
      }

      @Override
      public Object inject(HttpRequest request, HttpResponse response)
      {
         if (Application.class.isAssignableFrom(type))
         {
            return createApplicationProxy(type);
         }
         return null;
      }  

      /**
       * Use javassist to create a proxy for injecting an instance of Application.class
       */
      protected Object createApplicationProxy(Class<?> clazz)
      {  
         ClassLoaderProvider classLoaderProvider = new ClassLoaderProvider()
         {
            public ClassLoader get(ProxyFactory pf) {
               return new DualClassLoader(ProxyFactory.class.getClassLoader(), clazz.getClassLoader());
            }
         };
         ProxyFactory.classLoaderProvider = classLoaderProvider;
         ProxyFactory f = new ProxyFactory();
         f.setSuperclass(clazz);
         Class<?> c = f.createClass();
         Application a;
         try
         {
            a = (Application)c.newInstance();
         }
         catch (Exception e)
         {
            LogMessages.LOGGER.error(Messages.MESSAGES.unableToInstantiateJavassistClass(c.getName()));
            return null;
         }
         MethodHandler m = new JavassistHandler();
         ((javassist.util.proxy.Proxy) a).setHandler(m);
         return a;
      }

      /**
       * Works around javassist's attempt to load javassist classes and application classes
       * with the same classloader.
       */
      static class DualClassLoader extends ClassLoader
      {
         private ClassLoader delegate;
         public DualClassLoader(ClassLoader parent, ClassLoader delegate)
         {
            super(parent);
            this.delegate = delegate;
         }

         @Override
         protected Class<?> findClass(String name) throws ClassNotFoundException
         {
            return delegate.loadClass(name);
         }
      }

      /**
       * javassist proxy handler
       */
      static class JavassistHandler implements MethodHandler
      {
         @Override
         public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable
         {
            Object delegate = ResteasyProviderFactory.getContextData(Application.class);
            if (delegate == null)
            {
               throw new LoggableFailure(Messages.MESSAGES.unableToFindContextualData(Application.class.getName()));
            }
            String name = thisMethod.getName();
            Class<?>[] argClasses = thisMethod.getParameterTypes();
            Method method = delegate.getClass().getMethod(name, argClasses);
            if (method != null)
            {
               return method.invoke(delegate, args);
            }
            try
            {
               return proceed.invoke(delegate, args);
            }
            catch (Exception e)
            {
               throw new RuntimeException("Aaargh");
            }
         }
      }
   }
}
