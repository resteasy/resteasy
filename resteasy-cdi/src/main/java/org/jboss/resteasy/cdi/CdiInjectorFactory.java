package org.jboss.resteasy.cdi;

import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * @author Jozef Hartinger
 */
@SuppressWarnings("rawtypes")
public class CdiInjectorFactory implements InjectorFactory
{
   private static final Logger log = Logger.getLogger(CdiInjectorFactory.class);
   private PropertyInjector noopPropertyInjector = new NoopPropertyInjector();
   private InjectorFactory delegate;
   private BeanManager manager;
   private ResteasyCdiExtension extension;
   private Map<Class<?>, Class<?>> sessionBeanInterface;

   public CdiInjectorFactory()
   {
      this.delegate = ResteasyProviderFactory.getInstance().getInjectorFactory();
      this.manager = lookupBeanManager();
      this.extension = lookupResteasyCdiExtension();
      sessionBeanInterface = extension.getSessionBeanInterface();
   }


   public ConstructorInjector createConstructor(Constructor constructor)
   {
      Class<?> clazz = constructor.getDeclaringClass();

      if (!manager.getBeans(constructor.getDeclaringClass()).isEmpty())
      {
         log.debug("Using CdiConstructorInjector for class {}.", clazz);
         return new CdiConstructorInjector(clazz, manager);
      }

      if (sessionBeanInterface.containsKey((constructor.getDeclaringClass())))
      {
         Class<?> intfc = sessionBeanInterface.get(clazz);
         log.debug("Using {} for lookup of Session Bean {}.", intfc, clazz);
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

   public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type,
                                                 Type genericType, Annotation[] annotations, boolean useDefault)
   {
      return delegate.createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations, useDefault);
   }

   /**
    * Do a lookup for BeanManager instance. JNDI and ServletContext is searched.
    *
    * @return BeanManager instance
    */
   protected BeanManager lookupBeanManager()
   {
      BeanManager beanManager = null;

      // Do a lookup for BeanManager in JNDI (this is the only *portable* way)
      beanManager = lookupBeanManagerInJndi("java:comp/BeanManager");
      if (beanManager != null)
      {
         log.info("Found BeanManager at java:comp/BeanManager");
         return beanManager;
      }

      // Do a lookup for BeanManager at an alternative JNDI location (workaround for WELDINT-19)
      beanManager = lookupBeanManagerInJndi("java:app/BeanManager");
      if (beanManager != null)
      {
         log.info("Found BeanManager at java:app/BeanManager");
         return beanManager;
      }

      // Look for BeanManager in ServletContext
      ServletContext servletContext = ResteasyProviderFactory.getContextData(ServletContext.class);
      beanManager = (BeanManager) servletContext.getAttribute(BeanManager.class.getName());
      if (beanManager != null)
      {
         log.info("Found BeanManager in ServletContext");
         return beanManager;
      }

      throw new RuntimeException("Unable to lookup BeanManager.");
   }

   private BeanManager lookupBeanManagerInJndi(String name)
   {
      try
      {
         InitialContext ctx = new InitialContext();
         log.debug("Doing a lookup for BeanManager in {}", name);
         return (BeanManager) ctx.lookup(name);
      }
      catch (NamingException e)
      {
         log.debug("Unable to obtain BeanManager from {}", name);
         return null;
      }
      catch (NoClassDefFoundError ncdfe)
      {
         log.debug("Unable to perform JNDI lookups. You are probably running on GAE.");
         return null;
      }
   }

   /**
    * Lookup ResteasyCdiExtension instance that was instantiated during CDI bootstrap
    *
    * @return ResteasyCdiExtension instance
    */
   private ResteasyCdiExtension lookupResteasyCdiExtension()
   {
      Set<Bean<?>> beans = manager.getBeans(ResteasyCdiExtension.class);
      Bean<?> bean = manager.resolve(beans);
      if (bean == null)
      {
         throw new IllegalStateException("Unable to obtain ResteasyCdiExtension instance.");
      }
      CreationalContext<?> context = manager.createCreationalContext(bean);
      return (ResteasyCdiExtension) manager.getReference(bean, ResteasyCdiExtension.class, context);
   }
}
