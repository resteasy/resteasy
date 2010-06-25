package org.jboss.resteasy.plugins.spring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * The processor will register any bean annotated with @Path or @Provider into the Resteasy framework.
 * <p/>
 * It also sets up Registry and ResteasyProviderFactory to be autowirable.
 * <p/>
 * <b>Note</b>:
 * This class will have undesirable affects if you are doing handcoded proxying with Spring, i.e., with
 * ProxyFactoryBean.  If you are using auto-proxied beans, you will be ok.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SpringBeanProcessor implements BeanFactoryPostProcessor
{
   protected Registry registry;
   protected ResteasyProviderFactory providerFactory;
   protected Dispatcher dispatcher;

   protected Map<String, SpringResourceFactory> resourceFactories = new HashMap<String, SpringResourceFactory>();

   protected class ResteasyBeanPostProcessor implements BeanPostProcessor
   {
      private ConfigurableListableBeanFactory beanFactory;
  
      protected ResteasyBeanPostProcessor(ConfigurableListableBeanFactory beanFactory) 
      {
         this.beanFactory = beanFactory;
      }

      public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
      {
         return bean;
      }

      public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
      {
         SpringResourceFactory resourceFactory = resourceFactories.get(beanName);
         if (resourceFactory == null) return bean;

         PropertyInjector propertyInjector = resourceFactory.getPropertyInjector();

         HttpRequest request = ResteasyProviderFactory.getContextData(HttpRequest.class);
         if (isSingleton(beanName) || request == null)
         {
            propertyInjector.inject(bean);
         }
         else
         {
            HttpResponse response = ResteasyProviderFactory.getContextData(HttpResponse.class);
            propertyInjector.inject(request, response, bean);
         }

         return bean;
      }

      private boolean isSingleton(String beanName)
      {
         boolean isSingleton = false;
         try {
    	     BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
             isSingleton = beanDef.isSingleton();
         } catch (org.springframework.beans.factory.NoSuchBeanDefinitionException nsbde) {
             // cannot distinguish between singleton & prototype
         }
         return isSingleton;
      }
   }

   public SpringBeanProcessor(Dispatcher dispatcher)
   {
      this(dispatcher, dispatcher.getRegistry(), dispatcher.getProviderFactory());
   }
   
   public SpringBeanProcessor(Dispatcher dispatcher, Registry registry, ResteasyProviderFactory providerFactory)
   {
      this.setRegistry(registry);
      this.setProviderFactory(providerFactory);
      this.setDispatcher(dispatcher);
   }

   @Deprecated
   public SpringBeanProcessor(Registry registry, ResteasyProviderFactory providerFactory)
   {
      this.setRegistry(registry);
      this.setProviderFactory(providerFactory);
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

   @SuppressWarnings("unchecked")
   private Collection<String> createIgnoreList(
           final ConfigurableListableBeanFactory beanFactory)
   {
      Map<String, ResteasyRegistration> registries = beanFactory
              .getBeansOfType(ResteasyRegistration.class);

      final Collection<String> ignoreBeansList = new HashSet<String>();
      for (ResteasyRegistration registration : registries.values())
      {
         String beanName = registration.getBeanName();
         ignoreBeansList.add(beanName);
         BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
         Class beanClass = null;
         try
         {
            beanClass = Thread.currentThread().getContextClassLoader().loadClass(beanDef.getBeanClassName());
            SpringResourceFactory reg = new SpringResourceFactory(beanName, beanFactory, beanClass);
            getRegistry().addResourceFactory(reg, registration.getContext());
         }
         catch (ClassNotFoundException e)
         {
            throw new RuntimeException(e);
         }
      }
      return ignoreBeansList;
   }

   public void postProcessBeanFactory(
           final ConfigurableListableBeanFactory beanFactory)
           throws BeansException
   {
      beanFactory.registerResolvableDependency(Registry.class, getRegistry());
      beanFactory.registerResolvableDependency(ResteasyProviderFactory.class, getProviderFactory());
      if (dispatcher != null)
      {
         beanFactory.registerResolvableDependency(Dispatcher.class, getDispatcher());
      }
      beanFactory.addBeanPostProcessor(new ResteasyBeanPostProcessor(beanFactory));
      Collection<String> ignoreList = createIgnoreList(beanFactory);

      List<SpringResourceFactory> springResourceFactories = new ArrayList<SpringResourceFactory>();
      for (String name : beanFactory.getBeanDefinitionNames())
      {
         if (ignoreList.contains(name)) continue;

         BeanDefinition beanDef = beanFactory.getBeanDefinition(name);
         if (beanDef.getBeanClassName() == null) continue;
         if (beanDef.isAbstract()) continue;

         Class<?> beanClass = null;
         try
         {
            beanClass = Thread.currentThread().getContextClassLoader().loadClass(beanDef.getBeanClassName());
         }
         catch (ClassNotFoundException e)
         {
            throw new RuntimeException(e);
         }
         if (beanClass.isAnnotationPresent(Provider.class))
         {
            getProviderFactory().registerProviderInstance(beanFactory.getBean(name));
         }
         if (GetRestful.isRootResource(beanClass))
         {
            springResourceFactories.add(new SpringResourceFactory(name, beanFactory, beanClass));
         }
      }

      for (SpringResourceFactory resourceFactory : springResourceFactories)
      {
         resourceFactories.put(resourceFactory.beanName, resourceFactory);
         getRegistry().addResourceFactory(resourceFactory);
      }
   }
}
