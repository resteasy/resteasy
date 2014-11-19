package org.jboss.resteasy.cdi;

import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.core.ValueInjector;
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
   public static final String BEAN_MANAGER_ATTRIBUTE_PREFIX = "org.jboss.weld.environment.servlet.";
   private ResteasyProviderFactory providerFactory;
   private InjectorFactory delegate;
   private BeanManager manager;
   private ResteasyCdiExtension extension;
   private Map<Class<?>, Type> sessionBeanInterface;

   public CdiInjectorFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
      this.delegate = providerFactory.getInjectorFactory();
      this.manager = lookupBeanManager();
      this.extension = lookupResteasyCdiExtension();
      sessionBeanInterface = extension.getSessionBeanInterface();
   }


   public ConstructorInjector createConstructor(Constructor constructor)
   {
      Class<?> clazz = constructor.getDeclaringClass();

      if (!manager.getBeans(clazz).isEmpty())
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.usingCdiConstructorInjector(clazz));
         return new CdiConstructorInjector(clazz, manager);
      }

      if (sessionBeanInterface.containsKey(clazz))
      {
         Type intfc = sessionBeanInterface.get(clazz);
         LogMessages.LOGGER.debug(Messages.MESSAGES.usingInterfaceForLookup(intfc, clazz));
         return new CdiConstructorInjector(intfc, manager);
      }

      LogMessages.LOGGER.debug(Messages.MESSAGES.noCDIBeansFound(clazz));
      return delegate.createConstructor(constructor);
   }

   public MethodInjector createMethodInjector(Class root, Method method)
   {
      return delegate.createMethodInjector(root, method);
   }

   public PropertyInjector createPropertyInjector(Class resourceClass)
   {
      return new CdiPropertyInjector(delegate.createPropertyInjector(resourceClass), resourceClass, sessionBeanInterface, manager);
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
         LogMessages.LOGGER.info(Messages.MESSAGES.foundBeanManagerAtJavaComp());
         return beanManager;
      }

      // Do a lookup for BeanManager at an alternative JNDI location (workaround for WELDINT-19)
      beanManager = lookupBeanManagerInJndi("java:app/BeanManager");
      if (beanManager != null)
      {
         LogMessages.LOGGER.info(Messages.MESSAGES.foundBeanManagerAtJavaApp());
         return beanManager;
      }

      // Look for BeanManager in ServletContext
      ServletContext servletContext = ResteasyProviderFactory.getContextData(ServletContext.class);
      beanManager = (BeanManager) servletContext.getAttribute(BEAN_MANAGER_ATTRIBUTE_PREFIX + BeanManager.class.getName());
      if (beanManager != null)
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.foundBeanManagerInServletContext());
         return beanManager;
      }

      // Look for BeanManager in ServletContext (the old attribute name for backwards compatibility)
      beanManager = (BeanManager) servletContext.getAttribute(BeanManager.class.getName());
      if (beanManager != null)
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.foundBeanManagerInServletContext());
         return beanManager;
      }
      
      throw new RuntimeException(Messages.MESSAGES.unableToLookupBeanManager());
   }

   private BeanManager lookupBeanManagerInJndi(String name)
   {
      try
      {
         InitialContext ctx = new InitialContext();
         LogMessages.LOGGER.debug(Messages.MESSAGES.doingALookupForBeanManager(name));
         return (BeanManager) ctx.lookup(name);
      }
      catch (NamingException e)
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.unableToObtainBeanManager(name));
         return null;
      }
      catch (NoClassDefFoundError ncdfe)
      {
         LogMessages.LOGGER.debug(Messages.MESSAGES.unableToPerformJNDILookups());
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
         throw new IllegalStateException(Messages.MESSAGES.unableToObtainResteasyCdiExtension());
      }
      CreationalContext<?> context = manager.createCreationalContext(bean);
      return (ResteasyCdiExtension) manager.getReference(bean, ResteasyCdiExtension.class, context);
   }
}
