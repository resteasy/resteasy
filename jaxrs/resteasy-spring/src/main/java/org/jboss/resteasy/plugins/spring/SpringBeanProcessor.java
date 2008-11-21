package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import javax.ws.rs.ext.Provider;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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

   protected static class SpringResourceFactory implements ResourceFactory
   {

      protected BeanFactory beanFactory;
      protected String beanName;
      protected Class scannableClass;
      protected PropertyInjector propertyInjector;

      public SpringResourceFactory(String beanName, BeanFactory beanFactory, Class scannable)
      {
         this.beanName = beanName;
         this.beanFactory = beanFactory;
         this.scannableClass = scannable;
      }

      public PropertyInjector getPropertyInjector()
      {
         return propertyInjector;
      }

      public Object createResource(HttpRequest request, HttpResponse response,
                                   InjectorFactory factory)
      {
         return beanFactory.getBean(beanName);
      }

      public Class<?> getScannableClass()
      {
         return scannableClass;
      }

      public void registered(InjectorFactory factory)
      {
         this.propertyInjector = factory.createPropertyInjector(getScannableClass());
      }

      public void requestFinished(HttpRequest request, HttpResponse response,
                                  Object resource)
      {
      }

      public void unregistered()
      {
      }

   }

   protected class ResteasyBeanPostProcessor implements BeanPostProcessor
   {
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
         if (request == null)
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
            if (registration.getContext() != null)
            {
               getRegistry().addResourceFactory(reg, registration.getContext());
            }
            else
            {
               getRegistry().addResourceFactory(reg);
            }
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
      beanFactory.addBeanPostProcessor(new ResteasyBeanPostProcessor());
      Collection<String> ignoreList = createIgnoreList(beanFactory);
      for (String name : beanFactory.getBeanDefinitionNames())
      {
         if (ignoreList.contains(name)) continue;

         BeanDefinition beanDef = beanFactory.getBeanDefinition(name);
         if (beanDef.getBeanClassName() == null) continue;
         if (beanDef.isAbstract()) continue;
         Class beanClass = null;
         try
         {
            beanClass = Thread.currentThread().getContextClassLoader().loadClass(beanDef.getBeanClassName());
         }
         catch (ClassNotFoundException e)
         {
            throw new RuntimeException(e);
         }
         if (GetRestful.isRootResource(beanClass))
         {
            SpringResourceFactory resourceFactory = new SpringResourceFactory(name, beanFactory, beanClass);
            resourceFactories.put(name, resourceFactory);
            getRegistry().addResourceFactory(resourceFactory);
         }
         else if (beanClass.isAnnotationPresent(Provider.class))
         {
            getProviderFactory().registerProviderInstance(beanFactory.getBean(name));
         }
      }
   }

}
