package org.jboss.resteasy.cdi;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceConstructor;
import org.jboss.resteasy.spi.metadata.ResourceLocator;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Map;
import java.util.Set;

/**
 * @author Jozef Hartinger
 */
@SuppressWarnings("rawtypes")
public class CdiInjectorFactory implements InjectorFactory
{
   private static final Logger log = Logger.getLogger(CdiInjectorFactory.class);
   public static final String BEAN_MANAGER_ATTRIBUTE_PREFIX = "org.jboss.weld.environment.servlet.";
   private BeanManager manager;
   private InjectorFactory delegate = new InjectorFactoryImpl();
   private ResteasyCdiExtension extension;
   private Map<Class<?>, Type> sessionBeanInterface;

   public CdiInjectorFactory()
   {
      this.manager = lookupBeanManager();
      this.extension = lookupResteasyCdiExtension();
      sessionBeanInterface = extension.getSessionBeanInterface();
   }

   @Override
   public ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory)
   {
      return delegate.createParameterExtractor(parameter, providerFactory);
   }

   @Override
   public MethodInjector createMethodInjector(ResourceLocator method, ResteasyProviderFactory factory)
   {
      return delegate.createMethodInjector(method, factory);
   }

   @Override
   public PropertyInjector createPropertyInjector(ResourceClass resourceClass, ResteasyProviderFactory providerFactory)
   {
      return new CdiPropertyInjector(delegate.createPropertyInjector(resourceClass, providerFactory), resourceClass.getClazz(), sessionBeanInterface, manager);
   }

   @Override
   public ConstructorInjector createConstructor(ResourceConstructor constructor, ResteasyProviderFactory providerFactory)
   {
      Class<?> clazz = constructor.getConstructor().getDeclaringClass();

      ConstructorInjector injector = cdiConstructor(clazz);
      if (injector != null) return injector;

      log.debug("No CDI beans found for {0}. Using default ConstructorInjector.", clazz);
      return delegate.createConstructor(constructor, providerFactory);
   }

   @Override
   public ConstructorInjector createConstructor(Constructor constructor, ResteasyProviderFactory factory)
   {
      Class<?> clazz = constructor.getDeclaringClass();

      ConstructorInjector injector = cdiConstructor(clazz);
      if (injector != null) return injector;

      log.debug("No CDI beans found for {0}. Using default ConstructorInjector.", clazz);
      return delegate.createConstructor(constructor, factory);
   }



   protected ConstructorInjector cdiConstructor(Class<?> clazz)
   {
      if (!manager.getBeans(clazz).isEmpty())
      {
         log.debug("Using CdiConstructorInjector for class {0}.", clazz);
         return new CdiConstructorInjector(clazz, manager);
      }

      if (sessionBeanInterface.containsKey(clazz))
      {
         Type intfc = sessionBeanInterface.get(clazz);
         log.debug("Using {0} for lookup of Session Bean {1}.", intfc, clazz);
         return new CdiConstructorInjector(intfc, manager);
      }

      return null;
   }

   public PropertyInjector createPropertyInjector(Class resourceClass, ResteasyProviderFactory factory)
   {
      return new CdiPropertyInjector(delegate.createPropertyInjector(resourceClass, factory), resourceClass, sessionBeanInterface, manager);
   }

   public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      return delegate.createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations, factory);
   }

   public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, Class type,
                                                 Type genericType, Annotation[] annotations, boolean useDefault, ResteasyProviderFactory factory)
   {
      return delegate.createParameterExtractor(injectTargetClass, injectTarget, type, genericType, annotations, useDefault, factory);
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
         log.debug("Found BeanManager at java:comp/BeanManager");
         return beanManager;
      }

      // Do a lookup for BeanManager at an alternative JNDI location (workaround for WELDINT-19)
      beanManager = lookupBeanManagerInJndi("java:app/BeanManager");
      if (beanManager != null)
      {
         log.debug("Found BeanManager at java:app/BeanManager");
         return beanManager;
      }

      // Look for BeanManager in ServletContext
      ServletContext servletContext = ResteasyProviderFactory.getContextData(ServletContext.class);
      // null check for RESTEASY-1009
      if (servletContext != null)
      {
          beanManager = (BeanManager) servletContext.getAttribute(BEAN_MANAGER_ATTRIBUTE_PREFIX + BeanManager.class.getName());
          if (beanManager != null)
          {
             log.debug("Found BeanManager in ServletContext");
             return beanManager;
          }

          // Look for BeanManager in ServletContext (the old attribute name for backwards compatibility)
          beanManager = (BeanManager) servletContext.getAttribute(BeanManager.class.getName());
          if (beanManager != null)
          {
             log.debug("Found BeanManager in ServletContext");
             return beanManager;
          }
      }

       beanManager = lookupBeanManagerCDIUtil();
       if(beanManager != null)
       {
           log.debug("Found BeanManager via CDI Util");
           return beanManager;
       }

      throw new RuntimeException("Unable to lookup BeanManager.");
   }

   private BeanManager lookupBeanManagerInJndi(String name)
   {
      try
      {
         InitialContext ctx = new InitialContext();
         log.debug("Doing a lookup for BeanManager in {0}", name);
         return (BeanManager) ctx.lookup(name);
      }
      catch (NamingException e)
      {
         log.debug("Unable to obtain BeanManager from {0}", name);
         return null;
      }
      catch (NoClassDefFoundError ncdfe)
      {
         log.debug("Unable to perform JNDI lookups. You are probably running on GAE.");
         return null;
      }
   }

   public static BeanManager lookupBeanManagerCDIUtil()
   {
       BeanManager bm = null;
       try {
           Class<?> cdiClass = Class.forName("javax.enterprise.inject.spi.CDI");
           Object cdiObj = cdiClass.getMethod("current").invoke(null);
           if(cdiObj != null) {
               bm = (BeanManager)cdiClass.getMethod("getBeanManager").invoke(cdiObj);
           }
       } catch (ClassNotFoundException e) {
           log.debug("Not able to access CDI Object, class not found.",e);
       } catch (InvocationTargetException e) {
           log.debug("Not able to access CDI Object.",e);
       } catch (NoSuchMethodException e) {
           log.debug("Not able to access CDI Object.",e);
       } catch (IllegalAccessException e) {
           log.debug("Not able to access CDI Object.",e);
       }
       return bm;
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
