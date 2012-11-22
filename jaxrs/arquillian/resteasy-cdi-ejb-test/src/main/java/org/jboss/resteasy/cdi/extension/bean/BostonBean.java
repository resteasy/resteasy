/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
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
package org.jboss.resteasy.cdi.extension.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.cdi.util.Utilities;

/**
 * 
 * A BostonBean is just like other beans, only much, much better.
 * 
 * [Credit to Laurie Anderson:
 * 
 *  Paradise is exactly like where you are right now,
 *  Only much, much better.
 * ]
 * 
 * BostonBeans are handled by the CDI extension BostonBeanExtension, and are implemented by classes 
 * annotated with @Boston.  
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 13, 2012
 */
public class BostonBean<T> implements Bean<T>
{
   @Inject Logger log;
   
   private Class<T> clazz;
   private String className;
   private InjectionTarget<T> injectionTarget;
   
   private Set<Type> types;
   private Set<Annotation> qualifiers;
   private Class<? extends Annotation> scope;
   private Set<InjectionPoint> injectionPoints;
   
   public BostonBean(Class<T> clazz, InjectionTarget<T> injectionTarget)
   {
      this.clazz = clazz;
      this.className = clazz.getSimpleName();
      this.injectionTarget = injectionTarget;
      types = Utilities.getTypeClosure(clazz);
      qualifiers = Utilities.getQualifiers(clazz);
      injectionPoints = injectionTarget.getInjectionPoints();
      scope = Utilities.getScopeAnnotation(clazz);
      if (scope == null)
      {
         if (Utilities.isAnnotationPresent(clazz, Path.class))
         {
            scope = RequestScoped.class;
         }
         else if (Utilities.isAnnotationPresent(clazz, Provider.class))
         {
            scope = ApplicationScoped.class;
         }
         else
         {
            scope = RequestScoped.class;
         }
      }
   }
   
   @Override
   public T create(CreationalContext<T> creationalContext)
   {
      System.out.println("BostonBean[" + className + "].create()");
      T instance = injectionTarget.produce(creationalContext);
      System.out.println("BostonBean[" + className + "].create() raw instance: " + instance);
      injectionTarget.inject(instance, creationalContext);
      injectionTarget.postConstruct(instance);
      System.out.println("BostonBean[" + className + "].create(): cooked instance: " + instance);
      return instance;
   }

   @Override
   public void destroy(T instance, CreationalContext<T> creationalContext)
   {
      System.out.println("BostonBean[" + className + "].destroy()");
      creationalContext.release();
   }

   @Override
   public Set<Type> getTypes()
   {
      System.out.println("BostonBean[" + className + "].getTypes()");
      return types;
   }

   @Override
   public Set<Annotation> getQualifiers()
   {
      System.out.println("BostonBean[" + className + "].getQualifiers()");
      return qualifiers;
   }

   @Override
   public Class<? extends Annotation> getScope()
   {
      System.out.println("BostonBean[" + className + "].getScope()");
      return scope;
   }

   @Override
   public String getName()
   {
      System.out.println("BostonBean[" + className + "].getName()");
      return null;
   }

   @Override
   public Set<Class<? extends Annotation>> getStereotypes()
   {
      System.out.println("BostonBean[" + className + "].getStereotypes()");
      return new HashSet<Class<? extends Annotation>>();
   }

   @Override
   public Class<?> getBeanClass()
   {
      System.out.println("BostonBean[" + className + "].getBeanClass()");
      return clazz;
   }

   @Override
   public boolean isAlternative()
   {
      System.out.println("BostonBean[" + className + "].isAlternative()");
      return false;
   }

   @Override
   public boolean isNullable()
   {
      System.out.println("BostonBean[" + className + "].isNullable()");
      return false;
   }

   @Override
   public Set<InjectionPoint> getInjectionPoints()
   {
      System.out.println("BostonBean[" + className + "].getInjectionPoints()");
      return injectionPoints;
   }
   
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder("BostonBean[").append(clazz).append('\r').
                                    append("            scope: ").append(scope).append('\r').
                                    append("            types: ");
      Iterator<Type> it1 = types.iterator();
      while (it1.hasNext())
      {
                                 sb.append(it1.next()).append('\r').
                                    append("                   ");
      }
                                 sb.append('\r').
                                    append("       qualifiers: ");
      Iterator<Annotation> it2 = qualifiers.iterator();
      while (it2.hasNext())
      {
                                 sb.append(it2.next()).append('\r').
                                    append("                   ");
      }
                                 sb.append('\r').
                                    append(" injection points: ");
      Iterator<InjectionPoint> it3 = getInjectionPoints().iterator();
      while (it3.hasNext())
      {
                                 sb.append(it3.next()).append('\r').
                                    append("                   ");
      }
                                 sb.append('\r').
                                    append("]");
      return sb.toString();
   }
}
