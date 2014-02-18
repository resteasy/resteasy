package org.jboss.resteasy.plugins.spring;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.ClassUtils;

/**
 * <p>
 * The processor will register any bean annotated with @Path or @Provider into
 * the Resteasy framework.
 * </p>
 * <p>
 * It also sets up Registry and ResteasyProviderFactory to be autowirable via @Autowire
 * in Controllers/service layers.
 * </p>
 * <p/>
 * <p>
 * There's quite a bit of spring integration functionality under the covers:
 * </p>
 * <ol>
 * <li>@Providers, such as RESTEasy interceptors and String converters have to
 * be registered in RESTEasy before resources and registers. That gets a bit
 * tricky, so depends-on functionality is used as well</li>
 * <p/>
 * <li>
 * </ol>
 * <p/>
 * <p>
 * This class takes advantaage of quite a few Spring
 * </p>
 *
 * @author <a href="mailto:sduskis@burkecentral.com">Bill Burke</a>
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SpringBeanProcessor implements BeanFactoryPostProcessor, ApplicationListener
{
   protected Registry registry;
   protected ResteasyProviderFactory providerFactory;
   protected Dispatcher dispatcher;

   protected Map<String, SpringResourceFactory> resourceFactories = new HashMap<String, SpringResourceFactory>();
   protected Set<String> providerNames = new HashSet<String>();
   private int order;

   protected class ResteasyBeanPostProcessor implements BeanPostProcessor
   {
      private final ConfigurableListableBeanFactory beanFactory;

      protected ResteasyBeanPostProcessor(ConfigurableListableBeanFactory beanFactory)
      {
         this.beanFactory = beanFactory;
      }

      @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
              throws BeansException
      {
         return bean;
      }

      /**
       * This method is invoked after postProcessBeanFactory.
       * <p/>
       * this method is invoked when ever a new bean is created. This will
       * perform the following:
       * <p/>
       * <ol>
       * <p/>
       * <li>RESTEasy injection of singleton @Providers, as well as @Provider
       * registration
       * <p/>
       * <li>either singleton or request/prototype RESTeasy injection... but not
       * registration. The RESTEasy registration happens in the
       * onApplicationEvent() below, which happens at the end of the Spring
       * life-cycle
       * <p/>
       * </ol>
       *
       * @see SpringBeanProcessor.postProcessBeanFactory
       */
      @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
              throws BeansException
      {
         if (providerNames.contains(beanName))
         {
            PropertyInjector injector = getInjector(AopUtils.getTargetClass(bean));
            injector.inject(bean);
            providerFactory.registerProviderInstance(bean);
            return bean;
         }

         SpringResourceFactory resourceFactory = resourceFactories.get(beanName);
         if (resourceFactory != null)
         {
            inject(beanName, bean, getInjector(resourceFactory.getScannableClass()));
         }

         return bean;
      }

      public PropertyInjector getInjector(Class<?> clazz)
      {
         return providerFactory.getInjectorFactory().createPropertyInjector(clazz);
      }

      public void inject(String beanName, Object bean, PropertyInjector propertyInjector)
      {
         if (propertyInjector == null)
         {
            return;
         }
         HttpRequest request = ResteasyProviderFactory.getContextData(HttpRequest.class);
         if (request == null || isSingleton(beanName))
         {
            propertyInjector.inject(bean);
         }
         else
         {
            HttpResponse response = ResteasyProviderFactory.getContextData(HttpResponse.class);
            propertyInjector.inject(request, response, bean);
         }
      }

      private boolean isSingleton(String beanName)
      {
         boolean isSingleton = false;
         try
         {
            BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
            isSingleton = beanDef.isSingleton();
         }
         catch (org.springframework.beans.factory.NoSuchBeanDefinitionException nsbde)
         {
            // cannot distinguish between singleton & prototype
         }
         return isSingleton;
      }
   }

   public SpringBeanProcessor(Dispatcher dispatcher)
   {
      this(dispatcher, dispatcher.getRegistry(), dispatcher.getProviderFactory());
   }

   public SpringBeanProcessor(Dispatcher dispatcher, Registry registry,
                              ResteasyProviderFactory providerFactory)
   {
      this.setRegistry(registry);
      this.setProviderFactory(providerFactory);
      this.setDispatcher(dispatcher);
   }

   public SpringBeanProcessor()
   {
   }

   @Required
   public Registry getRegistry()
   {
      return registry;
   }

   public void setRegistry(Registry registry)
   {
      this.registry = registry;
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public void setProviderFactory(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   public Dispatcher getDispatcher()
   {
      return dispatcher;
   }

   public void setDispatcher(Dispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   /**
    * <p>
    * This method keeps track of @Provider and resources for future use. It also
    * registers the RESTEasy Registry, ProviderFactry, and Dispatcher for @Autowire
    * injection.
    * </p>
    * <p/>
    * <p>
    * Beyond tracking, this will ensure that non-MessageBody(Reader|Writer) @Providers
    * are created by Spring before any resources by having the resources
    * "depends-on" the @Providers.
    * </p>
    */
   @Override
public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
           throws BeansException
   {
      beanFactory.registerResolvableDependency(Registry.class, getRegistry());
      beanFactory.registerResolvableDependency(ResteasyProviderFactory.class, getProviderFactory());
      if (dispatcher != null)
      {
         beanFactory.registerResolvableDependency(Dispatcher.class, getDispatcher());
      }
      beanFactory.addBeanPostProcessor(new ResteasyBeanPostProcessor(beanFactory));

      findResteasyRegistrations(beanFactory);

      List<String> dependsOnProviders = new ArrayList<String>();
      for (String name : beanFactory.getBeanDefinitionNames())
      {
         if (resourceFactories.containsKey(name))
            continue;

         BeanDefinition beanDef = beanFactory.getBeanDefinition(name);
         if ((beanDef.getBeanClassName() == null && beanDef.getFactoryBeanName() == null)
                 || beanDef.isAbstract())
            continue;

         processBean(beanFactory, dependsOnProviders, name, beanDef);
      }

      String[] dependsOnArray = dependsOnProviders.toArray(new String[0]);

      if (dependsOnArray.length > 0)
      {
         for (SpringResourceFactory resourceFactory : resourceFactories.values())
         {
            BeanDefinition beanDef = beanFactory.getBeanDefinition(resourceFactory.getBeanName());
            beanDef.setDependsOn(concat(beanDef.getDependsOn(), dependsOnArray));
         }
      }
   }

   /**
    * process a single @Provider or a single resource.
    */
   protected Class<?> processBean(final ConfigurableListableBeanFactory beanFactory,
                                  List<String> dependsOnProviders, String name, BeanDefinition beanDef)
   {
      Class<?> beanClass = getBeanClass(name, beanDef, beanFactory);
      if (beanClass.isAnnotationPresent(Provider.class))
      {
         if (!isSingleton(beanDef))
         {
            throw new RuntimeException("Provider " + name
                    + " is not a singleton.  That's not allowed");
         }

         providerNames.add(name);

         if (!MessageBodyWriter.class.isAssignableFrom(beanClass)
                 && !MessageBodyReader.class.isAssignableFrom(beanClass))
         {
            dependsOnProviders.add(name);
         }
      }
      if (GetRestful.isRootResource(beanClass))
      {
         resourceFactories.put(name, new SpringResourceFactory(name, beanFactory, beanClass));
      }
      return beanClass;
   }

   /**
    * Find all beans of type ResteasyRegistration and ensure that RESTeasy
    * registers them under a different prefix url.
    */
   private void findResteasyRegistrations(final ConfigurableListableBeanFactory beanFactory)
   {
      Map<String, ResteasyRegistration> registries = beanFactory
              .getBeansOfType(ResteasyRegistration.class);

      for (ResteasyRegistration registration : registries.values())
      {
         String beanName = registration.getBeanName();
         BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
         Class<?> beanClass = getBeanClass(beanName, beanDef, beanFactory);
         SpringResourceFactory resourceFactory = new SpringResourceFactory(beanName, beanFactory,
                 beanClass);
         resourceFactory.setContext(registration.getContext());
         resourceFactories.put(beanName, resourceFactory);
      }
   }

   /**
    * merge two arrays.
    *
    * @param dependsOn
    * @param dependsOnProviders
    * @return
    */
   private static String[] concat(String[] dependsOn, String[] dependsOnProviders)
   {
      if (dependsOn == null || dependsOn.length == 0)
      {
         return dependsOnProviders;
      }

      String[] result = new String[dependsOn.length + dependsOnProviders.length];

      System.arraycopy(dependsOn, 0, result, 0, dependsOn.length);
      System.arraycopy(dependsOnProviders, 0, result, dependsOn.length, dependsOnProviders.length);

      return result;
   }

   /**
    * Get the bean class, and take @Configuration @Beans into consideration
    *
    * @param beanDef
    * @param beanFactory
    * @return
    */
   private static Class<?> getBeanClass(String name, BeanDefinition beanDef,
                                        ConfigurableListableBeanFactory beanFactory)
   {
      if (beanDef instanceof RootBeanDefinition)
      {
         RootBeanDefinition rootBeanDef = (RootBeanDefinition) beanDef;
         try
         {
            if (rootBeanDef.getBeanClass() != null)
            {
               return rootBeanDef.getBeanClass();
            }
         }
         catch (IllegalStateException e)
         {
            // do nothing. This gets thrown for factory beans
         }
      }

      // final String factoryBeanName = beanDef.getFactoryBeanName();
      final String factoryMethodName = beanDef.getFactoryMethodName();

      if (beanDef.getBeanClassName() != null && factoryMethodName == null)
      {
         return getBeanClass(beanDef.getBeanClassName());
      }

      if (factoryMethodName != null)
      {
         String factoryClassName = null;

         if (beanDef instanceof AnnotatedBeanDefinition)
         {
            factoryClassName = ((AnnotatedBeanDefinition) beanDef).getMetadata().getClassName();
         }
         else
         {
            // Checks if beanDefinition has a factorybean defined. If so, lookup the classname of that bean
            // definition and use that as the factory class name.
            if (beanDef.getFactoryBeanName() != null)
            {
               factoryClassName = beanFactory.getBeanDefinition(beanDef.getFactoryBeanName()).getBeanClassName();
            }
            else
            {
               factoryClassName = beanDef.getBeanClassName();
            }
         }

         for (Method method : getBeanClass(factoryClassName).getMethods())
         {
            if (method.getName().equals(factoryMethodName))
            {
               return method.getReturnType();
            }
         }
      }

      throw new IllegalStateException("could not find the type for bean named " + name);
   }

   private static boolean isSingleton(BeanDefinition beanDef)
   {
      try
      {
         return beanDef.isSingleton();
      }
      catch (NoSuchBeanDefinitionException nsbde)
      {
         // cannot distinguish between singleton & prototype
         return false;
      }
   }

   private static Class<?> getBeanClass(final String beanClassName)
   {
      try
      {
         return ClassUtils.forName(beanClassName, Thread.currentThread().getContextClassLoader());
      }
      catch (final ClassNotFoundException e)
      {
         throw new IllegalStateException("Could not convert '" + beanClassName + "' to a class.", e);
      }
   }

   /**
    * Register all of the resources into RESTEasy only when Spring finishes it's
    * life-cycle and the spring singleton bean creation is completed
    */
   @Override
   public void onApplicationEvent(ApplicationEvent event)
   {
      if (event.getClass() != ContextRefreshedEvent.class)
         return;
      for (SpringResourceFactory resourceFactory : resourceFactories.values())
      {
         getRegistry().removeRegistrations(resourceFactory.getScannableClass());
      }

//  The following code would reprocess the bean factory, in case the configuration changed.
//  However, it needs work.
//      if (event.getSource() instanceof XmlWebApplicationContext)
//      {
//         ConfigurableListableBeanFactory beanFactory = ((XmlWebApplicationContext) event.getSource()).getBeanFactory();
//         postProcessBeanFactory(beanFactory);
//      }
      for (SpringResourceFactory resourceFactory : resourceFactories.values())
      {
         getRegistry().addResourceFactory(resourceFactory, resourceFactory.getContext());
      }
   }

   public int getOrder()
   {
      return this.order;
   }

   public void setOrder(int order)
   {
      this.order = order;
   }
/*
   @Override
   public boolean supportsEventType(Class<? extends ApplicationEvent> eventType)
   {
      return eventType == ContextRefreshedEvent.class;
   }

   @Override
   public boolean supportsSourceType(Class<?> sourceType)
   {
      return ApplicationContext.class.isAssignableFrom(sourceType);
   }
   */
}
