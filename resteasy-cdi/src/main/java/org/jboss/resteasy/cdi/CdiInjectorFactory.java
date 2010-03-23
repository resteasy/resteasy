package org.jboss.resteasy.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.resteasy.cdi.Bootstrap.lookupResteasyCdiConfiguration;

/**
 * 
 * @author Jozef Hartinger
 * 
 */
public class CdiInjectorFactory implements InjectorFactory
{
   private static final Logger log = LoggerFactory.getLogger(CdiInjectorFactory.class);
   private PropertyInjector noopPropertyInjector = new NoopPropertyInjector();
   private InjectorFactory delegate;
   private BeanManager manager;
   private ResteasyCdiConfiguration configuration;

   public CdiInjectorFactory()
   {
      this.delegate = ResteasyProviderFactory.getInstance().getInjectorFactory();
      this.manager = lookupBeanManager();
      this.configuration = lookupResteasyCdiConfiguration(manager);
   }
   

   public ConstructorInjector createConstructor(Constructor constructor)
   {
      Class<?> clazz = constructor.getDeclaringClass();

      if (!manager.getBeans(constructor.getDeclaringClass()).isEmpty())
      {
         log.debug("Using CdiConstructorInjector for class {}.", clazz);
         return new CdiConstructorInjector(clazz, manager);
      }
      
      if (configuration.containsSessionBeanClass((constructor.getDeclaringClass())))
      {
         Class<?> intfc = configuration.getSessionBeanLocalInterface(clazz);
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
      return noopPropertyInjector;
   }
   
   public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations)
   {
      return delegate.createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations);
   }
   
   private BeanManager lookupBeanManager()
   {
      InitialContext ctx = null;
      try
      {
         log.debug("Doing a lookup of BeanManager in java:comp/BeanManager");
         ctx = new InitialContext();
         return (BeanManager) ctx.lookup("java:comp/BeanManager");
      }
      catch (NamingException e)
      {
         // Workaround for WELDINT-19
         try
         {
            log.debug("Lookup failed. Trying java:app/BeanManager");
            return (BeanManager) ctx.lookup("java:app/BeanManager");
         }
         catch (NamingException ne)
         {
            throw new RuntimeException("Unable to obtain BeanManager.", ne);
         }
      }
   }
}
