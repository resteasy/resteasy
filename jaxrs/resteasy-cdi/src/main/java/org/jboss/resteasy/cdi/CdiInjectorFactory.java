package org.jboss.resteasy.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.PropertyInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jozef Hartinger
 * 
 */
public class CdiInjectorFactory implements InjectorFactory
{
   private static final Logger log = LoggerFactory.getLogger(CdiInjectorFactory.class);
   private InjectorFactory delegate;
   private BeanManager manager;

   private Map<Class<?>, Class<?>> sessionBeanInterface;

   public CdiInjectorFactory(InjectorFactory delegate)
   {
      this.delegate = delegate;
   }
   
   protected void init(Map<Class<?>, Class<?>> sessionBeanInterface, BeanManager manager)
   {
      this.sessionBeanInterface = sessionBeanInterface;
      this.manager = manager;
   }

   public ConstructorInjector createConstructor(Constructor constructor)
   {
      Class<?> clazz = constructor.getDeclaringClass();

      if (!manager.getBeans(constructor.getDeclaringClass()).isEmpty())
      {
         log.debug("Using CdiConstructorInjector for class {}.", clazz);
         return new CdiConstructorInjector(clazz, manager);
      }
      
      if (sessionBeanInterface.containsKey(constructor.getDeclaringClass()))
      {
         Class<?> intfc = sessionBeanInterface.get(clazz);
         log.debug("Using interface {} for lookup of Session Bean {}.", intfc, clazz);
         return new CdiConstructorInjector(intfc, manager);
      }
      
      log.debug("No CDI beans found for {}. Using default ConstructorInjector.", clazz);
      return delegate.createConstructor(constructor);
   }

   public MethodInjector createMethodInjector(Class root, Method method)
   {
      return delegate.createMethodInjector(root, method);
   }

   public PropertyInjector createPropertyInjector(Class resourceClass)
   {
      // JAX-RS property injection is performed twice. Firstly by the JaxrsInjectionTarget
      // wrapper and then again by RESTEasy. To eliminate this, we return a noop PropertyInjector.
      return new NoopPropertyInjector();
   }
   
   public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations)
   {
      return delegate.createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations);
   }
}
