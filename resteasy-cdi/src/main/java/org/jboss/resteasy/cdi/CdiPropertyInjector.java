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

import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;

import javax.enterprise.inject.spi.BeanManager;
import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * JAX-RS property injection is performed twice on CDI Beans. Firstly by the JaxrsInjectionTarget
 * wrapper and then again by RESTEasy (which operates on Weld proxies instead of the underlying instances).
 * To eliminate this, we enabled the injector only for non-CDI beans (JAX-RS components outside of BDA) or 
 * CDI components that are not JAX-RS components.
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
   
   @Override
   public CompletionStage<Void> inject(Object target, boolean unwrapAsync)
   {
      if (injectorEnabled)
      {
         return delegate.inject(target, unwrapAsync);
      }
      return CompletableFuture.completedFuture(null);
   }

   @Override
   public CompletionStage<Void> inject(HttpRequest request, HttpResponse response, Object target, boolean unwrapAsync) throws Failure, WebApplicationException, ApplicationException
   {
      if (injectorEnabled)
      {
         return delegate.inject(request, response, target, unwrapAsync);
      }
      return CompletableFuture.completedFuture(null);
   }

   @Override
   public String toString()
   {
      return "CdiPropertyInjector (enabled: " + injectorEnabled + ") for " + clazz;
   }
}
